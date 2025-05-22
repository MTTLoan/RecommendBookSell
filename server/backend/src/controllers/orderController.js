import Order from "../models/Order.js";
import Book from "../models/Book.js";
import Notification from "../models/Notification.js";
import mongoose from "mongoose";
import { deleteSelectedCartItems } from "./cartController.js";

export const getOrderHistory = async (req, res) => {
  try {
    const userId = req.user.id;

    // Khởi tạo danh sách trạng thái
    const statuses = [
      "Đang đóng gói",
      "Chờ giao hàng",
      "Đã giao",
      "Trả hàng",
      "Đã hủy",
    ];
    const orderHistory = {};

    // Đảm bảo tất cả trạng thái đều có mảng rỗng
    statuses.forEach((status) => {
      orderHistory[status] = [];
    });

    // Lấy đơn hàng, thông tin người dùng và thông tin sách
    const orders = await Order.aggregate([
      { $match: { userId: userId } },
      { $sort: { updatedAt: -1 } }, // Sắp xếp theo updatedAt giảm dần (mới nhất trước)
      {
        $lookup: {
          from: "users",
          localField: "userId",
          foreignField: "id",
          as: "user",
        },
      },
      { $unwind: "$user" },
      {
        $lookup: {
          from: "books",
          localField: "items.bookId",
          foreignField: "id",
          as: "itemDetails",
        },
      },
      {
        $addFields: {
          items: {
            $map: {
              input: "$items",
              as: "item",
              in: {
                bookId: "$$item.bookId",
                quantity: "$$item.quantity",
                unitPrice: "$$item.unitPrice",
                book: {
                  $arrayElemAt: [
                    {
                      $filter: {
                        input: "$itemDetails",
                        as: "book",
                        cond: { $eq: ["$$book.id", "$$item.bookId"] },
                      },
                    },
                    0,
                  ],
                },
              },
            },
          },
        },
      },
      {
        $project: {
          itemDetails: 0,
          "user.password": 0,
        },
      },
    ]).catch((err) => {
      console.error("Aggregation error:", err);
      return [];
    });

    // Phân loại đơn hàng theo trạng thái
    orders.forEach((order) => {
      if (orderHistory.hasOwnProperty(order.status)) {
        orderHistory[order.status].push(order);
      }
    });

    res.status(200).json({
      success: true,
      data: orderHistory,
    });
  } catch (error) {
    console.error("Error fetching order history:", error);
    res.status(500).json({
      success: false,
      message: "Internal server error",
    });
  }
};

export const addOrder = async (req, res) => {
  try {
    const user = req.user;
    if (!user) {
      return res.status(401).json({ message: "Người dùng không hợp lệ" });
    }

    const {
      items,
      totalAmount,
      shippingCost,
      shippingProvince,
      shippingDistrict,
      shippingWard,
      shippingDetail,
    } = req.body;

    console.log("Adding order:", {
      userId: user.id,
      items,
      totalAmount,
      shippingCost,
      shippingProvince,
      shippingDistrict,
      shippingWard,
      shippingDetail,
    });

    // Kiểm tra dữ liệu đầu vào
    if (
      !items ||
      !Array.isArray(items) ||
      items.length === 0 ||
      !totalAmount ||
      totalAmount <= 0
    ) {
      console.log("Invalid input data");
      return res.status(400).json({
        success: false,
        message:
          "Dữ liệu không hợp lệ: Thiếu items hoặc totalAmount không hợp lệ",
      });
    }

    // Kiểm tra tính hợp lệ của items
    for (const item of items) {
      if (
        !item.bookId ||
        !item.quantity ||
        item.quantity <= 0 ||
        !item.unitPrice ||
        item.unitPrice <= 0
      ) {
        return res.status(400).json({
          success: false,
          message:
            "Dữ liệu item không hợp lệ: Thiếu bookId, quantity hoặc unitPrice",
        });
      }

      // Kiểm tra tồn kho sách
      const book = await Book.findOne({ id: item.bookId });
      if (!book) {
        return res.status(404).json({
          success: false,
          message: `Không tìm thấy sách với id ${item.bookId}`,
        });
      }
      if (book.stockQuantity < item.quantity) {
        return res.status(400).json({
          success: false,
          message: `Sách ${book.name} không đủ tồn kho`,
        });
      }
    }

    // Tăng seq và gán id
    const counterResult = await mongoose.connection.db
      .collection("counters")
      .findOneAndUpdate(
        { _id: "orderId" },
        { $inc: { seq: 1 } },
        { returnDocument: "after", upsert: true }
      );

    console.log("Counter result:", counterResult);

    // Kiểm tra kết quả
    if (!counterResult || typeof counterResult.seq !== "number") {
      throw new Error("Failed to retrieve or increment counter for orderId");
    }

    const orderId = counterResult.seq;

    // Tạo đơn hàng mới
    const order = new Order({
      id: orderId,
      userId: user.id,
      orderDate: new Date(),
      totalAmount,
      shippingCost: shippingCost || 0,
      status: "Đang đóng gói", // Trạng thái mặc định
      shippingProvince: shippingProvince || null,
      shippingDistrict: shippingDistrict || null,
      shippingWard: shippingWard || null,
      shippingDetail: shippingDetail || null,
      items,
    });

    console.log("Saving order:", order);

    // Lưu đơn hàng
    await order.save();

    // Cập nhật tồn kho sách
    for (const item of items) {
      await Book.updateOne(
        { id: item.bookId },
        { $inc: { stockQuantity: -item.quantity } }
      );
    }

    // Tạo thông báo
    const notificationCounterResult = await mongoose.connection.db
      .collection("counters")
      .findOneAndUpdate(
        { _id: "notificationId" },
        { $inc: { seq: 1 } },
        { returnDocument: "after", upsert: true }
      );

    if (
      !notificationCounterResult ||
      typeof notificationCounterResult.seq !== "number"
    ) {
      throw new Error(
        "Failed to retrieve or increment counter for notificationId"
      );
    }

    const notificationId = notificationCounterResult.seq;

    const notification = new Notification({
      id: notificationId,
      userId: user.id,
      orderId: orderId,
      title: "Đơn hàng mới",
      message: `Đơn hàng #${orderId} của bạn đã được tạo thành công và đang được xử lý.`,
      isRead: false,
      createdAt: new Date(),
    });

    console.log("Saving notification:", notification);
    await notification.save();

    // Xóa các sản phẩm đã chọn khỏi giỏ hàng
    await deleteSelectedCartItems(user.id);
    console.log("Selected items deleted from cart after order creation");

    res.status(201).json({
      success: true,
      data: order,
      notification,
    });
  } catch (error) {
    console.error("Error adding order:", error);
    res.status(500).json({
      success: false,
      message: "Lỗi máy chủ: " + error.message,
    });
  }
};

export const updateOrderStatus = async (req, res) => {
  try {
    const userId = req.user.id;
    const orderId = parseInt(req.params.id);
    const { status } = req.body;

    // Kiểm tra trạng thái hợp lệ
    const validStatuses = [
      "Đang đóng gói",
      "Chờ giao hàng",
      "Đã giao",
      "Trả hàng",
      "Đã hủy",
    ];
    if (!validStatuses.includes(status)) {
      return res.status(400).json({
        success: false,
        message: "Trạng thái không hợp lệ",
      });
    }

    // Tìm và cập nhật đơn hàng
    const order = await Order.findOneAndUpdate(
      { id: orderId, userId: userId },
      { status: status, updatedAt: new Date() }, // Cập nhật updatedAt
      { new: true }
    );

    if (!order) {
      return res.status(404).json({
        success: false,
        message: "Không tìm thấy đơn hàng",
      });
    }

    res.status(200).json({
      success: true,
      data: order,
    });
  } catch (error) {
    console.error("Error updating order status:", error);
    res.status(500).json({
      success: false,
      message: "Internal server error",
    });
  }
};

export const getOrderById = async (req, res) => {
  try {
    const user = req.user;
    if (!user) {
      return res.status(401).json({ message: "Người dùng không hợp lệ" });
    }

    const order = await Order.findOne({ id: req.params.id, userId: user.id });
    if (!order) {
      return res
        .status(404)
        .json({ message: "Không tìm thấy đơn hàng hoặc bạn không có quyền" });
    }

    const items = await Promise.all(
      order.items.map(async (item) => {
        const book = await Book.findOne({ id: item.bookId });
        return {
          ...item.toJSON(),
          book: book ? book.toJSON() : null,
        };
      })
    );

    res.status(200).json({
      ...order.toJSON(),
      items,
    });
  } catch (error) {
    console.error("Error fetching order by ID:", error.message);
    res.status(500).json({ message: error.message });
  }
};

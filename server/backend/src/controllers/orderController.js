import Order from "../models/Order.js";
import Book from "../models/Book.js";
import Notification from "../models/Notification.js";
import mongoose from "mongoose";
import { deleteSelectedCartItems } from "./cartController.js";
import User from "../models/User.js";

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

export const adminUpdateOrderStatus = async (req, res) => {
  try {
    const userId = req.user.id;
    const isAdmin = req.user.role === "admin";
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
    const order = isAdmin
      ? await Order.findOneAndUpdate(
          { id: orderId },
          { status: status, updatedAt: new Date() },
          { new: true }
        )
      : await Order.findOneAndUpdate(
          { id: orderId, userId: userId },
          { status: status, updatedAt: new Date() },
          { new: true }
        );

    if (!order) {
      return res.status(404).json({
        success: false,
        message: "Không tìm thấy đơn hàng",
      });
    }

    // Gửi thông báo cho người dùng
    const notificationTitles = {
      "Đang đóng gói": "Đơn hàng đang được đóng gói",
      "Chờ giao hàng": "Đơn hàng chờ giao",
      "Đã giao": "Đơn hàng đã giao thành công",
      "Trả hàng": "Đơn hàng đã được trả lại",
      "Đã hủy": "Đơn hàng đã bị hủy",
    };
    const notificationMessages = {
      "Đang đóng gói": `Đơn hàng #${order.id} của bạn đang được đóng gói.`,
      "Chờ giao hàng": `Đơn hàng #${order.id} của bạn đang chờ giao.`,
      "Đã giao": `Đơn hàng #${order.id} của bạn đã được giao thành công.`,
      "Trả hàng": `Đơn hàng #${order.id} của bạn đã được trả lại.`,
      "Đã hủy": `Đơn hàng #${order.id} của bạn đã bị hủy.`,
    };

    // Lấy userId của chủ đơn hàng
    const notifyUserId = order.userId;

    // Tăng seq cho notificationId
    const notificationCounterResult = await mongoose.connection.db
      .collection("counters")
      .findOneAndUpdate(
        { _id: "notificationId" },
        { $inc: { seq: 1 } },
        { returnDocument: "after", upsert: true }
      );
    const notificationId = notificationCounterResult.seq;

    // Tạo thông báo
    const notification = new Notification({
      id: notificationId,
      userId: notifyUserId,
      orderId: order.id,
      title: notificationTitles[status] || "Cập nhật đơn hàng",
      message: notificationMessages[status] || `Đơn hàng #${order.id} đã cập nhật trạng thái.`,
      isRead: false,
      createdAt: new Date(),
    });
    await notification.save();

    res.status(200).json({
      success: true,
      data: order,
      notification,
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

// Sửa thông tin đơn hàng (chỉ cho phép sửa nếu đơn hàng chưa giao/hủy)
export const updateOrder = async (req, res) => {
  try {
    const userId = req.user.id;
    const isAdmin = req.user.role === 'admin';
    const orderId = parseInt(req.params.id);
    const updateData = req.body;

    // Tìm đơn hàng
    const order = isAdmin
      ? await Order.findOne({ id: orderId })
      : await Order.findOne({ id: orderId, userId: userId });

    if (!order) {
      return res.status(404).json({
        success: false,
        message: "Không tìm thấy đơn hàng",
      });
    }

    // Chỉ cho phép sửa nếu trạng thái chưa phải "Đã giao" hoặc "Đã hủy"
    if (["Đã giao", "Đã hủy"].includes(order.status)) {
      return res.status(400).json({
        success: false,
        message: "Không thể sửa đơn hàng đã giao hoặc đã hủy",
      });
    }

    // Cập nhật đơn hàng
    Object.assign(order, updateData, { updatedAt: new Date() });
    await order.save();

    res.status(200).json({
      success: true,
      data: order,
    });
  } catch (error) {
    console.error("Error updating order:", error);
    res.status(500).json({
      success: false,
      message: "Internal server error",
    });
  }
};

// xem các đơn hàng đã hủy là đơn hàng bị xóa
export const getAllOrders = async (req, res) => {
  try {
    // Lấy tất cả đơn hàng
    const orders = await Order.find();

    // Lấy tất cả user và book liên quan
    const userIds = [...new Set(orders.map(o => o.userId))];
    const bookIds = [
      ...new Set(
        orders.flatMap(o => o.items.map(i => i.bookId))
      ),
    ];

    const users = await User.find({ id: { $in: userIds } });
    const books = await Book.find({ id: { $in: bookIds } });

    // Map userId -> user
    const userMap = {};
    users.forEach(u => { userMap[u.id] = u; });

    // Map bookId -> book
    const bookMap = {};
    books.forEach(b => { bookMap[b.id] = b; });

    // Gộp dữ liệu cho từng đơn hàng
    const result = orders.map(order => ({
      id: order.id,
      orderDate: order.orderDate,
      totalAmount: order.totalAmount,
      status: order.status,
      customer: userMap[order.userId]?.fullName || 'Ẩn',
      items: order.items.map(item => ({
        bookId: item.bookId,
        quantity: item.quantity,
        unitPrice: item.unitPrice,
        bookName: bookMap[item.bookId]?.name || '',
        bookImage: bookMap[item.bookId]?.images?.[0]?.url || bookMap[item.bookId]?.images?.[0] || '',
      })),
    }));

    res.json({ success: true, data: result });
  } catch (error) {
    console.error('getAllOrders error:', error);
    res.status(500).json({ success: false, message: error.message });
  }
};

// Tìm kiếm đơn hàng theo tên sản phẩm
export const searchOrdersByBookName = async (req, res) => {
  try {
    const { q } = req.query;
    if (!q) {
      return res.status(400).json({ success: false, message: "Thiếu từ khóa tìm kiếm." });
    }

    // Tìm các bookId có tên chứa từ khóa
    const books = await Book.find({ name: { $regex: q, $options: "i" } }).select("id name images");
    const bookIds = books.map(b => b.id);

    if (bookIds.length === 0) {
      return res.json({ success: true, data: [] });
    }

    // Tìm các đơn hàng có items chứa bookId này
    const orders = await Order.find({ "items.bookId": { $in: bookIds } });

    // Lấy thông tin user liên quan
    const userIds = [...new Set(orders.map(o => o.userId))];
    const users = await User.find({ id: { $in: userIds } });
    const userMap = {};
    users.forEach(u => { userMap[u.id] = u; });

    // Map bookId -> book
    const bookMap = {};
    books.forEach(b => { bookMap[Number(b.id)] = b; });

    // Chỉ lấy các item khớp bookId
    const result = orders.map(order => {
      // Lọc chỉ các item khớp bookId
      const matchedItems = order.items
        .filter(item => bookIds.includes(item.bookId))
        .map(item => {
          const book = bookMap[Number(item.bookId)];
          return {
            ...item.toObject(),
            bookName: book?.name || '',
            bookImage:
              Array.isArray(book?.images) && book.images.length > 0
                ? book.images[0].url
                : '',
          };
        });

      return {
        id: order.id,
        orderDate: order.orderDate,
        totalAmount: order.totalAmount,
        status: order.status,
        customer: userMap[order.userId]?.fullName || 'Ẩn',
        items: matchedItems,
      };
    }).filter(order => order.items.length > 0); // Chỉ trả về đơn hàng có item khớp

    res.json({ success: true, data: result });
  } catch (error) {
    console.error("searchOrdersByBookName error:", error);
    res.status(500).json({ success: false, message: error.message });
  }
};

export const adminGetOrderById = async (req, res) => {
  try {
    const order = await Order.findOne({ id: req.params.id });
    if (!order) {
      return res.status(404).json({ message: "Không tìm thấy đơn hàng" });
    }

    // Lấy thông tin user
    const user = await User.findOne({ id: order.userId });
    // Lấy thông tin từng sách trong đơn hàng
    const items = await Promise.all(
      order.items.map(async (item) => {
        const book = await Book.findOne({ id: item.bookId });
        return {
          ...item.toJSON(),
          bookName: book ? book.name : '',
          bookImage: book?.images?.[0]?.url || book?.images?.[0] || '',
        };
      })
    );

    res.status(200).json({
      id: order.id,
      orderDate: order.orderDate,
      totalAmount: order.totalAmount,
      status: order.status,
      customer: user?.fullName || 'Ẩn',
      address: order.shippingDetail || '',
      items,
    });
  } catch (error) {
    console.error("Error fetching order by ID:", error.message);
    res.status(500).json({ message: error.message });
  }
};
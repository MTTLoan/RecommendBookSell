import Order from "../models/Order.js";
import Book from "../models/Book.js";

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

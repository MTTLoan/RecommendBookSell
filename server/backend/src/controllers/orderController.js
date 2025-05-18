import Order from "../models/Order.js";

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

    // Lấy đơn hàng và thông tin người dùng
    const orders = await Order.aggregate([
      { $match: { userId: userId } },
      {
        $lookup: {
          from: "users",
          localField: "userId",
          foreignField: "id",
          as: "user",
        },
      },
      { $unwind: "$user" },
    ]).catch((err) => {
      console.error("Aggregation error:", err);
      return []; // Trả về mảng rỗng nếu lỗi
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

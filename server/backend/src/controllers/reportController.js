import RecommendationTracking from "../models/RecommendationTracking.js";
import Order from "../models/Order.js";

// Hàm lấy dữ liệu doanh thu
export const getRevenueStats = async (req, res) => {
  try {
    const { month, year } = req.query;
    const startDate = new Date(
      year || new Date().getFullYear(),
      (month || new Date().getMonth()) - 1,
      1
    );
    const endDate = new Date(
      year || new Date().getFullYear(),
      month || new Date().getMonth(),
      0,
      23,
      59,
      59
    );

    // Lấy tổng doanh thu từ Order theo tháng và năm
    const recommendedOrders = await Order.aggregate([
      {
        $match: {
          "items.recommend": true,
          orderDate: { $gte: startDate, $lte: endDate },
          status: "Đã giao",
        },
      },
      { $group: { _id: null, totalRevenue: { $sum: "$totalAmount" } } },
    ]);
    const totalRevenue =
      recommendedOrders.length > 0 ? recommendedOrders[0].totalRevenue : 0;

    // So sánh với tháng trước
    const lastMonthStart = new Date(startDate);
    lastMonthStart.setMonth(lastMonthStart.getMonth() - 1);
    const lastMonthEnd = new Date(endDate);
    lastMonthEnd.setMonth(lastMonthEnd.getMonth() - 1);
    const lastMonthOrders = await Order.aggregate([
      {
        $match: {
          "items.recommend": true,
          orderDate: { $gte: lastMonthStart, $lte: lastMonthEnd },
          status: "Đã giao",
        },
      },
      { $group: { _id: null, totalRevenue: { $sum: "$totalAmount" } } },
    ]);
    const lastMonthRevenue =
      lastMonthOrders.length > 0 ? lastMonthOrders[0].totalRevenue : 0;

    // Tính phần trăm thay đổi
    const revenuePercentage =
      lastMonthRevenue > 0
        ? ((totalRevenue - lastMonthRevenue) / lastMonthRevenue) * 100
        : 0;

    res.status(200).json({
      value: totalRevenue.toFixed(2),
      percentage: revenuePercentage.toFixed(2),
      isIncrease: revenuePercentage >= 0,
    });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Hàm lấy dữ liệu lượt nhấn
export const getClickStats = async (req, res) => {
  try {
    const { month, year } = req.query;
    const startDate = new Date(
      year || new Date().getFullYear(),
      (month || new Date().getMonth()) - 1,
      1
    );
    const endDate = new Date(
      year || new Date().getFullYear(),
      month || new Date().getMonth(),
      0,
      23,
      59,
      59
    );

    // Lấy dữ liệu lượt nhấn theo tháng và năm
    const clicks = await RecommendationTracking.countDocuments({
      action: "click",
      timestamp: { $gte: startDate, $lte: endDate },
    });

    // So sánh với tháng trước
    const lastMonthStart = new Date(startDate);
    lastMonthStart.setMonth(lastMonthStart.getMonth() - 1);
    const lastMonthEnd = new Date(endDate);
    lastMonthEnd.setMonth(lastMonthEnd.getMonth() - 1);
    const lastMonthClicks = await RecommendationTracking.countDocuments({
      action: "click",
      timestamp: { $gte: lastMonthStart, $lte: lastMonthEnd },
    });

    // Tính phần trăm thay đổi
    const clickPercentage =
      lastMonthClicks > 0
        ? ((clicks - lastMonthClicks) / lastMonthClicks) * 100
        : 0;

    res.status(200).json({
      value: clicks,
      percentage: clickPercentage.toFixed(2),
      isIncrease: clickPercentage >= 0,
    });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Hàm lấy dữ liệu lượt thêm vào giỏ hàng
export const getAddToCartStats = async (req, res) => {
  try {
    const { month, year } = req.query;
    const startDate = new Date(
      year || new Date().getFullYear(),
      (month || new Date().getMonth()) - 1,
      1
    );
    const endDate = new Date(
      year || new Date().getFullYear(),
      month || new Date().getMonth(),
      0,
      23,
      59,
      59
    );

    // Lấy dữ liệu lượt thêm vào giỏ hàng theo tháng và năm
    const addToCart = await RecommendationTracking.countDocuments({
      action: "add_to_cart",
      timestamp: { $gte: startDate, $lte: endDate },
    });

    // So sánh với tháng trước
    const lastMonthStart = new Date(startDate);
    lastMonthStart.setMonth(lastMonthStart.getMonth() - 1);
    const lastMonthEnd = new Date(endDate);
    lastMonthEnd.setMonth(lastMonthEnd.getMonth() - 1);
    const lastMonthAddToCart = await RecommendationTracking.countDocuments({
      action: "add_to_cart",
      timestamp: { $gte: lastMonthStart, $lte: lastMonthEnd },
    });

    // Tính phần trăm thay đổi
    const addToCartPercentage =
      lastMonthAddToCart > 0
        ? ((addToCart - lastMonthAddToCart) / lastMonthAddToCart) * 100
        : 0;

    res.status(200).json({
      value: addToCart,
      percentage: addToCartPercentage.toFixed(2),
      isIncrease: addToCartPercentage >= 0,
    });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Hàm lấy dữ liệu lượt mua hàng
export const getPurchaseStats = async (req, res) => {
  try {
    const { month, year } = req.query;
    const startDate = new Date(
      year || new Date().getFullYear(),
      (month || new Date().getMonth()) - 1,
      1
    );
    const endDate = new Date(
      year || new Date().getFullYear(),
      month || new Date().getMonth(),
      0,
      23,
      59,
      59
    );

    // Lấy dữ liệu lượt mua hàng theo tháng và năm
    const purchases = await RecommendationTracking.countDocuments({
      action: "purchase",
      timestamp: { $gte: startDate, $lte: endDate },
    });

    // So sánh với tháng trước
    const lastMonthStart = new Date(startDate);
    lastMonthStart.setMonth(lastMonthStart.getMonth() - 1);
    const lastMonthEnd = new Date(endDate);
    lastMonthEnd.setMonth(lastMonthEnd.getMonth() - 1);
    const lastMonthPurchases = await RecommendationTracking.countDocuments({
      action: "purchase",
      timestamp: { $gte: lastMonthStart, $lte: lastMonthEnd },
    });

    // Tính phần trăm thay đổi
    const purchasePercentage =
      lastMonthPurchases > 0
        ? ((purchases - lastMonthPurchases) / lastMonthPurchases) * 100
        : 0;

    res.status(200).json({
      value: purchases,
      percentage: purchasePercentage.toFixed(2),
      isIncrease: purchasePercentage >= 0,
    });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Hàm lấy danh sách sản phẩm hàng đầu
export const getTopProducts = async (req, res) => {
  try {
    const { month, year } = req.query;
    const startDate = new Date(
      year || new Date().getFullYear(),
      (month || new Date().getMonth()) - 1,
      1
    );
    const endDate = new Date(
      year || new Date().getFullYear(),
      month || new Date().getMonth(),
      0,
      23,
      59,
      59
    );

    // Lấy dữ liệu từ RecommendationTracking với action là "purchase"
    const topProducts = await RecommendationTracking.aggregate([
      // Lọc theo action "purchase" và khoảng thời gian
      {
        $match: {
          action: "purchase",
          timestamp: { $gte: startDate, $lte: endDate },
        },
      },
      // Nhóm theo bookId và đếm số lượng bán
      {
        $group: {
          _id: "$bookId",
          quantity: { $sum: 1 },
        },
      },
      // Sắp xếp theo số lượng bán giảm dần
      {
        $sort: { quantity: -1 },
      },
      // Giới hạn số lượng sản phẩm (ví dụ: top 10)
      {
        $limit: 10,
      },
      // Kết hợp với collection Book để lấy tên sản phẩm
      {
        $lookup: {
          from: "books",
          localField: "_id",
          foreignField: "id",
          as: "book",
        },
      },
      // Bỏ mảng book để lấy object đơn
      {
        $unwind: "$book",
      },
      // Chỉ lấy các trường cần thiết
      {
        $project: {
          name: "$book.name",
          quantity: 1,
        },
      },
    ]);

    res.status(200).json(topProducts);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Hàm lấy dữ liệu cho biểu đồ doanh thu (tổng và từ đề xuất)
export const getRevenueChartData = async (req, res) => {
  try {
    const { month, year } = req.query;
    const startDate = new Date(
      year || new Date().getFullYear(),
      (month || new Date().getMonth()) - 1,
      1
    );
    const endDate = new Date(
      year || new Date().getFullYear(),
      month || new Date().getMonth(),
      0,
      23,
      59,
      59
    );

    // Tính số ngày trong tháng
    const daysInMonth = endDate.getDate();
    const totalRevenueData = Array(daysInMonth).fill(0);
    const recommendedRevenueData = Array(daysInMonth).fill(0);

    // Lấy tổng doanh thu từ tất cả đơn hàng
    const totalRevenue = await Order.aggregate([
      {
        $match: {
          orderDate: { $gte: startDate, $lte: endDate },
          status: "Đã giao", // Thêm điều kiện trạng thái "Đã giao"
        },
      },
      {
        $group: {
          _id: { $dayOfMonth: "$orderDate" },
          total: { $sum: "$totalAmount" },
        },
      },
    ]);

    // Lấy doanh thu từ hệ thống đề xuất
    const recommendedRevenue = await Order.aggregate([
      {
        $match: {
          "items.recommend": true,
          orderDate: { $gte: startDate, $lte: endDate },
          status: "Đã giao",
        },
      },
      {
        $group: {
          _id: { $dayOfMonth: "$orderDate" },
          total: { $sum: "$totalAmount" },
        },
      },
    ]);

    // Điền dữ liệu vào mảng
    totalRevenue.forEach((entry) => {
      totalRevenueData[entry._id - 1] = entry.total;
    });
    recommendedRevenue.forEach((entry) => {
      recommendedRevenueData[entry._id - 1] = entry.total;
    });

    // Tạo nhãn cho các ngày trong tháng
    const labels = Array.from({ length: daysInMonth }, (_, i) =>
      (i + 1).toString()
    );

    res.status(200).json({
      labels,
      totalRevenue: totalRevenueData,
      recommendedRevenue: recommendedRevenueData,
    });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Hàm lấy dữ liệu doanh thu theo danh mục
export const getCategoryRevenueChartData = async (req, res) => {
  try {
    const { month, year } = req.query;
    const startDate = new Date(
      year || new Date().getFullYear(),
      (month || new Date().getMonth()) - 1,
      1
    );
    const endDate = new Date(
      year || new Date().getFullYear(),
      month || new Date().getMonth(),
      0,
      23,
      59,
      59
    );

    // Aggregation pipeline để tính doanh thu theo danh mục
    const categoryRevenue = await Order.aggregate([
      // Lọc đơn hàng trong khoảng thời gian
      {
        $match: {
          orderDate: { $gte: startDate, $lte: endDate },
        },
      },
      // Tách mảng items thành từng document riêng
      { $unwind: "$items" },
      // Join với collection Book để lấy category
      {
        $lookup: {
          from: "books",
          localField: "items.bookId",
          foreignField: "id",
          as: "book",
        },
      },
      // Đảm bảo book tồn tại
      {
        $unwind: {
          path: "$book",
          preserveNullAndEmptyArrays: true, // Giữ lại các document không có book tương ứng
        },
      },
      // Nhóm theo danh mục và tính tổng doanh thu
      {
        $group: {
          _id: "$book.categoryId", // Nhóm theo categoryId
          totalRevenue: {
            $sum: {
              $multiply: ["$items.quantity", "$items.unitPrice"], // Sử dụng unitPrice thay vì price
            },
          },
        },
      },
      // Join với collection Category để lấy tên danh mục
      {
        $lookup: {
          from: "categories",
          localField: "_id",
          foreignField: "id",
          as: "category",
        },
      },
      // Đảm bảo category tồn tại
      {
        $unwind: {
          path: "$category",
          preserveNullAndEmptyArrays: true,
        },
      },
      // Định dạng dữ liệu trả về
      {
        $project: {
          category: {
            $ifNull: ["$category.name", "Không xác định"], // Nếu không có category, trả về "Không xác định"
          },
          totalRevenue: 1,
          _id: 0,
        },
      },
    ]);

    // Chuẩn bị dữ liệu cho biểu đồ
    const labels = categoryRevenue.map((item) => item.category);
    const data = categoryRevenue.map((item) => item.totalRevenue);

    // Nếu không có dữ liệu, trả về giá trị mặc định
    if (labels.length === 0) {
      return res.status(200).json({
        labels: ["Không có dữ liệu"],
        data: [0],
      });
    }

    res.status(200).json({
      labels,
      data,
    });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

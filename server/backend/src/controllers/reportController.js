import RecommendationTracking from "../models/RecommendationTracking.js";
import Order from "../models/Order.js";

// Hàm lấy dữ liệu doanh thu
export const getRevenueStats = async (req, res) => {
  try {
    const { month, year } = req.query;
    const selectedYear = year || new Date().getFullYear();
    let startDate, endDate;

    if (month === undefined || month === "null" || month === null) {
      // Báo cáo theo năm
      startDate = new Date(selectedYear, 0, 1); // Ngày 1 tháng 1
      endDate = new Date(selectedYear, 11, 31, 23, 59, 59); // Ngày 31 tháng 12
    } else {
      // Báo cáo theo tháng
      const selectedMonth = month || new Date().getMonth();
      startDate = new Date(selectedYear, selectedMonth - 1, 1);
      endDate = new Date(selectedYear, selectedMonth, 0, 23, 59, 59);
    }

    // Lấy tổng doanh thu từ Order theo khoảng thời gian
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

    // So sánh với khoảng thời gian trước (năm trước hoặc tháng trước)
    let lastPeriodStart, lastPeriodEnd;
    if (month === undefined || month === "null" || month === null) {
      lastPeriodStart = new Date(selectedYear - 1, 0, 1);
      lastPeriodEnd = new Date(selectedYear - 1, 11, 31, 23, 59, 59);
    } else {
      lastPeriodStart = new Date(startDate);
      lastPeriodStart.setMonth(lastPeriodStart.getMonth() - 1);
      lastPeriodEnd = new Date(endDate);
      lastPeriodEnd.setMonth(lastPeriodEnd.getMonth() - 1);
    }

    const lastPeriodOrders = await Order.aggregate([
      {
        $match: {
          "items.recommend": true,
          orderDate: { $gte: lastPeriodStart, $lte: lastPeriodEnd },
          status: "Đã giao",
        },
      },
      { $group: { _id: null, totalRevenue: { $sum: "$totalAmount" } } },
    ]);
    const lastPeriodRevenue =
      lastPeriodOrders.length > 0 ? lastPeriodOrders[0].totalRevenue : 0;

    // Tính phần trăm thay đổi
    const revenuePercentage =
      lastPeriodRevenue > 0
        ? ((totalRevenue - lastPeriodRevenue) / lastPeriodRevenue) * 100
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
    const selectedYear = year || new Date().getFullYear();
    let startDate, endDate;

    if (month === undefined || month === "null" || month === null) {
      startDate = new Date(selectedYear, 0, 1);
      endDate = new Date(selectedYear, 11, 31, 23, 59, 59);
    } else {
      const selectedMonth = month || new Date().getMonth();
      startDate = new Date(selectedYear, selectedMonth - 1, 1);
      endDate = new Date(selectedYear, selectedMonth, 0, 23, 59, 59);
    }

    const clicks = await RecommendationTracking.countDocuments({
      action: "click",
      timestamp: { $gte: startDate, $lte: endDate },
    });

    let lastPeriodStart, lastPeriodEnd;
    if (month === undefined || month === "null" || month === null) {
      lastPeriodStart = new Date(selectedYear - 1, 0, 1);
      lastPeriodEnd = new Date(selectedYear - 1, 11, 31, 23, 59, 59);
    } else {
      lastPeriodStart = new Date(startDate);
      lastPeriodStart.setMonth(lastPeriodStart.getMonth() - 1);
      lastPeriodEnd = new Date(endDate);
      lastPeriodEnd.setMonth(lastPeriodEnd.getMonth() - 1);
    }

    const lastPeriodClicks = await RecommendationTracking.countDocuments({
      action: "click",
      timestamp: { $gte: lastPeriodStart, $lte: lastPeriodEnd },
    });

    const clickPercentage =
      lastPeriodClicks > 0
        ? ((clicks - lastPeriodClicks) / lastPeriodClicks) * 100
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
    const selectedYear = year || new Date().getFullYear();
    let startDate, endDate;

    if (month === undefined || month === "null" || month === null) {
      startDate = new Date(selectedYear, 0, 1);
      endDate = new Date(selectedYear, 11, 31, 23, 59, 59);
    } else {
      const selectedMonth = month || new Date().getMonth();
      startDate = new Date(selectedYear, selectedMonth - 1, 1);
      endDate = new Date(selectedYear, selectedMonth, 0, 23, 59, 59);
    }

    const addToCart = await RecommendationTracking.countDocuments({
      action: "add_to_cart",
      timestamp: { $gte: startDate, $lte: endDate },
    });

    let lastPeriodStart, lastPeriodEnd;
    if (month === undefined || month === "null" || month === null) {
      lastPeriodStart = new Date(selectedYear - 1, 0, 1);
      lastPeriodEnd = new Date(selectedYear - 1, 11, 31, 23, 59, 59);
    } else {
      lastPeriodStart = new Date(startDate);
      lastPeriodStart.setMonth(lastPeriodStart.getMonth() - 1);
      lastPeriodEnd = new Date(endDate);
      lastPeriodEnd.setMonth(lastPeriodEnd.getMonth() - 1);
    }

    const lastPeriodAddToCart = await RecommendationTracking.countDocuments({
      action: "add_to_cart",
      timestamp: { $gte: lastPeriodStart, $lte: lastPeriodEnd },
    });

    const addToCartPercentage =
      lastPeriodAddToCart > 0
        ? ((addToCart - lastPeriodAddToCart) / lastPeriodAddToCart) * 100
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
    const selectedYear = year || new Date().getFullYear();
    let startDate, endDate;

    if (month === undefined || month === "null" || month === null) {
      startDate = new Date(selectedYear, 0, 1);
      endDate = new Date(selectedYear, 11, 31, 23, 59, 59);
    } else {
      const selectedMonth = month || new Date().getMonth();
      startDate = new Date(selectedYear, selectedMonth - 1, 1);
      endDate = new Date(selectedYear, selectedMonth, 0, 23, 59, 59);
    }

    const purchases = await RecommendationTracking.countDocuments({
      action: "purchase",
      timestamp: { $gte: startDate, $lte: endDate },
    });

    let lastPeriodStart, lastPeriodEnd;
    if (month === undefined || month === "null" || month === null) {
      lastPeriodStart = new Date(selectedYear - 1, 0, 1);
      lastPeriodEnd = new Date(selectedYear - 1, 11, 31, 23, 59, 59);
    } else {
      lastPeriodStart = new Date(startDate);
      lastPeriodStart.setMonth(lastPeriodStart.getMonth() - 1);
      lastPeriodEnd = new Date(endDate);
      lastPeriodEnd.setMonth(lastPeriodEnd.getMonth() - 1);
    }

    const lastPeriodPurchases = await RecommendationTracking.countDocuments({
      action: "purchase",
      timestamp: { $gte: lastPeriodStart, $lte: lastPeriodEnd },
    });

    const purchasePercentage =
      lastPeriodPurchases > 0
        ? ((purchases - lastPeriodPurchases) / lastPeriodPurchases) * 100
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
    const selectedYear = year || new Date().getFullYear();
    let startDate, endDate;

    if (month === undefined || month === "null" || month === null) {
      startDate = new Date(selectedYear, 0, 1);
      endDate = new Date(selectedYear, 11, 31, 23, 59, 59);
    } else {
      const selectedMonth = month || new Date().getMonth();
      startDate = new Date(selectedYear, selectedMonth - 1, 1);
      endDate = new Date(selectedYear, selectedMonth, 0, 23, 59, 59);
    }

    const topProducts = await RecommendationTracking.aggregate([
      {
        $match: {
          action: "purchase",
          timestamp: { $gte: startDate, $lte: endDate },
        },
      },
      {
        $group: {
          _id: "$bookId",
          quantity: { $sum: 1 },
        },
      },
      {
        $sort: { quantity: -1 },
      },
      {
        $limit: 10,
      },
      {
        $lookup: {
          from: "books",
          localField: "_id",
          foreignField: "id",
          as: "book",
        },
      },
      {
        $unwind: "$book",
      },
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
    const selectedYear = year || new Date().getFullYear();
    let startDate, endDate, labels, totalRevenueData, recommendedRevenueData;

    if (month === undefined || month === "null" || month === null) {
      // Báo cáo theo năm (hiển thị theo tháng)
      startDate = new Date(selectedYear, 0, 1);
      endDate = new Date(selectedYear, 11, 31, 23, 59, 59);
      labels = Array.from({ length: 12 }, (_, i) => (i + 1).toString()); // Tháng 1-12
      totalRevenueData = Array(12).fill(0);
      recommendedRevenueData = Array(12).fill(0);

      // Lấy tổng doanh thu từ tất cả đơn hàng
      const totalRevenue = await Order.aggregate([
        {
          $match: {
            orderDate: { $gte: startDate, $lte: endDate },
            status: "Đã giao",
          },
        },
        {
          $group: {
            _id: { $month: "$orderDate" },
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
            _id: { $month: "$orderDate" },
            total: { $sum: "$totalAmount" },
          },
        },
      ]);

      totalRevenue.forEach((entry) => {
        totalRevenueData[entry._id - 1] = entry.total;
      });
      recommendedRevenue.forEach((entry) => {
        recommendedRevenueData[entry._id - 1] = entry.total;
      });
    } else {
      // Báo cáo theo tháng (hiển thị theo ngày)
      const selectedMonth = month || new Date().getMonth();
      startDate = new Date(selectedYear, selectedMonth - 1, 1);
      endDate = new Date(selectedYear, selectedMonth, 0, 23, 59, 59);
      const daysInMonth = endDate.getDate();
      labels = Array.from({ length: daysInMonth }, (_, i) =>
        (i + 1).toString()
      );
      totalRevenueData = Array(daysInMonth).fill(0);
      recommendedRevenueData = Array(daysInMonth).fill(0);

      const totalRevenue = await Order.aggregate([
        {
          $match: {
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

      totalRevenue.forEach((entry) => {
        totalRevenueData[entry._id - 1] = entry.total;
      });
      recommendedRevenue.forEach((entry) => {
        recommendedRevenueData[entry._id - 1] = entry.total;
      });
    }

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
    const selectedYear = year || new Date().getFullYear();
    let startDate, endDate;

    if (month === undefined || month === "null" || month === null) {
      startDate = new Date(selectedYear, 0, 1);
      endDate = new Date(selectedYear, 11, 31, 23, 59, 59);
    } else {
      const selectedMonth = month || new Date().getMonth();
      startDate = new Date(selectedYear, selectedMonth - 1, 1);
      endDate = new Date(selectedYear, selectedMonth, 0, 23, 59, 59);
    }

    const categoryRevenue = await Order.aggregate([
      {
        $match: {
          orderDate: { $gte: startDate, $lte: endDate },
        },
      },
      { $unwind: "$items" },
      {
        $lookup: {
          from: "books",
          localField: "items.bookId",
          foreignField: "id",
          as: "book",
        },
      },
      {
        $unwind: {
          path: "$book",
          preserveNullAndEmptyArrays: true,
        },
      },
      {
        $group: {
          _id: "$book.categoryId",
          totalRevenue: {
            $sum: {
              $multiply: ["$items.quantity", "$items.unitPrice"],
            },
          },
        },
      },
      {
        $lookup: {
          from: "categories",
          localField: "_id",
          foreignField: "id",
          as: "category",
        },
      },
      {
        $unwind: {
          path: "$category",
          preserveNullAndEmptyArrays: true,
        },
      },
      {
        $project: {
          category: {
            $ifNull: ["$category.name", "Không xác định"],
          },
          totalRevenue: 1,
          _id: 0,
        },
      },
    ]);

    const labels = categoryRevenue.map((item) => item.category);
    const data = categoryRevenue.map((item) => item.totalRevenue);

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

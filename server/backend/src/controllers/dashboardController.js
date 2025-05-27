import Order from "../models/Order.js";
import Book from "../models/Book.js";
import Category from "../models/Category.js";

export const getDashboardStats = async (req, res) => {
  try {
    const filterCategory = req.query.category || null;
    const filterMonth = req.query.month ? parseInt(req.query.month) : null;
    const filterYear = req.query.year ? parseInt(req.query.year) : null;

    console.log("Backend received:", { filterCategory, filterMonth, filterYear });

    const yearListAgg = await Order.aggregate([
      { $group: { _id: { $year: "$orderDate" } } },
      { $sort: { _id: -1 } },
    ]);
    const availableYears = yearListAgg.map((y) => y._id);

    const now = new Date();
    let start, end, prevStart, prevEnd;

    // Offset cho múi giờ +07:00
    const offsetMs = 7 * 60 * 60 * 1000;

    if (filterMonth === null && filterYear === null) {
      start = new Date(0);
      end = new Date(now.getTime() + offsetMs);
      prevStart = new Date(0);
      prevEnd = new Date(now.getFullYear(), now.getMonth(), 0, 23, 59, 59, 999);
    } else if (filterMonth === null && filterYear !== null) {
      start = new Date(filterYear, 0, 1, 0, 0, 0);
      end = new Date(filterYear, 11, 31, 23, 59, 59, 999);
      prevStart = new Date(filterYear - 1, 0, 1, 0, 0, 0);
      prevEnd = new Date(filterYear - 1, 11, 31, 23, 59, 59, 999);
    } else {
      const selectedMonth = filterMonth ?? now.getMonth();
      const selectedYear = filterYear ?? now.getFullYear();
      start = new Date(selectedYear, selectedMonth, 1, 0, 0, 0);
      end = new Date(selectedYear, selectedMonth + 1, 0, 23, 59, 59, 999);
      prevStart = new Date(selectedYear, selectedMonth - 1, 1, 0, 0, 0);
      prevEnd = new Date(selectedYear, selectedMonth, 0, 23, 59, 59, 999);
    }

    // Điều chỉnh về múi giờ +07:00
    start = new Date(start.getTime() + offsetMs);
    end = new Date(end.getTime() + offsetMs);
    prevStart = new Date(prevStart.getTime() + offsetMs);
    prevEnd = new Date(prevEnd.getTime() + offsetMs);

    // Log ngày giờ theo UTC để kiểm tra
    console.log("Date range (UTC):", {
      start: start.toISOString(),
      end: end.toISOString(),
      prevStart: prevStart.toISOString(),
      prevEnd: prevEnd.toISOString(),
    });

    // Lấy tất cả sách và danh mục để ánh xạ
    const allBooks = await Book.find({}).lean();
    const bookMap = {};
    allBooks.forEach((b) => {
      bookMap[b.id] = {
        name: b.name,
        categoryId: b.categoryId,
        price: b.price,
      };
    });

    const allCategories = await Category.find({}).lean();
    const categoryMap = {};
    allCategories.forEach((cat) => {
      categoryMap[cat.id] = cat.name;
    });

    // Lấy đơn hàng trong khoảng thời gian hiện tại và trước đó
    const ordersThisPeriod = await Order.find({
      status: "Đã giao",
      orderDate: { $gte: start, $lte: end },
    }).lean();
    const ordersPrevPeriod = await Order.find({
      status: "Đã giao",
      orderDate: { $gte: prevStart, $lte: prevEnd },
    }).lean();

    // Tính revenue, customers, orders với bộ lọc category
    let revenue = 0;
    let revenuePrev = 0;
    const customerIdsThisPeriod = new Set();
    const customerIdsPrevPeriod = new Set();

    for (const order of ordersThisPeriod) {
      let orderRevenue = 0;
      for (const item of order.items) {
        const book = bookMap[item.bookId];
        if (filterCategory && String(book?.categoryId) !== String(filterCategory)) {
          continue; // Bỏ qua nếu không thuộc danh mục được lọc
        }
        orderRevenue += (item.quantity || 0) * (item.unitPrice || book?.price || 0);
        customerIdsThisPeriod.add(order.userId);
      }
      revenue += orderRevenue;
    }

    for (const order of ordersPrevPeriod) {
      let orderRevenue = 0;
      for (const item of order.items) {
        const book = bookMap[item.bookId];
        if (filterCategory && String(book?.categoryId) !== String(filterCategory)) {
          continue; // Bỏ qua nếu không thuộc danh mục được lọc
        }
        orderRevenue += (item.quantity || 0) * (item.unitPrice || book?.price || 0);
        customerIdsPrevPeriod.add(order.userId);
      }
      revenuePrev += orderRevenue;
    }

    const customers = customerIdsThisPeriod.size;
    const customersPrev = customerIdsPrevPeriod.size;
    const revenueChange =
      revenuePrev === 0
        ? revenue > 0
          ? 100
          : 0
        : ((revenue - revenuePrev) / revenuePrev) * 100;
    const customersChange =
      customersPrev === 0
        ? customers > 0
          ? 100
          : 0
        : ((customers - customersPrev) / customersPrev) * 100;

    const orders = ordersThisPeriod.filter((order) => {
      return order.items.some((item) => {
        const book = bookMap[item.bookId];
        return !filterCategory || String(book?.categoryId) === String(filterCategory);
      });
    }).length;
    const ordersPrev = ordersPrevPeriod.filter((order) => {
      return order.items.some((item) => {
        const book = bookMap[item.bookId];
        return !filterCategory || String(book?.categoryId) === String(filterCategory);
      });
    }).length;
    const ordersChange =
      ordersPrev === 0
        ? orders > 0
          ? 100
          : 0
        : ((orders - ordersPrev) / ordersPrev) * 100;

    // Tính tổng số sản phẩm bán được
    let products = 0;
    let productsPrev = 0;

    // Tính tổng quantity từ các đơn hàng trong khoảng thời gian hiện tại
    ordersThisPeriod.forEach((order) => {
      order.items.forEach((item) => {
        const book = bookMap[item.bookId];
        if (filterCategory && String(book?.categoryId) !== String(filterCategory)) {
          return;
        }
        products += item.quantity || 0;
      });
    });

    // Tính tổng quantity từ các đơn hàng trong khoảng thời gian trước đó
    ordersPrevPeriod.forEach((order) => {
      order.items.forEach((item) => {
        const book = bookMap[item.bookId];
        if (filterCategory && String(book?.categoryId) !== String(filterCategory)) {
          return;
        }
        productsPrev += item.quantity || 0;
      });
    });

    const productsChange =
      productsPrev === 0
        ? products > 0
          ? 100
          : 0
        : ((products - productsPrev) / productsPrev) * 100;

    // Tính topProducts
    const productSales = {};
    ordersThisPeriod.forEach((order) => {
      order.items.forEach((item) => {
        const bookId = item.bookId;
        const book = bookMap[bookId] || {};
        if (filterCategory && String(book.categoryId) !== String(filterCategory)) {
          return;
        }
        if (!productSales[bookId]) {
          productSales[bookId] = {
            quantity: 0,
            name: item.bookName || book.name || "Không xác định",
            price: item.unitPrice || book.price || 0,
            bookId,
            categoryId: book.categoryId,
          };
        }
        productSales[bookId].quantity += item.quantity || 0;
      });
    });

    const topProducts = Object.values(productSales)
      .map((p) => ({
        name: p.name,
        price: p.price,
        quantity: p.quantity,
        category: categoryMap[p.categoryId] || "Không xác định",
        categoryId: p.categoryId,
      }))
      .sort((a, b) => b.quantity - a.quantity)
      .slice(0, 30);

    const allCategoryOptions = allCategories.map((cat) => ({
      value: cat.id,
      text: cat.name,
    }));

    res.json({
      revenue,
      revenueChange: isNaN(revenueChange) ? 0 : Math.round(revenueChange),
      customers,
      customersChange: isNaN(customersChange) ? 0 : Math.round(customersChange),
      orders,
      ordersChange: isNaN(ordersChange) ? 0 : Math.round(ordersChange),
      products,
      productsChange: isNaN(productsChange) ? 0 : Math.round(productsChange),
      topProducts,
      allCategoryOptions,
      availableYears,
    });
  } catch (err) {
    console.error("Lỗi server:", err);
    res.status(500).json({ message: "Lỗi server" });
  }
};

// API mới để lấy dữ liệu doanh thu cho biểu đồ
export const getRevenueData = async (req, res) => {
  try {
    const filterCategory = req.query.category || null;
    const filterMonth = req.query.month ? parseInt(req.query.month) : null;
    const filterYear = req.query.year ? parseInt(req.query.year) : null;

    console.log("Backend received for revenue chart:", {
      filterCategory,
      filterMonth,
      filterYear,
    });

    const now = new Date();
    let start, end;

    // Offset cho múi giờ +07:00
    const offsetMs = 7 * 60 * 60 * 1000;

    // Xác định khoảng thời gian dựa trên bộ lọc
    if (filterMonth === null && filterYear === null) {
      start = new Date(0); // Từ đầu dữ liệu
      end = new Date(now.getTime() + offsetMs); // Đến hiện tại
    } else if (filterMonth === null && filterYear !== null) {
      start = new Date(filterYear, 0, 1, 0, 0, 0); // Đầu năm
      end = new Date(filterYear, 11, 31, 23, 59, 59, 999); // Cuối năm
    } else {
      const selectedMonth = filterMonth ?? now.getMonth();
      const selectedYear = filterYear ?? now.getFullYear();
      start = new Date(selectedYear, selectedMonth, 1, 0, 0, 0); // Đầu tháng
      end = new Date(selectedYear, selectedMonth + 1, 0, 23, 59, 59, 999); // Cuối tháng
    }

    // Điều chỉnh múi giờ +07:00
    start = new Date(start.getTime() + offsetMs);
    end = new Date(end.getTime() + offsetMs);

    // Chuyển về UTC cho query
    const queryStart = new Date(start.getTime() - offsetMs);
    const queryEnd = new Date(end.getTime() - offsetMs);

    // Lấy tất cả sách để ánh xạ
    const allBooks = await Book.find({}).lean();
    const bookMap = {};
    allBooks.forEach((b) => {
      bookMap[b.id] = {
        name: b.name,
        categoryId: b.categoryId,
        price: b.price,
      };
    });

    // Truy vấn dữ liệu đơn hàng
    let revenueData = [];
    if (filterMonth !== null) {
      // Nếu có chọn tháng, nhóm theo ngày
      const orders = await Order.find({
        status: "Đã giao",
        orderDate: { $gte: queryStart, $lte: queryEnd },
      }).lean();

      // Tạo danh sách tất cả các ngày trong tháng
      const daysInMonth = new Date(filterYear, filterMonth + 1, 0).getDate();
      const revenueByDay = Array(daysInMonth).fill(0);

      for (const order of orders) {
        const orderDate = new Date(order.orderDate);
        const day = orderDate.getDate() - 1; // Ngày trong tháng (0-30)
        let totalAmount = 0;

        // Tính tổng doanh thu, áp dụng bộ lọc danh mục nếu có
        for (const item of order.items) {
          const book = bookMap[item.bookId];
          if (filterCategory && String(book?.categoryId) !== String(filterCategory)) {
            continue; // Bỏ qua nếu không thuộc danh mục được lọc
          }
          totalAmount += (item.quantity || 0) * (item.unitPrice || book?.price || 0);
        }

        revenueByDay[day] += totalAmount;
      }

      // Tạo dữ liệu cho biểu đồ (theo ngày)
      revenueData = revenueByDay.map((revenue, index) => ({
        date: `Ngày ${index + 1}`,
        revenue,
      }));
    } else {
      // Nếu không chọn tháng, nhóm theo tháng
      const orders = await Order.find({
        status: "Đã giao",
        orderDate: { $gte: queryStart, $lte: queryEnd },
      }).lean();

      // Tạo danh sách tất cả các tháng trong năm
      const revenueByMonth = Array(12).fill(0);

      for (const order of orders) {
        const orderDate = new Date(order.orderDate);
        const month = orderDate.getMonth();
        let totalAmount = 0;

        for (const item of order.items) {
          const book = bookMap[item.bookId];
          if (filterCategory && String(book?.categoryId) !== String(filterCategory)) {
            continue; // Bỏ qua nếu không thuộc danh mục được lọc
          }
          totalAmount += (item.quantity || 0) * (item.unitPrice || book?.price || 0);
        }

        revenueByMonth[month] += totalAmount;
      }

      // Tạo dữ liệu cho biểu đồ (theo tháng)
      revenueData = revenueByMonth.map((revenue, index) => ({
        date: `Tháng ${index + 1}`,
        revenue,
      }));
    }

    res.json(revenueData);
  } catch (err) {
    console.error("Lỗi server khi lấy dữ liệu doanh thu:", err);
    res.status(500).json({ message: "Lỗi server khi lấy dữ liệu doanh thu" });
  }
};
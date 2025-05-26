import Order from "../models/Order.js";
import User from "../models/User.js";
import Book from "../models/Book.js";
import Category from "../models/Category.js";

function getMonthRange(monthOffset = 0) {
  const now = new Date();
  const year = now.getFullYear();
  const month = now.getMonth() + monthOffset;
  const start = new Date(year, month, 1, 0, 0, 0, 0);
  const end = new Date(year, month + 1, 0, 23, 59, 59, 999);
  return { start, end };
}

export const getDashboardStats = async (req, res) => {
  try {
    const filterCategory = req.query.category || null;

    // --- Thời gian ---
    const { start: curStart, end: curEnd } = getMonthRange(0);
    const { start: prevStart, end: prevEnd } = getMonthRange(-1);

    // --- Đơn hàng ---
    const ordersThisMonth = await Order.find({
      status: "Đã giao",
      orderDate: { $gte: curStart, $lte: curEnd },
    });
    const ordersLastMonth = await Order.find({
      status: "Đã giao",
      orderDate: { $gte: prevStart, $lte: prevEnd },
    });

    // --- Doanh thu ---
    const revenue = ordersThisMonth.reduce(
      (sum, o) => sum + (o.totalAmount || 0),
      0
    );
    const revenuePrev = ordersLastMonth.reduce(
      (sum, o) => sum + (o.totalAmount || 0),
      0
    );
    const revenueChange =
      revenuePrev === 0
        ? revenue > 0
          ? 100
          : 0
        : ((revenue - revenuePrev) / revenuePrev) * 100;

    // --- Khách hàng ---
    const customers = await User.countDocuments({
      role: "user",
      createdAt: { $gte: curStart, $lte: curEnd },
    });
    const customersPrev = await User.countDocuments({
      role: "user",
      createdAt: { $gte: prevStart, $lte: prevEnd },
    });
    const customersChange =
      customersPrev === 0
        ? customers > 0
          ? 100
          : 0
        : ((customers - customersPrev) / customersPrev) * 100;

    // --- Đơn hàng ---
    const orders = await Order.countDocuments({
      orderDate: { $gte: curStart, $lte: curEnd },
    });
    const ordersPrev = await Order.countDocuments({
      orderDate: { $gte: prevStart, $lte: prevEnd },
    });
    const ordersChange =
      ordersPrev === 0
        ? orders > 0
          ? 100
          : 0
        : ((orders - ordersPrev) / ordersPrev) * 100;

    // --- Sản phẩm ---
    const products = await Book.countDocuments({
      createdAt: { $gte: curStart, $lte: curEnd },
    });
    const productsPrev = await Book.countDocuments({
      createdAt: { $gte: prevStart, $lte: prevEnd },
    });
    const productsChange =
      productsPrev === 0
        ? products > 0
          ? 100
          : 0
        : ((products - productsPrev) / productsPrev) * 100;

    // --- Lấy tất cả Book và Category ---
    const allBooks = await Book.find({});
    const bookMap = {};
    allBooks.forEach((b) => {
      bookMap[Number(b.id)] = {
        name: b.name,
        categoryId: b.categoryId,
        price: b.price,
      };
    });

    const allCategories = await Category.find({});
    const categoryMap = {};
    allCategories.forEach((cat) => {
      categoryMap[cat.id] = cat.name;
    });

    // --- Top sản phẩm bán chạy tháng này ---
    const productSales = {};
    ordersThisMonth.forEach((order) => {
      order.items.forEach((item) => {
        const bookId = Number(item.bookId);
        const book = bookMap[item.bookId] || {};
        // Nếu filterCategory, chỉ cộng sản phẩm thuộc category đó
        if (
          filterCategory &&
          String(book.categoryId) !== String(filterCategory)
        )
          return;
        if (!productSales[bookId]) {
          productSales[bookId] = {
            quantity: 0,
            name: item.bookName || book.name || "Không xác định",
            price: item.unitPrice || book.price,
            bookId: bookId,
            categoryId: book.categoryId,
          };
        }
        productSales[bookId].quantity += item.quantity;
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
      .slice(0, 10);

    // Trả về tất cả danh mục để FE render filter
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
    });
  } catch (err) {
    res.status(500).json({ message: "Lỗi server" });
  }
};

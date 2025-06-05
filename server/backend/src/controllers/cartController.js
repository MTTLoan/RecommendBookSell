import mongoose from "mongoose";
import RecommendationTracking from "../models/RecommendationTracking.js";
import Cart from "../models/Cart.js";
import Book from "../models/Book.js";
import Counter from "../models/Counter.js";

const getNextSequence = async (name) => {
  const counter = await Counter.findOneAndUpdate(
    { _id: name },
    { $inc: { seq: 1 } },
    { new: true, upsert: true }
  );
  return counter.seq;
};

// Hàm thêm vào giỏ hàng
export const addToCart = async (req, res) => {
  try {
    const userId = req.user.id;
    const { items } = req.body;

    if (!items || !Array.isArray(items) || items.length === 0) {
      return res.status(400).json({ message: "Items are required" });
    }

    let cart = await Cart.findOne({ userId });

    if (!cart) {
      cart = new Cart({
        id: await getNextSequence("cartId"),
        userId,
        items: [],
        createdAt: new Date(),
        updatedAt: new Date(),
      });
    }

    for (const item of items) {
      const { bookId, quantity, recommend = false } = item;

      if (!bookId || !quantity || quantity < 1) {
        return res.status(400).json({ message: "Invalid bookId or quantity" });
      }

      const book = await Book.findOne({ id: bookId });
      if (!book) {
        return res
          .status(404)
          .json({ message: `Book with id ${bookId} not found` });
      }

      const existingItem = cart.items.find((i) => i.bookId === bookId);
      if (existingItem) {
        existingItem.quantity += quantity;
        if (recommend) existingItem.recommend = true;
      } else {
        cart.items.push({ bookId, quantity, recommend, selected: false });
      }

      // Ghi nhận hành động add_to_cart nếu từ đề xuất
      if (recommend) {
        const tracking = new mongoose.model("RecommendationTracking")({
          id: await getNextSequence("recommendationTrackingId"),
          userId,
          bookId,
          action: "add_to_cart",
          timestamp: new Date(),
          cartId: cart.id,
        });
        await tracking.save();
      }
    }

    cart.updatedAt = new Date();
    await cart.save();

    return res.status(200).json(cart);
  } catch (error) {
    console.error("Error adding to cart:", error);
    return res.status(500).json({ message: "Server error" });
  }
};

// Hàm lấy giỏ hàng
export const getCart = async (req, res) => {
  try {
    const user = req.user;
    if (!user) {
      return res.status(401).json({ message: "Người dùng không hợp lệ" });
    }

    const cart = await Cart.findOne({ userId: user.id }).lean();

    if (!cart) {
      return res.status(404).json({ message: "Giỏ hàng không tồn tại" });
    }

    // Lấy tất cả bookId từ items
    const bookIds = cart.items.map((item) => item.bookId);

    // Truy vấn collection books để lấy thông tin sách dựa trên id
    const books = await Book.find({ id: { $in: bookIds } })
      .select("id name price images")
      .lean();

    // Tạo map để tra cứu nhanh thông tin sách
    const bookMap = books.reduce((map, book) => {
      map[book.id] = book;
      return map;
    }, {});

    // Cập nhật items với thông tin sách
    cart.items = cart.items.map((item) => ({
      bookId: item.bookId,
      quantity: item.quantity,
      selected: item.selected || false,
      recommend: item.recommend || false,
      book: bookMap[item.bookId] || null,
    }));

    // Loại bỏ các trường không mong muốn trong response
    const responseData = {
      id: cart.id,
      userId: cart.userId,
      items: cart.items,
      createdAt: cart.createdAt,
      updatedAt: cart.updatedAt,
    };

    res.status(200).json(responseData);
  } catch (error) {
    console.error("Error fetching cart:", error.message);
    res.status(500).json({ message: "Lỗi máy chủ: " + error.message });
  }
};

// Hàm cập nhật giỏ hàng
export const updateCart = async (req, res) => {
  try {
    const user = req.user;
    if (!user) {
      return res.status(401).json({ message: "Người dùng không hợp lệ" });
    }

    const { items } = req.body;
    if (!items || !Array.isArray(items)) {
      return res
        .status(400)
        .json({ message: "Danh sách sản phẩm không hợp lệ" });
    }

    let cart = await Cart.findOne({ userId: user.id });
    if (!cart) {
      return res.status(404).json({ message: "Giỏ hàng không tồn tại" });
    }

    cart.items = items.map((item) => ({
      bookId: item.bookId,
      quantity: item.quantity,
      selected: item.selected || false,
    }));
    cart.updatedAt = new Date();

    const updatedCart = await cart.save();
    res.status(200).json(updatedCart);
  } catch (error) {
    console.error("Error updating cart:", error.message);
    res.status(500).json({ message: "Lỗi máy chủ: " + error.message });
  }
};

// Hàm xóa sản phẩm khỏi giỏ hàng
export const deleteCartItem = async (req, res) => {
  try {
    const user = req.user;
    if (!user) {
      return res.status(401).json({ message: "Người dùng không hợp lệ" });
    }

    const bookId = parseInt(req.params.bookId);
    let cart = await Cart.findOne({ userId: user.id });
    if (!cart) {
      return res.status(404).json({ message: "Giỏ hàng không tồn tại" });
    }

    const itemIndex = cart.items.findIndex((item) => item.bookId === bookId);
    if (itemIndex === -1) {
      return res
        .status(404)
        .json({ message: "Sản phẩm không có trong giỏ hàng" });
    }

    cart.items.splice(itemIndex, 1);
    cart.updatedAt = new Date();

    const updatedCart = await cart.save();
    res.status(200).json(updatedCart);
  } catch (error) {
    console.error("Error deleting cart item:", error.message);
    res.status(500).json({ message: "Lỗi máy chủ: " + error.message });
  }
};

// Hàm xóa các sản phẩm đã chọn khỏi giỏ hàng
export const deleteSelectedCartItems = async (userId) => {
  try {
    const cart = await Cart.findOne({ userId });
    if (!cart) {
      throw new Error("Giỏ hàng không tồn tại");
    }

    // Lọc bỏ các sản phẩm có selected: true
    cart.items = cart.items.filter((item) => !item.selected);
    cart.updatedAt = new Date();

    const updatedCart = await cart.save();
    return updatedCart;
  } catch (error) {
    console.error("Error deleting selected cart items:", error.message);
    throw error;
  }
};

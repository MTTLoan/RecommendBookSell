// controllers/cartController.js
import mongoose from "mongoose";
import Cart from "../models/Cart.js";
import Book from "../models/Book.js";

// Hàm thêm vào giỏ hàng (giữ nguyên)
export const addToCart = async (req, res) => {
  try {
    const user = req.user;
    if (!user) {
      return res.status(401).json({ message: "Người dùng không hợp lệ" });
    }

    const { items } = req.body;
    if (!items || !Array.isArray(items) || items.length === 0) {
      console.log("Invalid input data:", req.body);
      return res
        .status(400)
        .json({ message: "Danh sách sản phẩm không hợp lệ" });
    }

    let cart = await Cart.findOne({ userId: user.id });

    if (!cart) {
      const counterResult = await mongoose.connection.db
        .collection("counters")
        .findOneAndUpdate(
          { _id: "cartId" },
          { $inc: { seq: 1 } },
          { returnDocument: "after", upsert: true }
        );

      console.log("Counter result:", counterResult);

      if (!counterResult || typeof counterResult.seq !== "number") {
        throw new Error("Failed to retrieve or increment counter for cartId");
      }

      const cartId = counterResult.seq;

      cart = new Cart({
        id: cartId,
        userId: user.id,
        items: items,
        createdAt: new Date(),
        updatedAt: new Date(),
      });
    } else {
      items.forEach((newItem) => {
        const existingItemIndex = cart.items.findIndex(
          (item) => item.bookId === newItem.bookId
        );
        if (existingItemIndex >= 0) {
          cart.items[existingItemIndex].quantity += newItem.quantity;
        } else {
          cart.items.push(newItem);
        }
      });
      cart.updatedAt = new Date();
    }

    console.log("Saving cart:", cart);
    const updatedCart = await cart.save();
    console.log("Cart saved:", updatedCart);

    res.status(200).json(updatedCart);
  } catch (error) {
    console.error("Error adding to cart:", error.message);
    res.status(500).json({ message: "Lỗi máy chủ: " + error.message });
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

    // Debug: Log dữ liệu thô
    console.log("Raw cart data:", cart);

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
      bookId: item.bookId, // Giữ bookId là số
      quantity: item.quantity,
      selected: item.selected || false, // Đảm bảo có trường selected
      book: bookMap[item.bookId] || null, // Thêm trường book chứa thông tin chi tiết
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

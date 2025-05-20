import mongoose from "mongoose";
import Cart from "../models/Cart.js";
import Counter from "../models/Counter.js";

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
      // Tăng seq và gán id
      const counterResult = await mongoose.connection.db
        .collection("counters")
        .findOneAndUpdate(
          { _id: "cartId" },
          { $inc: { seq: 1 } },
          { returnDocument: "after", upsert: true }
        );

      console.log("Counter result:", counterResult);

      // Kiểm tra kết quả
      if (!counterResult || typeof counterResult.seq !== "number") {
        throw new Error("Failed to retrieve or increment counter for cartId");
      }

      const cartId = counterResult.seq;

      // Tạo giỏ hàng mới
      cart = new Cart({
        id: cartId,
        userId: user.id,
        items: items,
        createdAt: new Date(),
        updatedAt: new Date(),
      });
    } else {
      // Cập nhật giỏ hàng
      items.forEach((newItem) => {
        const existingItemIndex = cart.items.findIndex(
          (item) => item.bookId === newItem.bookId
        );
        if (existingItemIndex >= 0) {
          // Cập nhật số lượng nếu sản phẩm đã có
          cart.items[existingItemIndex].quantity += newItem.quantity;
        } else {
          // Thêm sản phẩm mới
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

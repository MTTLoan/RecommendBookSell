// routes/cartRoutes.js
import express from "express";
import {
  addToCart,
  getCart,
  updateCart,
  deleteCartItem,
} from "../controllers/cartController.js";
import userJwtMiddleware from "../middleware/user_jwt.js";

const router = express.Router();

// Route thêm sản phẩm vào giỏ hàng
router.post("/", userJwtMiddleware, addToCart);
// Route lấy thông tin giỏ hàng
router.get("/", userJwtMiddleware, getCart);
// Route cập nhật giỏ hàng
router.put("/", userJwtMiddleware, updateCart);
// Route xóa sản phẩm khỏi giỏ hàng
router.delete("/items/:bookId", userJwtMiddleware, deleteCartItem);

export default router;

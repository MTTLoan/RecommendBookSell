import express from "express";
import {
  getOrderHistory,
  updateOrderStatus,
  getOrderById,
} from "../controllers/orderController.js";
import userJwtMiddleware from "../middleware/user_jwt.js";

const router = express.Router();

// Route lấy lịch sử đơn hàng
router.get("/history", userJwtMiddleware, getOrderHistory);

// Route cập nhật trạng thái đơn hàng
router.put("/:id/status", userJwtMiddleware, updateOrderStatus);

// Route lấy thông tin đơn hàng theo ID
router.get("/:id", userJwtMiddleware, getOrderById);

export default router;

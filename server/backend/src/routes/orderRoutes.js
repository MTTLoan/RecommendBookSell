import express from "express";
import {
  getOrderHistory,
  updateOrderStatus,
} from "../controllers/orderController.js";
import userJwtMiddleware from "../middleware/user_jwt.js";

const router = express.Router();

// Route lấy lịch sử đơn hàng
router.get("/history", userJwtMiddleware, getOrderHistory);

// Route cập nhật trạng thái đơn hàng
router.put("/:id/status", userJwtMiddleware, updateOrderStatus);

export default router;

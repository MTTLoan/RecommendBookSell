import express from "express";
import {
  getOrderHistory,
  updateOrderStatus,
  getOrderById,
  addOrder,
  getAllOrders,
  updateOrder,
  searchOrdersByBookName
} from "../controllers/orderController.js";
import userJwtMiddleware from "../middleware/user_jwt.js";

const router = express.Router();

// Route lấy lịch sử đơn hàng
router.get("/history", userJwtMiddleware, getOrderHistory);

router.get('/search', searchOrdersByBookName);

// Route cập nhật trạng thái đơn hàng
router.put("/:id/status", userJwtMiddleware, updateOrderStatus);

// Route lấy thông tin đơn hàng theo ID
router.get("/:id", userJwtMiddleware, getOrderById);

// Route thêm đơn hàng mới
router.post("/", userJwtMiddleware, addOrder);

// Sửa đơn hàng (hay dùng sửa status thôi?)
router.put("/:id", userJwtMiddleware, updateOrder);

router.get("/", getAllOrders);

export default router;

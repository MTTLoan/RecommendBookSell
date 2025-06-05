import express from "express";
import {
  getOrderHistory,
  updateOrderStatus,
  getOrderById,
  addOrder,
  getAllOrders,
  updateOrder,
  adminGetOrderById,
  searchOrdersByBookName,
  adminUpdateOrderStatus,
} from "../controllers/orderController.js";
import userJwtMiddleware from "../middleware/user_jwt.js";

const router = express.Router();

// Route lấy lịch sử đơn hàng
router.get("/history", userJwtMiddleware, getOrderHistory);
// Route tìm kiếm đơn hàng theo tên sách
router.get("/search", searchOrdersByBookName);
// Route cập nhật trạng thái đơn hàng
router.put("/:id/status", userJwtMiddleware, updateOrderStatus);
// Route lấy thông tin đơn hàng theo ID
router.get("/:id", userJwtMiddleware, getOrderById);
// Route lấy thông tin đơn hàng theo ID (admin)
router.get("/admin/:id", userJwtMiddleware, adminGetOrderById);
// Route thêm đơn hàng mới
router.post("/", userJwtMiddleware, addOrder);
// Sửa đơn hàng
router.put("/admin/:id/status", userJwtMiddleware, adminUpdateOrderStatus);

router.get("/", getAllOrders);

export default router;

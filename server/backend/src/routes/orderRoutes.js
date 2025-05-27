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
router.get("/search", userJwtMiddleware, searchOrdersByBookName);
// Route cập nhật trạng thái đơn hàng
router.put("/:id/status", userJwtMiddleware, updateOrderStatus);
// Route lấy thông tin đơn hàng theo ID
router.get("/:id", userJwtMiddleware, getOrderById);
// Route lấy thông tin đơn hàng admin (nếu cần phân biệt quyền, nên dùng middleware kiểm tra role admin)
router.get("/admin/:id", userJwtMiddleware, adminGetOrderById);
// Thêm đơn hàng mới
router.post("/", userJwtMiddleware, addOrder);
// Sửa trạng thái đơn hàng admin (nên dùng PUT /:id/status hoặc PATCH /:id/status, không nên để /admin/:id/status)
router.put("/:id/status/admin", userJwtMiddleware, adminUpdateOrderStatus);
// Lấy danh sách đơn hàng
router.get("/", userJwtMiddleware, getAllOrders);

export default router;

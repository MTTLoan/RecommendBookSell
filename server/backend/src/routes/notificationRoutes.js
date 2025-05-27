import express from "express";
import {
  getNotifications,
  markAsRead,
  addNotification,
  getAllNotificationsForAdmin,
  deleteNotification,
  updateNotification,
} from "../controllers/notificationController.js";
import userJwtMiddleware from "../middleware/user_jwt.js";

const router = express.Router();

// Route lấy tất cả thông báo của người dùng
router.get("/", userJwtMiddleware, getNotifications);
// Route đánh dấu thông báo là đã đọc
router.put("/:id", userJwtMiddleware, markAsRead);
// Route thêm thông báo mới
router.post("/", userJwtMiddleware, addNotification);
// Route lấy tất cả thông báo cho quản trị viên
router.get("/admin", userJwtMiddleware, getAllNotificationsForAdmin);
// Route xóa thông báo theo ID
router.delete("/:id", userJwtMiddleware, deleteNotification);
// Route cập nhật thông báo theo ID
router.put("/admin/:id", userJwtMiddleware, updateNotification);

export default router;

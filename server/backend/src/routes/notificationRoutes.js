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

// Route để lấy thông báo cho người dùng
router.get("/", userJwtMiddleware, getNotifications);
// Route để đánh dấu thông báo là đã đọc
router.put("/:id", userJwtMiddleware, markAsRead);
// Route để thêm thông báo mới
router.post("/", userJwtMiddleware, addNotification);
// Route để lấy tất cả thông báo cho quản trị viên
router.get("/admin", userJwtMiddleware, getAllNotificationsForAdmin);
// Route để xóa thông báo
router.delete("/:id", userJwtMiddleware, deleteNotification);
// Route để cập nhật thông báo
router.put("/admin/:id", userJwtMiddleware, updateNotification);

export default router;

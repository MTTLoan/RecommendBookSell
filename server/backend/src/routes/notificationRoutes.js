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

router.get("/", userJwtMiddleware, getNotifications);
router.put("/:id", userJwtMiddleware, markAsRead);
router.post("/", userJwtMiddleware, addNotification);
router.get("/admin", userJwtMiddleware, getAllNotificationsForAdmin);
router.delete("/:id", userJwtMiddleware, deleteNotification);
router.put("/admin/:id", userJwtMiddleware, updateNotification);

export default router;

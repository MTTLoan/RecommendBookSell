import express from "express";
import {
  getNotifications,
  markAsRead,
  addNotification,
  getAllNotificationsForAdmin
} from "../controllers/notificationController.js";
import userJwtMiddleware from "../middleware/user_jwt.js";

const router = express.Router();

router.get("/", userJwtMiddleware, getNotifications);
router.put("/:id", userJwtMiddleware, markAsRead);
router.post("/", userJwtMiddleware, addNotification);
router.get('/admin', userJwtMiddleware, getAllNotificationsForAdmin);

export default router;

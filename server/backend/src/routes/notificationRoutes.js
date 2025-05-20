import express from "express";
import {
  getNotifications,
  createNotification,
  markAsRead,
} from "../controllers/notificationController.js";
import userJwtMiddleware from "../middleware/user_jwt.js";

const router = express.Router();

router.get("/", userJwtMiddleware, getNotifications);
router.post("/", createNotification);
router.put("/:id", userJwtMiddleware, markAsRead);

export default router;

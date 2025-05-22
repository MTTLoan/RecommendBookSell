import express from "express";
import {
  getNotifications,
  markAsRead,
  addNotification,
} from "../controllers/notificationController.js";
import userJwtMiddleware from "../middleware/user_jwt.js";

const router = express.Router();

router.get("/", userJwtMiddleware, getNotifications);
router.put("/:id", userJwtMiddleware, markAsRead);
router.post("/", userJwtMiddleware, addNotification);

export default router;

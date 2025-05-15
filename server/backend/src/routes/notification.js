import express from "express";
import Notification from "../models/Notification.js";

const router = express.Router();

router.get("/notifications", async (req, res) => {
  try {
    const { userId } = req.query; // Lấy userId từ query string
    let query = {};

    if (userId) {
      query.userId = parseInt(userId); // Chuyển đổi userId thành số
    }

    const notifications = await Notification.find(query); // Lấy toàn bộ dữ liệu

    console.log(
      `Found ${notifications.length} notifications for userId ${
        userId || "all"
      }`
    );
    res.status(200).json(notifications);
  } catch (error) {
    console.error("Error fetching notifications:", error.message);
    res.status(500).json({ message: error.message });
  }
});

router.post("/notifications", async (req, res) => {
  const notification = new Notification({
    id: req.body.id,
    userId: req.body.userId,
    orderId: req.body.orderId || null,
    title: req.body.title,
    message: req.body.message,
    isRead: req.body.isRead || false,
    createdAt: req.body.createdAt,
  });

  try {
    const newNotification = await notification.save();
    res.status(201).json(newNotification);
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
});

router.put("/notifications/:id", async (req, res) => {
  try {
    const notification = await Notification.findOne({ id: req.params.id });
    if (notification) {
      notification.isRead = true;
      const updatedNotification = await notification.save();
      res.status(200).json(updatedNotification);
    } else {
      res.status(404).json({ message: "Notification not found" });
    }
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
});

export default router;

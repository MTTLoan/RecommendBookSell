import Notification from "../models/Notification.js";
import Order from "../models/Order.js";
import User from '../models/User.js';
import Book from "../models/Book.js";
import Counter from "../models/Counter.js";
import mongoose from "mongoose";

export const getNotifications = async (req, res) => {
  try {
    const user = req.user;
    if (!user) {
      return res.status(401).json({ message: "Người dùng không hợp lệ" });
    }

    const notifications = await Notification.find({ userId: user.id }).sort({
      createdAt: -1,
    });

    // Lấy thêm imageUrl từ Book thông qua Order
    const enrichedNotifications = await Promise.all(
      notifications.map(async (notification) => {
        let imageUrl = null;
        if (notification.orderId) {
          const order = await Order.findOne({ id: notification.orderId });
          if (order && order.items && order.items.length > 0) {
            const bookId = order.items[0].bookId;
            const book = await Book.findOne({ id: bookId });
            if (book && book.images && book.images.length > 0) {
              imageUrl = book.images[0].url;
            }
          }
        }
        return {
          ...notification.toJSON(),
          imageUrl,
        };
      })
    );

    console.log(
      `Found ${enrichedNotifications.length} notifications for userId ${user.id}`
    );
    res.status(200).json(enrichedNotifications);
  } catch (error) {
    console.error("Error fetching notifications:", error.message);
    res.status(500).json({ message: error.message });
  }
};

export const addNotification = async (req, res) => {
  try {
    const user = req.user;
    if (!user) {
      return res.status(401).json({ message: "Người dùng không hợp lệ" });
    }

    const { userId, orderId, title, message } = req.body;
    console.log("Adding notification:", {
      userId,
      orderId,
      title,
      message,
    });

    // Kiểm tra dữ liệu đầu vào
    if (!userId || !title || !message) {
      console.log("Invalid input data");
      return res.status(400).json({
        success: false,
        message: "Dữ liệu không hợp lệ: Thiếu userId, title hoặc message",
      });
    }

    // Tăng seq và gán id
    const counterResult = await mongoose.connection.db
      .collection("counters")
      .findOneAndUpdate(
        { _id: "notificationId" },
        { $inc: { seq: 1 } },
        { returnDocument: "after", upsert: true }
      );

    console.log("Counter result:", counterResult);

    // Kiểm tra kết quả
    if (!counterResult || typeof counterResult.seq !== "number") {
      throw new Error(
        "Failed to retrieve or increment counter for notificationId"
      );
    }

    const notificationId = counterResult.seq;

    // Tạo notification mới
    const notification = new Notification({
      id: notificationId,
      userId,
      orderId: orderId || null,
      title,
      message,
      isRead: false,
      createdAt: new Date(),
    });

    console.log("Saving notification:", notification);
    await notification.save();

    res.status(201).json({
      success: true,
      data: notification,
    });
  } catch (error) {
    console.error("Error adding notification:", error);
    res.status(500).json({
      success: false,
      message: "Lỗi máy chủ: " + error.message,
    });
  }
};

export const markAsRead = async (req, res) => {
  try {
    const user = req.user;
    if (!user) {
      return res.status(401).json({ message: "Người dùng không hợp lệ" });
    }

    const notification = await Notification.findOne({
      id: req.params.id,
      userId: user.id,
    });
    if (!notification) {
      return res
        .status(404)
        .json({ message: "Không tìm thấy thông báo hoặc bạn không có quyền" });
    }

    notification.isRead = true;
    const updatedNotification = await notification.save();
    res.status(200).json(updatedNotification);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

export const getAllNotificationsForAdmin = async (req, res) => {
  try {
    if (!req.user || req.user.role !== 'admin') {
      return res.status(403).json({ message: "Bạn không có quyền truy cập" });
    }

    const notifications = await Notification.find().sort({ createdAt: -1 });
    
    const userIds = [...new Set(notifications.map(n => n.userId))];
    const users = await User.find({ id: { $in: userIds } });
    const userMap = {};
    users.forEach(u => { userMap[u.id] = u; });

    const result = notifications.map(n => ({
      id: n.id,
      customerName: userMap[n.userId]?.fullName || 'Ẩn',
      title: n.title,
      content: n.message,
      createdAt: n.createdAt,
    }));

    res.status(200).json(result);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};
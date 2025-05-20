import Notification from "../models/Notification.js";
import Order from "../models/Order.js";
import Book from "../models/Book.js";
import Counter from "../models/Counter.js";

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

export const createNotification = async (req, res) => {
  try {
    const counter = await Counter.findOneAndUpdate(
      { _id: "notificationId" },
      { $inc: { seq: 1 } },
      { new: true, upsert: true }
    );

    let imageUrl = null;
    if (req.body.orderId) {
      const order = await Order.findOne({ id: req.body.orderId });
      if (order && order.items && order.items.length > 0) {
        const bookId = order.items[0].bookId;
        const book = await Book.findOne({ id: bookId });
        if (book && book.images && book.images.length > 0) {
          imageUrl = book.images[0].url;
        }
      }
    }

    const notification = new Notification({
      id: counter.seq,
      userId: req.body.userId,
      orderId: req.body.orderId || null,
      title: req.body.title,
      message: req.body.message,
      isRead: req.body.isRead || false,
      createdAt: req.body.createdAt,
      imageUrl,
    });

    const newNotification = await notification.save();
    res.status(201).json(newNotification);
  } catch (error) {
    res.status(400).json({ message: error.message });
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

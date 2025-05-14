import mongoose from "mongoose";

const NotificationSchema = new mongoose.Schema({
  id: { type: Number, required: true, unique: true },
  userId: { type: Number, ref: "User", required: true },
  orderId: { type: Number, ref: "Order", required: true },
  title: { type: String, required: true },
  message: { type: String, required: true },
  isRead: { type: Boolean, default: false },
  createdAt: { type: Date, required: true },
});

export default mongoose.model("Notification", NotificationSchema);

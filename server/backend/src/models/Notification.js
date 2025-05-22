import mongoose from "mongoose";

const notificationSchema = new mongoose.Schema(
  {
    id: {
      type: Number,
      required: true,
      unique: true,
    },
    userId: {
      type: Number,
      required: true,
    },
    orderId: {
      type: Number,
      default: null,
    },
    title: {
      type: String,
      required: true,
    },
    message: {
      type: String,
      required: true,
    },
    isRead: {
      type: Boolean,
      default: false,
    },
  },
  {
    timestamps: true,
  }
);

// Đồng bộ Counter với id lớn nhất trong notifications
const syncCounter = async () => {
  try {
    const Counter = mongoose.model("Counter");
    const maxId = await mongoose
      .model("Notification")
      .findOne()
      .sort({ id: -1 })
      .select("id")
      .exec();
    const currentMaxId = maxId ? maxId.id : 0;
    await Counter.updateOne(
      { _id: "notificationId" },
      { $set: { seq: currentMaxId } },
      { upsert: true }
    );
    console.log(`Counter synced with max id: ${currentMaxId}`);
  } catch (error) {
    console.error("Error syncing counter:", error);
  }
};

mongoose.connection.once("open", syncCounter);

notificationSchema.set("toJSON", { virtuals: true });

export default mongoose.model("Notification", notificationSchema);

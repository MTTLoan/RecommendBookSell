// backend/src/models/RecommendationTracking.js
import mongoose from "mongoose";

const RecommendationTrackingSchema = new mongoose.Schema({
  id: { type: Number, required: true, unique: true },
  userId: { type: Number, ref: "User", required: true },
  bookId: { type: Number, ref: "Book", required: true },
  action: {
    type: String,
    required: true,
    enum: ["click", "add_to_cart", "purchase"],
  },
  timestamp: { type: Date, required: true, default: Date.now },
  orderId: { type: Number, ref: "Order", required: false },
  cartId: { type: Number, ref: "Cart", required: false },
});

// Đồng bộ Counter với id lớn nhất trong RecommendationTracking
const syncCounter = async () => {
  try {
    const Counter = mongoose.model("Counter");
    const maxId = await mongoose
      .model("RecommendationTracking")
      .findOne()
      .sort({ id: -1 })
      .select("id")
      .exec();
    const currentMaxId = maxId ? maxId.id : 0;
    await Counter.updateOne(
      { _id: "recommendationTrackingId" },
      { $set: { seq: currentMaxId } },
      { upsert: true }
    );
    console.log(
      `RecommendationTracking counter synced with max id: ${currentMaxId}`
    );
  } catch (error) {
    console.error("Error syncing RecommendationTracking counter:", error);
  }
};

// Gọi đồng bộ khi khởi động
mongoose.connection.once("open", syncCounter);

// Đảm bảo virtuals được bao gồm trong JSON output
RecommendationTrackingSchema.set("toJSON", { virtuals: true });

export default mongoose.model(
  "RecommendationTracking",
  RecommendationTrackingSchema,
  "recommendation_trackings"
);

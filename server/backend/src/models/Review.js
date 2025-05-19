import mongoose from "mongoose";

const ReviewSchema = new mongoose.Schema({
  id: { type: Number, required: true, unique: true }, // id sẽ được gán trong controller
  userId: { type: Number, ref: "User", required: true },
  bookId: { type: Number, ref: "Book", required: true },
  rating: { type: Number, required: true },
  comment: { type: String, required: false },
  createdAt: { type: Date, required: true },
});

// Đồng bộ Counter với id lớn nhất trong users
const syncCounter = async () => {
  try {
    const Counter = mongoose.model("Counter");
    const maxId = await mongoose
      .model("Review")
      .findOne()
      .sort({ id: -1 })
      .select("id")
      .exec();
    const currentMaxId = maxId ? maxId.id : 0;
    await Counter.updateOne(
      { _id: "reviewId" },
      { $set: { seq: currentMaxId } },
      { upsert: true }
    );
    console.log(`Counter synced with max id: ${currentMaxId}`);
  } catch (error) {
    console.error("Error syncing counter:", error);
  }
};

ReviewSchema.set("toJSON", { virtuals: true });

export default mongoose.model("Review", ReviewSchema);

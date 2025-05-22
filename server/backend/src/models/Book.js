import mongoose from "mongoose";
import Counter from './Counter.js';

const ImageSchema = new mongoose.Schema({
  url: { type: String, required: true },
});

const BookSchema = new mongoose.Schema({
  id: { type: Number, required: true, unique: true },
  name: { type: String, required: true },
  description: { type: String, required: false },
  images: [ImageSchema],
  price: { type: Number, required: true },
  averageRating: { type: Number, required: false, default: 0 },
  ratingCount: { type: Number, required: false, default: 0 },
  stockQuantity: { type: Number, required: true },
  categoryId: { type: Number, required: true },
  createdAt: { type: Date, required: true },
  author: [{ type: String }],
});

// Đồng bộ Counter với id lớn nhất trong books
const syncCounter = async () => {
  try {
    const Counter = mongoose.model("Counter");
    const maxId = await mongoose
      .model("Book")
      .findOne()
      .sort({ id: -1 })
      .select("id")
      .exec();
    const currentMaxId = maxId ? maxId.id : 0;
    await Counter.updateOne(
      { _id: "bookId" },
      { $set: { seq: currentMaxId } },
      { upsert: true }
    );
    console.log(`Book counter synced with max id: ${currentMaxId}`);
  } catch (error) {
    console.error("Error syncing book counter:", error);
  }
};

// Gọi đồng bộ khi khởi động
mongoose.connection.once("open", syncCounter);

BookSchema.set("toJSON", { virtuals: true });

// Tắt _id mặc định và không index nó
BookSchema.set("id", false);
// BookSchema.set("_id", false);

export default mongoose.model("Book", BookSchema, "books"); // Collection name là "books"

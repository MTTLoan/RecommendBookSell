import mongoose from "mongoose";
import Counter from './Counter.js';

const ImageSchema = new mongoose.Schema({
  url: { type: String, required: true },
  alt: { type: String, required: false },
});

const BookSchema = new mongoose.Schema(
  {
    id: { type: Number, required: true, unique: true },
    name: { type: String, required: true },
    description: { type: String, required: false },
    images: [ImageSchema],
    price: { type: Number, required: true },
    averageRating: { type: Number, required: false },
    ratingCount: { type: Number, required: false },
    stockQuantity: { type: Number, required: true },
    categoryId: { type: Number, ref: "Category", required: true },
    createdAt: { type: Date, required: true },
    author: [{ type: String }],
  },
  {
    timestamps: { createdAt: "createdAt", updatedAt: "updatedAt" },
  }
);


const syncBookCounter = async () => {
  try {
    const Counter = mongoose.model("Counter");
    const BookModel = mongoose.model("Book"); // Lấy model đã đăng ký
    const maxIdDoc = await BookModel.findOne().sort({ id: -1 }).select("id").exec();
    const currentMaxId = maxIdDoc ? maxIdDoc.id : 0;
    await Counter.updateOne(
      { _id: "bookId" },
      { $set: { seq: currentMaxId } },
      { upsert: true }
    );
    console.log(`Book Counter synced with max id: ${currentMaxId}`);
  } catch (error) {
    console.error("Error syncing book counter:", error);
  }
};

mongoose.connection.once("open", syncBookCounter);


export default mongoose.model("Book", BookSchema);

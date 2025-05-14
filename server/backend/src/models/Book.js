import mongoose from "mongoose";

const ImageSchema = new mongoose.Schema({
  url: { type: String, required: true },
  alt: { type: String, required: false },
});

const BookSchema = new mongoose.Schema({
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
  author: { type: String, required: true },
});

export default mongoose.model("Book", BookSchema);

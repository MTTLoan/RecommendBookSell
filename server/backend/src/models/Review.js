import mongoose from "mongoose";

const ReviewSchema = new mongoose.Schema({
  id: { type: Number, required: true, unique: true },
  userId: { type: Number, ref: "User", required: true },
  bookId: { type: Number, ref: "Book", required: true },
  rating: { type: Number, required: true },
  comment: { type: String, required: false },
  createdAt: { type: Date, required: true },
});

export default mongoose.model("Review", ReviewSchema);

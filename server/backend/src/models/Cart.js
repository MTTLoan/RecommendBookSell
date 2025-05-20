import mongoose from "mongoose";

const CartItemSchema = new mongoose.Schema({
  bookId: { type: Number, ref: "Book", required: true },
  quantity: { type: Number, required: true },
});

const CartSchema = new mongoose.Schema({
  id: { type: Number, required: true, unique: true },
  userId: { type: Number, ref: "User", required: true },
  items: [CartItemSchema],
  createdAt: { type: Date, required: true },
  updatedAt: { type: Date, required: true },
});

export default mongoose.model("Cart", CartSchema);

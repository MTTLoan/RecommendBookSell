import mongoose from "mongoose";

const OrderItemSchema = new mongoose.Schema({
  bookId: { type: Number, ref: "Book", required: true },
  quantity: { type: Number, required: true },
  unitPrice: { type: Number, required: true },
});

const OrderSchema = new mongoose.Schema({
  id: { type: Number, required: true, unique: true },
  userId: { type: Number, ref: "User", required: true },
  orderDate: { type: Date, required: true },
  totalAmount: { type: Number, required: true },
  status: { type: String, required: true },
  shippingProvince: { type: Number, required: false },
  shippingDistrict: { type: Number, required: false },
  shippingWard: { type: Number, required: false },
  shippingDetail: { type: String, required: false },
  items: [OrderItemSchema],
  createdAt: { type: Date, required: true },
  updatedAt: { type: Date, required: true },
});

export default mongoose.model("Order", OrderSchema);

import mongoose from "mongoose";

const OrderItemSchema = new mongoose.Schema({
  bookId: { type: Number, ref: "Book", required: true },
  quantity: { type: Number, required: true },
  unitPrice: { type: Number, required: true },
  recommend: { type: Boolean, default: false },
});

const OrderSchema = new mongoose.Schema(
  {
    id: { type: Number, required: true, unique: true },
    userId: { type: Number, ref: "User", required: true },
    orderDate: { type: Date, required: true },
    shippingCost: { type: Number, required: false },
    totalAmount: { type: Number, required: true },
    status: { type: String, required: true },
    shippingProvince: { type: Number, required: false },
    shippingDistrict: { type: Number, required: false },
    shippingWard: { type: Number, required: false },
    shippingDetail: { type: String, required: false },
    items: [OrderItemSchema],
  },
  {
    timestamps: { createdAt: "createdAt", updatedAt: "updatedAt" },
  }
);

// Đồng bộ Counter với id lớn nhất trong users
const syncCounter = async () => {
  try {
    const Counter = mongoose.model("Counter");
    const maxId = await mongoose
      .model("Order")
      .findOne()
      .sort({ id: -1 })
      .select("id")
      .exec();
    const currentMaxId = maxId ? maxId.id : 0;
    await Counter.updateOne(
      { _id: "orderId" },
      { $set: { seq: currentMaxId } },
      { upsert: true }
    );
    console.log(`Counter synced with max id: ${currentMaxId}`);
  } catch (error) {
    console.error("Error syncing counter:", error);
  }
};

// Gọi đồng bộ khi khởi động
mongoose.connection.once("open", syncCounter);

// Đảm bảo virtuals được bao gồm trong JSON output
OrderSchema.set("toJSON", { virtuals: true });

export default mongoose.model("Order", OrderSchema);

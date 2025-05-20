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

// Đồng bộ Counter với id lớn nhất trong carts
const syncCounter = async () => {
  try {
    const Counter = mongoose.model("Counter");
    const maxId = await mongoose
      .model("Cart")
      .findOne()
      .sort({ id: -1 })
      .select("id")
      .exec();
    const currentMaxId = maxId ? maxId.id : 0;
    await Counter.updateOne(
      { _id: "cartId" },
      { $set: { seq: currentMaxId } },
      { upsert: true }
    );
    console.log(`Cart counter synced with max id: ${currentMaxId}`);
  } catch (error) {
    console.error("Error syncing cart counter:", error);
  }
};

// Gọi đồng bộ khi khởi động
mongoose.connection.once("open", syncCounter);

CartSchema.set("toJSON", { virtuals: true });

export default mongoose.model("Cart", CartSchema);

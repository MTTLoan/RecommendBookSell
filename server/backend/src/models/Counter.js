import mongoose from "mongoose";

const CounterSchema = new mongoose.Schema({
  _id: { type: String, required: true }, // Tên counter, ví dụ: "userId", "bookId"
  seq: { type: Number, default: 0 }, // Giá trị id tiếp theo
});

export default mongoose.model("Counter", CounterSchema);

import mongoose from "mongoose";

const CategorySchema = new mongoose.Schema({
  id: { type: Number, required: true, unique: true },
  name: { type: String, required: true },
  description: { type: String, required: false },
  imageUrl: { type: String, required: false },
});

export default mongoose.model("Category", CategorySchema);

import mongoose from "mongoose";

const CategorySchema = new mongoose.Schema({
  id: { type: Number, required: true, unique: true },
  name: { type: String, required: true },
  description: { type: String, required: false },
  imageUrl: { type: String, required: false },
});

const syncCategoryCounter = async () => {
  try {
    const Counter = mongoose.model("Counter");
    const maxIdDoc = await mongoose
      .model("Category")
      .findOne()
      .sort({ id: -1 })
      .select("id")
      .exec();
    const currentMaxId = maxIdDoc ? maxIdDoc.id : 0;
    await Counter.updateOne(
      { _id: "categoryId" },
      { $set: { seq: currentMaxId } },
      { upsert: true }
    );
    console.log(`Category Counter synced with max id: ${currentMaxId}`);
  } catch (error) {
    console.error("Error syncing category counter:", error);
  }
};

mongoose.connection.once("open", syncCategoryCounter);

export default mongoose.model("Category", CategorySchema);

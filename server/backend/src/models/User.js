import mongoose from "mongoose";
import "../models/Counter.js";

const UserSchema = new mongoose.Schema(
  {
    id: { type: Number, required: true, unique: true },
    username: { type: String, unique: true },
    fullName: { type: String },
    email: { type: String, unique: true },
    phoneNumber: { type: String, required: true },
    addressProvince: { type: Number },
    addressDistrict: { type: Number },
    addressWard: { type: Number },
    addressDetail: { type: String },
    password: { type: String, required: true },
    role: { type: String, default: "customer" },
    birthday: { type: Date },
    avatar: { type: String },
    token: { type: String },
    verified: { type: Boolean, default: false },
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
      .model("User")
      .findOne()
      .sort({ id: -1 })
      .select("id")
      .exec();
    const currentMaxId = maxId ? maxId.id : 0;
    await Counter.updateOne(
      { _id: "userId" },
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

// Pre-save hook để gán id tự tăng
UserSchema.pre("save", async function (next) {
  if (this.isNew) {
    try {
      const Counter = mongoose.model("Counter");
      const counter = await Counter.findOneAndUpdate(
        { _id: "userId" },
        { $inc: { seq: 1 } },
        { new: true, upsert: true }
      );
      this.id = counter.seq;
    } catch (error) {
      return next(error);
    }
  }
  next();
});

// Đảm bảo virtuals được bao gồm trong JSON output
UserSchema.set("toJSON", { virtuals: true });

export default mongoose.model("User", UserSchema);

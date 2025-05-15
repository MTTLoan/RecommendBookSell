import mongoose from "mongoose";

const UserSchema = new mongoose.Schema(
  {
    id: { type: Number, unique: true },
    username: { type: String, unique: true },
    fullName: { type: String },
    email: { type: String, unique: true },
    phoneNumber: { type: String, required: true },
    addressProvince: { type: Number, required: false },
    addressDistrict: { type: Number, required: false },
    addressWard: { type: Number, required: false },
    addressDetail: { type: String, required: false },
    password: { type: String },
    role: { type: String, default: "user" },
    birthday: { type: Date, required: false },
    avatar: { type: String, required: false },
    token: { type: String, required: false },
    verified: { type: Boolean, default: false },
  },
  {
    timestamps: { createdAt: "createdAt", updatedAt: "updatedAt" }, // Auto-manage createdAt, updatedAt
  }
);

// Ensure virtuals are included in JSON output
UserSchema.set("toJSON", { virtuals: true });

export default mongoose.model("User", UserSchema);

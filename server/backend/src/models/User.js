
import mongoose from "mongoose";

const UserSchema = new mongoose.Schema({
  username: { type: String, required: true, unique: true },
  fullName: { type: String, required: true },
  email: { type: String, required: true, unique: true },
  phoneNumber: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  role: { type: String, enum: ["user", "admin"], default: "user" },
  verified: { type: Boolean, default: false },
  googleId: { type: String, unique: true, sparse: true }, // Lưu ID từ Google
  photoUrl: { type: String }, // Lưu URL ảnh đại diện từ Google
  token: { type: String }, // Lưu JWT token
});

export default mongoose.model("User", UserSchema);

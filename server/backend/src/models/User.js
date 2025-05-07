import mongoose from "mongoose";

const Userchema = new mongoose.Schema({
  username: { type: String, required: true, unique: true },
  fullName: { type: String, required: true },
  email: { type: String, required: true, unique: true },
  phoneNumber: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  role: { type: String, enum: ["user", "admin"], default: "user" },
  verified: { type: Boolean, default: false },
  googleId: {type: String, unique: true, sparse: true // Chỉ cần nếu đăng nhập Google
  },
  photo_url: { type: String,   // Lưu URL ảnh đại diện của Google (nếu có)
  },
  token: { type: String, },
});

export default mongoose.model("User", Userchema);

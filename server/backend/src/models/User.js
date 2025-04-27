import mongoose from "mongoose";

const userSchema = new mongoose.Schema({
  username: { type: String, required: true, unique: true },
  email: { type: String, required: true, unique: true },
  password: { type: String },
  googleId: { type: String, unique: true, sparse: true },
  role: { type: String, enum: ["user", "admin"], default: "user" },
  resetToken: String,
  resetTokenExpiry: Date,
});

export default mongoose.model("User", userSchema);

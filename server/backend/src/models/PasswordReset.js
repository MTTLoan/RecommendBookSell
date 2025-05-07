import mongoose from "mongoose";

const passwordResetSchema = new mongoose.Schema({
  userId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "User", // Liên kết với bảng Users
    required: true,
  },
  token: {
    type: String,
    required: true,
    unique: true, // Token phải là duy nhất
  },
  expiresAt: {
    type: Date,
    required: true, // Thời gian hết hạn của token
  },
  createdAt: {
    type: Date,
    default: Date.now, // Thời gian tạo yêu cầu
  },
});

const PasswordReset = mongoose.model("PasswordReset", passwordResetSchema);

export default PasswordReset;
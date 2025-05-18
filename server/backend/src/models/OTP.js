import mongoose from "mongoose";

const { Schema } = mongoose;

const OTPSchema = new Schema({
  email: {
    type: String,
    unique: true,
    sparse: true,
  },
  otp: String,
  createdAt: Date,
  expiresAt: Date,
});

export default mongoose.model("OTP", OTPSchema);

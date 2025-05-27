import express from "express";
import { sendOTP, verifyOTP, deleteOTP } from "../controllers/OTPController.js";

const router = express.Router();

// verify OTP
router.post("/verify", async (req, res) => {
  try {
    let { email, otp } = req.body;

    const validOTP = await verifyOTP({ email, otp });

    return res.status(200).json({
      success: true,
      valid: validOTP,
    });
  } catch (error) {
    return res.status(400).json({
      success: false,
      msg: error.message,
    });
  }
});

// request new verification otp
router.post("/send", async (req, res) => {
  try {
    let { email, subject, message, duration } = req.body;

    // init
    if (!subject) subject = "BookProject: Email Verification";
    if (!message) message = "Verify your email with the code below.";
    if (!duration) duration = 3;

    const createdOTP = await sendOTP({
      email,
      subject,
      message,
      duration,
    });

    return res.status(200).json({
      success: true,
      msg: createdOTP,
    });
  } catch (error) {
    return res.status(400).json({
      success: false,
      msg: error.message,
    });
  }
});

// check OTP only (không ảnh hưởng verify)
router.post("/check", async (req, res) => {
  try {
    let { email, otp } = req.body;
    if (!(email && otp)) {
      return res.status(400).json({
        success: false,
        valid: false,
        msg: "Vui lòng cung cấp email và mã OTP.",
      });
    }
    // Tìm OTP record
    const OTPModel = (await import("../models/OTP.js")).default;
    const matchedOTPRecord = await OTPModel.findOne({ email });
    if (!matchedOTPRecord) {
      return res.status(400).json({
        success: false,
        valid: false,
        msg: "Không tìm thấy mã OTP cho email này.",
      });
    }
    const { expiresAt, otp: hashedOTP } = matchedOTPRecord;
    if (expiresAt < Date.now()) {
      await OTPModel.deleteOne({ email });
      return res.status(400).json({
        success: false,
        valid: false,
        msg: "Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới.",
      });
    }
    // So sánh OTP
    const { verifyHashedData } = await import("../util/hashData.js");
    const validOTP = await verifyHashedData(otp, hashedOTP);
    if (!validOTP) {
      return res.status(400).json({
        success: false,
        valid: false,
        msg: "Mã OTP không đúng.",
      });
    }
    return res.status(200).json({
      success: true,
      valid: true,
      msg: "Mã OTP hợp lệ.",
    });
  } catch (error) {
    return res.status(500).json({
      success: false,
      valid: false,
      msg: error.message || "Lỗi hệ thống khi kiểm tra OTP.",
    });
  }
});

export default router;

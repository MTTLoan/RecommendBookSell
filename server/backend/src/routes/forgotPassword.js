import express from "express";
import { sendPasswordResetOTP, resetPassword } from "../controllers/forgotPasswordController.js";
import { forgotPasswordRequestValidator, resetPasswordValidator }  from "../middleware/userMiddleware.js";

const router = express.Router();

// Route to send password reset OTP
router.post("/forgot-password", forgotPasswordRequestValidator, async (req, res) => {
  const { email } = req.body;
  try {
    const otpRecord = await sendPasswordResetOTP(email);
    res.status(200).json({ message: "OTP sent successfully", otpRecord });
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
});

// Route to reset password
router.post("/reset-password", resetPasswordValidator, async (req, res) => {
    const { email, newPassword, confirmPassword, otp } = req.body;
  
    if (newPassword !== confirmPassword) {
      return res.status(400).json({ message: "Passwords do not match." });
    }
  
    try {
      const resetMessage = await resetPassword({ email, newPassword, otp });
      res.status(200).json(resetMessage);
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
  });
  

export default router;

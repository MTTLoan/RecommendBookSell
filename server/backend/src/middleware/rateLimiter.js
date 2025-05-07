import PasswordReset from "../models/PasswordReset.js";
import User from "../models/User.js";

export const rateLimitForgotPassword = async (req, res, next) => {
  const { email } = req.body;

  try {
    const user = await User.findOne({ email });
    if (!user) {
      return res.status(404).json({ message: "Email không tồn tại." });
    }

    const recentRequests = await PasswordReset.find({
      userId: user._id,
      createdAt: { $gt: Date.now() - 3600000 }, // Chỉ tính các yêu cầu trong 1 giờ qua
    });

    if (recentRequests.length >= 3) {
      return res.status(429).json({
        message: "Bạn đã gửi quá nhiều yêu cầu. Vui lòng thử lại sau 1 giờ.",
      });
    }

    next();
  } catch (error) {
    res.status(500).json({ message: "Lỗi máy chủ: " + error.message });
  }
};
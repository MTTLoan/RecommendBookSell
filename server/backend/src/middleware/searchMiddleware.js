import rateLimit from "express-rate-limit";
import { query, validationResult } from "express-validator";

// Middleware to validate search query parameters
export const searchRateLimiter = rateLimit({
  windowMs: 15 * 60 * 1000,
  max: 100,
  message: {
    success: false,
    msg: "Quá nhiều yêu cầu, vui lòng thử lại sau.",
  },
});

// Middleware để xác thực các tham số truy vấn tìm kiếm
export const handleValidationErrors = (req, res, next) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({
      success: false,
      msg: errors.array()[0].msg,
    });
  }
  next();
};

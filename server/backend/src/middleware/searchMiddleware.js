import rateLimit from 'express-rate-limit';
import { query, validationResult } from 'express-validator';

// Rate limiting middleware (100 requests per 15 minutes per IP)
export const searchRateLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100, // Limit each IP to 100 requests per window
  message: {
    success: false,
    msg: 'Quá nhiều yêu cầu, vui lòng thử lại sau.',
  },
});


// Middleware to handle validation errors
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
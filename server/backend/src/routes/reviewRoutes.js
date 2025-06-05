import express from "express";
import {
  submitReview,
  getReviewStatsForBooks,
  getReviewsByBookId,
} from "../controllers/reviewController.js";
import userJwtMiddleware from "../middleware/user_jwt.js";
import Review from "../models/Review.js";
import mongoose from "mongoose";

const router = express.Router();

// Route lấy tất cả đánh giá
router.get("/", async (req, res) => {
  try {
    const reviews = await Review.find().select("userId bookId rating comment");
    res.status(200).json(reviews);
  } catch (error) {
    res.status(500).json({ error: "Lỗi khi lấy dữ liệu đánh giá" });
  }
});
// Route gửi đánh giá
router.post("/", userJwtMiddleware, submitReview);
// Route kiểm tra xem người dùng đã đánh giá đơn hàng hay chưa
router.get("/:orderId/reviews", userJwtMiddleware, async (req, res) => {
  try {
    const orderId = parseInt(req.params.orderId);
    const userId = req.user.id;
    const reviewCount = await mongoose
      .model("Review")
      .countDocuments({ orderId, userId });
    res.status(200).json({
      success: true,
      hasReviews: reviewCount > 0,
    });
  } catch (error) {
    console.error("Error checking reviews:", error);
    res.status(500).json({
      success: false,
      message: "Lỗi máy chủ: " + error.message,
    });
  }
});
// Route lấy thống kê đánh giá cho các sách
router.post("/stats", getReviewStatsForBooks);
// Route lấy đánh giá theo bookId
router.get("/book/:bookId", getReviewsByBookId);

export default router;

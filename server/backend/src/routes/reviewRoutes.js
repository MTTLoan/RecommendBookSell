import express from "express";
import { submitReview } from "../controllers/reviewController.js";
import userJwtMiddleware from "../middleware/user_jwt.js";

const router = express.Router();

// Route gửi đánh giá
router.post("/", userJwtMiddleware, submitReview);

// Endpoint để kiểm tra review của đơn hàng
router.get("/:orderId/reviews", userJwtMiddleware, async (req, res) => {
  try {
    const orderId = parseInt(req.params.orderId);
    const userId = req.user.id;
    const reviews = await mongoose.model("Review").find({ orderId, userId });
    res.status(200).json({
      success: true,
      data: reviews,
    });
  } catch (error) {
    console.error("Error fetching reviews:", error);
    res.status(500).json({
      success: false,
      message: "Lỗi máy chủ: " + error.message,
    });
  }
});

export default router;

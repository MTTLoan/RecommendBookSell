import express from "express";
import userJwtMiddleware from "../middleware/user_jwt.js";
import {
  getRecommendations,
  getRecommendationsByBook,
} from "../controllers/recommendationController.js";
import {
  recordClick,
  recordAddToCart,
  recordPurchase,
} from "../controllers/recommendationTrackingController.js";

const router = express.Router();

// API để lấy gợi ý sách cho người dùng
router.get("/", userJwtMiddleware, getRecommendations);
// API để lấy gợi ý sách dựa trên bookId
router.get("/book/:bookId", userJwtMiddleware, getRecommendationsByBook);
// API để ghi nhận hành động click
router.post("/click", userJwtMiddleware, recordClick);
// API để ghi nhận hành động thêm vào giỏ hàng
router.post("/add-to-cart", userJwtMiddleware, recordAddToCart);
// API để ghi nhận hành động mua hàng
router.post("/purchase", userJwtMiddleware, recordPurchase);

export default router;

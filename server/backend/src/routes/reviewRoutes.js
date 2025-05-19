import express from "express";
import { submitReview } from "../controllers/reviewController.js";
import userJwtMiddleware from "../middleware/user_jwt.js";

const router = express.Router();

// Route gửi đánh giá
router.post("/", userJwtMiddleware, submitReview);

export default router;

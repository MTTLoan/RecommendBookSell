import express from "express";
import userJwtMiddleware from "../middleware/user_jwt.js";
import { getRecommendations } from "../controllers/recommendationController.js";
import {
  recordClick,
  recordAddToCart,
  recordPurchase,
} from "../controllers/recommendationTrackingController.js";

const router = express.Router();

router.get("/", userJwtMiddleware, getRecommendations);
router.post("/click", userJwtMiddleware, recordClick);
router.post("/add-to-cart", userJwtMiddleware, recordAddToCart);
router.post("/purchase", userJwtMiddleware, recordPurchase);

export default router;

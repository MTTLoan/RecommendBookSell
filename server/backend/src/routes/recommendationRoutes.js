import express from "express";
import userJwtMiddleware from "../middleware/user_jwt.js";
import { getRecommendations } from "../controllers/recommendationController.js";
import recommendationTrackingController from "../controllers/recommendationTrackingController.js";

const router = express.Router();

router.get("/", userJwtMiddleware, getRecommendations);
router.post(
  "/click",
  userJwtMiddleware,
  recommendationTrackingController.recordClick
);

export default router;

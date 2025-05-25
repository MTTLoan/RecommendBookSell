import express from "express";
import userJwtMiddleware from "../middleware/user_jwt.js";
import { getRecommendations } from "../controllers/recommendationController.js";

const router = express.Router();

router.get("/", userJwtMiddleware, getRecommendations);

export default router;

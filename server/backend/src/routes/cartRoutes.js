import express from "express";
import { addToCart } from "../controllers/cartController.js";
import userJwtMiddleware from "../middleware/user_jwt.js";

const router = express.Router();

router.post("/", userJwtMiddleware, addToCart);

export default router;

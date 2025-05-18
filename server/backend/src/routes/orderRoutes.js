import express from "express";
import { getOrderHistory } from "../controllers/orderController.js";
import userJwtMiddleware from "../middleware/user_jwt.js";

const router = express.Router();

// Route lấy lịch sử đơn hàng
router.get("/history", userJwtMiddleware, getOrderHistory);

export default router;

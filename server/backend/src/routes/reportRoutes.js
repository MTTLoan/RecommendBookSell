import express from "express";
import {
  getRevenueStats,
  getClickStats,
  getAddToCartStats,
  getPurchaseStats,
  getTopProducts,
  getRevenueChartData,
  getCategoryRevenueChartData,
} from "../controllers/reportController.js";
import user_jwt from "../middleware/user_jwt.js";

const router = express.Router();

// Các route riêng biệt cho từng loại dữ liệu
router.get("/stats/revenue", user_jwt, getRevenueStats);
router.get("/stats/clicks", user_jwt, getClickStats);
router.get("/stats/add-to-cart", user_jwt, getAddToCartStats);
router.get("/stats/purchases", user_jwt, getPurchaseStats);
router.get("/top-products", user_jwt, getTopProducts);
router.get("/chart/revenue", user_jwt, getRevenueChartData);
router.get("/chart/category-revenue", user_jwt, getCategoryRevenueChartData);

export default router;

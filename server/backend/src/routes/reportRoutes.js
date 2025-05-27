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

const router = express.Router();

// Các route riêng biệt cho từng loại dữ liệu
router.get("/stats/revenue", getRevenueStats);
router.get("/stats/clicks", getClickStats);
router.get("/stats/add-to-cart", getAddToCartStats);
router.get("/stats/purchases", getPurchaseStats);
router.get("/top-products", getTopProducts);
router.get("/chart/revenue", getRevenueChartData);
router.get("/chart/category-revenue", getCategoryRevenueChartData);

export default router;

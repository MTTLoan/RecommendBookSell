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

// Thống kê doanh thu
router.get("/stats/revenue", user_jwt, getRevenueStats);
// Thống kê lượt click
router.get("/stats/clicks", user_jwt, getClickStats);
// Thống kê thêm vào giỏ hàng
router.get("/stats/add-to-cart", user_jwt, getAddToCartStats);
// Thống kê mua hàng
router.get("/stats/purchases", user_jwt, getPurchaseStats);
// Top sản phẩm
router.get("/top-products", user_jwt, getTopProducts);
// Biểu đồ doanh thu
router.get("/charts/revenue", user_jwt, getRevenueChartData);
// Biểu đồ doanh thu theo danh mục
router.get("/charts/category-revenue", user_jwt, getCategoryRevenueChartData);

export default router;

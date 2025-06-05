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

// Route để lấy thống kê doanh thu
router.get("/stats/revenue", getRevenueStats);
// Route để lấy thống kê click
router.get("/stats/clicks", getClickStats);
// Route để lấy thống kê thêm vào giỏ hàng
router.get("/stats/add-to-cart", getAddToCartStats);
// Route để lấy thống kê mua hàng
router.get("/stats/purchases", getPurchaseStats);
// Route để lấy sản phẩm bán chạy nhất
router.get("/top-products", getTopProducts);
// Route để lấy dữ liệu biểu đồ doanh thu
router.get("/chart/revenue", getRevenueChartData);
// Route để lấy dữ liệu biểu đồ doanh thu theo danh mục
router.get("/chart/category-revenue", getCategoryRevenueChartData);

export default router;

import express from "express";
import {
  getDashboardStats,
  getRevenueData,
} from "../controllers/dashboardController.js";
const router = express.Router();

// Route lấy dữ liệu thống kê tổng quan
router.get("/stats", getDashboardStats);
// Route lấy dữ liệu doanh thu cho biểu đồ
router.get("/revenue", getRevenueData);

export default router;

// import express from 'express';
// import { getDashboardData } from '../controllers/dashboardController.js';

// const router = express.Router();

// router.get('/', getDashboardData);

// export default router;

import express from "express";
import {
  getDashboardStats,
  getRevenueData,
} from "../controllers/dashboardController.js";
import user_jwt from "../middleware/user_jwt.js";
const router = express.Router();

// Route lấy dữ liệu thống kê tổng quan
router.get("/stats", user_jwt, getDashboardStats);
// Route lấy dữ liệu doanh thu cho biểu đồ
router.get("/revenue", user_jwt, getRevenueData);

export default router;

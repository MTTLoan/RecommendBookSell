import express from "express";
import {
  getCategories,
  getCategoryById,
  getCategoryStats,
  createCategory,
  updateCategory,
  deleteCategory,
  searchCategories,
} from "../controllers/categoryController.js";
import uploadAvatar from "../middleware/uploadToS3.js";
import user_jwt from "../middleware/user_jwt.js";

const router = express.Router();

// Lấy thống kê danh mục
router.get("/stats", user_jwt, getCategoryStats);
// Tìm kiếm danh mục
router.get("/search", user_jwt, searchCategories);
// Lấy danh sách danh mục
router.get("/", user_jwt, getCategories);
// Lấy chi tiết danh mục
router.get("/:id", user_jwt, getCategoryById);
// Thêm danh mục mới (có upload ảnh)
router.post("/", user_jwt, uploadAvatar, createCategory);
// Sửa danh mục (có upload ảnh)
router.put("/:id", user_jwt, uploadAvatar, updateCategory);
// Xóa danh mục
router.delete("/:id", user_jwt, deleteCategory);

export default router;

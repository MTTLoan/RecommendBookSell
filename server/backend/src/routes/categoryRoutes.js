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

router.get("/category-stats", user_jwt, getCategoryStats);
router.get("/search", user_jwt, searchCategories);

// Lấy danh sách danh mục
router.get("/all-categories", user_jwt, getCategories);

// Lấy chi tiết danh mục
router.get("/:id", user_jwt, getCategoryById);

// Thêm danh mục mới (có upload ảnh)
router.post("/add-category", user_jwt, uploadAvatar, createCategory);
// Sửa danh mục (có upload ảnh)
router.put("/update-category/:id", user_jwt, uploadAvatar, updateCategory);
router.delete("/delete-category/:id", user_jwt, deleteCategory);

export default router;

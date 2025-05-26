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

const router = express.Router();

router.get("/category-stats", getCategoryStats);

router.get("/search", searchCategories);

// Lấy danh sách danh mục
router.get("/all-categories", getCategories);

// Lấy chi tiết danh mục
router.get("/:id", getCategoryById);

// Thêm danh mục mới (có upload ảnh)
router.post("/add-category", uploadAvatar, createCategory);
// Sửa danh mục (có upload ảnh)
router.put("/update-category/:id", uploadAvatar, updateCategory);
router.delete("/delete-category/:id", deleteCategory);

export default router;

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

// Route để lấy thống kê danh mục
router.get("/category-stats", getCategoryStats);
// Route để tìm kiếm danh mục
router.get("/search", searchCategories);
// Route để lấy tất cả danh mục
router.get("/all-categories", getCategories);
// Route để lấy danh mục theo ID
router.get("/:id", getCategoryById);
// Route để thêm danh mục (có upload ảnh)
router.post("/add-category", uploadAvatar, createCategory);
// Route để cập nhật danh mục (có upload ảnh)
router.put("/update-category/:id", uploadAvatar, updateCategory);
// Route để xóa danh mục
router.delete("/delete-category/:id", deleteCategory);

export default router;

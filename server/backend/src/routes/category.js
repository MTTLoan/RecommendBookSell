import express from 'express';
import {
  getCategories,
  getCategoryById,
  getCategoryStats,
  createCategory,
  updateCategory,
  deleteCategory
} from '../controllers/categoryController.js';

const router = express.Router();

// Lấy danh sách danh mục
router.get('/all-categories', getCategories);

// Lấy chi tiết danh mục
router.get('/:id', getCategoryById);

router.post('/add-category', createCategory);
router.put('/update-category/:id', updateCategory);
router.delete('/delete-category/:id', deleteCategory);
router.get('/category-stats', getCategoryStats);

export default router;
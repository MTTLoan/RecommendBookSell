import express from 'express';
import {
  getCategories,
  getCategoryById
} from '../controllers/categoryController.js';

const router = express.Router();

// Lấy danh sách danh mục
router.get('/all-categories', getCategories);

// Lấy chi tiết danh mục
router.get('/:id', getCategoryById);

export default router;
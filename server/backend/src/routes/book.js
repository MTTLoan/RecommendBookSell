import express from 'express';
import { getBookDetail, getBookReviews } from '../controllers/bookController.js';

const router = express.Router();

router.get('/book-detail/:id', getBookDetail);
router.get('/book-detail/:bookId/reviews', getBookReviews);

export default router;
import express from 'express';
import { getBooks, getBookDetail, getBookReviews } from '../controllers/bookController.js';

const router = express.Router();

router.get('/all-book', getBooks); // This route might need to be adjusted based on your requirements
router.get('/book-detail/:id', getBookDetail);
router.get('/book-detail/:bookId/reviews', getBookReviews);

export default router;
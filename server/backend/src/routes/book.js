import express from 'express';
import { getBooks, getBookDetail, getBookReviews, searchBooks } from '../controllers/bookController.js';
import { searchRateLimiter, handleValidationErrors } from '../middleware/searchMiddleware.js';

const router = express.Router();

router.get('/all-book', getBooks); 
router.get('/book-detail/:id', getBookDetail);
router.get('/book-detail/:bookId/reviews', getBookReviews);
router.get('/search', searchRateLimiter, handleValidationErrors, searchBooks);


export default router;
import express from 'express';
import { getBooks, getBookDetail, getBookReviews, searchBooks } from '../controllers/bookController.js';
import { searchRateLimiter, handleValidationErrors } from '../middleware/searchMiddleware.js';
import { searchNameBooks, createBook, peekNextBookIdApi, updateBook, deleteBook } from '../controllers/bookController.js';

const router = express.Router();

router.get('/all-book', getBooks); 
router.get('/book-detail/:id', getBookDetail);
router.get('/book-detail/:bookId/reviews', getBookReviews);
router.get('/search', searchRateLimiter, handleValidationErrors, searchBooks);

router.get('/peek-next-id', peekNextBookIdApi);
router.post('/add-book', createBook);
router.put('/update-book/:id', updateBook);
router.delete('/delete-book/:id', deleteBook);
router.get("/search", searchNameBooks);

export default router;
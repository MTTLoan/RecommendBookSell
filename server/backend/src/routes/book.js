import express from 'express';
import { searchBooks, getBooks, getBookDetail, getBookReviews, createBook, peekNextBookIdApi, updateBook, deleteBook } from '../controllers/bookController.js';

const router = express.Router();

router.get('/all-book', getBooks); // This route might need to be adjusted based on your requirements
router.get('/book-detail/:id', getBookDetail);
router.get('/book-detail/:bookId/reviews', getBookReviews);
router.get('/peek-next-id', peekNextBookIdApi);
router.post('/add-book', createBook);
router.put('/update-book/:id', updateBook);
router.delete('/delete-book/:id', deleteBook);
router.get("/search", searchBooks);

export default router;
import express from "express";
import {
  getBooks,
  getBookDetail,
  getBookReviews,
  searchBooks,
  getBestSellers,
  getNewBooks,
} from "../controllers/bookController.js";
import {
  searchRateLimiter,
  handleValidationErrors,
} from "../middleware/searchMiddleware.js";
import {
  searchNameBooks,
  createBook,
  peekNextBookIdApi,
  updateBook,
  deleteBook,
} from "../controllers/bookController.js";
import Book from "../models/Book.js";

const router = express.Router();

router.get("/", async (req, res) => {
  try {
    const books = await Book.find().select("id name averageRating");
    res.status(200).json(books);
  } catch (error) {
    res.status(500).json({ error: "Lỗi khi lấy dữ liệu sách" });
  }
});

router.get("/all-book", getBooks);
router.get("/book-detail/:id", getBookDetail);
router.get("/book-detail/:bookId/reviews", getBookReviews);
router.get("/search", searchRateLimiter, handleValidationErrors, searchBooks);
router.get("/best-sellers", getBestSellers);
router.get("/new-books", getNewBooks);
router.get("/peek-next-id", peekNextBookIdApi);
router.post("/add-book", createBook);
router.put("/update-book/:id", updateBook);
router.delete("/delete-book/:id", deleteBook);
router.get("/search", searchNameBooks);

export default router;

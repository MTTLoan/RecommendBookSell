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
import user_jwt from "../middleware/user_jwt.js";
import uploadProductImages from "../middleware/uploadProductImages.js";

const router = express.Router();

router.get("/", async (req, res) => {
  try {
    const books = await Book.find().select("id name averageRating");
    res.status(200).json(books);
  } catch (error) {
    res.status(500).json({ error: "Lỗi khi lấy dữ liệu sách" });
  }
});

router.get("/all-book", user_jwt, getBooks);
router.get("/book-detail/:id", getBookDetail);
router.get("/book-detail/:bookId/reviews", getBookReviews);
router.get("/search", searchRateLimiter, handleValidationErrors, searchBooks);
router.get("/best-sellers", getBestSellers);
router.get("/new-books", getNewBooks);
router.get("/peek-next-id", peekNextBookIdApi);
router.post("/add-book", user_jwt, createBook);
router.put("/update-book/:id", user_jwt, updateBook);
router.delete("/delete-book/:id", user_jwt, deleteBook);
router.get("/search", user_jwt, searchNameBooks);
// API upload nhiều ảnh sản phẩm
router.post("/upload-image", uploadProductImages, (req, res) => {
  if (!req.files || req.files.length === 0) {
    return res
      .status(400)
      .json({ success: false, msg: "Không có file ảnh nào được upload." });
  }
  // Trả về mảng url ảnh
  const images = req.files.map((f) => f.location);
  // Nếu chỉ upload 1 ảnh, trả về image cho frontend cũ
  if (images.length === 1) {
    return res.json({ success: true, image: images[0] });
  }
  return res.json({ success: true, images });
});

export default router;

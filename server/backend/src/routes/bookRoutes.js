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
import uploadProductImages from "../middleware/uploadProductImages.js";

const router = express.Router();

// Route để lấy danh sách sách với thông tin cơ bản
router.get("/", async (req, res) => {
  try {
    const books = await Book.find().select("id name averageRating");
    res.status(200).json(books);
  } catch (error) {
    res.status(500).json({ error: "Lỗi khi lấy dữ liệu sách" });
  }
});
// Route để lấy danh sách sách
router.get("/all-book", getBooks);
// Route để lấy chi tiết sách theo ID
router.get("/book-detail/:id", getBookDetail);
// Route để lấy đánh giá sách theo ID sách
router.get("/book-detail/:bookId/reviews", getBookReviews);
// Route tìm kiếm sách theo tên
router.get("/search", searchRateLimiter, handleValidationErrors, searchBooks);
// Route tìm kiếm sách theo tên (dùng cho admin)
router.get("/best-sellers", getBestSellers);
// Route lấy sách mới nhất
router.get("/new-books", getNewBooks);
// Route lấy ID sách tiếp theo
router.get("/peek-next-id", peekNextBookIdApi);
// Route để lấy sách theo tên (dùng cho admin)
router.post("/add-book", createBook);
// Route để cập nhật thông tin sách
router.put("/update-book/:id", updateBook);
// Route để xóa sách
router.delete("/delete-book/:id", deleteBook);
// Route tìm kiếm sách theo tên (dùng cho admin)
router.get("/search", searchNameBooks);
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

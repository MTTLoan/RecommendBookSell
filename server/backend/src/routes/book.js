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

// Lấy danh sách sách
router.get("/", user_jwt, getBooks);
// Lấy chi tiết sách
router.get("/:id", getBookDetail);
// Lấy review của sách
router.get("/:id/reviews", getBookReviews);
// Tìm kiếm sách
router.get("/search", searchRateLimiter, handleValidationErrors, searchBooks);
// Sách bán chạy
router.get("/best-sellers", getBestSellers);
// Sách mới
router.get("/new-books", getNewBooks);
// Lấy id tiếp theo
router.get("/peek-next-id", peekNextBookIdApi);
// Thêm sách
router.post("/", user_jwt, createBook);
// Sửa sách
router.put("/:id", user_jwt, updateBook);
// Xóa sách
router.delete("/:id", user_jwt, deleteBook);
// Tìm kiếm ở admin
router.get("/admin/search", user_jwt, searchNameBooks);
// Upload nhiều ảnh sản phẩm
router.post("/upload-image", user_jwt, uploadProductImages, (req, res) => {
  if (!req.files || req.files.length === 0) {
    return res
      .status(400)
      .json({ success: false, msg: "Không có file ảnh nào được upload." });
  }
  // Trả về mảng url ảnh
  const images = req.files.map((f) => f.location);
  if (images.length === 1) {
    return res.json({ success: true, image: images[0] });
  }
  return res.json({ success: true, images });
});

export default router;

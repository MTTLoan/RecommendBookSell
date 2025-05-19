import Book from '../models/Book.js';
import Review from '../models/Review.js';

export const getBookDetail = async (req, res) => {
  try {
    const bookId = parseInt(req.params.id);
    const book = await Book.findOne({ id: bookId });

    if (!book) {
      return res.status(404).json({
        success: false,
        msg: 'Không tìm thấy sách.',
      });
    }

    return res.status(200).json({
      success: true,
      msg: 'Lấy thông tin sách thành công.',
      book,
    });
  } catch (error) {
    console.error('Lỗi lấy thông tin sách:', error.message);
    return res.status(500).json({
      success: false,
      msg: `Lỗi server: ${error.message}`,
    });
  }
};

export const getBookReviews = async (req, res) => {
  try {
    const bookId = parseInt(req.params.bookId);
    const reviews = await Review.find({ bookId }).sort({ createdAt: -1 });
    return res.status(200).json({
      success: true,
      msg: 'Lấy danh sách đánh giá thành công.',
      reviews,
    });
  } catch (error) {
    console.error('Lỗi lấy danh sách đánh giá:', error.message);
    return res.status(500).json({
      success: false,
      msg: `Lỗi server: ${error.message}`,
    });
  }
};
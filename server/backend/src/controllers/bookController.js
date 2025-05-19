import Book from '../models/Book.js';
import Review from '../models/Review.js';
import User from '../models/User.js'; // Import User model

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
    const reviews = await Review.aggregate([
      { $match: { bookId } },
      {
        $lookup: {
          from: 'users', // Collection name in MongoDB (lowercase, plural)
          localField: 'userId',
          foreignField: 'id',
          as: 'user',
        },
      },
      { $unwind: { path: '$user', preserveNullAndEmptyArrays: true } }, // Keep reviews even if no user found
      {
        $project: {
          id: 1,
          userId: 1,
          bookId: 1,
          rating: 1,
          comment: 1,
          createdAt: 1,
          username: '$user.username', // Add username from user.name
        },
      },
      { $sort: { createdAt: -1 } },
    ]);

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
import mongoose from "mongoose";
import Review from "../models/Review.js";
import Book from "../models/Book.js";
import User from "../models/User.js";

export const submitReview = async (req, res) => {
  try {
    const userId = req.user.id;
    const { bookId, rating, comment, orderId } = req.body;

    // Kiểm tra dữ liệu đầu vào
    if (!bookId || !rating || rating < 1 || rating > 5 || !orderId) {
      console.log("Invalid input data");
      return res.status(400).json({
        success: false,
        message: "Dữ liệu không hợp lệ: Thiếu bookId, rating, hoặc orderId",
      });
    }

    // Kiểm tra xem đã có review cho bookId và orderId chưa
    const existingReview = await Review.findOne({ userId, bookId, orderId });
    if (existingReview) {
      return res.status(400).json({
        success: false,
        message: "Bạn đã đánh giá sách này cho đơn hàng này",
      });
    }

    // Tăng seq và gán id
    const counterResult = await mongoose.connection.db
      .collection("counters")
      .findOneAndUpdate(
        { _id: "reviewId" },
        { $inc: { seq: 1 } },
        { returnDocument: "after", upsert: true }
      );

    // Kiểm tra kết quả
    if (!counterResult || typeof counterResult.seq !== "number") {
      throw new Error("Failed to retrieve or increment counter for reviewId");
    }

    const reviewId = counterResult.seq;

    // Tạo review mới
    const review = new Review({
      id: reviewId,
      userId,
      bookId,
      orderId,
      rating,
      comment,
      createdAt: new Date(),
    });

    console.log("Saving review:", review);
    await review.save();

    // Cập nhật averageRating và ratingCount của Book
    const book = await Book.findOne({ id: bookId });
    if (!book) {
      console.log("Book not found for id:", bookId);
      return res.status(404).json({
        success: false,
        message: "Không tìm thấy sách",
      });
    }

    const newRatingCount = (book.ratingCount || 0) + 1;
    const newAverageRating =
      ((book.averageRating || 0) * (newRatingCount - 1) + rating) /
      newRatingCount;

    await Book.updateOne(
      { id: bookId },
      {
        ratingCount: newRatingCount,
        averageRating: newAverageRating,
      }
    );
    console.log("Updated book:", {
      id: bookId,
      newRatingCount,
      newAverageRating,
    });

    res.status(201).json({
      success: true,
      data: review,
    });
  } catch (error) {
    console.error("Error submitting review:", error);
    res.status(500).json({
      success: false,
      message: "Lỗi máy chủ: " + error.message,
    });
  }
};

// API lấy thống kê review cho nhiều sách
export const getReviewStatsForBooks = async (req, res) => {
  try {
    const { bookIds } = req.body;
    if (!Array.isArray(bookIds) || bookIds.length === 0)
      return res.json({ stats: [] });

    // Lấy số lượt đánh giá và điểm trung bình cho từng sách
    const stats = await Review.aggregate([
      { $match: { bookId: { $in: bookIds } } },
      {
        $group: {
          _id: "$bookId",
          reviewCount: { $sum: 1 },
          avgRating: { $avg: "$rating" },
        },
      },
      {
        $project: {
          bookId: "$_id",
          reviewCount: 1,
          avgRating: { $ifNull: ["$avgRating", 0] },
        },
      },
    ]);
    res.json({ stats });
  } catch (error) {
    res.status(500).json({ stats: [] });
  }
};

export const getReviewsByBookId = async (req, res) => {
  try {
    const bookId = req.params.bookId;
    // Lấy tất cả review của sách
    const reviews = await Review.find({ bookId });

    // Lấy tất cả userId duy nhất từ review
    const userIds = [...new Set(reviews.map((r) => r.userId))];

    // Lấy thông tin user tương ứng
    const users = await User.find(
      { id: { $in: userIds } },
      { id: 1, username: 1 }
    );

    // Map userId -> username
    const userMap = {};
    users.forEach((u) => {
      userMap[u.id] = u.username;
    });

    // Gắn username vào từng review
    const mapped = reviews.map((r) => ({
      ...r._doc,
      userName: userMap[r.userId] || "Ẩn danh",
    }));

    res.json({ reviews: mapped });
  } catch (error) {
    res.status(500).json({ reviews: [] });
  }
};

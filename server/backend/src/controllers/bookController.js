import Book from "../models/Book.js";
import Review from "../models/Review.js";

export const getBooks = async (req, res) => {
  try {
    const categoryId = req.query.categoryId
      ? parseInt(req.query.categoryId)
      : null;
    let books;

    if (categoryId) {
      books = await Book.find({ categoryId }); // Lọc sách theo categoryId
    } else {
      books = await Book.find(); // Lấy tất cả sách nếu không có categoryId
    }

    return res.status(200).json({
      success: true,
      msg: "Lấy danh sách sách thành công.",
      book: books,
    });
  } catch (error) {
    console.error("Lỗi lấy danh sách sách:", error.message);
    return res.status(500).json({
      success: false,
      msg: `Lỗi server: ${error.message}`,
    });
  }
};

// Lấy chi tiết sách theo ID
export const getBookDetail = async (req, res) => {
  try {
    const bookId = parseInt(req.params.id);
    const book = await Book.findOne({ id: bookId });

    if (!book) {
      return res.status(404).json({
        success: false,
        msg: "Không tìm thấy sách.",
      });
    }

    return res.status(200).json({
      success: true,
      msg: "Lấy thông tin sách thành công.",
      book,
    });
  } catch (error) {
    console.error("Lỗi lấy thông tin sách:", error.message);
    return res.status(500).json({
      success: false,
      msg: `Lỗi server: ${error.message}`,
    });
  }
};

// Lấy danh sách đánh giá của một cuốn sách
export const getBookReviews = async (req, res) => {
  try {
    const bookId = parseInt(req.params.bookId);
    const reviews = await Review.aggregate([
      { $match: { bookId } },
      {
        $lookup: {
          from: "users", // Collection name in MongoDB (lowercase, plural)
          localField: "userId",
          foreignField: "id",
          as: "user",
        },
      },
      { $unwind: { path: "$user", preserveNullAndEmptyArrays: true } }, // Keep reviews even if no user found
      {
        $project: {
          id: 1,
          userId: 1,
          bookId: 1,
          rating: 1,
          comment: 1,
          createdAt: 1,
          username: "$user.username", // Add username from user.name
        },
      },
      { $sort: { createdAt: -1 } },
    ]);

    return res.status(200).json({
      success: true,
      msg: "Lấy danh sách đánh giá thành công.",
      reviews,
    });
  } catch (error) {
    console.error("Lỗi lấy danh sách đánh giá:", error.message);
    return res.status(500).json({
      success: false,
      msg: `Lỗi server: ${error.message}`,
    });
  }
};

// Search books controller
export const searchBooks = async (req, res) => {
  try {
    const query = req.query.query || ""; // Default to empty string if not provided
    const categoryId = req.query.categoryId
      ? parseInt(req.query.categoryId)
      : null;
    const minPrice = req.query.minPrice ? parseFloat(req.query.minPrice) : 0;
    const maxPrice = req.query.maxPrice
      ? parseFloat(req.query.maxPrice)
      : Number.MAX_SAFE_INTEGER;

    // Log incoming parameters
    console.log("SearchBooks - Query:", query);
    console.log("SearchBooks - CategoryId:", categoryId);
    console.log("SearchBooks - Price Range:", minPrice, "to", maxPrice);

    // Build the search query
    const searchCriteria = {};

    // Query filter
    if (query.trim()) {
      searchCriteria.$or = [
        { name: { $regex: query.trim(), $options: "i" } },
        { author: { $regex: query.trim(), $options: "i" } },
      ];
    }

    // Category filter
    if (categoryId !== null) {
      searchCriteria.categoryId = categoryId;
    }

    // Price filter
    if (minPrice !== 0 || maxPrice !== Number.MAX_SAFE_INTEGER) {
      searchCriteria.price = { $gte: minPrice, $lte: maxPrice };
    }

    const books = await Book.find(searchCriteria);

    // Log the number of books found
    console.log("SearchBooks - Found:", books.length, "books");

    return res.status(200).json({
      success: true,
      msg:
        books.length > 0
          ? "Tìm kiếm sách thành công."
          : "Không tìm thấy sách phù hợp.",
      books: books,
    });
  } catch (error) {
    console.error("Lỗi tìm kiếm sách:", error.message);
    return res.status(500).json({
      success: false,
      msg: `Lỗi server: ${error.message}`,
    });
  }
};

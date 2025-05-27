import Book from "../models/Book.js";
import Review from "../models/Review.js";
import Counter from "../models/Counter.js";
import Order from "../models/Order.js";

// Hàm lấy id tự tăng
async function getNextBookId() {
  const counter = await Counter.findByIdAndUpdate(
    { _id: "bookId" },
    { $inc: { seq: 1 } },
    { new: true, upsert: true }
  );
  return counter.seq;
}

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

// Lấy danh sách sách bán chạy nhất (the bestsellers)
export const getBestSellers = async (req, res) => {
  try {
    // Tính thời gian cho tháng trước
    const currentDate = new Date(); 
    const startOfLastMonth = new Date(
      currentDate.getFullYear(),
      currentDate.getMonth() - 1,
      1
    ); 
    const endOfLastMonth = new Date(
      currentDate.getFullYear(),
      currentDate.getMonth(),
      0
    );

    // Aggregate orders to calculate total quantity sold per book for last month
    const bestSellers = await Order.aggregate([
      {
        $match: {
          status: "Đã giao",
          // Lọc đơn hàng trong khoảng thời gian
          orderDate: {
            $gte: startOfLastMonth,
            $lte: endOfLastMonth,
          },
        },
      },
      {
        $unwind: "$items",
      },
      {
        $group: {
          _id: "$items.bookId",
          totalQuantitySold: { $sum: "$items.quantity" },
        },
      },
      {
        $sort: {
          totalQuantitySold: -1,
        },
      },
      {
        $limit: 20,
      },
      // Lookup to join with the books collection
      {
        $lookup: {
          from: "books", // Tên của collection books
          localField: "_id",
          foreignField: "id",
          as: "bookDetails",
        },
      },
      {
        $unwind: "$bookDetails",
      },
      // Project các trường cần thiết
      {
        $project: {
          _id: 0,
          id: "$bookDetails.id",
          name: "$bookDetails.name",
          price: "$bookDetails.price",
          images: "$bookDetails.images",
          categoryId: "$bookDetails.categoryId",
          totalQuantitySold: 1,
        },
      },
    ]);

    // Kiểm tra xem có sách bán chạy nào được tìm thấy không
    if (!bestSellers || bestSellers.length === 0) {
      return res.status(200).json({
        success: true,
        msg: "Không tìm thấy sách bán chạy trong tháng trước.",
        book: [],
      });
    }

    // Trả về kết quả
    res.status(200).json({
      success: true,
      msg: "Lấy danh sách sách bán chạy tháng trước thành công.",
      book: bestSellers,
    });
  } catch (error) {
    console.error("Error fetching best sellers:", error);
    res.status(500).json({
      success: false,
      msg: "Lỗi server khi lấy danh sách sách bán chạy.",
      error: error.message,
    });
  }
};

export const getNewBooks = async (req, res) => {
  try {
    const currentMonth = new Date().getMonth() + 1; // 5 cho tháng 5
    const currentYear = new Date().getFullYear(); // 2025
    const books = await Book.find({
      createdAt: {
        $gte: new Date(`${currentYear}-${currentMonth}-01`),
        $lt: new Date(`${currentYear}-${currentMonth + 1}-01`),
      },
    })
      .select(
        "id name description images price stockQuantity categoryId createdAt author averageRating ratingCount"
      )
      .sort({ createdAt: -1 }) // Sắp xếp theo thời gian giảm dần
      .limit(20); // Giới hạn 20 sách mới nhất

    if (!books || books.length === 0) {
      return res.status(200).json({
        success: true,
        msg: "Không tìm thấy sách mới trong tháng này.",
        book: [],
      });
    }

    res.status(200).json({
      success: true,
      msg: "Lấy danh sách sách mới nhất thành công.",
      book: books,
    });
  } catch (error) {
    console.error("Error fetching new books:", error);
    res.status(500).json({
      success: false,
      msg: "Lỗi server khi lấy danh sách sách mới.",
      error: error.message,
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
      book: books,
    });
  } catch (error) {
    console.error("Lỗi tìm kiếm sách:", error.message);
    return res.status(500).json({
      success: false,
      msg: `Lỗi server: ${error.message}`,
    });
  }
};

// Hàm chỉ xem số hiện tại, không tăng
async function peekNextBookId() {
  const counter = await Counter.findById("bookId");
  return counter ? counter.seq + 1 : 1;
}

// API cho frontend xem trước mã tiếp theo (KHÔNG tăng)
export const peekNextBookIdApi = async (req, res) => {
  try {
    const nextId = await peekNextBookId();
    res.json({ id: nextId });
  } catch (error) {
    res.status(500).json({ error: "Không lấy được mã sách mới" });
  }
};

// Thêm sách mới
export const createBook = async (req, res) => {
  try {
    const {
      name,
      description,
      price,
      stockQuantity,
      images,
      categoryId,
      authors, // frontend gửi authors
      author, // phòng trường hợp gửi author
    } = req.body;
    const id = await getNextBookId(); // Lấy id tự tăng
    // Đảm bảo luôn lưu vào trường 'author' (schema)
    const authorArr = Array.isArray(authors)
      ? authors
      : Array.isArray(author)
      ? author
      : [];
    const newBook = new Book({
      id,
      name,
      description,
      price,
      stockQuantity,
      images,
      categoryId,
      author: authorArr, // luôn lưu vào trường 'author'
      createdAt: new Date(),
    });
    await newBook.save();
    return res.status(201).json({
      success: true,
      msg: "Thêm sách thành công.",
      book: newBook,
    });
  } catch (error) {
    console.error("Lỗi thêm sách:", error.message);
    return res.status(500).json({
      success: false,
      msg: `Lỗi server: ${error.message}`,
    });
  }
};

// Sửa thông tin sách
export const updateBook = async (req, res) => {
  try {
    const bookId = parseInt(req.params.id);
    const updateData = { ...req.body };
    // Đồng bộ trường 'author' khi update
    if (updateData.authors) {
      updateData.author = Array.isArray(updateData.authors)
        ? updateData.authors
        : [];
      delete updateData.authors;
    }
    const updatedBook = await Book.findOneAndUpdate(
      { id: bookId },
      updateData,
      { new: true }
    );
    if (!updatedBook) {
      return res.status(404).json({
        success: false,
        msg: "Không tìm thấy sách để cập nhật.",
      });
    }
    return res.status(200).json({
      success: true,
      msg: "Cập nhật sách thành công.",
      book: updatedBook,
    });
  } catch (error) {
    console.error("Lỗi cập nhật sách:", error.message);
    return res.status(500).json({
      success: false,
      msg: `Lỗi server: ${error.message}`,
    });
  }
};

// Xóa sách
export const deleteBook = async (req, res) => {
  try {
    const bookId = parseInt(req.params.id);
    const deletedBook = await Book.findOneAndDelete({ id: bookId });
    if (!deletedBook) {
      return res.status(404).json({
        success: false,
        msg: "Không tìm thấy sách để xóa.",
      });
    }
    return res.status(200).json({
      success: true,
      msg: "Xóa sách thành công.",
      book: deletedBook,
    });
  } catch (error) {
    console.error("Lỗi xóa sách:", error.message);
    return res.status(500).json({
      success: false,
      msg: `Lỗi server: ${error.message}`,
    });
  }
};

export const searchNameBooks = async (req, res) => {
  try {
    const { q } = req.query;
    if (!q) {
      return res
        .status(400)
        .json({ success: false, msg: "Thiếu từ khóa tìm kiếm." });
    }
    // Chỉ tìm theo tên (name), không tìm theo id
    const query = {
      name: { $regex: q, $options: "i" },
    };

    const books = await Book.find(query);
    res.json({ success: true, books });
  } catch (error) {
    console.error("Lỗi tìm kiếm sách:", error);
    res
      .status(500)
      .json({ success: false, msg: "Lỗi server khi tìm kiếm sản phẩm." });
  }
};

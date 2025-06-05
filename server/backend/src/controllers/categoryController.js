import Category from "../models/Category.js";
import Counter from "../models/Counter.js";
import Book from "../models/Book.js";
import { deleteS3File } from "../middleware/uploadToS3.js";

// Lấy thống kê số lượng sách theo danh mục
export const getCategoryStats = async (req, res) => {
  try {
    console.log("Request received:", req.url, req.query);
    console.log("Full req.query:", req.query);
    const categoryId = req.query.categoryId
      ? parseInt(req.query.categoryId)
      : null;
    console.log("Raw categoryId from query:", req.query.categoryId);
    console.log("Parsed categoryId:", categoryId);

    if (categoryId && isNaN(categoryId)) {
      return res.status(400).json({
        success: false,
        msg: "ID danh mục không hợp lệ. Vui lòng cung cấp một số hợp lệ.",
      });
    }

    const matchStage = categoryId ? { categoryId: categoryId } : {};
    const stats = await Book.aggregate([
      {
        $match: matchStage,
      },
      {
        $group: {
          _id: "$categoryId",
          quantity: { $sum: 1 },
        },
      },
      {
        $project: {
          _id: { $toInt: "$_id" },
          quantity: 1,
        },
      },
    ]);

    if (categoryId && stats.length === 0) {
      const categoryExists = await Category.findOne({ id: categoryId });
      if (!categoryExists) {
        return res.status(404).json({
          success: false,
          msg: "Không tìm thấy danh mục với ID này.",
        });
      }
      return res.status(200).json({
        success: true,
        msg: "Lấy thống kê danh mục thành công.",
        stats: [{ _id: categoryId, quantity: 0 }],
      });
    }

    return res.status(200).json({
      success: true,
      msg: "Lấy thống kê danh mục thành công.",
      stats,
    });
  } catch (error) {
    console.error("Lỗi lấy thống kê danh mục:", error.message);
    return res.status(500).json({
      success: false,
      msg: `Lỗi server: ${error.message}`,
    });
  }
};

// Hàm lấy id tự tăng
async function getNextCategoryId() {
  const counter = await Counter.findByIdAndUpdate(
    { _id: "categoryId" },
    { $inc: { seq: 1 } },
    { new: true, upsert: true }
  );
  return counter.seq;
}

// Lấy danh sách tất cả các danh mục
export const getCategories = async (req, res) => {
  try {
    const categories = await Category.find();
    return res.status(200).json({
      success: true,
      msg: "Lấy danh sách danh mục thành công.",
      categories,
    });
  } catch (error) {
    console.error("Lỗi lấy danh sách danh mục:", error.message);
    return res.status(500).json({
      success: false,
      msg: `Lỗi server: ${error.message}`,
    });
  }
};

// Lấy chi tiết một danh mục theo ID
export const getCategoryById = async (req, res) => {
  try {
    const categoryId = parseInt(req.params.id);

    // Kiểm tra nếu categoryId là NaN hoặc không phải số
    if (isNaN(categoryId)) {
      return res.status(400).json({
        success: false,
        msg: "ID danh mục không hợp lệ. Vui lòng cung cấp một số hợp lệ.",
      });
    }

    const category = await Category.findOne({ id: categoryId });

    if (!category) {
      return res.status(404).json({
        success: false,
        msg: "Không tìm thấy danh mục.",
      });
    }

    return res.status(200).json({
      success: true,
      msg: "Lấy thông tin danh mục thành công.",
      category,
    });
  } catch (error) {
    console.error("Lỗi lấy thông tin danh mục:", error.message);
    return res.status(500).json({
      success: false,
      msg: `Lỗi server: ${error.message}`,
    });
  }
};

// Thêm danh mục mới
export const createCategory = async (req, res) => {
  try {
    const { name, description } = req.body;

    // Kiểm tra dữ liệu đầu vào
    if (!name || name.trim() === "") {
      return res.status(400).json({
        success: false,
        msg: "Tên danh mục không được để trống.",
      });
    }

    const id = await getNextCategoryId();

    // Lấy imageUrl từ file upload hoặc body
    let imageUrl = "";
    if (req.file && req.file.location) {
      imageUrl = req.file.location;
      console.log("Image uploaded to:", imageUrl);
    }

    const newCategory = new Category({
      id,
      name: name.trim(),
      description: description ? description.trim() : "",
      imageUrl,
    });

    console.log("Creating category:", newCategory);
    await newCategory.save();

    return res.status(201).json({
      success: true,
      msg: "Thêm danh mục thành công.",
      category: newCategory,
    });
  } catch (error) {
    console.error("Lỗi thêm danh mục:", error);
    console.error("Error stack:", error.stack);
    return res.status(500).json({
      success: false,
      msg: `Lỗi server: ${error.message}`,
    });
  }
};

// Sửa danh mục
export const updateCategory = async (req, res) => {
  try {
    const categoryId = parseInt(req.params.id);
    const { name, description, removeImage } = req.body;

    // Lấy category cũ để kiểm tra ảnh cũ
    const oldCategory = await Category.findOne({ id: categoryId });
    if (!oldCategory) {
      return res.status(404).json({
        success: false,
        msg: "Không tìm thấy danh mục để cập nhật.",
      });
    }

    const updateData = { name, description };

    // Nếu có yêu cầu xóa ảnh
    if (removeImage === "true" && oldCategory.imageUrl) {
      await deleteS3File(oldCategory.imageUrl);
      updateData.imageUrl = "";
    }
    // Nếu có upload file mới
    else if (req.file && req.file.location) {
      // Xóa ảnh cũ nếu có
      if (oldCategory.imageUrl) {
        await deleteS3File(oldCategory.imageUrl);
      }
      updateData.imageUrl = req.file.location;
    }
    // Nếu không có thay đổi về ảnh, giữ nguyên
    else if (!removeImage) {
      updateData.imageUrl = oldCategory.imageUrl;
    }

    const updatedCategory = await Category.findOneAndUpdate(
      { id: categoryId },
      updateData,
      { new: true }
    );

    if (!updatedCategory) {
      return res.status(404).json({
        success: false,
        msg: "Không tìm thấy danh mục để cập nhật.",
      });
    }

    return res.status(200).json({
      success: true,
      msg: "Cập nhật danh mục thành công.",
      category: updatedCategory,
    });
  } catch (error) {
    console.error("Lỗi cập nhật danh mục:", error.message);
    return res.status(500).json({
      success: false,
      msg: `Lỗi server: ${error.message}`,
    });
  }
};

// Xóa danh mục
export const deleteCategory = async (req, res) => {
  try {
    const categoryId = parseInt(req.params.id);
    const deletedCategory = await Category.findOneAndDelete({ id: categoryId });

    if (!deletedCategory) {
      return res.status(404).json({
        success: false,
        msg: "Không tìm thấy danh mục để xóa.",
      });
    }

    // Xóa hình ảnh khỏi S3 nếu có
    if (deletedCategory.imageUrl) {
      await deleteS3File(deletedCategory.imageUrl);
    }

    return res.status(200).json({
      success: true,
      msg: "Xóa danh mục thành công.",
      category: deletedCategory,
    });
  } catch (error) {
    console.error("Lỗi xóa danh mục:", error.message);
    return res.status(500).json({
      success: false,
      msg: `Lỗi server: ${error.message}`,
    });
  }
};

// Tìm kiếm danh mục theo từ khóa
export const searchCategories = async (req, res) => {
  try {
    const { q } = req.query;
    if (!q) {
      return res.status(400).json({
        success: false,
        msg: "Thiếu từ khóa tìm kiếm.",
      });
    }
    const categories = await Category.find({
      name: { $regex: q, $options: "i" },
    });
    return res.status(200).json({
      success: true,
      msg: "Tìm kiếm danh mục thành công.",
      categories,
    });
  } catch (error) {
    console.error("Lỗi tìm kiếm danh mục:", error.message);
    return res.status(500).json({
      success: false,
      msg: `Lỗi server: ${error.message}`,
    });
  }
};

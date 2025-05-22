import Category from '../models/Category.js';
import Counter from '../models/Counter.js';
import Book from '../models/Book.js';

export const getCategoryStats = async (req, res) => {
  try {
    const stats = await Book.aggregate([
      {
        $group: {
          _id: "$categoryId",
          quantity: { $sum: 1 }
        }
      }
    ]);
    res.json({ stats });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};

// Hàm lấy id tự tăng
async function getNextCategoryId() {
  const counter = await Counter.findByIdAndUpdate(
    { _id: 'categoryid' },
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
      msg: 'Lấy danh sách danh mục thành công.',
      categories,
    });
  } catch (error) {
    console.error('Lỗi lấy danh sách danh mục:', error.message);
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
    const category = await Category.findOne({ id: categoryId });

    if (!category) {
      return res.status(404).json({
        success: false,
        msg: 'Không tìm thấy danh mục.',
      });
    }

    return res.status(200).json({
      success: true,
      msg: 'Lấy thông tin danh mục thành công.',
      category,
    });
  } catch (error) {
    console.error('Lỗi lấy thông tin danh mục:', error.message);
    return res.status(500).json({
      success: false,
      msg: `Lỗi server: ${error.message}`,
    });
  }
};

// Thêm danh mục mới
export const createCategory = async (req, res) => {
  try {
    const { name, description, imageUrl } = req.body;
    const id = await getNextCategoryId();
    const newCategory = new Category({
      id,
      name,
      description,
      imageUrl,
    });
    await newCategory.save();
    return res.status(201).json({
      success: true,
      msg: 'Thêm danh mục thành công.',
      category: newCategory,
    });
  } catch (error) {
    console.error('Lỗi thêm danh mục:', error.message);
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
    const updateData = req.body;
    const updatedCategory = await Category.findOneAndUpdate({ id: categoryId }, updateData, { new: true });
    if (!updatedCategory) {
      return res.status(404).json({
        success: false,
        msg: 'Không tìm thấy danh mục để cập nhật.',
      });
    }
    return res.status(200).json({
      success: true,
      msg: 'Cập nhật danh mục thành công.',
      category: updatedCategory,
    });
  } catch (error) {
    console.error('Lỗi cập nhật danh mục:', error.message);
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
        msg: 'Không tìm thấy danh mục để xóa.',
      });
    }
    return res.status(200).json({
      success: true,
      msg: 'Xóa danh mục thành công.',
      category: deletedCategory,
    });
  } catch (error) {
    console.error('Lỗi xóa danh mục:', error.message);
    return res.status(500).json({
      success: false,
      msg: `Lỗi server: ${error.message}`,
    });
  }
};
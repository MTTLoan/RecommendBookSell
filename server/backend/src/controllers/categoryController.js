import Category from '../models/Category.js';

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
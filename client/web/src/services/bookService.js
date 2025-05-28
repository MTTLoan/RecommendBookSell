import axios from "axios";
import { API_BASE_URL } from "../utils/constants";

// Helper để lấy headers nếu có token
const getAuthHeaders = () => {
  const token = localStorage.getItem("token");
  return token ? { Authorization: `Bearer ${token}` } : {};
};

export const searchNameBooks = async (query) => {
  try {
    const res = await axios.get(
      `${API_BASE_URL}/books/search?query=${encodeURIComponent(query)}`,
      { headers: getAuthHeaders() }
    );
    return res.data.book || res.data.books || [];
  } catch (error) {
    console.error(
      "Lỗi searchNameBooks:",
      error.response?.data || error.message
    );
    throw error;
  }
};

export const fetchBooks = async (categoryId) => {
  try {
    const params = categoryId ? { categoryId } : {};
    const res = await axios.get(`${API_BASE_URL}/books`, {
      headers: getAuthHeaders(),
      params,
    });
    return res.data.book || res.data.books || res.data;
  } catch (error) {
    console.error("Lỗi fetchBooks:", error.response?.data || error.message);
    throw error;
  }
};

export const fetchBookDetail = async (id) => {
  try {
    const res = await axios.get(`${API_BASE_URL}/books/${Number(id)}`, {
      headers: getAuthHeaders(),
    });
    return res.data.book || res.data;
  } catch (error) {
    console.error(
      "Lỗi fetchBookDetail:",
      error.response?.data || error.message
    );
    throw error;
  }
};

export const addBook = async (bookData) => {
  const token = localStorage.getItem("token");
  const headers = token ? { Authorization: `Bearer ${token}` } : {};
  const res = await axios.post(`${API_BASE_URL}/books/add-book`, bookData, {
    headers,
  });
  return res.data.book;
};

export const fetchNextBookId = async () => {
  const res = await axios.get(`${API_BASE_URL}/books/next-id`);
  return res.data;
};

export const peekNextBookId = async () => {
  const res = await axios.get(`${API_BASE_URL}/books/peek-next-id`);
  return res.data;
};

export const updateBook = async (id, bookData) => {
  const token = localStorage.getItem("token");
  const headers = token ? { Authorization: `Bearer ${token}` } : {};
  const res = await axios.put(
    `${API_BASE_URL}/books/update-book/${id}`,
    bookData,
    { headers }
  );
  return res.data.book;
};

export const deleteBook = async (id) => {
  const token = localStorage.getItem("token");
  const headers = token ? { Authorization: `Bearer ${token}` } : {};
  const res = await axios.delete(`${API_BASE_URL}/books/delete-book/${id}`, {
    headers,
  });
  return res.data;
};

// Upload ảnh sản phẩm lên S3 (dùng chung S3, backend sẽ không gán vào user)
export const uploadProductImage = async (formData) => {
  try {
    const response = await axios.post(
      `${API_BASE_URL}/books/upload-image`,
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
          ...getAuthHeaders(),
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Upload product image error:", error);
    if (error.response) {
      throw new Error(error.response.data.msg || "Tải ảnh sản phẩm thất bại");
    } else if (error.request) {
      throw new Error(
        "Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng."
      );
    } else {
      throw new Error("Đã xảy ra lỗi. Vui lòng thử lại.");
    }
  }
};

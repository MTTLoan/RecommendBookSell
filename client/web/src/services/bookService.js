import axios from "axios";
import { API_BASE_URL } from "../utils/constants";

const API_URL = "http://52.5.208.132:5000/api/books/all-book";

// Tạo instance axios riêng cho bookService nếu cần, hoặc dùng axios trực tiếp
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});

export const searchNameBooks = async (query) => {
  const res = await axios.get(
    `${API_BASE_URL}/books/search?q=${encodeURIComponent(query)}`
  );
  return res.data.books || [];
};

export const fetchBooks = async () => {
  const res = await axios.get(API_URL);
  return res.data.book;
};

export const fetchBookDetail = async (id) => {
  const res = await axios.get(`${API_BASE_URL}/books/book-detail/${id}`);
  return res.data.book;
};

export const addBook = async (bookData) => {
  const res = await axios.post(`${API_BASE_URL}/books/add-book`, bookData);
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
  const res = await axios.put(
    `${API_BASE_URL}/books/update-book/${id}`,
    bookData
  );
  return res.data.book;
};

export const deleteBook = async (id) => {
  const res = await axios.delete(`${API_BASE_URL}/books/delete-book/${id}`);
  return res.data;
};

// Upload ảnh sản phẩm lên S3 (dùng chung S3, backend sẽ không gán vào user)
export const uploadProductImage = async (formData) => {
  try {
    const response = await api.post("/books/upload-image", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
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

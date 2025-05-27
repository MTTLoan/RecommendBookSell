import axios from "axios";
import { API_BASE_URL } from "../utils/constants";

// Hàm lấy dữ liệu thống kê từ API với các bộ lọc
export const fetchDashboardStats = async (category, month, year) => {
  try {
    // Tạo query params
    const params = {};
    if (category) params.category = category; // Chỉ gửi nếu category không rỗng
    if (month !== null && month !== undefined) params.month = month; // Chỉ gửi nếu month không null
    if (year !== null && year !== undefined) params.year = year; // Chỉ gửi nếu năm không null

    console.log("Gửi yêu cầu API với params:", params);

    // Gửi yêu cầu GET với query params
    const response = await axios.get(`${API_BASE_URL}/dashboard/stats`, {
      params,
    });

    console.log("Dữ liệu từ API:", response.data);
    return response.data;
  } catch (error) {
    console.error("Lỗi khi gọi API:", error.message);
    throw error; // Ném lỗi để xử lý trong component
  }
};

// Hàm lấy dữ liệu doanh thu cho biểu đồ
export const fetchRevenueData = async (category, month, year) => {
  try {
    // Tạo query params
    const params = {};
    if (category) params.category = category; // Chỉ gửi nếu category không rỗng
    if (month !== null && month !== undefined) params.month = month; // Chỉ gửi nếu month không null
    if (year !== null && year !== undefined) params.year = year; // Chỉ gửi nếu năm không null

    console.log("Gửi yêu cầu API doanh thu với params:", params);

    // Gửi yêu cầu GET với query params
    const response = await axios.get(`${API_BASE_URL}/dashboard/revenue`, {
      params,
    });

    console.log("Dữ liệu doanh thu từ API:", response.data);
    return response.data;
  } catch (error) {
    console.error("Lỗi khi gọi API doanh thu:", error.message);
    throw error; // Ném lỗi để xử lý trong component
  }
};

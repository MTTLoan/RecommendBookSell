import axios from 'axios';
import { API_BASE_URL } from '../utils/constants';

// Tạo instance axios với cấu hình mặc định
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true // Thêm option này để gửi credentials
});

// Thêm interceptor để xử lý token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    console.error('Request error:', error);
    return Promise.reject(error);
  }
);

// Thêm interceptor để xử lý response
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error);
    if (error.response) {
      // Server trả về response với status code nằm ngoài range 2xx
      console.error('Error data:', error.response.data);
      console.error('Error status:', error.response.status);
      console.error('Error headers:', error.response.headers);
      
      if (error.response.status === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/auth/login';
      }
    } else if (error.request) {
      // Request được gửi nhưng không nhận được response
      console.error('No response received:', error.request);
    } else {
      // Có lỗi khi setting up request
      console.error('Error setting up request:', error.message);
    }
    return Promise.reject(error);
  }
);

// Hàm đăng nhập
export const login = async (credentials) => {
  try {
    console.log('Sending login request to:', `${API_BASE_URL}/auth/login`);
    console.log('Credentials:', credentials);
    
    const response = await api.post('/auth/login', credentials);
    console.log('Login response:', response.data);
    
    const { token, user } = response.data;
    
    // Lưu token vào localStorage
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
    
    return response.data;
  } catch (error) {
    console.error('Login error:', error);
    if (error.response) {
      throw new Error(error.response.data.message || 'Đăng nhập thất bại');
    } else if (error.request) {
      throw new Error('Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng.');
    } else {
      throw new Error('Đã xảy ra lỗi. Vui lòng thử lại.');
    }
  }
};

// Hàm đăng xuất
export const logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  window.location.href = '/auth/login';
};

// Hàm kiểm tra trạng thái đăng nhập
export const isAuthenticated = () => {
  return !!localStorage.getItem('token');
};

// Hàm quên mật khẩu
export const forgotPassword = async (email) => {
  console.log("Sending email to backend:", email); // Log email gửi đi
  console.log("API URL:", `${API_BASE_URL}/auth/forgot-password`); // Log URL API
  try {
    const response = await axios.post(`${API_BASE_URL}/auth/forgot-password`, { email });
    return response.data;
  } catch (error) {
    if (error.response) {
      throw new Error(error.response.data.message || 'Gửi yêu cầu thất bại');
    } else if (error.request) {
      throw new Error('Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng.');
    } else {
      throw new Error('Đã xảy ra lỗi. Vui lòng thử lại.');
    }
  }
};

// Hàm đặt lại mật khẩu
export const resetPassword = async (data) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/auth/reset-password`, data);
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || 'Đặt lại mật khẩu thất bại');
  }
};

// Hàm lấy thông tin người dùng hiện tại
export const getCurrentUser = () => {
  const user = localStorage.getItem('user');
  return user ? JSON.parse(user) : null;
}; 

export const fetchAllCustomers = async () => {
  const res = await api.get(`${API_BASE_URL}/auth/admin/users`);
  return res.data.users || [];
};
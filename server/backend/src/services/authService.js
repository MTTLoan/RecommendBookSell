// src/services/authService.js
import axios from 'axios';
import { API_BASE_URL } from '../utils/constants';

const AUTH_TOKEN_KEY = 'auth_token';

/**
 * Gửi request đăng nhập đến API
 * @param {Object} credentials - thông tin đăng nhập
 * @param {string} credentials.username - tên đăng nhập hoặc email
 * @param {string} credentials.password - mật khẩu
 * @returns {Promise<Object>} - thông tin người dùng và token
 */
export const login = async (credentials) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/auth/login`, credentials);
    const { token, user } = response.data;
    
    // Lưu token vào localStorage
    sessionStorage.setItem('token', token);
    sessionStorage.setItem('user', JSON.stringify(user));
    
    return response.data;
  } catch (error) {
    if (error.response) {
      // Lỗi từ phía server
      throw new Error(error.response.data.message || 'Đăng nhập thất bại');
    } else if (error.request) {
      // Không nhận được phản hồi từ server
      throw new Error('Không thể kết nối đến máy chủ. Vui lòng thử lại sau.');
    } else {
      // Lỗi khi thiết lập request
      throw new Error('Đã xảy ra lỗi. Vui lòng thử lại.');
    }
  }
};

/**
 * Kiểm tra người dùng đã đăng nhập hay chưa
 * @returns {boolean} - trạng thái đăng nhập
 */
export const isAuthenticated = () => {
  return !!sessionStorage.getItem('token');
};

/**
 * Đăng xuất khỏi hệ thống
 */
export const logout = () => {
  sessionStorage.removeItem('token');
  sessionStorage.removeItem('user');
  window.location.href = '/auth/login';
};

/**
 * Gửi yêu cầu quên mật khẩu
 * @param {Object} data - thông tin yêu cầu
 * @param {string} data.email - email người dùng
 * @returns {Promise<Object>} - thông báo từ server
 */
export const forgotPassword = async (data) => {
  try {
    console.log('Sending forgot password request to:', `${API_BASE_URL}/auth/forgot-password`);
    console.log('Data:', data);
    const response = await axios.post(`${API_BASE_URL}/auth/forgot-password`, data);
    return response.data;
  } catch (error) {
    if (error.response) {
      throw new Error(error.response.data.message || 'Yêu cầu khôi phục mật khẩu thất bại');
    } else if (error.request) {
      throw new Error('Không thể kết nối đến máy chủ. Vui lòng thử lại sau.');
    } else {
      throw new Error('Đã xảy ra lỗi. Vui lòng thử lại.');
    }
  }
};

/**
 * Đặt lại mật khẩu
 * @param {Object} data - thông tin đặt lại mật khẩu
 * @param {string} data.token - token xác thực
 * @param {string} data.password - mật khẩu mới
 * @returns {Promise<Object>} - thông báo từ server
 */
export const resetPassword = async (data) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/auth/reset-password`, data);
    return response.data;
  } catch (error) {
    if (error.response) {
      throw new Error(error.response.data.message || 'Đặt lại mật khẩu thất bại');
    } else if (error.request) {
      throw new Error('Không thể kết nối đến máy chủ. Vui lòng thử lại sau.');
    } else {
      throw new Error('Đã xảy ra lỗi. Vui lòng thử lại.');
    }
  }
};

/**
 * Cập nhật thông tin người dùng
 * @param {Object} userData - thông tin người dùng cần cập nhật
 * @returns {Promise<Object>} - thông tin người dùng đã cập nhật
 */
export const updateProfile = async (userData) => {
  try {
    const token = localStorage.getItem(AUTH_TOKEN_KEY);
    const response = await axios.put(`${API_BASE_URL}/users/profile`, userData, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
    return response.data;
  } catch (error) {
    if (error.response) {
      throw new Error(error.response.data.message || 'Cập nhật thông tin thất bại');
    } else if (error.request) {
      throw new Error('Không thể kết nối đến máy chủ. Vui lòng thử lại sau.');
    } else {
      throw new Error('Đã xảy ra lỗi. Vui lòng thử lại.');
    }
  }
};
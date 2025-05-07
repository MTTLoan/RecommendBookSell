// src/utils/validators.js
import { MIN_PASSWORD_LENGTH, EMAIL_REGEX } from './constants';

/**
 * Kiểm tra email hợp lệ
 * @param {string} email - Email cần kiểm tra
 * @returns {boolean} - true nếu email hợp lệ
 */
export const isValidEmail = (email) => {
  return EMAIL_REGEX.test(email);
};

/**
 * Kiểm tra mật khẩu hợp lệ
 * @param {string} password - Mật khẩu cần kiểm tra
 * @returns {object} - { isValid, message }
 */
export const validatePassword = (password) => {
  if (!password) {
    return { isValid: false, message: 'Mật khẩu không được để trống' };
  }

  if (password.length < MIN_PASSWORD_LENGTH) {
    return { 
      isValid: false, 
      message: `Mật khẩu phải có ít nhất ${MIN_PASSWORD_LENGTH} ký tự` 
    };
  }

  const hasUpperCase = /[A-Z]/.test(password);
  const hasLowerCase = /[a-z]/.test(password);
  const hasNumbers = /\d/.test(password);
  const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password);

  if (!hasUpperCase || !hasLowerCase || !hasNumbers || !hasSpecialChar) {
    return {
      isValid: false,
      message: 'Mật khẩu phải bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt'
    };
  }

  return { isValid: true, message: '' };
};

/**
 * Kiểm tra hai mật khẩu có khớp không
 * @param {string} password - Mật khẩu
 * @param {string} confirmPassword - Xác nhận mật khẩu
 * @returns {boolean} - true nếu hai mật khẩu khớp
 */
export const doPasswordsMatch = (password, confirmPassword) => {
  return password === confirmPassword;
};

/**
 * Kiểm tra tên người dùng hợp lệ
 * @param {string} username - Tên người dùng
 * @returns {object} - { isValid, message }
 */
export const validateUsername = (username) => {
  if (!username) {
    return { isValid: false, message: 'Tên đăng nhập không được để trống' };
  }

  if (username.length < 3) {
    return { isValid: false, message: 'Tên đăng nhập phải có ít nhất 3 ký tự' };
  }

  if (!/^[a-zA-Z0-9_]+$/.test(username)) {
    return { 
      isValid: false, 
      message: 'Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới' 
    };
  }

  return { isValid: true, message: '' };
};

/**
 * Kiểm tra số điện thoại hợp lệ
 * @param {string} phone - Số điện thoại
 * @returns {boolean} - true nếu số điện thoại hợp lệ
 */
export const isValidPhone = (phone) => {
  return /^(0|\+84)[3|5|7|8|9][0-9]{8}$/.test(phone);
};

/**
 * Kiểm tra trường bắt buộc
 * @param {string} value - Giá trị cần kiểm tra
 * @param {string} fieldName - Tên trường
 * @returns {object} - { isValid, message }
 */
export const validateRequired = (value, fieldName) => {
  if (!value) {
    return { 
      isValid: false, 
      message: `${fieldName} không được để trống` 
    };
  }
  return { isValid: true, message: '' };
};
// Độ dài tối thiểu của mật khẩu
export const MIN_PASSWORD_LENGTH = 8;

// Regex kiểm tra email hợp lệ
export const EMAIL_REGEX = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

// Các loại thông báo
export const NOTIFICATION_TYPES = {
  SUCCESS: "success",
  ERROR: "error",
  WARNING: "warning",
  INFO: "info",
};

// Trạng thái của đơn hàng
export const ORDER_STATUS = {
  PACKAGING: "packaging", // Đang đóng gói
  SHIPPING: "shipping", // Đang giao hàng
  DELIVERED: "delivered", // Đã giao
  RETURNED: "returned", // Trả hàng
  CANCELLED: "cancelled", // Đã hủy
};

// Phương thức thanh toán
export const PAYMENT_METHODS = {
  CASH: "cash",
  BANK_TRANSFER: "bank_transfer",
  CREDIT_CARD: "credit_card",
  MOMO: "momo",
  VNPAY: "vnpay",
};

// Vai trò người dùng
export const USER_ROLES = {
  ADMIN: "admin", // Quản trị viên
  USER: "user", // Người dùng thông thường
};

// Giới hạn phân trang
export const PAGINATION_LIMIT = {
  DEFAULT: 10,
  OPTIONS: [5, 10, 25, 50, 100],
};

// Hằng số thời gian
export const TIME_CONSTANTS = {
  MILLISECONDS_PER_SECOND: 1000,
  SECONDS_PER_MINUTE: 60,
  MINUTES_PER_HOUR: 60,
  HOURS_PER_DAY: 24,
  MONTHS_PER_YEAR: 12,
};

// URL API
export const API_BASE_URL =
  process.env.REACT_APP_API_URL || "http://localhost:5000/api";

// Đường dẫn chính
export const ROUTES = {
  HOME: "/",
  LOGIN: "/auth/login",
  FORGOT_PASSWORD: "/auth/forgot-password",
  RESET_PASSWORD: "/auth/reset-password",
  DASHBOARD: "/dashboard",
  PRODUCTS: "/products",
  CATEGORIES: "/categories",
  ORDERS: "/orders",
  CUSTOMERS: "/customers",
  SETTINGS: "/settings",
  PROFILE: "/settings/profile",
  SECURITY: "/settings/security",
  REPORTS: "/reports",
};

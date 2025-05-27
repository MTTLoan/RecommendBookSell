import axios from "axios";
import { API_BASE_URL } from "../utils/constants";

// Tạo instance axios với cấu hình mặc định
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // Thêm option này để gửi credentials
});

// Thêm interceptor để xử lý token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    console.error("Request error:", error);
    return Promise.reject(error);
  }
);

// Thêm interceptor để xử lý response
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error("API Error:", error);
    if (error.response) {
      // Server trả về response với status code nằm ngoài range 2xx
      console.error("Error data:", error.response.data);
      console.error("Error status:", error.response.status);
      console.error("Error headers:", error.response.headers);

      if (error.response.status === 401) {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        window.location.href = "/auth/login";
      }
    } else if (error.request) {
      // Request được gửi nhưng không nhận được response
      console.error("No response received:", error.request);
    } else {
      // Có lỗi khi setting up request
      console.error("Error setting up request:", error.message);
    }
    return Promise.reject(error);
  }
);
// Hàm đăng nhập
export const login = async (credentials) => {
  try {
    console.log("Sending login request to:", `${API_BASE_URL}/auth/login`);
    console.log("Credentials:", credentials);

    const response = await api.post("/auth/login", credentials);
    console.log("Login response:", response.data);

    const { token, user } = response.data;

    // Lưu token vào localStorage
    localStorage.setItem("token", token);
    localStorage.setItem("user", JSON.stringify(user));

    return response.data;
  } catch (error) {
    console.error("Login error:", error);
    if (error.response) {
      // Ưu tiên lấy msg, nếu không có thì lấy message
      const errMsg =
        error.response.data.msg ||
        error.response.data.message ||
        "Đăng nhập thất bại";
      throw new Error(errMsg);
    } else if (error.request) {
      throw new Error(
        "Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng."
      );
    } else {
      throw new Error("Đã xảy ra lỗi. Vui lòng thử lại.");
    }
  }
};

// Hàm đăng xuất
export const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  window.location.href = "/auth/login";
};

// Hàm kiểm tra trạng thái đăng nhập
export const isAuthenticated = () => {
  return !!localStorage.getItem("token");
};

// Hàm quên mật khẩu
export const forgotPassword = async (email) => {
  console.log("Sending email to backend:", email); // Log email gửi đi
  console.log("API URL:", `${API_BASE_URL}/forgot_password/forgot-password`); // Log URL API
  try {
    const response = await axios.post(
      `${API_BASE_URL}/forgot_password/forgot-password`,
      {
        email,
      }
    );
    return response.data;
  } catch (error) {
    if (error.response) {
      throw new Error(error.response.data.message || "Gửi yêu cầu thất bại");
    } else if (error.request) {
      throw new Error(
        "Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng."
      );
    } else {
      throw new Error("Đã xảy ra lỗi. Vui lòng thử lại.");
    }
  }
};

// Hàm đặt lại mật khẩu
export const resetPassword = async (data) => {
  try {
    // Đúng endpoint backend cho OTP: /forgot_password/reset-password
    const response = await axios.post(
      `${API_BASE_URL}/forgot_password/reset-password`,
      data
    );
    return response.data;
  } catch (error) {
    throw new Error(
      error.response?.data?.message || "Đặt lại mật khẩu thất bại"
    );
  }
};

// Hàm lấy thông tin người dùng hiện tại
export const getCurrentUser = () => {
  const user = localStorage.getItem("user");
  return user ? JSON.parse(user) : null;
};

export const peekNextUserId = async () => {
  const res = await api.get("/user/peek-next-id");
  return res.data;
};

export const fetchAllCustomers = async () => {
  try {
    const res = await api.get("/user/");
    if (!res.data.success || !Array.isArray(res.data.users)) {
      console.error("Dữ liệu khách hàng không hợp lệ:", res.data);
      return [];
    }
    // Trả về nguyên user object để bảng có đủ trường
    return res.data.users;
  } catch (error) {
    console.error(
      "Lỗi khi lấy danh sách khách hàng:",
      error.response?.data || error.message
    );
    return [];
  }
};

export const searchCustomers = async (query) => {
  const res = await api.get(`/user/search?q=${encodeURIComponent(query)}`);
  return res.data.users || [];
};

export const fetchCustomerDetail = async (id) => {
  const token = localStorage.getItem("token");
  const res = await axios.get(`${API_BASE_URL}/user/${id}`, {
    headers: token ? { Authorization: `Bearer ${token}` } : {},
    withCredentials: true,
  });

  return res.data.user || res.data;
};

export const adminAddUser = async (formData) => {
  const res = await api.post("/user", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return res.data;
};

export const adminUpdateUser = async (id, formData) => {
  const res = await api.put(`/user/${id}`, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return res.data;
};

export const fetchProvinces = async () => {
  try {
    const res = await axios.get("https://provinces.open-api.vn/api/p/");
    return res.data;
  } catch {
    return [];
  }
};

export const fetchDistricts = async (provinceCode) => {
  if (!provinceCode) return [];
  try {
    const res = await axios.get(
      `https://provinces.open-api.vn/api/p/${provinceCode}?depth=2`
    );
    return res.data.districts || [];
  } catch {
    return [];
  }
};

export const fetchWards = async (districtCode) => {
  if (!districtCode) return [];
  try {
    const res = await axios.get(
      `https://provinces.open-api.vn/api/d/${districtCode}?depth=2`
    );
    return res.data.wards || [];
  } catch {
    return [];
  }
};

export const adminDeleteUser = async (id) => {
  const res = await api.delete(`/user/${id}`);
  return res.data;
};

export const fetchReviewStatsForBooks = async (idList) => {
  if (!idList || idList.length === 0) return [];
  const res = await api.post("/review/stats", { bookIds: idList });
  return res.data.stats || [];
};

export const fetchAdminOrderById = async (id) => {
  const res = await api.get(`/orders/admin/${id}`);
  return res.data;
};

// Hàm lấy thông tin hồ sơ người dùng
export const getProfile = async () => {
  try {
    const response = await api.get("/auth/profile");
    return response.data;
  } catch (error) {
    console.error("Lỗi lấy user từ API:", error);
    return null;
  }
};

// Hàm cập nhật hồ sơ
export const updateProfileWithAvatar = async (data) => {
  try {
    const response = await api.put(
      "/auth/update-profile-with-avt",
      data /* no need to set headers here */
    );
    return response.data;
  } catch (error) {
    console.error("Update profile with avatar error:", error);
    if (error.response) {
      throw new Error(error.response.data.msg || "Cập nhật hồ sơ thất bại");
    } else if (error.request) {
      throw new Error(
        "Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng."
      );
    } else {
      throw new Error("Đã xảy ra lỗi. Vui lòng thử lại.");
    }
  }
};

// Hàm upload ảnh đại diện
export const uploadAvatar = async (formData) => {
  try {
    const response = await api.post("/auth/upload-avatar", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    return response.data;
  } catch (error) {
    console.error("Upload avatar error:", error);
    if (error.response) {
      throw new Error(error.response.data.msg || "Tải ảnh đại diện thất bại");
    } else if (error.request) {
      throw new Error(
        "Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng."
      );
    } else {
      throw new Error("Đã xảy ra lỗi. Vui lòng thử lại.");
    }
  }
};

// Hàm đổi mật khẩu
export const changePassWord = async (data) => {
  try {
    const response = await api.post("/auth/change-password", data);
    return response.data;
  } catch (error) {
    console.error("Change password error:", error);
    if (error.response) {
      throw new Error(error.response.data.msg || "Đổi mật khẩu thất bại");
    }
  }
};

// Kiểm tra OTP hợp lệ với backend
export async function verifyOTP(email, otp) {
  try {
    const res = await fetch("http://localhost:5000/api/otp/verify", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, otp }),
    });
    const contentType = res.headers.get("content-type");
    if (!contentType || !contentType.includes("application/json")) {
      throw new Error("Lỗi hệ thống hoặc kết nối. Vui lòng thử lại sau.");
    }
    const data = await res.json();
    // Nếu backend trả về success=false hoặc valid=false thì throw luôn để frontend catch
    if (!data.success || !data.valid) {
      throw new Error("Mã xác nhận (OTP) không đúng hoặc đã hết hạn.");
    }
    return data;
  } catch (err) {
    // Đảm bảo luôn throw để frontend catch
    throw new Error(err.message || "Lỗi xác thực OTP");
  }
}

// Kiểm tra OTP hợp lệ với backend (API mới: /api/otp/check)
export async function checkOTP(email, otp) {
  try {
    const res = await fetch("http://localhost:5000/api/otp/check", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, otp }),
    });
    const contentType = res.headers.get("content-type");
    if (!contentType || !contentType.includes("application/json")) {
      throw new Error("Lỗi hệ thống hoặc kết nối. Vui lòng thử lại sau.");
    }
    const data = await res.json();
    if (!data.success || !data.valid) {
      throw new Error(
        data.msg || "Mã xác nhận (OTP) không đúng hoặc đã hết hạn."
      );
    }
    return data;
  } catch (err) {
    throw new Error(err.message || "Lỗi xác thực OTP");
  }
}

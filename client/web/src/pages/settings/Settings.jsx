import React, { useState, useEffect } from "react";
import Navbar from "../../components/layout/Navbar";
import Sidebar from "../../components/layout/Sidebar";
import Input from "../../components/common/Input";
import Button from "../../components/common/Button";
import defaultAvatar from "../../assets/images/default-avatar.jpg";
import "../../styles/setting.css";
import {
  getProfile,
  updateProfileWithAvatar,
  uploadAvatar,
  changePassWord,
} from "../../services/authService";
import { validateUsername, validateRequired } from "../../utils/validators";

// Component Dialog tùy chỉnh để hiển thị thông báo
const Dialog = ({ isOpen, message, onClose }) => {
  if (!isOpen) return null;
  return (
    <div className="dialog-overlay">
      <div className="dialog">
        <h3>Thông báo</h3>
        <p>{message}</p>
        <div className="dialog-actions">
          <Button variant="primary" onClick={onClose}>
            Đóng
          </Button>
        </div>
      </div>
    </div>
  );
};

const Settings = () => {
  // Khởi tạo trạng thái cho component
  const [activeTab, setActiveTab] = useState("Tài khoản"); // Quản lý tab hiện tại (Tài khoản hoặc Bảo mật)
  const [avatar, setAvatar] = useState(null); // Lưu URL tạm thời của ảnh đại diện (hiển thị trên UI)
  const [avatarFileName, setAvatarFileName] = useState(""); // Lưu tên file ảnh đại diện được chọn
  const [formData, setFormData] = useState({
    username: "",
    fullName: "",
    email: "",
    birthday: "",
  }); // Lưu dữ liệu biểu mẫu hồ sơ
  const [initialFormData, setInitialFormData] = useState(null); // Lưu dữ liệu hồ sơ ban đầu để khôi phục khi hủy
  const [errors, setErrors] = useState({}); // Lưu các lỗi xác thực biểu mẫu
  const [loading, setLoading] = useState(false); // Quản lý trạng thái đang tải
  const [successMessage, setSuccessMessage] = useState(""); // Lưu thông báo thành công cho tab Tài khoản
  const [isDialogOpen, setIsDialogOpen] = useState(false); // Quản lý trạng thái mở/đóng dialog
  const [dialogMessage, setDialogMessage] = useState(""); // Lưu nội dung thông báo trong dialog
  const [avatarFileObject, setAvatarFileObject] = useState(null); // Lưu đối tượng file ảnh đại diện được chọn
  const [passwordData, setPasswordData] = useState({
    oldPassword: "",
    newPassword: "",
    confirmPassword: "",
  }); // Lưu dữ liệu biểu mẫu đổi mật khẩu
  const [refreshKey, setRefreshKey] = useState(0); // Khóa để buộc làm mới giao diện và gọi lại API

  // Hàm này chạy khi component được gắn vào hoặc khi refreshKey thay đổi
  // Mục đích: Lấy thông tin hồ sơ người dùng từ server và cập nhật trạng thái
  useEffect(() => {
    const fetchProfile = async () => {
      try {
        setLoading(true); // Bật trạng thái đang tải
        const response = await getProfile(); // Gọi API lấy thông tin hồ sơ
        console.log("Profile response:", response);
        if (response && response.user) {
          const user = response.user;
          // Tạo đối tượng dữ liệu hồ sơ từ phản hồi API
          const profileData = {
            username: user.username || "",
            fullName: user.fullName || "",
            email: user.email || "",
            birthday: user.birthday
              ? new Date(user.birthday).toISOString().split("T")[0]
              : "",
          };
          setFormData(profileData); // Cập nhật dữ liệu biểu mẫu
          setInitialFormData(profileData); // Lưu dữ liệu ban đầu
          setAvatar(user.avatar ? `${user.avatar}?t=${Date.now()}` : null); // Cập nhật URL ảnh đại diện với tham số chống cache
          setAvatarFileName(user.avatar ? "Current Avatar" : ""); // Cập nhật tên file ảnh
        } else {
          setErrors({
            general: "Không tìm thấy thông tin người dùng.",
          });
        }
      } catch (error) {
        console.error("Error fetching profile:", error.message);
        setErrors({
          general: "Không thể tải thông tin hồ sơ. Vui lòng thử lại.",
        });
      } finally {
        setLoading(false); // Tắt trạng thái đang tải
      }
    };
    fetchProfile(); // Gọi hàm lấy hồ sơ
  }, [refreshKey]);

  // Hàm xử lý khi người dùng chọn file ảnh đại diện mới
  // Mục đích: Xác thực file ảnh và cập nhật trạng thái giao diện
  const handleAvatarChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      console.log("Selected avatar file:", {
        name: file.name,
        size: file.size,
        type: file.type,
      });
      if (file.size > 5 * 1024 * 1024) {
        setErrors({ ...errors, avatar: "Ảnh quá lớn (tối đa 5MB)" }); // Báo lỗi nếu file quá lớn
        return;
      }
      if (!file.type.startsWith("image/")) {
        setErrors({ ...errors, avatar: "Vui lòng chọn file ảnh" }); // Báo lỗi nếu không phải file ảnh
        return;
      }
      setAvatar(URL.createObjectURL(file)); // Tạo URL tạm thời để hiển thị ảnh
      setAvatarFileName(file.name); // Cập nhật tên file
      setAvatarFileObject(file); // Lưu đối tượng file
      setErrors({ ...errors, avatar: "" }); // Xóa lỗi ảnh nếu có
    }
  };

  // Hàm xử lý khi người dùng thay đổi dữ liệu trong các trường nhập liệu hồ sơ
  // Mục đích: Cập nhật trạng thái formData và xóa lỗi liên quan
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value }); // Cập nhật giá trị trường nhập liệu
    setErrors({ ...errors, [name]: "" }); // Xóa lỗi của trường đó
  };

  // Hàm xử lý khi người dùng thay đổi dữ liệu trong các trường nhập liệu mật khẩu
  // Mục đích: Cập nhật trạng thái passwordData và xóa lỗi liên quan
  const handlePasswordChange = (e) => {
    const { name, value } = e.target;
    setPasswordData({ ...passwordData, [name]: value }); // Cập nhật giá trị trường mật khẩu
    setErrors({ ...errors, [name]: "" }); // Xóa lỗi của trường đó
  };

  // Hàm xác thực dữ liệu biểu mẫu hồ sơ
  // Mục đích: Kiểm tra tính hợp lệ của tên đăng nhập, họ tên và ngày sinh
  const validateForm = () => {
    const newErrors = {};
    const usernameValidation = validateUsername(formData.username); // Xác thực tên đăng nhập
    if (!usernameValidation.isValid) {
      newErrors.username = usernameValidation.message;
    }
    const fullNameValidation = validateRequired(formData.fullName, "Họ và tên"); // Xác thực họ tên
    if (!fullNameValidation.isValid) {
      newErrors.fullName = fullNameValidation.message;
    }
    if (formData.birthday && isNaN(new Date(formData.birthday).getTime())) {
      newErrors.birthday = "Ngày sinh không hợp lệ"; // Kiểm tra ngày sinh hợp lệ
    }
    return newErrors; // Trả về danh sách lỗi
  };

  // Hàm xác thực dữ liệu biểu mẫu đổi mật khẩu
  // Mục đích: Kiểm tra tính hợp lệ của mật khẩu cũ, mới và xác nhận
  const validatePasswordForm = () => {
    const newErrors = {};
    if (!validateRequired(passwordData.oldPassword, "Mật khẩu cũ").isValid) {
      newErrors.oldPassword = "Vui lòng nhập mật khẩu cũ"; // Kiểm tra mật khẩu cũ
    }
    if (passwordData.newPassword.length < 8) {
      newErrors.newPassword = "Mật khẩu mới phải có ít nhất 8 ký tự"; // Kiểm tra độ dài mật khẩu mới
    }
    if (passwordData.newPassword !== passwordData.confirmPassword) {
      newErrors.confirmPassword = "Mật khẩu xác nhận không khớp"; // Kiểm tra khớp mật khẩu
    }
    return newErrors; // Trả về danh sách lỗi
  };

  // Hàm xử lý khi người dùng nhấn nút "Cập nhật" trong tab Tài khoản
  // Mục đích: Tải ảnh đại diện (nếu có) và cập nhật thông tin hồ sơ
  const handleSubmit = async (e) => {
    e.preventDefault();
    const validationErrors = validateForm();
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors); // Hiển thị lỗi nếu xác thực thất bại
      return;
    }

    try {
      setLoading(true); // Bật trạng thái đang tải

      // Tải ảnh đại diện nếu người dùng chọn ảnh mới
      if (avatarFileObject) {
        const avatarPayload = new FormData();
        avatarPayload.append("avatar", avatarFileObject); // Thêm file ảnh vào payload
        console.log("Avatar FormData payload:");
        for (let [key, value] of avatarPayload.entries()) {
          console.log(`${key}: ${value instanceof File ? value.name : value}`);
        }
        const avatarResponse = await uploadAvatar(avatarPayload); // Gọi API tải ảnh
        if (!avatarResponse.success) {
          throw new Error(avatarResponse.msg || "Tải ảnh đại diện thất bại");
        }
      }

      // Cập nhật thông tin hồ sơ
      const profilePayload = new FormData();
      profilePayload.append("username", formData.username);
      profilePayload.append("fullName", formData.fullName);
      profilePayload.append("birthday", formData.birthday || "");
      console.log("Profile FormData payload:");
      for (let [key, value] of profilePayload.entries()) {
        console.log(`${key}: ${value instanceof File ? value.name : value}`);
      }

      const profileResponse = await updateProfileWithAvatar(profilePayload); // Gọi API cập nhật hồ sơ
      if (profileResponse.success) {
        const fetchResponse = await getProfile(); // Lấy lại thông tin hồ sơ mới nhất
        if (fetchResponse && fetchResponse.user) {
          const updatedUser = fetchResponse.user;
          const profileData = {
            username: updatedUser.username || "",
            fullName: updatedUser.fullName || "",
            email: updatedUser.email || "",
            birthday: updatedUser.birthday
              ? new Date(updatedUser.birthday).toISOString().split("T")[0]
              : "",
          };
          setFormData(profileData); // Cập nhật dữ liệu biểu mẫu
          setInitialFormData(profileData); // Cập nhật dữ liệu ban đầu
          setAvatar(updatedUser.avatar ? `${updatedUser.avatar}?t=${Date.now()}` : null); // Cập nhật ảnh đại diện
          setAvatarFileName(updatedUser.avatar ? "Current Avatar" : ""); // Cập nhật tên file
          setAvatarFileObject(null); // Xóa file ảnh đã chọn
          setSuccessMessage("Cập nhật hồ sơ thành công!"); // Hiển thị thông báo thành công
          setErrors({}); // Xóa lỗi
          setRefreshKey((prev) => prev + 1); // Buộc làm mới giao diện
          setTimeout(() => setSuccessMessage(""), 3000); // Xóa thông báo sau 3 giây
        } else {
          throw new Error("Không thể tải lại thông tin hồ sơ.");
        }
      }
    } catch (error) {
      console.error("Error updating profile:", error.message);
      setErrors({
        general: error.message || "Cập nhật hồ sơ thất bại. Vui lòng thử lại.",
      });
      setAvatar(initialFormData?.avatar ? `${initialFormData.avatar}?t=${Date.now()}` : null); // Khôi phục ảnh cũ
      setAvatarFileName(initialFormData?.avatar ? "Current Avatar" : ""); // Khôi phục tên file
      setAvatarFileObject(null); // Xóa file ảnh đã chọn
    } finally {
      setLoading(false); // Tắt trạng thái đang tải
    }
  };

  // Hàm xử lý khi người dùng nhấn nút "Cập nhật" trong tab Bảo mật
  // Mục đích: Gọi API đổi mật khẩu và hiển thị dialog nếu thành công
  const handlePasswordSubmit = async (e) => {
    e.preventDefault();
    const validationErrors = validatePasswordForm();
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors); // Hiển thị lỗi nếu xác thực thất bại
      return;
    }
    try {
      setLoading(true); // Bật trạng thái đang tải
      const payload = {
        oldPassword: passwordData.oldPassword,
        newPassword: passwordData.newPassword,
        confirmPassword: passwordData.confirmPassword,
      };
      console.log("Password change payload:", payload);
      const response = await changePassWord(payload); // Gọi API đổi mật khẩu
      if (response.success) {
        setDialogMessage(response.msg || "Mật khẩu đã được thay đổi thành công."); // Lưu thông báo thành công
        setIsDialogOpen(true); // Mở dialog
        setErrors({}); // Xóa lỗi
        setPasswordData({
          oldPassword: "",
          newPassword: "",
          confirmPassword: "",
        }); // Đặt lại biểu mẫu mật khẩu
      } else {
        throw new Error(response.msg || "Đổi mật khẩu thất bại.");
      }
    } catch (error) {
      console.error("Error updating password:", error.message);
      setErrors({
        general: error.message || "Đổi mật khẩu thất bại. Vui lòng thử lại.",
      });
    } finally {
      setLoading(false); // Tắt trạng thái đang tải
    }
  };

  // Hàm đóng dialog thông báo
  // Mục đích: Đặt lại trạng thái dialog khi người dùng nhấn nút đóng
  const closeDialog = () => {
    setIsDialogOpen(false);
    setDialogMessage("");
  };

  // Hàm xử lý khi người dùng nhấn nút "Hủy"
  // Mục đích: Khôi phục dữ liệu hồ sơ về trạng thái ban đầu
  const handleCancel = () => {
    if (initialFormData) {
      setFormData(initialFormData); // Khôi phục dữ liệu biểu mẫu
      setAvatar(initialFormData.avatar ? `${initialFormData.avatar}?t=${Date.now()}` : null); // Khôi phục ảnh đại diện
      setAvatarFileName(initialFormData.avatar ? "Current Avatar" : ""); // Khôi phục tên file
      setAvatarFileObject(null); // Xóa file ảnh đã chọn
      setErrors({}); // Xóa lỗi
      setSuccessMessage(""); // Xóa thông báo thành công
    }
  };

  // Giao diện chính của component
  return (
    <div className="dashboard-layout">
      <Navbar />
      <Sidebar />
      <main className="dashboard-content">
        <div className="product-header">
          <h1>Cài đặt tài khoản</h1>
          <p className="product-subtitle">
            <span className="subtitle">Cài đặt tài khoản</span>
            <span className="subtitle subtitle-sep">{">"}</span>
            <span className="subtitle active">{activeTab}</span>
          </p>
        </div>
        <div className="setting-navigation">
          <span
            className={`nav-tab${activeTab === "Tài khoản" ? " active" : ""}`}
            onClick={() => setActiveTab("Tài khoản")}
          >
            Tài khoản
          </span>
          <span
            className={`nav-tab${activeTab === "Bảo mật" ? " active" : ""}`}
            onClick={() => setActiveTab("Bảo mật")}
          >
            Bảo mật
          </span>
        </div>
        <div className="setting-content">
          {successMessage && (
            <div className="success-message">{successMessage}</div>
          )}
          {errors.general && (
            <div className="error-message">{errors.general}</div>
          )}
          {activeTab === "Tài khoản" && (
            <form
              className="setting-form setting-form-horizontal"
              onSubmit={handleSubmit}
            >
              <h2>Thông tin cá nhân</h2>
              <div className="setting-form-row-horizontal">
                <div className="setting-avatar-col">
                  <label
                    style={{ fontWeight: 800, marginBottom: 6, fontSize: 14 }}
                  >
                    Ảnh đại diện
                  </label>
                  <img
                    src={avatar || defaultAvatar}
                    alt="avatar"
                    className="avatar-img"
                    style={{ marginBottom: 12 }}
                    key={refreshKey}
                  />
                  <div className="avatar-upload">
                    <label className="avatar-file-label">
                      <span className="avatar-file-btn">Chọn ảnh</span>
                      <input
                        type="file"
                        accept="image/*"
                        onChange={handleAvatarChange}
                        style={{ display: "none" }}
                      />
                    </label>
                    <div className="avatar-file-name">
                      {avatarFileName || "Chưa chọn tệp"}
                    </div>
                    {errors.avatar && (
                      <div className="error-message">{errors.avatar}</div>
                    )}
                  </div>
                </div>
                <div className="setting-info-col">
                  <div className="form-row">
                    <Input
                      label="Tên đăng nhập"
                      type="text"
                      name="username"
                      value={formData.username}
                      onChange={handleInputChange}
                      placeholder="Username"
                      error={errors.username}
                    />
                    <Input
                      label="Họ và tên"
                      type="text"
                      name="fullName"
                      value={formData.fullName}
                      onChange={handleInputChange}
                      placeholder="Full name"
                      error={errors.fullName}
                    />
                  </div>
                  <div className="form-row">
                    <Input
                      label="Email"
                      type="email"
                      name="email"
                      value={formData.email}
                      onChange={handleInputChange}
                      placeholder="Email"
                      disabled
                      error={errors.email}
                    />
                    <Input
                      label="Ngày sinh"
                      type="date"
                      name="birthday"
                      value={formData.birthday}
                      onChange={handleInputChange}
                      error={errors.birthday}
                    />
                  </div>
                  <div className="form-actions form-actions-left">
                    <Button type="submit" variant="primary" disabled={loading}>
                      {loading ? "Đang cập nhật..." : "Cập nhật"}
                    </Button>
                    <Button
                      type="button"
                      variant="secondary"
                      onClick={handleCancel}
                    >
                      Hủy
                    </Button>
                  </div>
                </div>
              </div>
            </form>
          )}
          {activeTab === "Bảo mật" && (
            <form className="setting-form" onSubmit={handlePasswordSubmit}>
              <h2>Mật khẩu</h2>
              <div className="form-row">
                <Input
                  label="Mật khẩu cũ"
                  type="password"
                  name="oldPassword"
                  value={passwordData.oldPassword}
                  onChange={handlePasswordChange}
                  placeholder="Nhập mật khẩu cũ"
                  error={errors.oldPassword}
                />
                <Input
                  label="Mật khẩu mới"
                  type="password"
                  name="newPassword"
                  value={passwordData.newPassword}
                  onChange={handlePasswordChange}
                  placeholder="Nhập mật khẩu mới"
                  error={errors.newPassword}
                />
                <Input
                  label="Xác nhận mật khẩu"
                  type="password"
                  name="confirmPassword"
                  value={passwordData.confirmPassword}
                  onChange={handlePasswordChange}
                  placeholder="Xác nhận mật khẩu mới"
                  error={errors.confirmPassword}
                />
              </div>
              <div className="password-checklist">
                <label>Yêu cầu mật khẩu:</label>
                <ul>
                  <li>
                    <input
                      type="checkbox"
                      checked={passwordData.newPassword.length >= 8}
                      readOnly
                    />
                    Tối thiểu 8 ký tự
                  </li>
                  <li>
                    <input
                      type="checkbox"
                      checked={
                        /[A-Z]/.test(passwordData.newPassword) &&
                        /[a-z]/.test(passwordData.newPassword)
                      }
                      readOnly
                    />
                    Có chữ hoa và thường
                  </li>
                  <li>
                    <input
                      type="checkbox"
                      checked={/[!@#$%^&*]/.test(passwordData.newPassword)}
                      readOnly
                    />
                    Có ký tự đặc biệt
                  </li>
                </ul>
              </div>
              <div className="form-actions">
                <Button type="submit" variant="primary" disabled={loading}>
                  {loading ? "Đang cập nhật..." : "Cập nhật"}
                </Button>
                <Button
                  type="button"
                  variant="secondary"
                  onClick={() =>
                    setPasswordData({
                      oldPassword: "",
                      newPassword: "",
                      confirmPassword: "",
                    })
                  }
                >
                  Hủy
                </Button>
              </div>
            </form>
          )}
          <Dialog
            isOpen={isDialogOpen}
            message={dialogMessage}
            onClose={closeDialog}
          />
        </div>
      </main>
    </div>
  );
};

export default Settings;
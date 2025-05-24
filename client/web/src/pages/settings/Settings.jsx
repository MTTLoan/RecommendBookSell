import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../../components/layout/Navbar";
import Sidebar from "../../components/layout/Sidebar";
import Input from "../../components/common/Input";
import Button from "../../components/common/Button";
import defaultAvatar from "../../assets/images/default-avatar.jpg";
import { isAuthenticated, logout } from '../../services/authService';
import "../../styles/setting.css";
import {
  getProfile,
  updateProfileWithAvatar,
} from "../../services/authService";
import { validateUsername, validateRequired } from "../../utils/validators";

const Settings = () => {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState("Tài khoản");
  const [avatar, setAvatar] = useState(null);
  const [avatarFileName, setAvatarFileName] = useState("");
  const [formData, setFormData] = useState({
    username: "",
    fullName: "",
    email: "",
    birthday: "",
  });
  const [initialFormData, setInitialFormData] = useState(null);
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");
  const [avatarFileObject, setAvatarFileObject] = useState(null);
  const [passwordData, setPasswordData] = useState({
    oldPassword: "",
    newPassword: "",
    confirmPassword: "",
  });

  useEffect(() => {
    if (!isAuthenticated()) {
      navigate("/auth/login");
    }
    const fetchProfile = async () => {
      try {
        setLoading(true);
        const response = await getProfile();
        console.log("Profile response:", response);
        if (response && response.user) {
          const user = response.user;
          const profileData = {
            username: user.username || "",
            fullName: user.fullName || "",
            email: user.email || "",
            birthday: user.birthday
              ? new Date(user.birthday).toISOString().split("T")[0]
              : "",
          };
          setFormData(profileData);
          setInitialFormData(profileData);
          setAvatar(user.avatar || null);
          setAvatarFileName(user.avatar ? "Current Avatar" : "");
        } else {
          setErrors({
            general: "Không tìm thấy thông tin người dùng.",
          });
        }
      } catch (error) {
        console.error("Error fetching profile:", error);
        setErrors({
          general: "Không thể tải thông tin hồ sơ. Vui lòng thử lại.",
        });
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, [navigate]);

  const handleAvatarChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      console.log("Selected avatar file:", {
        name: file.name,
        size: file.size,
        type: file.type,
      });
      if (file.size > 5 * 1024 * 1024) {
        setErrors({ ...errors, avatar: "Ảnh quá lớn (tối đa 5MB)" });
        return;
      }
      if (!file.type.startsWith("image/")) {
        setErrors({ ...errors, avatar: "Vui lòng chọn file ảnh" });
        return;
      }
      setAvatar(URL.createObjectURL(file));
      setAvatarFileName(file.name);
      setAvatarFileObject(file);
      setErrors({ ...errors, avatar: "" });
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
    setErrors({ ...errors, [name]: "" });
  };

  const handlePasswordChange = (e) => {
    const { name, value } = e.target;
    setPasswordData({ ...passwordData, [name]: value });
    setErrors({ ...errors, [name]: "" });
  };

  const validateForm = () => {
    const newErrors = {};
    const usernameValidation = validateUsername(formData.username);
    if (!usernameValidation.isValid) {
      newErrors.username = usernameValidation.message;
    }
    const fullNameValidation = validateRequired(formData.fullName, "Họ và tên");
    if (!fullNameValidation.isValid) {
      newErrors.fullName = fullNameValidation.message;
    }
    if (formData.birthday && isNaN(new Date(formData.birthday).getTime())) {
      newErrors.birthday = "Ngày sinh không hợp lệ";
    }
    return newErrors;
  };

  const validatePasswordForm = () => {
    const newErrors = {};
    if (!validateRequired(passwordData.oldPassword, "Mật khẩu cũ").isValid) {
      newErrors.oldPassword = "Vui lòng nhập mật khẩu cũ";
    }
    if (passwordData.newPassword.length < 8) {
      newErrors.newPassword = "Mật khẩu mới phải có ít nhất 8 ký tự";
    }
    if (passwordData.newPassword !== passwordData.confirmPassword) {
      newErrors.confirmPassword = "Mật khẩu xác nhận không khớp";
    }
    return newErrors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const validationErrors = validateForm();
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    try {
      setLoading(true);
      const payload = new FormData();
      payload.append("username", formData.username);
      payload.append("fullName", formData.fullName);
      payload.append("birthday", formData.birthday || "");
      if (avatarFileObject) {
        payload.append("avatar", avatarFileObject);
      }
      console.log("FormData payload:");
      for (let [key, value] of payload.entries()) {
        console.log(`${key}: ${value instanceof File ? value.name : value}`);
      }

      const response = await updateProfileWithAvatar(payload);
      if (response.success) {
        const profileResponse = await getProfile();
        if (profileResponse && profileResponse.user) {
          const updatedUser = profileResponse.user;
          const profileData = {
            username: updatedUser.username || "",
            fullName: updatedUser.fullName || "",
            email: updatedUser.email || "",
            birthday: updatedUser.birthday
              ? new Date(updatedUser.birthday).toISOString().split("T")[0]
              : "",
          };
          setFormData(profileData);
          setInitialFormData(profileData);
          setAvatar(
            updatedUser.avatar ? `${updatedUser.avatar}?t=${Date.now()}` : null
          );
          setAvatarFileName(updatedUser.avatar ? "Current Avatar" : "");
          setAvatarFileObject(null);
          setSuccessMessage("Cập nhật hồ sơ thành công!");
          setErrors({});
          setTimeout(() => setSuccessMessage(""), 3000);
        } else {
          throw new Error("Không thể tải lại thông tin hồ sơ.");
        }
      }
    } catch (error) {
      console.error("Error updating profile:", error);
      setErrors({
        general: error.message || "Cập nhật hồ sơ thất bại. Vui lòng thử lại.",
      });
      setAvatar(initialFormData?.avatar || null);
      setAvatarFileName(initialFormData?.avatar ? "Current Avatar" : "");
      setAvatarFileObject(null);
    } finally {
      setLoading(false);
    }
  };

  const handlePasswordSubmit = async (e) => {
    e.preventDefault();
    const validationErrors = validatePasswordForm();
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }
    try {
      setLoading(true);
      // Placeholder for password update API
      setSuccessMessage("Cập nhật mật khẩu thành công!");
      setErrors({});
      setPasswordData({
        oldPassword: "",
        newPassword: "",
        confirmPassword: "",
      });
      setTimeout(() => setSuccessMessage(""), 3000);
    } catch (error) {
      setErrors({
        general:
          error.message || "Cập nhật mật khẩu thất bại. Vui lòng thử lại.",
      });
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    if (initialFormData) {
      setFormData(initialFormData);
      setAvatar(initialFormData.avatar || null);
      setAvatarFileName(initialFormData.avatar ? "Current Avatar" : "");
      setAvatarFileObject(null);
      setErrors({});
      setSuccessMessage("");
    }
  };

    const user = JSON.parse(sessionStorage.getItem('user')) || {};

  return (
    <div className="dashboard-layout">
      <Navbar user={user} onLogout={logout} />
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
        </div>
      </main>
    </div>
  );
};

export default Settings;

import React, { useState } from "react";
import Navbar from "../../components/layout/Navbar";
import Sidebar from "../../components/layout/Sidebar";
import Input from "../../components/common/Input";
import Button from "../../components/common/Button";
import defaultAvatar from '../../assets/images/default-avatar.jpg';
import "../../styles/setting.css";

const Settings = () => {
  const [activeTab, setActiveTab] = useState("Tài khoản");
  const [avatar, setAvatar] = useState(null);
  const [avatarFileName, setAvatarFileName] = useState("");
  const handleAvatarChange = (e) => {
  if (e.target.files && e.target.files[0]) {
    setAvatar(URL.createObjectURL(e.target.files[0]));
    setAvatarFileName(e.target.files[0].name);
  }
};

  return (
    <div className="dashboard-layout">
      <Navbar />
      <Sidebar />
      <main className="dashboard-content">
        {/* Title + Subtitle + Navigation bar chung nền trắng */}
        <div className="product-header">
          <h1>Cài đặt tài khoản</h1>
          <p className="product-subtitle">
            <span className="subtitle">Cài đặt tài khoản</span>
            <span className="subtitle subtitle-sep">{'>'}</span>
            <span className="subtitle active">{activeTab}</span>
          </p>
        </div>
          {/* Navigation bar */}
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

        {/* Form nội dung */}
        <div className="setting-content">
          {activeTab === "Tài khoản" && (
            <>
              <form className="setting-form setting-form-horizontal">
                <h2>Thông tin cá nhân</h2>
                <div className="setting-form-row-horizontal">
                  <div className="setting-avatar-col">
                    <label style={{ fontWeight: 800, marginBottom: 6, fontSize: 14 }}>Ảnh đại diện</label>
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
                      {avatarFileName ? avatarFileName : "Chưa chọn tệp"}
                    </div>
                  </div>
                </div>
                  <div className="setting-info-col">
                    <div className="form-row">
                      <Input label="Tên đăng nhập" type="text" name="username" placeholder="Username"/>
                      <Input label="Họ và tên" type="text" name="fullName" placeholder="Full name"/>
                    </div>
                    <div className="form-row">
                      <Input label="Email" type="email" name="email" placeholder="Email"/>
                      <Input label="Ngày sinh" type="date" name="dob"/>
                    </div>
                  </div>
                </div>
              </form>
              <div className="form-actions form-actions-left">
                <Button type="submit" variant="primary">Cập nhật</Button>
                <Button type="button" variant="secondary">Hủy</Button>
              </div>
            </>
          )}

          {activeTab === "Bảo mật" && (
            <>
            <form className="setting-form">
              <h2>Mật khẩu</h2>
              <div className="form-row">
                <Input label="Mật khẩu cũ" type="password" name="oldPassword" placeholder="Nhập mật khẩu cũ"/>
                <Input label="Mật khẩu mới" type="password" name="newPassword" placeholder="Nhập mật khẩu mới"/>
                <Input label="Xác nhận mật khẩu" type="password" name="confirmPassword" placeholder="Xác nhận mật khẩu mới"/>
              </div>
              <div className="password-checklist">
                <label>Yêu cầu mật khẩu:</label>
                <ul>
                  <li>
                    <input type="checkbox" checked={false} readOnly /> Tối thiểu 8 ký tự
                  </li>
                  <li>
                    <input type="checkbox" checked={false} readOnly /> Có chữ hoa và thường
                  </li>
                  <li>
                    <input type="checkbox" checked={false} readOnly /> Có ký tự đặc biệt
                  </li>
                </ul>
              </div>
            </form>
              <div className="form-actions">
                <Button type="submit" variant="primary">Cập nhật</Button>
                <Button type="button" variant="secondary">Hủy</Button>
              </div>
          </>
          )}
        </div>
      </main>
    </div>
  );
};

export default Settings;
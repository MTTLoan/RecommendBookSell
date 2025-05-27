import React, { useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { resetPassword } from "../../services/authService";
import Logo from "../../components/common/Logo";
import Input from "../../components/common/Input";
import Button from "../../components/common/Button";
import loginImage from "../../assets/images/login-bg.png";
import "../../styles/auth.css";

const ResetPassword = () => {
  const [searchParams] = useSearchParams();
  const emailFromUrl = searchParams.get("email") || "";
  const [email, setEmail] = useState(emailFromUrl);
  const [otp, setOtp] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    if (!email) {
      setError("Vui lòng nhập email đã nhận mã xác nhận.");
      return;
    }
    if (!otp) {
      setError("Vui lòng nhập mã xác nhận (OTP) đã gửi về email.");
      return;
    }
    if (password !== confirmPassword) {
      setError("Mật khẩu và xác nhận mật khẩu không khớp.");
      return;
    }
    try {
      await resetPassword({
        email,
        newPassword: password,
        confirmPassword,
        otp,
      });
      setSuccess(
        "Mật khẩu của bạn đã được đặt lại thành công. Đang chuyển sang trang đăng nhập..."
      );
      setTimeout(() => {
        navigate("/auth/login");
      }, 1500);
    } catch (err) {
      setError(err.message || "Đặt lại mật khẩu thất bại. Vui lòng thử lại.");
    }
  };

  return (
    <div className="auth-container">
      {/* Left side - Reset Password form */}
      <div className="auth-form-container">
        <div className="auth-form-wrapper">
          <div className="auth-logo">
            <Logo />
          </div>
          <h1 className="auth-title">Khôi phục mật khẩu</h1>
          <form onSubmit={handleSubmit}>
            <div className="mb-4">
              {/* Nếu không có email trên URL, cho phép nhập email */}
              <Input
                label="Email"
                type="email"
                name="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Nhập email đã nhận mã xác nhận"
                required
                autoFocus
              />
            </div>
            <div className="mb-4">
              <Input
                label="Mã xác nhận (OTP)"
                type="text"
                name="otp"
                value={otp}
                onChange={(e) => setOtp(e.target.value)}
                placeholder="Nhập mã xác nhận từ email"
                required
              />
            </div>
            <div className="mb-4">
              <Input
                label="Mật khẩu mới"
                type="password"
                name="newPassword"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Nhập mật khẩu mới"
                required
              />
            </div>
            <div className="mb-4">
              <Input
                label="Xác nhận mật khẩu"
                type="password"
                name="confirmPassword"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder="Nhập lại mật khẩu mới"
                required
              />
            </div>
            {error && <div className="auth-error">{error}</div>}
            {success && <div className="auth-success">{success}</div>}
            <Button type="submit">Đổi mật khẩu</Button>
          </form>
        </div>
      </div>
      {/* Right side - Image */}
      <div
        className="auth-image-container"
        style={{ backgroundImage: `url(${loginImage})` }}
      ></div>
    </div>
  );
};

export default ResetPassword;

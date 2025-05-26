import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { login } from "../../services/authService";
import Logo from "../../components/common/Logo";
import Input from "../../components/common/Input";
import Button from "../../components/common/Button";
import Popup from "../../components/common/Popup";
import loginImage from "../../assets/images/login-bg.png";
import "../../styles/auth.css";

const Login = () => {
  const navigate = useNavigate();
  const [credentials, setCredentials] = useState({
    identifier: "",
    password: "",
  });
  const [error, setError] = useState("");
  const [showSuccess, setShowSuccess] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCredentials({
      ...credentials,
      [name]: value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const response = await login(credentials);
      if (response.user.role !== "admin") {
        setError("Chỉ tài khoản admin mới được đăng nhập.");
        return;
      }
      setShowSuccess(true); // Hiện popup thành công
      setTimeout(() => {
        setShowSuccess(false);
        navigate("/dashboard");
      }, 3000);
    } catch (err) {
      console.error("Login error:", err);
      setError(err.message || "Đăng nhập thất bại. Vui lòng thử lại.");
    }
  };

  return (
    <div className="auth-container">
      {/* Left side - Login form */}
      <div className="auth-form-container">
        <div className="auth-form-wrapper">
          <div className="auth-logo">
            <Logo />
          </div>
          <h1 className="auth-title">Đăng nhập</h1>

          {error && <div className="auth-error">{error}</div>}

          <form onSubmit={handleSubmit}>
            <div className="mb-4">
              <Input
                label="Tên đăng nhập"
                type="text"
                name="identifier"
                value={credentials.identifier}
                onChange={handleChange}
                placeholder="Nhập tên đăng nhập"
                required
              />
            </div>

            <div className="mb-4">
              <Input
                label="Mật khẩu"
                type="password"
                name="password"
                value={credentials.password}
                onChange={handleChange}
                placeholder="Nhập mật khẩu"
                required
              />
            </div>

            <div className="auth-forgot-password">
              <Link to="/auth/forgot-password">Quên mật khẩu?</Link>
            </div>

            <Button type="submit">Đăng nhập</Button>
          </form>
        </div>
      </div>

      {/* Right side - Image */}
      <div
        className="auth-image-container"
        style={{ backgroundImage: `url(${loginImage})` }}
      ></div>

      {showSuccess && (
        <Popup
          open={showSuccess}
          title="Đăng nhập thành công!"
          titleColor="success"
          content="Chào mừng bạn quay lại hệ thống quản trị."
          showCancel={false}
          showConfirm={false}
          onClose={() => setShowSuccess(false)}
        />
      )}
    </div>
  );
};

export default Login;

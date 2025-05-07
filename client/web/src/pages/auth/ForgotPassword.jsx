import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { forgotPassword } from '../../services/authService';
import Logo from '../../components/common/Logo';
import Input from '../../components/common/Input';
import Button from '../../components/common/Button';
import loginImage from '../../assets/images/login-bg.png';
import '../../styles/auth.css';

const ForgotPassword = () => {
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleChange = (e) => {
    setEmail(e.target.value);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      await forgotPassword(email);
      setSuccess('Yêu cầu đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra email của bạn.');
    } catch (err) {
      setError(err.message || 'Gửi yêu cầu thất bại. Vui lòng thử lại.');
    } finally {
    }
  };

  return (
    <div className="auth-container">
      {/* Left side - Forgot Password form */}
      <div className="auth-form-container">
        <div className="auth-form-wrapper">
          <div className="auth-logo">
            <Logo />
          </div>
          <h1 className="auth-title">Quên mật khẩu</h1>

          {error && <div className="auth-error">{error}</div>}
          {success && <div className="auth-success">{success}</div>}

          <form onSubmit={handleSubmit}>
            <div className="mb-4">
              <Input
                label="Email"
                type="email"
                name="email"
                value={email}
                onChange={handleChange}
                placeholder="Nhập email của bạn"
                required
              />

          <div className="auth-forgot-password">
            <Link to="/auth/login">Đăng nhập</Link>
          </div>
            </div>

            <Button type="submit">
              Khôi phục mật khẩu
            </Button>
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

export default ForgotPassword;
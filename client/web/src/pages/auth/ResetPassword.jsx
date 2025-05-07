import React, { useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { resetPassword } from '../../services/authService';
import Logo from '../../components/common/Logo';
import Input from '../../components/common/Input';
import Button from '../../components/common/Button';
import loginImage from '../../assets/images/login-bg.png';
import '../../styles/auth.css';

const ResetPassword = () => {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (password !== confirmPassword) {
      setError('Mật khẩu và xác nhận mật khẩu không khớp.');
      return;
    }

    try {
      await resetPassword({ token, password }); // Gửi yêu cầu đổi mật khẩu đến backend
      setSuccess('Mật khẩu của bạn đã được đặt lại thành công.');
    } catch (err) {
      setError(err.message || 'Đặt lại mật khẩu thất bại. Vui lòng thử lại.');
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

          {error && <div className="auth-error">{error}</div>}
          {success && <div className="auth-success">{success}</div>}

          <form onSubmit={handleSubmit}>
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

            <Button type="submit">
              Đổi mật khẩu
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

export default ResetPassword;
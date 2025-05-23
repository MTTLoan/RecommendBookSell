import React, { useState } from 'react';

const Input = ({ label, type, name, value, onChange, placeholder, required }) => {
  const [showPassword, setShowPassword] = useState(false);

  const togglePasswordVisibility = () => {
    setShowPassword((prev) => !prev);
  };

  const validate = (val) => {
    if (name === 'email') return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val);
    if (name === 'username') return val.trim().length >= 4;
    return true;
  };

  const getLabelText = (name) => {
    switch (name) {
      case 'usernameOrEmail': return 'Tên đăng nhập hoặc email';
      case 'username': return 'Tên đăng nhập';
      case 'fullName': return 'Họ và tên';
      case 'email': return 'Email';
      case 'password': return 'Mật khẩu';
      case 'confirmPassword': return 'Xác nhận mật khẩu';
      case 'newPassword': return 'Mật khẩu mới';
      default: return name.charAt(0).toUpperCase() + name.slice(1);
    }
  };

  return (
    <div className="input-wrapper" style={{ fontFamily: "'Quicksand', sans-serif" }}>
      <label htmlFor={name} style={{ fontFamily: "'Quicksand', sans-serif" }}>
        {label || getLabelText(name)}
      </label>

      <input
        type={type === 'password' && showPassword ? 'text' : type}
        name={name}
        id={name}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        required={required}
        style={{ fontFamily: "'Quicksand', sans-serif" }}
      />

      {/* Icon xác thực */}

      {(name === 'email' || name === 'username') && value && (
        <span className={`input-icon ${validate(value) ? 'valid' : ''}`}>
          <span className="material-symbols-outlined">check_circle</span>
        </span>
      )}

      {/* Icon hiện/ẩn mật khẩu */}
      {type === 'password' && (
        <span
          className="password-icon"
          onClick={togglePasswordVisibility}
          title={showPassword ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'}
          style={{ fontFamily: "'Quicksand', sans-serif" }}
        >
          <span className="material-symbols-outlined">
            {showPassword ? 'visibility_off' : 'visibility'}
          </span>
        </span>
      )}
    </div>
  );
};

export default Input;
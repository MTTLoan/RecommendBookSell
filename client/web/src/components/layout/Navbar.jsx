import React, { useState, useEffect } from 'react';
import '../../styles/navbar.css';
import { getCurrentUser } from '../../services/authService';
import defaultAvatar from '../../assets/images/default-avatar.jpg';
import { logout } from '../../services/authService';

const Navbar = () => {
  const [user, setUser] = useState(getCurrentUser());

  useEffect(() => {
    // Nếu muốn tự động cập nhật khi user thay đổi ở localStorage, có thể lắng nghe sự kiện storage
    const handleStorage = () => setUser(getCurrentUser());
    window.addEventListener('storage', handleStorage);
    return () => window.removeEventListener('storage', handleStorage);
  }, []);

  return (
  <nav className="navbar">
    <div className="navbar-left">
    </div>
    <div className="navbar-right">
      <div className="navbar-user">
        <img
          src={user?.avatar || defaultAvatar}
          alt="avatar"
          className="navbar-avatar"
        />
        <div className="navbar-user-info">
          <span className="navbar-username">{user?.username || 'Admin'}</span>
          <span className="navbar-fullname">{user?.fullName || 'Administrator'}</span>
        </div>
      </div>
      <button className="navbar-logout" onClick={logout} title="Đăng xuất">
        <span className="material-symbols-outlined navbar-logout-icon">logout</span>
      </button>
    </div>
  </nav>
  );
};

export default Navbar;
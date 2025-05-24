import React, { useState, useEffect } from 'react';
import '../../styles/navbar.css';
import { getProfile } from '../../services/authService';
import defaultAvatar from '../../assets/images/default-avatar.jpg';
import { logout } from '../../services/authService';

const Navbar = () => {
  const [user, setUser] = useState(null);

  useEffect(() => {
    // Hàm lấy thông tin người dùng từ API
    const fetchUser = async () => {
      try {
        const profile = await getProfile();
        setUser(profile?.user || null); // Vì response trả về { message, user }
      } catch (error) {
        console.error("Error fetching profile in Navbar:", error.message);
        setUser(null);
      }
    };
    fetchUser();

    // Hàm xử lý sự kiện avatarUpdated
    const handleAvatarUpdate = () => {
      fetchUser();
    };

    // Lắng nghe sự kiện avatarUpdated
    window.addEventListener('avatarUpdated', handleAvatarUpdate);

    // Dọn dẹp sự kiện khi component unmount
    return () => {
      window.removeEventListener('avatarUpdated AscendingAvatarUpdated', handleAvatarUpdate);
    };
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
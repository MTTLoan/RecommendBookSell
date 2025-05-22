import React from 'react';
import '../../styles/navbar.css';
import defaultAvatar from '../../assets/images/default-avatar.jpg';

const Navbar = ({ user, onLogout }) => (
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
      <button className="navbar-logout" onClick={onLogout} title="Đăng xuất">
        <span className="material-symbols-outlined navbar-logout-icon">logout</span>
      </button>
    </div>
  </nav>
);

export default Navbar;
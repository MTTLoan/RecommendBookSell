import React from 'react';

const Navbar = ({ user, onLogout }) => (
  <nav className="navbar">
    <div className="navbar-left">
      <span className="navbar-title">Chapter One</span>
    </div>
    <div className="navbar-right">
      <span className="navbar-user">
        <img
          src={user.avatar || '/default-avatar.png'}
          alt="avatar"
          className="navbar-avatar"
        />
        {user.fullname}
      </span>
      <button className="navbar-logout" onClick={onLogout} title="Đăng xuất">
        <span className="material-symbols-outlined" style={{ fontSize: '20px' }}>
          logout
        </span>
      </button>
    </div>
  </nav>
);

export default Navbar;
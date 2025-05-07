import React from 'react';
import { Link } from 'react-router-dom';
import logoImage from '../../assets/images/logo.png'; // Import logo

const Sidebar = () => (
  <aside className="sidebar">
    <div className="sidebar-logo">
      <img src={logoImage} alt="Logo" className="sidebar-logo-image" />
    </div>
    <ul>
      <li><Link to="/dashboard">Tổng quan</Link></li>
      <li><Link to="/products">Sản phẩm</Link></li>
      <li><Link to="/categories">Danh mục</Link></li>
      <li><Link to="/orders">Đơn hàng</Link></li>
      <li><Link to="/customers">Khách hàng</Link></li>
      <li><Link to="/reports">Báo cáo</Link></li>
      <li><Link to="/settings">Cài đặt tài khoản</Link></li>
    </ul>
  </aside>
);

export default Sidebar;
import React from 'react';
import { NavLink } from 'react-router-dom';
import logoImage from '../../assets/images/logo.png'; // Import logo
import '../../styles/sidebar.css';

const Sidebar = () => (
  <aside className="sidebar">
    <div className="sidebar-logo">
      <img src={logoImage} alt="Logo" className="sidebar-logo-image" />
    </div>
    <div className="side-general">
      <h2>CHUNG</h2>
      <ul className="sidebar-menu">
        <li>
          <NavLink to="/dashboard" activeClassName="active">
            <span className="material-symbols-outlined sidebar-icon">home</span>
            Tổng quan
          </NavLink>
        </li>
        <li>
          <NavLink to="/products" activeClassName="active">
            <span className="material-symbols-outlined sidebar-icon">storefront</span>
            Sản phẩm
          </NavLink>
        </li>
        <li>
          <NavLink to="/categories" activeClassName="active">
            <span className="material-symbols-outlined sidebar-icon">list</span>
            Danh mục
          </NavLink>
        </li>
        <li>
          <NavLink to="/orders" activeClassName="active">
            <span className="material-symbols-outlined sidebar-icon">description</span>
            Đơn hàng
          </NavLink>
        </li>
        <li>
          <NavLink to="/customers" activeClassName="active">
            <span className="material-symbols-outlined sidebar-icon">group</span>
            Khách hàng
          </NavLink>
        </li>
        <li>
        <NavLink to="/notifications" activeClassName="active">
          <span className="material-symbols-outlined sidebar-icon">notifications</span>
          Thông báo
        </NavLink>
        </li>
        <li>
        <NavLink to="/reviews" activeClassName="active">
          <span className="material-symbols-outlined sidebar-icon">rate_review</span>
          Đánh giá
        </NavLink>
        </li>
        <li>
          <NavLink to="/reports" activeClassName="active">
            <span className="material-symbols-outlined sidebar-icon">show_chart</span>
            Báo cáo hệ thống đề xuất
          </NavLink>
        </li>
      </ul>
    </div>
    <div className="side-general">
      <h2>CÔNG CỤ</h2>
      <ul className="sidebar-menu">
        <li>
          <NavLink to="/settings" activeClassName="active">
            <span className="material-symbols-outlined sidebar-icon">settings</span>
            Cài đặt tài khoản
          </NavLink>
        </li>
      </ul>
    </div>
  </aside>
);

export default Sidebar;
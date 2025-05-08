import { NavLink } from 'react-router-dom';
import React, { useState } from 'react';
import logo from '../../assets/images/logo.png';
import '../../styles/sidebar.css';

const menu = [
  { section: 'CHUNG', items: [
    { label: 'Tổng quan', icon: 'home', to: '/dashboard' },
    { label: 'Sản phẩm', icon: 'storefront', to: '/products' },
    { label: 'Danh mục', icon: 'list', to: '/categories' },
    { label: 'Đơn hàng', icon: 'description', to: '/orders' },
    { label: 'Khách hàng', icon: 'group', to: '/customers' },
    { label: 'Báo cáo hệ thống đề xuất', icon: 'show_chart', to: '/recommendation-report' },
  ]},
  { section: 'CÔNG CỤ', items: [
    { label: 'Cài đặt tài khoản', icon: 'settings', to: '/settings' },
  ]}
];

export default function Sidebar() {
  const [isVisible, setIsVisible] = useState(false);

  const toggleSidebar = () => {
    setIsVisible(!isVisible);
  };

  return (
    <>
    <div className={`sidebar${isVisible ? 'visible' : 'hidden'}`}>
      <div className="logo">
        <img src={logo} alt="Logo" />
      </div>
      {menu.map(({ section, items }) => (
        <div key={section}>
          <p className="section">{section}</p>
          {items.map(({ label, icon, to }) => (
            <NavLink
              key={label}
              to={to}
              className={({ isActive }) =>
                `nav-link ${isActive ? 'active' : ''}`
              }
            >
              <span className="material-symbols-outlined">{icon}</span>
              <span>{label}</span>
            </NavLink>
          ))}
        </div>
      ))}
    </div>
    <span className="material-symbols-outlined menu-toggle" onClick={toggleSidebar}>
    menu_open
  </span>
</>
  );
}

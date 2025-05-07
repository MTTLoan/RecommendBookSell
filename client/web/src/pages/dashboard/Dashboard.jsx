import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../../components/common/Navbar';
import Sidebar from '../../components/common/Sidebar';
import { isAuthenticated, logout } from '../../services/authService';
import '../../styles/auth.css';

const Dashboard = () => {
  const navigate = useNavigate();

  useEffect(() => {
    if (!isAuthenticated()) {
      navigate('/auth/login');
    }
  }, [navigate]);

  const user = JSON.parse(sessionStorage.getItem('user')) || {};

  return (
    <div className="dashboard-layout">
      <Navbar user={user} onLogout={logout} />
      <Sidebar />
      <main className="dashboard-content">
        <h1>Chào mừng, {user.fullName || 'Người dùng'}!</h1>
        {/* Nội dung dashboard */}
      </main>
    </div>
  );
};

export default Dashboard;
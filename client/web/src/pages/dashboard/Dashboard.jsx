import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../../components/layout/Navbar';
import Sidebar from '../../components/layout/Sidebar';
import Widget from '../../components/common/Widget';
import Chart from '../../components/layout/Chart';
import Table from '../../components/layout/Table';
import { isAuthenticated } from '../../services/authService';
import '../../styles/dashboard.css';

const data = [
  { name: 'Sản phẩm A', price: '$100', quantity: 10 },
  { name: 'Sản phẩm B', price: '$200', quantity: 5 },
  { name: 'Sản phẩm C', price: '$150', quantity: 8 },
  { name: 'Sản phẩm A', price: '$100', quantity: 10 },
  { name: 'Sản phẩm B', price: '$200', quantity: 5 },
  { name: 'Sản phẩm C', price: '$150', quantity: 8 },
];

const columns = [
  { key: 'name', label: 'Tên sản phẩm' },
  { key: 'price', label: 'Giá' },
  { key: 'quantity', label: 'Số lượng' },
];

const Dashboard = () => {
  const navigate = useNavigate();
  const [activeSubtitle, setActiveSubtitle] = useState('Tổng quan'); // Subtitle được chọn


  useEffect(() => {
    if (!isAuthenticated()) {
      navigate('/auth/login');
    }
  }, [navigate]);

  const user = JSON.parse(sessionStorage.getItem('user')) || {};

  return (
    <div className="dashboard-layout">
      <Navbar />
      <Sidebar />
      <main className="dashboard-content">
        {/* Title */}
        <div className="dashboard-title">
          <h1>Tổng quan</h1>
          <div className="dashboard-subtitles">
            <span
              className={`subtitle ${activeSubtitle === 'Tổng quan' ? 'active' : ''}`}
              onClick={() => setActiveSubtitle('Tổng quan')}
            >
              Tổng quan
            </span>
          </div>
        </div>

        {/* Nội dung */}
        {activeSubtitle === 'Tổng quan' && (
          <div>
        <div className="dashboard-top">
          {/* Bên trái: chart */}
          <div className="dashboard-chart">
            <Chart />
            </div>
          {/* Bên phải: widget */}
        <div className="dashboard-widgets">
          <Widget
            title="Doanh thu"
            value="$12,345"
            percentage={15}
            isIncrease={true}
            description="So với tháng trước"
          />
          <Widget
            title="Khách hàng"
            value="1,234"
            percentage={-5}
            isIncrease={false}
            description="So với tháng trước"
          />
          <Widget
            title="Đơn hàng"
            value="567"
            percentage={10}
            isIncrease={true}
            description="So với tháng trước"
          />
          <Widget
            title="Sản phẩm"
            value="890"
            percentage={20}
            isIncrease={true}
            description="So với tháng trước"
          />
        </div>
        </div>
        {/* Bên dưới */}
        <div className="dashboard-bottom">
        <Table
          title="Top sản phẩm"
          data={data}
          columns={columns}
          onAdd={() => console.log('Thêm sản phẩm')}
          showHeader={true}
          showSearch={false}
          showFilter={true}
          showDownload={true}
          showAddButton={false}
          showCheckbox={false}
          showSort={true}
        />
        </div>
        </div>
        )}
      </main>
    </div>
  );
};

export default Dashboard;
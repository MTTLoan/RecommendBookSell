import React, { useState } from 'react';
import '../../styles/chart.css';

const Chart = () => {
  const [filter, setFilter] = useState('Tất cả'); // Nội dung lọc được chọn
  const [isDropdownOpen, setIsDropdownOpen] = useState(false); // Trạng thái mở/đóng dropdown

  const handleFilterChange = (newFilter) => {
    setFilter(newFilter);
    setIsDropdownOpen(false); // Đóng dropdown sau khi chọn
  };

  const toggleDropdown = () => {
    setIsDropdownOpen(!isDropdownOpen);
  };

  const handleDownload = () => {
    // Logic tải xuống dữ liệu chart
    console.log('Tải xuống dữ liệu chart');
  };

  return (
    <div className="chart-container">
      <div className="chart-header">
        <div className="chart-title">
          <h2>Biểu đồ doanh thu</h2>
          <span className="chart-subtitle">Chú thích: Doanh thu theo tháng</span>
        </div>
        <div className="chart-actions">
          <div className="chart-filter">
            <button className="filter-button" onClick={toggleDropdown}>
              <span className="filter-text">{filter}</span>
              <span className="material-symbols-outlined filter-icon">filter_list</span>
            </button>
            {isDropdownOpen && (
              <ul className="filter-dropdown">
                <li onClick={() => handleFilterChange('Tất cả')}>Tất cả</li>
                <li onClick={() => handleFilterChange('Tháng này')}>Tháng này</li>
                <li onClick={() => handleFilterChange('Quý này')}>Quý này</li>
                <li onClick={() => handleFilterChange('Năm nay')}>Năm nay</li>
              </ul>
            )}
          </div>
          <button className="chart-download" onClick={handleDownload} title="Tải xuống">
            <span className="material-symbols-outlined download-icon">download</span>
          </button>
        </div>
      </div>
      <div className="chart-body">
        {/* Nội dung biểu đồ sẽ được hiển thị ở đây */}
        <p>Biểu đồ sẽ được hiển thị tại đây.</p>
      </div>
    </div>
  );
};

export default Chart;
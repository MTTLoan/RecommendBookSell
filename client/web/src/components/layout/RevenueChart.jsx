import React, { useState } from "react";
import { Line } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";
import "../../styles/chart.css";

// Đăng ký các thành phần cần thiết của Chart.js
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

const RevenueChart = ({ chartData }) => {
  const [filter, setFilter] = useState("Tất cả");
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);

  const handleFilterChange = (newFilter) => {
    setFilter(newFilter);
    setIsDropdownOpen(false);
  };

  const toggleDropdown = () => {
    setIsDropdownOpen(!isDropdownOpen);
  };

  const handleDownload = () => {
    const csv = [
      ["Ngày", "Tổng doanh thu", "Doanh thu từ đề xuất"],
      ...chartData.labels.map((label, index) => [
        label,
        chartData.totalRevenue[index] || 0,
        chartData.recommendedRevenue[index] || 0,
      ]),
    ]
      .map((row) => row.join(","))
      .join("\n");

    const blob = new Blob([csv], { type: "text/csv" });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "revenue-chart.csv";
    a.click();
    window.URL.revokeObjectURL(url);
  };

  // Chuẩn bị dữ liệu cho biểu đồ
  const data = {
    labels: chartData.labels,
    datasets: [
      {
        label: "Tổng doanh thu",
        data: chartData.totalRevenue,
        borderColor: "#3fbf48",
        backgroundColor: "rgba(63, 191, 72, 0.2)",
        fill: true,
      },
      {
        label: "Doanh thu từ đề xuất",
        data: chartData.recommendedRevenue,
        borderColor: "#ff6384",
        backgroundColor: "rgba(255, 99, 132, 0.2)",
        fill: true,
      },
    ],
  };

  const options = {
    responsive: true,
    plugins: {
      legend: {
        position: "top",
      },
      title: {
        display: true,
        text: "Biểu đồ doanh thu",
      },
    },
    scales: {
      x: {
        title: {
          display: true,
          text: "Ngày",
        },
      },
      y: {
        title: {
          display: true,
          text: "Doanh thu (VNĐ)",
        },
        beginAtZero: true,
      },
    },
  };

  return (
    <div className="chart-container">
      <div className="chart-header">
        <div className="chart-title">
          <h2>Biểu đồ doanh thu</h2>
          <span className="chart-subtitle">Chú thích: Doanh thu theo ngày</span>
        </div>
        <div className="chart-actions">
          <div className="chart-filter">
            <button className="filter-button" onClick={toggleDropdown}>
              <span className="filter-text">{filter}</span>
              <span className="material-symbols-outlined filter-icon">
                filter_list
              </span>
            </button>
            {isDropdownOpen && (
              <ul className="filter-dropdown">
                <li onClick={() => handleFilterChange("Tất cả")}>Tất cả</li>
                <li onClick={() => handleFilterChange("Tháng này")}>
                  Tháng này
                </li>
                <li onClick={() => handleFilterChange("Quý này")}>Quý này</li>
                <li onClick={() => handleFilterChange("Năm nay")}>Năm nay</li>
              </ul>
            )}
          </div>
          <button
            className="chart-download"
            onClick={handleDownload}
            title="Tải xuống"
          >
            <span className="material-symbols-outlined download-icon">
              download
            </span>
          </button>
        </div>
      </div>
      <div className="chart-body">
        <Line data={data} options={options} />
      </div>
    </div>
  );
};

export default RevenueChart;

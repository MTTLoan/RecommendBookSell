import React, { useState } from "react";
import { Pie } from "react-chartjs-2";
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from "chart.js";
import "../../styles/chart.css";

// Đăng ký các thành phần cần thiết của Chart.js
ChartJS.register(ArcElement, Tooltip, Legend);

const CategoryChart = ({ chartData }) => {
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
      ["Danh mục", "Doanh thu"],
      ...chartData.labels.map((label, index) => [
        label,
        chartData.data[index] || 0,
      ]),
    ]
      .map((row) => row.join(","))
      .join("\n");

    const blob = new Blob([csv], { type: "text/csv" });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "category-revenue-chart.csv";
    a.click();
    window.URL.revokeObjectURL(url);
  };

  // Chuẩn bị dữ liệu cho biểu đồ
  const data = {
    labels: chartData.labels,
    datasets: [
      {
        label: "Doanh thu theo danh mục",
        data: chartData.data,
        backgroundColor: [
          "#ff6384",
          "#36a2eb",
          "#ffce56",
          "#4bc0c0",
          "#9966ff",
          "#ff9f40",
        ],
        borderColor: [
          "#ff6384",
          "#36a2eb",
          "#ffce56",
          "#4bc0c0",
          "#9966ff",
          "#ff9f40",
        ],
        borderWidth: 0, // Xóa viền của các phần trong biểu đồ tròn
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
        text: "Doanh thu theo danh mục",
      },
    },
  };

  return (
    <div className="chart-container">
      <div className="chart-header">
        <div className="chart-title">
          <h2>Doanh thu theo danh mục</h2>
          <span className="chart-subtitle">
            Chú thích: Phân bổ doanh thu theo danh mục
          </span>
        </div>
      </div>
      <div className="chart-body">
        <Pie data={data} options={options} />
      </div>
    </div>
  );
};

export default CategoryChart;

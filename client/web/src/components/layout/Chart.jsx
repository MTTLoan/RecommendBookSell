import React, { useEffect, useRef } from "react";
import "../../styles/chart.css";
import Chart from "chart.js/auto"; // Nhập Chart.js

const ChartComponent = ({ revenueData }) => {
  const chartRef = useRef(null); // Tham chiếu đến canvas
  const chartInstanceRef = useRef(null); // Tham chiếu đến instance của Chart.js

  // Hàm vẽ hoặc cập nhật biểu đồ
  useEffect(() => {
    if (!chartRef.current || !revenueData || revenueData.length === 0) return;

    // Hủy biểu đồ cũ nếu đã tồn tại
    if (chartInstanceRef.current) {
      chartInstanceRef.current.destroy();
    }

    // Chuẩn bị dữ liệu cho biểu đồ
    const labels = revenueData.map((item) => item.date); // Trục X: Ngày hoặc Tháng
    const data = revenueData.map((item) => item.revenue); // Trục Y: Doanh thu

    // Tạo biểu đồ mới
    const ctx = chartRef.current.getContext("2d");
    chartInstanceRef.current = new Chart(ctx, {
      type: "line", // Biểu đồ đường
      data: {
        labels: labels,
        datasets: [
          {
            label: "Doanh thu (VND)",
            data: data,
            borderColor: "#3FBF48", // Màu đường: xanh lá cây
            backgroundColor: "rgba(63, 191, 72, 0.2)", // Màu nền dưới đường (xanh lá cây nhạt)
            fill: true, // Đổ màu dưới đường
            tension: 0.4, // Độ cong của đường
            pointBackgroundColor: "#3FBF48", // Màu điểm
            pointBorderColor: "#fff", // Viền điểm
            pointHoverBackgroundColor: "#fff", // Màu điểm khi hover
            pointHoverBorderColor: "#3FBF48", // Viền điểm khi hover
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          x: {
            title: {
              display: true,
              text: revenueData[0]?.date?.includes("Ngày") ? "Ngày" : "Tháng", // Động theo loại dữ liệu
              font: { size: 14 },
            },
            grid: {
              display: false, // Ẩn lưới trục X
            },
          },
          y: {
            title: {
              display: true,
              text: "Doanh thu (VND)",
              font: { size: 14 },
            },
            beginAtZero: true,
            ticks: {
              callback: (value) => value.toLocaleString("vi-VN"), // Định dạng số trên trục Y
            },
          },
        },
        plugins: {
          legend: {
            display: true,
            position: "top",
            labels: {
              font: { size: 14 },
            },
          },
          tooltip: {
            callbacks: {
              label: (context) => {
                const value = context.parsed.y;
                return `Doanh thu: ${value.toLocaleString("vi-VN")} VND`;
              },
            },
          },
        },
      },
    });

    // Cleanup khi component unmount
    return () => {
      if (chartInstanceRef.current) {
        chartInstanceRef.current.destroy();
      }
    };
  }, [revenueData]); // Cập nhật biểu đồ khi revenueData thay đổi

  return (
    <div style={{ height: "300px", width: "100%", position: "relative" }}>
      {revenueData && revenueData.length > 0 ? (
        <canvas ref={chartRef} />
      ) : (
        <div style={{ textAlign: "center", padding: "20px", color: "#666" }}>
          Không có dữ liệu để hiển thị.
        </div>
      )}
    </div>
  );
};

export default ChartComponent;

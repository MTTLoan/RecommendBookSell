import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../../components/layout/Navbar";
import Sidebar from "../../components/layout/Sidebar";
import Widget from "../../components/common/Widget";
import Chart from "../../components/layout/Chart";
import Table from "../../components/layout/Table";
import { isAuthenticated, logout } from "../../services/authService";
import "../../styles/report.css";

const data = [
  { name: "Sản phẩm A", rate: "10%", quantity: 10 },
  { name: "Sản phẩm B", rate: "20%", quantity: 5 },
  { name: "Sản phẩm C", rate: "30%", quantity: 8 },
  { name: "Sản phẩm A", rate: "10%", quantity: 10 },
  { name: "Sản phẩm B", rate: "20%", quantity: 5 },
  { name: "Sản phẩm C", rate: "35%", quantity: 8 },
];

const columns = [
  { key: "name", label: "Tên sản phẩm" },
  { key: "rate", label: "Tỷ lệ sản phẩm được gợi ý" },
  { key: "quantity", label: "Lượt bán" },
];

const Reports = () => {
  const navigate = useNavigate();
  const [activeSubtitle, setActiveSubtitle] = useState("Báo cáo"); // Subtitle được chọn

  useEffect(() => {
    if (!isAuthenticated()) {
      navigate("/auth/login");
    }
  }, [navigate]);

  const user = JSON.parse(sessionStorage.getItem("user")) || {};

  return (
    <div className="dashboard-layout">
      <Navbar user={user} onLogout={logout} />
      <Sidebar />
      <main className="dashboard-content">
        {/* Title */}
        <div className="dashboard-title">
          <h1>Báo cáo hệ thống đề xuất</h1>
          <div className="dashboard-subtitles">
            <span
              className={`subtitle ${
                activeSubtitle === "Báo cáo" ? "active" : ""
              }`}
              onClick={() => setActiveSubtitle("Báo cáo")}
            >
              Báo cáo
            </span>
          </div>
        </div>

        {/* Nội dung */}
        {activeSubtitle === "Báo cáo" && (
          <div>
            <div className="dashboard-top">
              {/* Bên trái: chart */}
              <div className="dashboard-chart">
                <Chart />
              </div>
              {/* Bên phải: widget */}
              <div className="dashboard-widgets">
                <Widget
                  title="Doanh thu từ hệ thống đề xuất"
                  value="0"
                  percentage={15}
                  isIncrease={true}
                  description="So với tháng trước"
                />
                <Widget
                  title="Lượt nhấn vào sản phẩm gợi ý"
                  value="0"
                  percentage={-5}
                  isIncrease={false}
                  description="So với tháng trước"
                />
                <Widget
                  title="Lượt thêm vào giỏ hàng"
                  value="0"
                  percentage={10}
                  isIncrease={true}
                  description="So với tháng trước"
                />
                <Widget
                  title="Lượt mua hàng từ gợi ý"
                  value="0"
                  percentage={20}
                  isIncrease={true}
                  description="So với tháng trước"
                />
              </div>
            </div>
            {/* Bên dưới */}
            <div className="dashboard-bottom-flex">
              <div className="dashboard-bottom-chart">
                <Chart />
              </div>
              <div className="dashboard-bottom-table">
                <Table
                  title="Top sản phẩm"
                  data={data}
                  columns={columns}
                  onAdd={() => console.log("Thêm sản phẩm")}
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
          </div>
        )}
      </main>
    </div>
  );
};

export default Reports;

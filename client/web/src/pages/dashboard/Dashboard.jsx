import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../../components/layout/Navbar";
import Sidebar from "../../components/layout/Sidebar";
import Widget from "../../components/common/Widget";
import Chart from "../../components/layout/Chart";
import Table from "../../components/layout/Table";
import { isAuthenticated } from "../../services/authService";
import { fetchDashboardStats } from "../../services/statisticService";
import "../../styles/dashboard.css";

const Dashboard = () => {
  const navigate = useNavigate();
  const [activeSubtitle, setActiveSubtitle] = useState("Tổng quan");
  const [stats, setStats] = useState({
    revenue: 0,
    revenueChange: 0,
    customers: 0,
    customersChange: 0,
    orders: 0,
    ordersChange: 0,
    products: 0,
    productsChange: 0,
    topProducts: [],
    allCategoryOptions: [],
  });
  const [categoryFilter, setCategoryFilter] = useState("");

  useEffect(() => {
    if (!isAuthenticated()) {
      navigate("/auth/login");
    }
  }, [navigate]);

  useEffect(() => {
    const fetchStatsData = async () => {
      try {
        const res = await fetchDashboardStats(categoryFilter);
        setStats(res);
      } catch {
        setStats({
          revenue: 0,
          revenueChange: 0,
          customers: 0,
          customersChange: 0,
          orders: 0,
          ordersChange: 0,
          products: 0,
          productsChange: 0,
          topProducts: [],
          allCategoryOptions: [],
        });
      }
    };
    fetchStatsData();
  }, [categoryFilter]);

  // Tạo filter cho Table từ allCategoryOptions
  const categoryOptions = stats.allCategoryOptions || [];

  const columns = [
    { key: "name", label: "Tên sản phẩm" },
    {
      key: "category",
      label: "Danh mục",
      filters: categoryOptions,
    },
    { key: "price", label: "Giá" },
    { key: "quantity", label: "Số lượng bán" },
  ];

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
              className={`subtitle ${
                activeSubtitle === "Tổng quan" ? "active" : ""
              }`}
              onClick={() => setActiveSubtitle("Tổng quan")}
            >
              Tổng quan
            </span>
          </div>
        </div>

        {/* Nội dung */}
        {activeSubtitle === "Tổng quan" && (
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
                  value={stats.revenue?.toLocaleString("vi-VN") + " VND"}
                  percentage={Math.abs(stats.revenueChange)}
                  isIncrease={stats.revenueChange >= 0}
                  description={
                    stats.revenueChange === 0
                      ? "Không thay đổi so với tháng trước"
                      : (stats.revenueChange > 0 ? "Tăng " : "Giảm ") +
                        Math.abs(stats.revenueChange) +
                        "% so với tháng trước"
                  }
                />
                <Widget
                  title="Khách hàng"
                  value={stats.customers}
                  percentage={Math.abs(stats.customersChange)}
                  isIncrease={stats.customersChange >= 0}
                  description={
                    stats.customersChange === 0
                      ? "Không thay đổi so với tháng trước"
                      : (stats.customersChange > 0 ? "Tăng " : "Giảm ") +
                        Math.abs(stats.customersChange) +
                        "% so với tháng trước"
                  }
                />
                <Widget
                  title="Đơn hàng"
                  value={stats.orders}
                  percentage={Math.abs(stats.ordersChange)}
                  isIncrease={stats.ordersChange >= 0}
                  description={
                    stats.ordersChange === 0
                      ? "Không thay đổi so với tháng trước"
                      : (stats.ordersChange > 0 ? "Tăng " : "Giảm ") +
                        Math.abs(stats.ordersChange) +
                        "% so với tháng trước"
                  }
                />
                <Widget
                  title="Sản phẩm"
                  value={stats.products}
                  percentage={Math.abs(stats.productsChange)}
                  isIncrease={stats.productsChange >= 0}
                  description={
                    stats.productsChange === 0
                      ? "Không thay đổi so với tháng trước"
                      : (stats.productsChange > 0 ? "Tăng " : "Giảm ") +
                        Math.abs(stats.productsChange) +
                        "% so với tháng trước"
                  }
                />
              </div>
            </div>
            {/* Bên dưới */}
            <div
              className="dashboard-bottom"
              style={{ width: "100%", marginTop: 32 }}
            >
              <Table
                title="Top sản phẩm"
                data={stats.topProducts}
                columns={columns}
                showHeader={true}
                showSearch={false}
                showFilter={true}
                showDownload={true}
                showAddButton={false}
                showCheckbox={false}
                showSort={true}
                style={{ width: "100%" }}
                filterValue={categoryFilter}
                setFilterValue={setCategoryFilter}
              />
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

export default Dashboard;

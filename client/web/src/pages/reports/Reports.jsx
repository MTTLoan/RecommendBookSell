import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../../components/layout/Navbar";
import Sidebar from "../../components/layout/Sidebar";
import Widget from "../../components/common/Widget";
import Table from "../../components/layout/Table";
import RevenueChart from "../../components/layout/RevenueChart";
import CategoryChart from "../../components/layout/CategoryChart";
import { isAuthenticated, logout } from "../../services/authService";
import "../../styles/report.css";
import {
  fetchRevenueStats,
  fetchClickStats,
  fetchAddToCartStats,
  fetchPurchaseStats,
  fetchTopProducts,
  fetchRevenueChartData,
  fetchCategoryRevenueChartData,
} from "../../services/reportService";

const Reports = () => {
  const navigate = useNavigate();
  const [activeSubtitle, setActiveSubtitle] = useState("Báo cáo");
  const [stats, setStats] = useState({
    revenue: { value: "0", percentage: 0, isIncrease: true },
    clicks: { value: "0", percentage: 0, isIncrease: false },
    addToCart: { value: "0", percentage: 0, isIncrease: true },
    purchases: { value: "0", percentage: 0, isIncrease: true },
  });
  const [topProducts, setTopProducts] = useState([]);
  const [revenueChartData, setRevenueChartData] = useState({
    labels: [],
    totalRevenue: [],
    recommendedRevenue: [],
  });
  const [categoryChartData, setCategoryChartData] = useState({
    labels: [],
    data: [],
  });
  const [month, setMonth] = useState(new Date().getMonth() + 1);
  const [year, setYear] = useState(new Date().getFullYear());

  useEffect(() => {
    if (!isAuthenticated()) {
      navigate("/auth/login");
    } else {
      fetchStats();
      fetchTopProductsData();
      fetchChartData();
    }
  }, [navigate, month, year]);

  const fetchStats = async () => {
    try {
      const [revenueData, clicksData, addToCartData, purchasesData] =
        await Promise.all([
          fetchRevenueStats(month, year),
          fetchClickStats(month, year),
          fetchAddToCartStats(month, year),
          fetchPurchaseStats(month, year),
        ]);

      setStats({
        revenue: revenueData,
        clicks: clicksData,
        addToCart: addToCartData,
        purchases: purchasesData,
      });
    } catch (error) {
      console.error("Error fetching stats:", error);
    }
  };

  const fetchTopProductsData = async () => {
    try {
      const data = await fetchTopProducts(month, year);
      setTopProducts(data);
    } catch (error) {
      console.error("Error fetching top products:", error);
    }
  };

  const fetchChartData = async () => {
    try {
      const [revenueData, categoryData] = await Promise.all([
        fetchRevenueChartData(month, year),
        fetchCategoryRevenueChartData(month, year),
      ]);

      setRevenueChartData({
        labels: revenueData.labels,
        totalRevenue: revenueData.totalRevenue,
        recommendedRevenue: revenueData.recommendedRevenue,
      });

      setCategoryChartData({
        labels: categoryData.labels,
        data: categoryData.data,
      });
    } catch (error) {
      console.error("Error fetching chart data:", error);
    }
  };

  const user = JSON.parse(localStorage.getItem("user")) || {};

  const months = Array.from({ length: 12 }, (_, i) => i + 1);
  const years = Array.from(
    { length: new Date().getFullYear() - 2019 },
    (_, i) => 2020 + i
  );

  const columns = [
    { key: "name", label: "Tên sản phẩm" },
    { key: "quantity", label: "Lượt bán" },
  ];

  return (
    <div className="dashboard-layout">
      <Navbar user={user} onLogout={logout} />
      <Sidebar />
      <main className="dashboard-content">
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
          <div className="dashboard-date-filters">
            <select
              value={month}
              onChange={(e) => setMonth(Number(e.target.value))}
            >
              {months.map((m) => (
                <option key={m} value={m}>
                  {m}
                </option>
              ))}
            </select>
            <select
              value={year}
              onChange={(e) => setYear(Number(e.target.value))}
            >
              {years.map((y) => (
                <option key={y} value={y}>
                  {y}
                </option>
              ))}
            </select>
          </div>
        </div>

        {activeSubtitle === "Báo cáo" && (
          <div>
            <div className="dashboard-top">
              <div className="dashboard-chart">
                <RevenueChart chartData={revenueChartData} />
              </div>
              <div className="dashboard-widgets">
                <Widget
                  title="Doanh thu từ hệ thống đề xuất"
                  value={stats.revenue.value}
                  percentage={stats.revenue.percentage}
                  isIncrease={stats.revenue.isIncrease}
                  description="So với tháng trước"
                />
                <Widget
                  title="Lượt nhấn vào sản phẩm gợi ý"
                  value={stats.clicks.value}
                  percentage={stats.clicks.percentage}
                  isIncrease={stats.clicks.isIncrease}
                  description="So với tháng trước"
                />
                <Widget
                  title="Lượt thêm vào giỏ hàng"
                  value={stats.addToCart.value}
                  percentage={stats.addToCart.percentage}
                  isIncrease={stats.addToCart.isIncrease}
                  description="So với tháng trước"
                />
                <Widget
                  title="Lượt mua hàng từ gợi ý"
                  value={stats.purchases.value}
                  percentage={stats.purchases.percentage}
                  isIncrease={stats.purchases.isIncrease}
                  description="So với tháng trước"
                />
              </div>
            </div>
            <div className="dashboard-bottom-flex">
              <div className="dashboard-bottom-chart">
                <CategoryChart chartData={categoryChartData} />
              </div>
              <div className="dashboard-bottom-table">
                <Table
                  title="Top sản phẩm"
                  data={topProducts}
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

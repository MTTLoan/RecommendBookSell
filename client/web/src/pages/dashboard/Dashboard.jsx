// File: Dashboard.jsx
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../../components/layout/Navbar";
import Sidebar from "../../components/layout/Sidebar";
import Widget from "../../components/common/Widget";
import Chart from "../../components/layout/Chart";
import Table from "../../components/layout/Table";
import { isAuthenticated } from "../../services/authService";
import {
  fetchDashboardStats,
  fetchRevenueData,
} from "../../services/statisticService";
import "../../styles/dashboard.css";
import { getWidgetProps } from "../../utils/widgetUtils";

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
    availableYears: [],
  });
  const [revenueData, setRevenueData] = useState([]); // Dữ liệu doanh thu cho biểu đồ
  const [filterValue, setFilterValue] = useState(""); // Bộ lọc danh mục
  const [monthFilter, setMonthFilter] = useState(new Date().getMonth()); // Bộ lọc tháng (mặc định là tháng hiện tại)
  const [yearFilter, setYearFilter] = useState(new Date().getFullYear()); // Bộ lọc năm (mặc định là năm hiện tại)
  const [error, setError] = useState(null); // Lưu lỗi nếu có

  // Kiểm tra xác thực người dùng
  useEffect(() => {
    if (!isAuthenticated()) {
      navigate("/auth/login");
    }
  }, [navigate]);

  // Lấy dữ liệu thống kê tổng quan
  useEffect(() => {
    const fetchStatsData = async () => {
      console.log("Đang lấy dữ liệu với bộ lọc:", {
        filterValue,
        monthFilter,
        yearFilter,
      });
      try {
        const res = await fetchDashboardStats(
          filterValue,
          monthFilter,
          yearFilter
        );
        setStats(res);
        setError(null);
      } catch (error) {
        console.error("Lỗi fetch dữ liệu:", error);
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
          availableYears: [],
        });
        setError("Đã xảy ra lỗi khi lấy dữ liệu. Vui lòng thử lại sau.");
      }
    };
    fetchStatsData();
  }, [filterValue, monthFilter, yearFilter]);

  // Lấy dữ liệu doanh thu cho biểu đồ
  useEffect(() => {
    const fetchRevenueChartData = async () => {
      try {
        const data = await fetchRevenueData(
          filterValue,
          monthFilter,
          yearFilter
        );
        setRevenueData(data);
      } catch (error) {
        console.error("Lỗi fetch dữ liệu doanh thu:", error);
        setRevenueData([]);
        setError(
          "Đã xảy ra lỗi khi lấy dữ liệu doanh thu. Vui lòng thử lại sau."
        );
      }
    };
    fetchRevenueChartData();
  }, [filterValue, monthFilter, yearFilter]);

  // Cấu hình cột cho bảng Top sản phẩm
  const columns = [
    { key: "name", label: "Tên sản phẩm" },
    {
      key: "category",
      label: "Danh mục",
      filters: stats.allCategoryOptions || [],
    },
    { key: "price", label: "Giá" },
    { key: "quantity", label: "Số lượng bán" },
  ];

  return (
    <div className="dashboard-layout">
      <Navbar />
      <Sidebar />
      <main className="dashboard-content">
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
        {error && <div className="error-message">{error}</div>}
        {activeSubtitle === "Tổng quan" && (
          <div>
            <div className="dashboard-top">
              <div className="dashboard-chart">
                {/* Truyền dữ liệu doanh thu vào Chart */}
                <Chart revenueData={revenueData} />
              </div>
              <div className="dashboard-widgets">
                <Widget
                  {...getWidgetProps(
                    "Doanh thu",
                    stats.revenue?.toLocaleString("vi-VN") + " VND",
                    stats.revenueChange,
                    monthFilter
                  )}
                />
                <Widget
                  {...getWidgetProps(
                    "Khách hàng",
                    stats.customers,
                    stats.customersChange,
                    monthFilter
                  )}
                />
                <Widget
                  {...getWidgetProps(
                    "Đơn hàng",
                    stats.orders,
                    stats.ordersChange,
                    monthFilter
                  )}
                />
                <Widget
                  {...getWidgetProps(
                    "Sản phẩm",
                    stats.products,
                    stats.productsChange,
                    monthFilter
                  )}
                />
              </div>
            </div>
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
                showMonthFilter={true}
                showYearFilter={true}
                showDownload={true}
                showAddButton={false}
                showCheckbox={false}
                showSort={true}
                filterValue={filterValue}
                setFilterValue={setFilterValue}
                monthFilter={monthFilter}
                setMonthFilter={setMonthFilter}
                yearFilter={yearFilter}
                setYearFilter={setYearFilter}
                availableYears={stats.availableYears || []}
                widgetStats={stats} // Truyền dữ liệu widget
                chartData={revenueData} // Truyền dữ liệu biểu đồ
                exportConfig={{
                  fileNamePrefix: "Báo cáo Tổng quan",
                  worksheetNames: {
                    widget: "ThongKeTongQuan",
                    table: "TopSanPham",
                    chart: "DoanhThu",
                  },
                }}
              />
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

export default Dashboard;

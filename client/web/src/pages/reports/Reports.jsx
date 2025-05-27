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

const monthOptions = [
  { value: null, text: "Tất cả tháng" },
  { value: 0, text: "Tháng 1" },
  { value: 1, text: "Tháng 2" },
  { value: 2, text: "Tháng 3" },
  { value: 3, text: "Tháng 4" },
  { value: 4, text: "Tháng 5" },
  { value: 5, text: "Tháng 6" },
  { value: 6, text: "Tháng 7" },
  { value: 7, text: "Tháng 8" },
  { value: 8, text: "Tháng 9" },
  { value: 9, text: "Tháng 10" },
  { value: 10, text: "Tháng 11" },
  { value: 11, text: "Tháng 12" },
];

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

  const [monthFilter, setMonthFilter] = useState(4);
  const [yearFilter, setYearFilter] = useState(2025);
  const [monthText, setMonthText] = useState("Tháng 5");
  const [yearText, setYearText] = useState("2025");
  const [isMonthDropdownOpen, setIsMonthDropdownOpen] = useState(false);
  const [isYearDropdownOpen, setIsYearDropdownOpen] = useState(false);

  const availableYears = [2024, 2025];

  useEffect(() => {
    if (!isAuthenticated()) {
      navigate("/auth/login");
    } else {
      fetchStats();
      fetchTopProductsData();
      fetchChartData();
    }
  }, [navigate, monthFilter, yearFilter]);

  const fetchStats = async () => {
    try {
      const [revenueData, clicksData, addToCartData, purchasesData] =
        await Promise.all([
          fetchRevenueStats(monthFilter, yearFilter),
          fetchClickStats(monthFilter, yearFilter),
          fetchAddToCartStats(monthFilter, yearFilter),
          fetchPurchaseStats(monthFilter, yearFilter),
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
      const data = await fetchTopProducts(monthFilter, yearFilter);
      setTopProducts(data);
    } catch (error) {
      console.error("Error fetching top products:", error);
    }
  };

  const fetchChartData = async () => {
    try {
      const [revenueData, categoryData] = await Promise.all([
        fetchRevenueChartData(monthFilter, yearFilter),
        fetchCategoryRevenueChartData(monthFilter, yearFilter),
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

  const columns = [
    { key: "name", label: "Tên sản phẩm" },
    { key: "quantity", label: "Lượt bán" },
  ];

  const handleMonthOptionClick = (value) => {
    const found = monthOptions.find((m) => m.value === value);
    setMonthText(found ? found.text : "Tất cả tháng");
    setMonthFilter(value);
    setIsMonthDropdownOpen(false);
  };

  const handleYearOptionClick = (value) => {
    setYearText(value === null ? "Năm" : value.toString());
    setYearFilter(value);
    setIsYearDropdownOpen(false);
  };

  const toggleMonthDropdown = () => {
    setIsMonthDropdownOpen((prev) => !prev);
    setIsYearDropdownOpen(false);
  };

  const toggleYearDropdown = () => {
    setIsYearDropdownOpen((prev) => !prev);
    setIsMonthDropdownOpen(false);
  };

  const handleDownloadExcel = () => {
    const fileNamePrefix = "Báo cáo";
    let fileName;
    const currentDate = new Date().toISOString().split("T")[0];
    if (yearFilter === null) {
      fileName = `${fileNamePrefix}_${currentDate}.xlsx`;
    } else if (monthFilter === null) {
      fileName = `${fileNamePrefix}_${yearFilter}.xlsx`;
    } else {
      const month = String(monthFilter + 1).padStart(2, "0");
      fileName = `${fileNamePrefix}_${yearFilter}-${month}.xlsx`;
    }

    const widgetData = [
      ["Tiêu chí", "Giá trị", "Phần trăm thay đổi", "Tăng/Giảm"],
      [
        "Doanh thu từ hệ thống đề xuất",
        `${Number(stats.revenue.value).toLocaleString("vi-VN")} VND`,
        stats.revenue.percentage,
        stats.revenue.isIncrease ? "Tăng" : "Giảm",
      ],
      [
        "Lượt nhấn vào sản phẩm gợi ý",
        stats.clicks.value,
        stats.clicks.percentage,
        stats.clicks.isIncrease ? "Tăng" : "Giảm",
      ],
      [
        "Lượt thêm vào giỏ hàng",
        stats.addToCart.value,
        stats.addToCart.percentage,
        stats.addToCart.isIncrease ? "Tăng" : "Giảm",
      ],
      [
        "Lượt mua hàng từ gợi ý",
        stats.purchases.value,
        stats.purchases.percentage,
        stats.purchases.isIncrease ? "Tăng" : "Giảm",
      ],
    ];

    const tableData = [
      ["Tên sản phẩm", "Lượt bán"],
      ...topProducts.map((item) => [item.name, item.quantity]),
    ];

    const revenueChartCsv = [
      ["Ngày", "Tổng doanh thu", "Doanh thu từ đề xuất"],
      ...revenueChartData.labels.map((label, index) => [
        label,
        revenueChartData.totalRevenue[index] || 0,
        revenueChartData.recommendedRevenue[index] || 0,
      ]),
    ];

    const categoryChartCsv = [
      ["Danh mục", "Doanh thu"],
      ...categoryChartData.labels.map((label, index) => [
        label,
        categoryChartData.data[index] || 0,
      ]),
    ];

    const XLSX = require("xlsx");
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(
      workbook,
      XLSX.utils.aoa_to_sheet(widgetData),
      "ThongKe"
    );
    XLSX.utils.book_append_sheet(
      workbook,
      XLSX.utils.aoa_to_sheet(tableData),
      "DanhSach"
    );
    XLSX.utils.book_append_sheet(
      workbook,
      XLSX.utils.aoa_to_sheet(revenueChartCsv),
      "BieuDoDoanhThu"
    );
    XLSX.utils.book_append_sheet(
      workbook,
      XLSX.utils.aoa_to_sheet(categoryChartCsv),
      "BieuDoDanhMuc"
    );

    XLSX.writeFile(workbook, fileName);
  };

  const formatRevenue = (value) => {
    return `${Number(value).toLocaleString("vi-VN")} VND`;
  };

  // Hàm xuất Excel tổng hợp cho Table (gồm nhiều sheet)
  const handleExportAllReportExcel = () => {
    const XLSX = require("xlsx");
    const wb = XLSX.utils.book_new();

    // Định dạng thời gian theo bộ lọc
    const timeLabel = `${year}_${month.toString().padStart(2, "0")}`;

    // 1. Sheet: Biểu đồ doanh thu
    if (revenueChartData && revenueChartData.labels?.length) {
      const headers = ["Thời gian", "Tổng doanh thu", "Doanh thu từ gợi ý"];
      const data = revenueChartData.labels.map((label, idx) => [
        label,
        (revenueChartData.totalRevenue[idx] || 0).toLocaleString("vi-VN"),
        (revenueChartData.recommendedRevenue[idx] || 0).toLocaleString("vi-VN"),
      ]);
      const worksheetData = [headers, ...data];
      const ws = XLSX.utils.aoa_to_sheet(worksheetData);
      XLSX.utils.book_append_sheet(wb, ws, "Biểu đồ doanh thu");
    }

    // 2. Sheet: Biểu đồ doanh thu theo danh mục
    if (categoryChartData && categoryChartData.labels?.length) {
      const headers = ["Danh mục", "Doanh thu"];
      const data = categoryChartData.labels.map((label, idx) => [
        label,
        (categoryChartData.data[idx] || 0).toLocaleString("vi-VN"),
      ]);
      const worksheetData = [headers, ...data];
      const ws = XLSX.utils.aoa_to_sheet(worksheetData);
      XLSX.utils.book_append_sheet(wb, ws, "Biểu đồ danh mục");
    }

    // 3. Sheet: Widget thống kê
    const widgetHeaders = [
      "Chỉ số",
      "Giá trị",
      "% thay đổi",
      "Tăng/Giảm",
      "Mô tả",
    ];
    const widgetData = [
      [
        "Doanh thu từ hệ thống đề xuất",
        stats.revenue.value,
        stats.revenue.percentage + "%",
        stats.revenue.isIncrease ? "Tăng" : "Giảm",
        "So với tháng trước",
      ],
      [
        "Lượt nhấn vào sản phẩm gợi ý",
        stats.clicks.value,
        stats.clicks.percentage + "%",
        stats.clicks.isIncrease ? "Tăng" : "Giảm",
        "So với tháng trước",
      ],
      [
        "Lượt thêm vào giỏ hàng",
        stats.addToCart.value,
        stats.addToCart.percentage + "%",
        stats.addToCart.isIncrease ? "Tăng" : "Giảm",
        "So với tháng trước",
      ],
      [
        "Lượt mua hàng từ gợi ý",
        stats.purchases.value,
        stats.purchases.percentage + "%",
        stats.purchases.isIncrease ? "Tăng" : "Giảm",
        "So với tháng trước",
      ],
    ];
    const wsWidget = XLSX.utils.aoa_to_sheet([widgetHeaders, ...widgetData]);
    XLSX.utils.book_append_sheet(wb, wsWidget, "Widget thống kê");

    // 4. Sheet: Top sản phẩm
    if (topProducts && topProducts.length) {
      const exportColumns = columns;
      const headers = exportColumns.map((col) => col.label);
      const exportData = topProducts.map((row) =>
        exportColumns.map((col) => row[col.key] || "")
      );
      const worksheetData = [headers, ...exportData];
      const ws = XLSX.utils.aoa_to_sheet(worksheetData);
      XLSX.utils.book_append_sheet(wb, ws, "Top sản phẩm");
    }

    // Tên file theo bộ lọc năm_tháng
    const fileName = `Báo cáo Recommendation System_${timeLabel}.xlsx`;
    XLSX.writeFile(wb, fileName);
  };

  return (
    <div className="dashboard-layout">
      <Navbar user={user} onLogout={logout} />
      <Sidebar />
      <main className="dashboard-content">
        {/* Bọc tiêu đề và bộ lọc trong một container flex */}
        <div className="header-section">
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
          <div className="table-header">
            <div className="table-actions">
              <div className="table-filter">
                <button className="filter-button" onClick={toggleMonthDropdown}>
                  <span className="filter-text">{monthText}</span>
                  <span className="material-symbols-outlined filter-icon">
                    filter_list
                  </span>
                </button>
                {isMonthDropdownOpen && (
                  <ul className="filter-dropdown">
                    {monthOptions.map((month) => (
                      <li
                        key={month.value ?? "all"}
                        onClick={() => handleMonthOptionClick(month.value)}
                      >
                        {month.text}
                      </li>
                    ))}
                  </ul>
                )}
              </div>
              <div className="table-filter">
                <button className="filter-button" onClick={toggleYearDropdown}>
                  <span className="filter-text">{yearText}</span>
                  <span className="material-symbols-outlined filter-icon">
                    filter_list
                  </span>
                </button>
                {isYearDropdownOpen && (
                  <ul className="filter-dropdown">
                    <li onClick={() => handleYearOptionClick(null)}>Năm</li>
                    {availableYears.map((year) => (
                      <li
                        key={year}
                        onClick={() => handleYearOptionClick(year)}
                      >
                        {year}
                      </li>
                    ))}
                  </ul>
                )}
              </div>
              <button className="table-download" onClick={handleDownloadExcel}>
                <span className="download-text">Tải xuống</span>
                <span className="material-symbols-outlined download-icon">
                  download
                </span>
              </button>
            </div>
          </div>
        </div>

        {activeSubtitle === "Báo cáo" && (
          <div>
            <div className="dashboard-top">
              <div className="dashboard-chart">
                <RevenueChart
                  chartData={revenueChartData}
                  showDownload={false}
                  showFilter={false}
                />
              </div>
              <div className="dashboard-widgets">
                <Widget
                  title="Doanh thu từ hệ thống đề xuất"
                  value={formatRevenue(stats.revenue.value)}
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
                <CategoryChart
                  chartData={categoryChartData}
                  showDownload={false}
                  showFilter={false}
                />
              </div>
              <div className="dashboard-bottom-table">
                <Table
                  title="Top sản phẩm"
                  data={topProducts}
                  columns={columns}
                  showHeader={true}
                  showSearch={false}
                  showFilter={true}
                  showDownload={false}
                  showFilter={false}
                  showDownload={true}
                  showAddButton={false}
                  showCheckbox={false}
                  showSort={true}
                  onDownload={handleExportAllReportExcel}
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

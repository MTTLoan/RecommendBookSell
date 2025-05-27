import React, { useState, useEffect } from "react";
import "../../styles/table.css";
import * as XLSX from "xlsx";
import { generateWidgetDataForExport } from "../../utils/widgetUtils";

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

const Table = ({
  title,
  data = [],
  columns = [],
  onAdd,
  searchValue = "",
  setSearchValue,
  onSearch,
  showHeader = true,
  showSearch = true,
  showFilter = true,
  showMonthFilter = false,
  showYearFilter = false,
  showDownload = false,
  showAddButton = false,
  addButtonText = "Thêm",
  downloadButtonText = "Tải xuống",
  showCheckbox = true,
  showSort = true,
  showNavigationBar = false,
  filterValue,
  setFilterValue,
  monthFilter,
  setMonthFilter,
  yearFilter,
  setYearFilter,
  availableYears = [],
  tabs = [],
  widgetStats = {},
  exportConfig = {}, // Thêm config cho xuất file Excel
  chartData = [],
  onDownload, // Thêm prop onDownload để tuỳ chỉnh xuất file
}) => {
  const [currentPage, setCurrentPage] = useState(1);
  const [rowsPerPage] = useState(20);
  const [sortConfig, setSortConfig] = useState({ key: null, direction: "asc" });
  const [selectedRows, setSelectedRows] = useState([]);
  const [isFilterDropdownOpen, setIsFilterDropdownOpen] = useState(false);
  const [isMonthDropdownOpen, setIsMonthDropdownOpen] = useState(false);
  const [isYearDropdownOpen, setIsYearDropdownOpen] = useState(false);
  const [categoryText, setCategoryText] = useState("Tất cả");
  const [monthText, setMonthText] = useState("Tất cả tháng");
  const [yearText, setYearText] = useState("Năm");
  const [activeTab, setActiveTab] = useState(tabs[0] || "");

  // Lọc dữ liệu dựa trên filterValue (hỗ trợ filter động cho mọi loại filter)
  let filteredData = data || [];
  // Xác định filterCol chỉ cho UI dropdown, không dùng cho filter logic nữa
  const filterCol = columns.find(
    (col) => Array.isArray(col.filters) && col.filters.length > 0
  );
  if (filterValue) {
    if (
      ["lt100", "100-300", "300-500", "gt500", "instock", "outstock"].includes(
        filterValue
      )
    ) {
      filteredData = data.filter((row) => {
        if (["lt100", "100-300", "300-500", "gt500"].includes(filterValue)) {
          const price = Number(String(row.price).replace(/[^\d]/g, ""));
          if (filterValue === "lt100") return price < 100000;
          if (filterValue === "100-300")
            return price >= 100000 && price <= 300000;
          if (filterValue === "300-500")
            return price > 300000 && price <= 500000;
          if (filterValue === "gt500") return price > 500000;
        } else if (filterValue === "instock") {
          return row.status === "Còn hàng";
        } else if (filterValue === "outstock") {
          return row.status === "Hết hàng";
        }
        return true;
      });
    } else if (filterCol) {
      filteredData = data.filter(
        (row) => String(row[filterCol.key]) === String(filterValue)
      );
    }
  }

  const totalPages = Math.ceil(filteredData.length / rowsPerPage);
  const startIndex = (currentPage - 1) * rowsPerPage;
  const endIndex = Math.min(startIndex + rowsPerPage, filteredData.length);
  const paginatedData = filteredData.slice(startIndex, endIndex);

  const handleSort = (key) => {
    if (!showSort) return;
    let direction = "asc";
    if (sortConfig.key === key && sortConfig.direction === "asc") {
      direction = "desc";
    }
    setSortConfig({ key, direction });
  };

  const sortedData = [...paginatedData].sort((a, b) => {
    if (sortConfig.key) {
      const col = columns.find((c) => c.key === sortConfig.key);
      if (col && typeof col.sorter === "function") {
        return sortConfig.direction === "asc"
          ? col.sorter(a, b)
          : -col.sorter(a, b);
      }
      if (sortConfig.key === "id") {
        return sortConfig.direction === "asc"
          ? Number(a.id) - Number(b.id)
          : Number(b.id) - Number(a.id);
      }
      return sortConfig.direction === "asc"
        ? a[sortConfig.key] > b[sortConfig.key]
          ? 1
          : -1
        : a[sortConfig.key] < b[sortConfig.key]
        ? 1
        : -1;
    }
    return 0;
  });

  const handleSelectAll = (isChecked) => {
    if (isChecked) {
      const currentPageIds = sortedData.map((row) => row.id);
      setSelectedRows(currentPageIds);
    } else {
      const currentPageIds = sortedData.map((row) => row.id);
      setSelectedRows((prevSelected) =>
        prevSelected.filter((id) => !currentPageIds.includes(id))
      );
    }
  };

  const handlePageChange = (newPage) => {
    if (newPage >= 1 && newPage <= totalPages) {
      setCurrentPage(newPage);
    }
  };

  const handleSelectRow = (id) => {
    setSelectedRows((prevSelected) =>
      prevSelected.includes(id)
        ? prevSelected.filter((rowId) => rowId !== id)
        : [...prevSelected, id]
    );
  };

  useEffect(() => {
    if (!filterValue) {
      setCategoryText("Tất cả");
    } else {
      const found = filterCol?.filters.find(
        (f) => String(f.value) === String(filterValue)
      );
      setCategoryText(found ? found.text : "Tất cả");
    }
  }, [filterValue, columns, filterCol]);

  useEffect(() => {
    if (monthFilter === null || monthFilter === undefined) {
      setMonthText("Tất cả tháng");
    } else {
      const found = monthOptions.find((m) => m.value === monthFilter);
      setMonthText(found ? found.text : "Tất cả tháng");
    }
  }, [monthFilter]);

  useEffect(() => {
    if (yearFilter === null || yearFilter === undefined) {
      setYearText("Năm");
    } else {
      setYearText(yearFilter.toString());
    }
  }, [yearFilter]);

  const handleFilterOptionClick = (value) => {
    const found = filterCol?.filters.find(
      (f) => String(f.value) === String(value)
    );
    setCategoryText(found ? found.text : "Tất cả");
    setFilterValue(value || "");
    setIsFilterDropdownOpen(false);
    setCurrentPage(1);
  };

  const handleMonthOptionClick = (value) => {
    const found = monthOptions.find((m) => m.value === value);
    setMonthText(found ? found.text : "Tất cả tháng");
    setMonthFilter(value);
    setIsMonthDropdownOpen(false);
    setCurrentPage(1);
  };

  const handleYearOptionClick = (value) => {
    setYearText(value === null ? "Năm" : value.toString());
    setYearFilter(value);
    setIsYearDropdownOpen(false);
    setCurrentPage(1);
  };

  const toggleFilterDropdown = () => {
    setIsFilterDropdownOpen((prev) => !prev);
    setIsMonthDropdownOpen(false);
    setIsYearDropdownOpen(false);
  };

  const toggleMonthDropdown = () => {
    setIsMonthDropdownOpen((prev) => !prev);
    setIsFilterDropdownOpen(false);
    setIsYearDropdownOpen(false);
  };

  const toggleYearDropdown = () => {
    setIsYearDropdownOpen((prev) => !prev);
    setIsFilterDropdownOpen(false);
    setIsMonthDropdownOpen(false);
  };

  const handleSearchInput = (e) => {
    setSearchValue(e.target.value);
  };

  const handleSearchKeyDown = (e) => {
    if (e.key === "Enter" && onSearch) {
      onSearch(searchValue);
    }
  };

  const handleSearchClick = () => {
    if (onSearch) onSearch(searchValue);
  };

  // Hàm xuất file Excel
  const handleDownloadExcel = () => {
    const {
      fileNamePrefix = "Báo cáo",
      worksheetNames = {
        widget: "ThongKe",
        table: "DanhSach",
        chart: "BieuDo",
      },
    } = exportConfig;

    // 1. Worksheet cho dữ liệu widget (nếu có)
    const widgetWorksheetData =
      widgetStats && Object.keys(widgetStats).length
        ? generateWidgetDataForExport(widgetStats, monthFilter)
        : [["Không có dữ liệu widget"]];

    // 2. Worksheet cho dữ liệu bảng
    const headers = columns.map((col) => col.label);
    const excelData = filteredData.map((row) =>
      columns.map((col) => {
        if (col.key === "price") {
          return row[col.key]?.toLocaleString("vi-VN") + " VND";
        }
        return row[col.key] || "";
      })
    );
    const tableWorksheetData = [headers, ...excelData];

    // 3. Worksheet cho dữ liệu biểu đồ (nếu có)
    const chartHeaders = ["Thời gian", "Giá trị"];
    const chartWorksheetData =
      chartData.length > 0
        ? [
            chartHeaders,
            ...chartData.map((item) => [
              item.time || item.date || "Không xác định",
              (item.value || item.revenue || 0)?.toLocaleString("vi-VN") || "0",
            ]),
          ]
        : [chartHeaders];

    // Tạo workbook
    const workbook = XLSX.utils.book_new();

    // Thêm worksheet cho widget
    const widgetWorksheet = XLSX.utils.aoa_to_sheet(widgetWorksheetData);
    XLSX.utils.book_append_sheet(
      workbook,
      widgetWorksheet,
      worksheetNames.widget
    );

    // Thêm worksheet cho bảng
    const tableWorksheet = XLSX.utils.aoa_to_sheet(tableWorksheetData);
    XLSX.utils.book_append_sheet(
      workbook,
      tableWorksheet,
      worksheetNames.table
    );

    // Thêm worksheet cho biểu đồ
    const chartWorksheet = XLSX.utils.aoa_to_sheet(chartWorksheetData);
    XLSX.utils.book_append_sheet(
      workbook,
      chartWorksheet,
      worksheetNames.chart
    );

    // Tạo tên file dựa trên các bộ lọc
    let fileName;
    const currentDate = new Date().toISOString().split("T")[0];
    if (yearFilter === null || yearFilter === undefined) {
      fileName = `${fileNamePrefix}_${currentDate}_${categoryText}.xlsx`;
    } else if (monthFilter === null || monthFilter === undefined) {
      fileName = `${fileNamePrefix}_${yearFilter}_${categoryText}.xlsx`;
    } else {
      const month = String(monthFilter + 1).padStart(2, "0");
      fileName = `${fileNamePrefix}_${yearFilter}-${month}_${categoryText}.xlsx`;
    }

    // Xuất file Excel
    XLSX.writeFile(workbook, fileName);
  };

  return (
    <div className="table-container">
      {showHeader && (
        <div className="table-title">
          <h2>{title}</h2>
        </div>
      )}
      <div className="table-header">
        {showSearch && (
          <div className="table-search">
            <input
              type="text"
              className="search-input"
              placeholder="Tìm kiếm"
              value={searchValue}
              onChange={handleSearchInput}
              onKeyDown={handleSearchKeyDown}
            />
            <span
              className="material-symbols-outlined search-icon"
              style={{ cursor: "pointer" }}
              onClick={handleSearchClick}
            >
              search
            </span>
          </div>
        )}
        <div className="table-actions">
          {showFilter && filterCol && (
            <div className="table-filter">
              <button className="filter-button" onClick={toggleFilterDropdown}>
                <span className="filter-text">{categoryText}</span>
                <span className="material-symbols-outlined filter-icon">
                  filter_list
                </span>
              </button>
              {isFilterDropdownOpen && (
                <ul className="filter-dropdown">
                  <li onClick={() => handleFilterOptionClick("")}>Tất cả</li>
                  {filterCol.filters.map((filter) => (
                    <li
                      key={filter.value}
                      onClick={() => handleFilterOptionClick(filter.value)}
                    >
                      {filter.text}
                    </li>
                  ))}
                </ul>
              )}
            </div>
          )}
          {showMonthFilter && (
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
          )}
          {showYearFilter && (
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
                    <li key={year} onClick={() => handleYearOptionClick(year)}>
                      {year}
                    </li>
                  ))}
                </ul>
              )}
            </div>
          )}
          {showDownload && (
            <button
              className="table-download"
              onClick={onDownload ? onDownload : handleDownloadExcel}
            >
              <span className="download-text">{downloadButtonText}</span>
              <span className="material-symbols-outlined download-icon">
                download
              </span>
            </button>
          )}
          {showAddButton && (
            <button className="add-button" onClick={onAdd}>
              <span className="add-text">{addButtonText}</span>
              <span className="material-symbols-outlined add-icon">add</span>
            </button>
          )}
        </div>
      </div>
      {showNavigationBar && tabs.length > 0 && (
        <div className="table-navigation">
          {tabs.map((tab, index) => (
            <span
              key={index}
              className={`nav-tab ${tab === activeTab ? "active" : ""}`} // Compare with activeTab
              onClick={() => setActiveTab(tab)} // Update active tab on click
            >
              {tab}
            </span>
          ))}
        </div>
      )}
      {/* Bảng dữ liệu */}
      <div className="table-content-spacing table-scroll">
        {data && data.length > 0 ? (
          <table className="table">
            <thead>
              <tr>
                {showCheckbox && (
                  <th style={{ width: "100px" }}>
                    <input
                      type="checkbox"
                      onChange={(e) => handleSelectAll(e.target.checked)}
                      checked={sortedData.every((row) =>
                        selectedRows.includes(row.id)
                      )}
                    />
                  </th>
                )}
                {columns.map((col) => (
                  <th
                    key={col.key}
                    style={
                      col.width
                        ? {
                            width: col.width,
                            minWidth: col.width,
                            maxWidth: col.width,
                          }
                        : {}
                    }
                    onClick={() => !col.disableSort && handleSort(col.key)}
                  >
                    {col.label}
                    {showSort && !col.disableSort && (
                      <span className="sort-icon material-symbols-outlined">
                        {sortConfig.key === col.key
                          ? sortConfig.direction === "asc"
                            ? "arrow_upward"
                            : "arrow_downward"
                          : "unfold_more"}
                      </span>
                    )}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {sortedData.map((row, index) => (
                <tr
                  key={index}
                  className={
                    selectedRows.includes(row.id) ? "selected-row" : ""
                  }
                >
                  {showCheckbox && (
                    <td style={{ width: "70px" }}>
                      <input
                        type="checkbox"
                        checked={selectedRows.includes(row.id)}
                        onChange={() => handleSelectRow(row.id)}
                      />
                    </td>
                  )}
                  {columns.map((col) => (
                    <td
                      key={col.key}
                      style={
                        col.width
                          ? {
                              width: col.width,
                              minWidth: col.width,
                              maxWidth: col.width,
                            }
                          : {}
                      }
                    >
                      {col.render ? col.render(row) : row[col.key] || ""}
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <div className="no-data-message">Không có dữ liệu.</div>
        )}
      </div>
      {/* Phân trang */}
      <div className="table-footer">
        <div className="pagination-left">
          Hiển thị {filteredData.length === 0 ? 0 : startIndex + 1}-
          {filteredData.length === 0 ? 0 : startIndex + paginatedData.length}{" "}
          của {filteredData.length} dòng
        </div>
        <div className="pagination-right">
          <span>Trang</span>
          <select
            className="page-select"
            value={currentPage}
            onChange={(e) => handlePageChange(Number(e.target.value))}
          >
            {Array.from({ length: totalPages }, (_, i) => (
              <option key={i + 1} value={i + 1}>
                {i + 1}
              </option>
            ))}
          </select>
          <button
            className="page-button"
            onClick={() => handlePageChange(currentPage - 1)}
            disabled={currentPage === 1}
          >
            «
          </button>
          <button
            className="page-button"
            onClick={() => handlePageChange(currentPage + 1)}
            disabled={currentPage === totalPages}
          >
            »
          </button>
        </div>
      </div>
    </div>
  );
};

export default Table;

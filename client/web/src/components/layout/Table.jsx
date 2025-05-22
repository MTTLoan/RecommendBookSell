import React, { useState } from 'react';
import '../../styles/table.css';

const Table = ({
  title,
  data,
  columns,
  onAdd,
  searchValue,
  setSearchValue,
  onSearch,
  showHeader = true,
  showSearch = true,
  showFilter = true,
  showDownload = true,
  showAddButton = true,
  addButtonText = "Thêm sản phẩm",
  downloadButtonText = "Tải xuống",
  showCheckbox = true,
  showSort = true,
  showNavigationBar = false,
  tabs = [],
}) => {
  const [currentPage, setCurrentPage] = useState(1);
  const [rowsPerPage] = useState(20);
  const [sortConfig, setSortConfig] = useState({ key: null, direction: 'asc' });
  const [selectedRows, setSelectedRows] = useState([]);
  const [isFilterDropdownOpen, setIsFilterDropdownOpen] = useState(false);
  const [filterText, setFilterText] = useState('Lọc');
  const [activeTab, setActiveTab] = useState(tabs.length > 0 ? tabs[0] : '');

  const totalPages = Math.ceil(data.length / rowsPerPage);
  const startIndex = (currentPage - 1) * rowsPerPage;
  const endIndex = Math.min(startIndex + rowsPerPage, data.length);
  const paginatedData = data.slice(startIndex, endIndex);

  // Xử lý sắp xếp
  const handleSort = (key) => {
    if (!showSort) return;
    let direction = 'asc';
    if (sortConfig.key === key && sortConfig.direction === 'asc') {
      direction = 'desc';
    }
    setSortConfig({ key, direction });
  };

  const sortedData = [...paginatedData].sort((a, b) => {
    if (sortConfig.key) {
      const col = columns.find(c => c.key === sortConfig.key);
      if (col && typeof col.sorter === 'function') {
        return sortConfig.direction === 'asc'
          ? col.sorter(a, b)
          : -col.sorter(a, b);
      } else {
        // Nếu sort theo id (mã), so sánh số
        if (sortConfig.key === 'id') {
          return sortConfig.direction === 'asc'
            ? Number(a.id) - Number(b.id)
            : Number(b.id) - Number(a.id);
        }
        // So sánh mặc định
        if (sortConfig.direction === 'asc') {
          return a[sortConfig.key] > b[sortConfig.key] ? 1 : -1;
        } else {
          return a[sortConfig.key] < b[sortConfig.key] ? 1 : -1;
        }
      }
    }
    return 0;
  });

  // Xử lý chọn tất cả
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

  // Xử lý chọn từng dòng
  const handleSelectRow = (id) => {
    setSelectedRows((prevSelected) =>
      prevSelected.includes(id)
        ? prevSelected.filter((rowId) => rowId !== id)
        : [...prevSelected, id]
    );
  };

  // Xử lý toggle dropdown filter
  const toggleFilterDropdown = () => {
    setIsFilterDropdownOpen((prev) => !prev);
  };

  // Xử lý chọn một tùy chọn trong dropdown filter
  const handleFilterOptionClick = (option) => {
    setFilterText(option);
    setIsFilterDropdownOpen(false);
  };

    // Xử lý tìm kiếm
const handleSearchInput = (e) => {
  setSearchValue(e.target.value);
};

const handleSearchKeyDown = (e) => {
  if (e.key === 'Enter' && onSearch) {
    onSearch(searchValue);
  }
};

const handleSearchClick = () => {
  if (onSearch) onSearch(searchValue);
};

  return (
    <div className="table-container">
      {/* Header tên bảng */}
      {showHeader && (
        <div className="table-title">
          <h2>{title}</h2>
        </div>
      )}

      <div className="table-header">
        {/* Ô tìm kiếm */}
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
            <span className="material-symbols-outlined search-icon"
              style={{ cursor: 'pointer' }}
              onClick={handleSearchClick}
            >
              search</span>
          </div>
        )}

        {/* Các nút hành động */}
        <div className="table-actions">
          {showFilter && (
            <div className="table-filter">
              <button className="filter-button" onClick={toggleFilterDropdown}>
                <span className="filter-text">{filterText}</span>
                <span className="material-symbols-outlined filter-icon">filter_list</span>
              </button>
              {isFilterDropdownOpen && (
                <ul className="filter-dropdown">
                  <li onClick={() => handleFilterOptionClick('Tất cả')}>Tất cả</li>
                  <li onClick={() => handleFilterOptionClick('Còn hàng')}>Còn hàng</li>
                  <li onClick={() => handleFilterOptionClick('Hết hàng')}>Hết hàng</li>
                </ul>
              )}
            </div>
          )}
          {showDownload && (
            <button className="table-download" onClick={() => console.log('Tải xuống')}>
              <span className="download-text">{downloadButtonText}</span>
              <span className="material-symbols-outlined download-icon">download</span>
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

      {/* Navigation bar */}
      {showNavigationBar && tabs.length > 0 && (
        <div className="table-navigation">
          {tabs.map((tab, index) => (
            <span
              key={index}
              className={`nav-tab ${activeTab === tab ? 'active' : ''}`}
              onClick={() => setActiveTab(tab)}
            >
              {tab}
            </span>
          ))}
        </div>
      )}

      {/* Bảng */}
      <div className="table-scroll">
        <table className="table">
          <thead>
            <tr>
              {showCheckbox && (
                <th style={{ width: '100px' }}>
                  <input
                    type="checkbox"
                    onChange={(e) => handleSelectAll(e.target.checked)}
                    checked={sortedData.every((row) => selectedRows.includes(row.id))}
                  />
                </th>
              )}
              {columns.map((col) => (
                <th
                  key={col.key}
                  style={col.width ? { width: col.width, minWidth: col.width, maxWidth: col.width } : {}}
                  onClick={() => !col.disableSort && handleSort(col.key)}
                >
                  {col.label}
                  {showSort && !col.disableSort && (
                    <span className="sort-icon material-symbols-outlined">
                      {sortConfig.key === col.key
                        ? sortConfig.direction === 'asc'
                          ? 'arrow_upward'
                          : 'arrow_downward'
                        : 'unfold_more'}
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
                className={selectedRows.includes(row.id) ? 'selected-row' : ''}
              >
                {showCheckbox && (
                  <td style={{ width: '70px' }}>
                    <input
                      type="checkbox"
                      checked={selectedRows.includes(row.id)}
                      onChange={() => handleSelectRow(row.id)}
                    />
                  </td>
                )}
                {columns.map((col) => (
                  <td key={col.key}
                    style={col.width ? { width: col.width, minWidth: col.width, maxWidth: col.width } : {}}>
                    {col.render ? col.render(row) : row[col.key]}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Phân trang */}
      <div className="table-footer">
        <div className="pagination-left">
          Hiển thị {startIndex + 1}-{endIndex} của {data.length} dòng
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
            &laquo;
          </button>
          <button
            className="page-button"
            onClick={() => handlePageChange(currentPage + 1)}
            disabled={currentPage === totalPages}
          >
            &raquo;
          </button>
        </div>
      </div>
    </div>
  );
};

export default Table;
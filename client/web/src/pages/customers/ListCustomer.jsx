import React, { useEffect, useState } from "react";
import Navbar from "../../components/layout/Navbar";
import Sidebar from "../../components/layout/Sidebar";
import Table from "../../components/layout/Table";
import defaultAvatar from "../../assets/images/default-avatar.jpg";
import "../../styles/listcustomer.css";
import { fetchAllCustomers, adminDeleteUser } from "../../services/authService";
import Popup from "../../components/common/Popup";
import { useNavigate } from "react-router-dom";

const ListCustomer = () => {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchValue, setSearchValue] = useState("");
  const [showDelete, setShowDelete] = useState(false);
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [showSuccess, setShowSuccess] = useState(false);
  const [successTimeout, setSuccessTimeout] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await fetchAllCustomers();
      const users = res.users || res;
      setCustomers(users);
    } catch {
      setCustomers([]);
    }
    setLoading(false);
  };

  const handleSearch = (query) => {
    setSearchValue(query);
  };

  const filteredCustomers = customers.filter((c) =>
    c.fullName?.toLowerCase().includes(searchValue.toLowerCase())
  );

  const handleDelete = async () => {
    if (!selectedCustomer) return;
    try {
      await adminDeleteUser(selectedCustomer.id);
      await loadData();
      setShowDelete(false);
      setSelectedCustomer(null);
      setShowSuccess(true);
      const timeout = setTimeout(() => {
        setShowSuccess(false);
        navigate("/customers");
      }, 2500);
      setSuccessTimeout(timeout);
    } catch (err) {
      alert("Xóa khách hàng thất bại!");
    }
  };

  useEffect(() => {
    return () => {
      if (successTimeout) clearTimeout(successTimeout);
    };
  }, [successTimeout]);

  const columns = [
    {
      key: "fullName",
      label: "Tên",
      render: (user) => (
        <div className="customer-info">
          <img
            src={user.avatar || defaultAvatar}
            alt={user.fullName}
            style={{
              width: 36,
              height: 36,
              borderRadius: "50%",
              marginRight: 8,
              objectFit: "cover",
            }}
          />
          <span>{user.fullName}</span>
        </div>
      ),
    },
    { key: "phoneNumber", label: "Liên hệ" },
    {
      key: "address",
      label: "Địa chỉ",
      render: (user) => user.addressDetail || "Chưa cập nhật",
    },
    {
      key: "revenue",
      label: "Doanh thu",
      render: (user) => user.revenue?.toLocaleString("vi-VN") + " đ",
    },
    {
      key: "orderCount",
      label: "Số đơn hàng",
      render: (user) => user.orderCount,
    },
    {
      key: "createdAt",
      label: "Ngày tạo",
      render: (user) =>
        user.createdAt
          ? new Date(user.createdAt).toLocaleDateString("vi-VN")
          : "",
    },
    {
      key: "actions",
      label: "Hành động",
      render: (user) => (
        <div className="actions">
          <span
            className="material-symbols-outlined action-icon"
            title="Xem"
            style={{ cursor: "pointer" }}
            onClick={() => navigate(`/customers/view/${user.id}`)}
          >
            visibility
          </span>
          <span
            className="material-symbols-outlined action-icon"
            title="Sửa"
            style={{ cursor: "pointer" }}
            onClick={() => navigate(`/customers/edit/${user.id}`)}
          >
            edit_square
          </span>
          <span
            className="material-symbols-outlined action-icon"
            title="Xoá"
            style={{ cursor: "pointer" }}
            onClick={() => {
              setSelectedCustomer(user);
              setShowDelete(true);
            }}
          >
            delete
          </span>
        </div>
      ),
      disableSort: true,
    },
  ];

  // Thêm hàm xuất Excel
  const handleExportCustomerExcel = () => {
    // Lấy các cột không phải Hành động
    const exportColumns = columns.filter((col) => col.key !== "actions");
    const headers = exportColumns.map((col) => col.label);
    // Lấy dữ liệu đang hiển thị (sau filter/search)
    const exportData = filteredCustomers.map((row) =>
      exportColumns.map((col) => {
        if (col.key === "revenue") {
          return (row.revenue || 0).toLocaleString("vi-VN") + " đ";
        }
        if (col.key === "createdAt") {
          return row.createdAt
            ? new Date(row.createdAt).toLocaleDateString("vi-VN")
            : "";
        }
        if (col.key === "address") {
          return row.addressDetail || "Chưa cập nhật";
        }
        if (col.key === "fullName") {
          return row.fullName;
        }
        return row[col.key] || "";
      })
    );
    const worksheetData = [headers, ...exportData];
    // Tên file: Khách hàng_(tìm kiếm hoặc Tất cả)_(ngày-giờ tải).xlsx
    let filterLabel = "Tất cả";
    if (searchValue) {
      filterLabel = `Tìm: ${searchValue}`;
    }
    const now = new Date();
    const dateStr = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(
      2,
      "0"
    )}-${String(now.getDate()).padStart(2, "0")}_${String(
      now.getHours()
    ).padStart(2, "0")}${String(now.getMinutes()).padStart(2, "0")}`;
    const fileName = `Khách hàng_${filterLabel}_${dateStr}.xlsx`;
    const XLSX = require("xlsx");
    const wb = XLSX.utils.book_new();
    const ws = XLSX.utils.aoa_to_sheet(worksheetData);
    XLSX.utils.book_append_sheet(wb, ws, "Danh sách khách hàng");
    XLSX.writeFile(wb, fileName);
  };

  return (
    <div className="dashboard-layout">
      <Navbar />
      <Sidebar />
      <main className="dashboard-content">
        {/* Title và Subtitle */}
        <div className="product-header">
          <h1>Khách hàng</h1>
          <p className="product-subtitle">
            <span className="subtitle active">Khách hàng</span>
          </p>
        </div>
        {/* Bảng danh sách sản phẩm */}
        <Table
          title=""
          data={filteredCustomers}
          columns={columns}
          showHeader={true}
          showSearch={true}
          showFilter={false}
          showDownload={true}
          showAddButton={true}
          showCheckbox={false}
          showSort={true}
          addButtonText="Thêm khách hàng"
          downloadButtonText="Xuất file"
          searchValue={searchValue}
          setSearchValue={setSearchValue}
          onSearch={handleSearch}
          onAdd={() => navigate("/customers/add")}
          onDownload={handleExportCustomerExcel}
        />
        <Popup
          open={showDelete && selectedCustomer}
          title="Xác nhận xóa khách hàng"
          titleColor="error"
          content={
            selectedCustomer
              ? `Bạn có chắc muốn xóa khách hàng "${selectedCustomer.fullName}" không?`
              : ""
          }
          contentColor="error"
          onClose={() => setShowDelete(false)}
          onConfirm={handleDelete}
          confirmText="Xóa"
          confirmColor="error"
          cancelText="Hủy"
          cancelColor="gray"
        />
        <Popup
          open={showSuccess}
          title="Thành công"
          titleColor="success"
          content="Xóa khách hàng thành công!"
          contentColor="success"
          showCancel={false}
          showConfirm={false}
        />
      </main>
    </div>
  );
};

export default ListCustomer;

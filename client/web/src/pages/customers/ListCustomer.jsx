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
    } catch (err) {
      alert("Xóa khách hàng thất bại!");
    }
  };
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
          showCheckbox={true}
          showSort={true}
          addButtonText="Thêm khách hàng"
          downloadButtonText="Xuất file"
          searchValue={searchValue}
          setSearchValue={setSearchValue}
          onSearch={handleSearch}
          onAdd={() => navigate("/customers/add")}
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
      </main>
    </div>
  );
};

export default ListCustomer;

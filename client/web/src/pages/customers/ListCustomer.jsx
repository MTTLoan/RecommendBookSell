import React, { useEffect, useState } from 'react';
import Navbar from '../../components/layout/Navbar';
import Sidebar from '../../components/layout/Sidebar';
import Table from '../../components/layout/Table';
import defaultAvatar from '../../assets/images/default-avatar.jpg';
import '../../styles/customer.css';
import { fetchAllCustomers, searchCustomers } from '../../services/authService';


const ListCustomer = () => {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchValue, setSearchValue] = useState('');


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

  const handleSearch = async (query) => {
    setSearchValue(query);
    setLoading(true);
    try {
      if (!query) {
        await loadData();
      } else {
        const users = await searchCustomers(query);
        setCustomers(users);
      }
    } catch {
      setCustomers([]);
    }
    setLoading(false);
  };

  const columns = [
    {
      key: 'fullName',
      label: 'Tên',
      render: (user) => (
        <div className="customer-info">
          <img
            src={user.avatar || defaultAvatar}
            alt={user.fullName}
            style={{ width: 36, height: 36, borderRadius: '50%', marginRight: 8, objectFit: 'cover' }}
          />
          <span>{user.fullName}</span>
        </div>
      ),
    },
    { key: 'phoneNumber', label: 'Liên hệ' },
    {
      key: 'address',
      label: 'Địa chỉ',
      render: (user) =>
        user.addressDetail || 'Chưa cập nhật'
    },
    {
      key: 'revenue',
      label: 'Doanh thu',
      render: () => '---' // Nếu muốn tính doanh thu, cần backend trả về
    },
    {
      key: 'orderCount',
      label: 'Số đơn hàng',
      render: () => '---' // Nếu muốn tính số đơn, cần backend trả về
    },
        {
      key: 'createdAt',
      label: 'Ngày tạo',
      render: (user) =>
        user.createdAt
          ? new Date(user.createdAt).toLocaleDateString('vi-VN')
          : ''
    },
    {
      key: 'actions',
      label: 'Hành động',
      render: (user) => (
        <div className="actions">
          <span className="material-symbols-outlined action-icon" title="Xem">visibility</span>
          <span className="material-symbols-outlined action-icon" title="Sửa">edit_square</span>
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
          data={customers}
          columns={columns}
          showHeader={true}
          showSearch={true}
          showFilter={true}
          showDownload={true}
          showAddButton={false}
          showCheckbox={true}
          showSort={true}
          downloadButtonText="Xuất file"
          searchValue={searchValue}
          setSearchValue={setSearchValue}
          onSearch={handleSearch}
        />
      </main>
    </div>
  );
};

export default ListCustomer;
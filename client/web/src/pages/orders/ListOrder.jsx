import React, { useState, useEffect } from 'react';
import Navbar from '../../components/layout/Navbar';
import Sidebar from '../../components/layout/Sidebar';
import Table from '../../components/layout/Table';
import '../../styles/order.css';
import { fetchAllOrders, searchOrders } from '../../services/orderService';


const ListOrder = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchValue, setSearchValue] = useState('');

    useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await fetchAllOrders();
      setOrders(res.data || []);
    } catch {
      setOrders([]);
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
        const res = await searchOrders(query);
        setOrders(res.data?.data || []);
      }
    } catch {
      setOrders([]);
    }
    setLoading(false);
  };

  const columns = [
    {
    key: 'order',
    label: 'Đơn hàng',
    sorter: (a, b) => a.id - b.id,
    render: (order) => {
      const firstItem = order.items?.[0];
      return (
        <div className="product-info">
          <img
            src={firstItem?.bookImage || 'https://via.placeholder.com/40x40?text=No+Img'}
            alt={firstItem?.bookName || ''}
            className="product-image"
            style={{ width: 40, height: 40, objectFit: 'cover', borderRadius: 8, border: '1px solid #eee' }}
          />
          <div className="product-details">
            <p className="product-name">{firstItem?.bookName || ''}</p>
          </div>
        </div>
        );
      },
    },
    {
    key: 'customer',
    label: 'Khách hàng',
    render: (order) => order.customer || 'Ẩn'
    },
    {
      key: 'totalAmount',
      label: 'Thành tiền',
      render: (order) => order.totalAmount?.toLocaleString('vi-VN') + ' VND'
    },
    {
      key: 'orderDate',
      label: 'Ngày đặt',
      render: (order) =>
        order.orderDate
          ? new Date(order.orderDate).toLocaleDateString('vi-VN')
          : ''
    },
    {
      key: 'status',
      label: 'Trạng thái',
      render: (order) => order.status
    },
    {
      key: 'actions',
      label: 'Hành động',
      render: (order) => (
        <div className="actions">
          <span className="material-symbols-outlined action-icon" title="Xem">visibility</span>
          <span className="material-symbols-outlined action-icon" title="Sửa">edit_square</span>
          <span className="material-symbols-outlined action-icon" title="Xóa">delete</span>
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
          <h1>Đơn hàng</h1>
          <p className="product-subtitle">
            <span className="subtitle active">Tất cả đơn hàng</span>
          </p>
        </div>
        {loading ? (
          <div style={{ padding: 40, textAlign: 'center' }}>Đang tải dữ liệu...</div>
        ) : (
          <Table
            title=""
            data={orders}
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
        )}
      </main>
    </div>
  );
};

export default ListOrder;
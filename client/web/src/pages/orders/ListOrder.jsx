import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../../components/layout/Navbar";
import Sidebar from "../../components/layout/Sidebar";
import Table from "../../components/layout/Table";
import "../../styles/listorder.css";
import { fetchAllOrders } from "../../services/orderService";

const ListOrder = () => {
  const [orders, setOrders] = useState([]);
  const [allOrders, setAllOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchValue, setSearchValue] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await fetchAllOrders();
      setOrders(res.data || []);
      setAllOrders(res.data || []);
    } catch {
      setOrders([]);
      setAllOrders([]);
    }
    setLoading(false);
  };

  useEffect(() => {
    if (!searchValue) {
      setOrders(allOrders);
    } else {
      setOrders(
        allOrders.filter(
          (order) =>
            (order.customer || "")
              .toLowerCase()
              .includes(searchValue.toLowerCase()) ||
            String(order.id).includes(searchValue) ||
            (order.status || "")
              .toLowerCase()
              .includes(searchValue.toLowerCase())
        )
      );
    }
  }, [searchValue, allOrders]);

  const columns = [
    {
      key: "order",
      label: "Đơn hàng",
      sorter: (a, b) => a.id - b.id,
      render: (order) => {
        const firstItem = order.items?.[0];
        return (
          <div>
            <div>
              <b>Mã đơn:</b> {order.id}
            </div>
            <div>
              <b>Số sản phẩm:</b> {order.items?.length || 0}
            </div>
          </div>
        );
      },
    },
    {
      key: "customer",
      label: "Khách hàng",
      render: (order) => order.customer || "Ẩn",
    },
    {
      key: "totalAmount",
      label: "Thành tiền",
      render: (order) => order.totalAmount?.toLocaleString("vi-VN") + " VND",
    },
    {
      key: "orderDate",
      label: "Ngày đặt",
      render: (order) =>
        order.orderDate
          ? new Date(order.orderDate).toLocaleDateString("vi-VN")
          : "",
    },
    {
      key: "status",
      label: "Trạng thái",
      render: (order) => {
        let color = "#888";
        let bg = "#f0f0f0";
        let text = order.status;
        if (text === "Đang đóng gói") {
          color = "#43009b";
          bg = "#eac9ff";
        } else if (text === "Chờ giao hàng") {
          color = "#fd3300";
          bg = "#fff1bd";
        } else if (text === "Đã giao") {
          color = "#04910c";
          bg = "#b2ffb4";
        } else if (text === "Trả hàng") {
          color = "#007afc";
          bg = "#e2feff";
        } else if (text === "Đã hủy") {
          color = "#f00";
          bg = "#ffdcdc";
        }
        return (
          <span
            style={{
              background: bg,
              color: color,
              fontWeight: 700,
              borderRadius: 10,
              padding: "8px 12px",
              display: "inline-block",
            }}
          >
            {text}
          </span>
        );
      },
      filters: [
        { text: "Đang đóng gói", value: "Đang đóng gói" },
        { text: "Chờ giao hàng", value: "Chờ giao hàng" },
        { text: "Đã giao", value: "Đã giao" },
        { text: "Trả hàng", value: "Trả hàng" },
        { text: "Đã hủy", value: "Đã hủy" },
      ],
      onFilter: (value, record) => record.status === value,
    },
    {
      key: "actions",
      label: "Hành động",
      render: (order) => (
        <div className="actions">
          <span
            className="material-symbols-outlined action-icon"
            title="Xem"
            onClick={() => navigate(`/orders/view/${order.id}`)}
            style={{ cursor: "pointer" }}
          >
            visibility
          </span>
          <span
            className="material-symbols-outlined action-icon"
            title="Sửa"
            onClick={() => navigate(`/orders/edit/${order.id}`)}
            style={{ cursor: "pointer" }}
          >
            edit_square
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
          <h1>Đơn hàng</h1>
          <p className="product-subtitle">
            <span className="subtitle active">Tất cả đơn hàng</span>
          </p>
        </div>
        {loading ? (
          <div style={{ padding: 40, textAlign: "center" }}>
            Đang tải dữ liệu...
          </div>
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
            showCheckbox={false}
            showSort={true}
            downloadButtonText="Xuất file"
            searchValue={searchValue}
            setSearchValue={setSearchValue}
          />
        )}
      </main>
    </div>
  );
};

export default ListOrder;

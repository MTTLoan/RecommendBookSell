import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Navbar from "../../components/layout/Navbar";
import Sidebar from "../../components/layout/Sidebar";
import Input from "../../components/common/Input";
import Table from "../../components/layout/Table";
import "../../styles/vieworder.css";
import { fetchAdminOrderById } from "../../services/authService";

const ViewOrder = () => {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const loadOrder = async () => {
      setLoading(true);
      try {
        const data = await fetchAdminOrderById(id);
        setOrder(data);
      } catch {
        setOrder(null);
      }
      setLoading(false);
    };
    loadOrder();
  }, [id]);

  // Cột cho bảng sản phẩm
  const columns = [
    {
      key: "bookName",
      label: "Tên sản phẩm",
      render: (item) => (
        <div style={{ display: "flex", alignItems: "center" }}>
          {item.bookImage && (
            <img
              src={item.bookImage}
              alt={item.bookName}
              style={{
                width: 40,
                height: 40,
                objectFit: "cover",
                marginRight: 8,
                borderRadius: 4,
              }}
            />
          )}
          {item.bookName}
        </div>
      ),
    },
    {
      key: "quantity",
      label: "Số lượng",
      render: (item) => item.quantity,
    },
    {
      key: "unitPrice",
      label: "Đơn giá",
      render: (item) =>
        item.unitPrice ? item.unitPrice.toLocaleString("vi-VN") + " VND" : "",
    },
    {
      key: "total",
      label: "Thành tiền",
      render: (item) =>
        (item.unitPrice * item.quantity).toLocaleString("vi-VN") + " VND",
    },
  ];

  return (
    <div className="dashboard-layout">
      <Navbar />
      <Sidebar />
      <main className="dashboard-content view-product-main">
        <div className="product-header">
          <h1>Đơn hàng</h1>
          <p className="product-subtitle">
            <span
              className="subtitle"
              style={{ cursor: "pointer" }}
              onClick={() => navigate("/orders")}
            >
              Đơn hàng
            </span>
            <span className="subtitle subtitle-sep">{">"}</span>
            <span className="subtitle active">Chi tiết đơn hàng</span>
          </p>
        </div>
        {loading ? (
          <div style={{ padding: 40, textAlign: "center" }}>
            Đang tải dữ liệu...
          </div>
        ) : !order ? (
          <div style={{ padding: 40, textAlign: "center", color: "red" }}>
            Không tìm thấy đơn hàng!
          </div>
        ) : (
          <div
            className="vp-content"
            style={{
              display: "flex",
              gap: 32,
              alignItems: "flex-start",
              marginTop: 24,
            }}
          >
            {/* Bên trái: Bảng sản phẩm */}
            <div className="view-product-col" style={{ flex: 1 }}>
              <div className="vp-box-info">
                <h2>Danh sách sản phẩm</h2>
                <Table
                  data={order.items}
                  columns={columns}
                  showHeader={true}
                  showSearch={false}
                  showFilter={false}
                  showDownload={false}
                  showAddButton={false}
                  showCheckbox={false}
                  showSort={false}
                />
                <div
                  style={{
                    marginTop: 16,
                    textAlign: "right",
                    fontWeight: 600,
                    fontSize: 16,
                  }}
                >
                  Tổng tiền: {order.totalAmount?.toLocaleString("vi-VN")} VND
                </div>
              </div>
            </div>
            {/* Bên phải: Thông tin người nhận */}
            <div className="view-product-col" style={{ width: 340 }}>
              <div className="vp-box-info">
                <h2>Thông tin đơn hàng</h2>
                <div className="vp-form-group">
                  <Input
                    label="Tên khách hàng"
                    value={order.customer || "Ẩn"}
                    disabled
                  />
                </div>
                <div className="vp-form-group">
                  <Input
                    label="Địa chỉ giao hàng"
                    value={order.address || "Ẩn"}
                    disabled
                  />
                </div>
                <div className="vp-form-group">
                  <Input label="Trạng thái" value={order.status} disabled />
                </div>
                <div className="vp-form-group">
                  <Input
                    label="Ngày đặt"
                    value={
                      order.orderDate
                        ? new Date(order.orderDate).toLocaleDateString("vi-VN")
                        : ""
                    }
                    disabled
                  />
                </div>
              </div>
              <div className="vp-box-actions vp-box-actions-alone">
                <button
                  className="btn btn-secondary"
                  onClick={() => navigate("/orders")}
                >
                  Thoát
                </button>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

export default ViewOrder;

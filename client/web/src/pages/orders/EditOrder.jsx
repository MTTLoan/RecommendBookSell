import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Navbar from "../../components/layout/Navbar";
import Sidebar from "../../components/layout/Sidebar";
import Input from "../../components/common/Input";
import Table from "../../components/layout/Table";
import "../../styles/editorder.css";
import { updateOrderStatus } from "../../services/orderService";
import { fetchAdminOrderById } from "../../services/authService";
import Popup from "../../components/common/Popup";

const ORDER_STATUS = [
  "Đang đóng gói",
  "Chờ giao hàng",
  "Đã giao",
  "Trả hàng",
  "Đã hủy",
];

// Hàm xác định các trạng thái hợp lệ tiếp theo cho đơn hàng
function getNextStatusOptions(currentStatus) {
  switch (currentStatus) {
    case "Đang đóng gói":
      return ["Chờ giao hàng", "Đã hủy"];
    case "Chờ giao hàng":
      return ["Đã giao", "Đã hủy"];
    case "Đã giao":
      return ["Trả hàng"];
    case "Trả hàng":
      return [];
    case "Đã hủy":
      return [];
    default:
      return [];
  }
}

const EditOrder = () => {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [status, setStatus] = useState("");
  const [showSuccess, setShowSuccess] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const loadOrder = async () => {
      setLoading(true);
      try {
        const data = await fetchAdminOrderById(id);
        setOrder(data);
        setStatus(data.status || "");
      } catch {
        setOrder(null);
      }
      setLoading(false);
    };
    loadOrder();
  }, [id]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await updateOrderStatus(id, status);
      setShowSuccess(true);
      setTimeout(() => {
        setShowSuccess(false);
        navigate("/orders");
      }, 1200);
    } catch {
      alert("Cập nhật thất bại!");
    }
  };

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
        {showSuccess && (
          <Popup
            open={showSuccess}
            title="Cập nhật thành công!"
            titleColor="success"
            showCancel={false}
            showConfirm={false}
          />
        )}
        <div className="product-header">
          <h1>Chỉnh sửa đơn hàng</h1>
          <p className="product-subtitle">
            <span
              className="subtitle"
              style={{ cursor: "pointer" }}
              onClick={() => navigate("/orders")}
            >
              Đơn hàng
            </span>
            <span className="subtitle subtitle-sep">{">"}</span>
            <span className="subtitle active">Chỉnh sửa đơn hàng</span>
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
          <form
            className="vp-content"
            style={{
              display: "flex",
              gap: 32,
              alignItems: "flex-start",
              marginTop: 24,
            }}
            onSubmit={handleSubmit}
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
                    fontSize: 16,
                    fontWeight: "bold",
                  }}
                >
                  <div style={{ marginBottom: 8 }}>
                    Phí vận chuyển: {(20000).toLocaleString("vi-VN")} VND
                  </div>
                  Tổng tiền: {order.totalAmount?.toLocaleString("vi-VN")} VND
                </div>
              </div>
            </div>
            {/* Bên phải: Thông tin đơn hàng */}
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
                  <label className="input-label">Trạng thái</label>
                  <select
                    className="input"
                    value={status}
                    onChange={(e) => setStatus(e.target.value)}
                    required
                    disabled={
                      order.status === "Đã hủy" ||
                      order.status === "Trả hàng" ||
                      getNextStatusOptions(order.status).length === 0
                    }
                  >
                    <option value="">Chọn trạng thái</option>
                    {getNextStatusOptions(order.status).map((s) => (
                      <option key={s} value={s}>
                        {s}
                      </option>
                    ))}
                  </select>
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
                  type="button"
                  onClick={() => navigate("/orders")}
                >
                  Thoát
                </button>
                <button
                  className="btn btn-primary"
                  type="submit"
                  style={{ marginLeft: 12 }}
                >
                  Lưu
                </button>
              </div>
            </div>
          </form>
        )}
      </main>
    </div>
  );
};

export default EditOrder;

import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Navbar from "../../components/layout/Navbar";
import Sidebar from "../../components/layout/Sidebar";
import Input from "../../components/common/Input";
import Button from "../../components/common/Button";
import defaultAvatar from "../../assets/images/default-avatar.jpg";
import "../../styles/editcustomer.css";
import {
  fetchCustomerDetail,
  fetchProvinces,
  fetchDistricts,
  fetchWards,
  adminUpdateUser,
} from "../../services/authService";

const EditCustomer = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [customer, setCustomer] = useState(null);
  const [loading, setLoading] = useState(true);

  // Dropdown địa chỉ
  const [provinces, setProvinces] = useState([]);
  const [districts, setDistricts] = useState([]);
  const [wards, setWards] = useState([]);

  useEffect(() => {
    setLoading(true);
    fetchCustomerDetail(id)
      .then((data) => {
        setCustomer(data);
        setLoading(false);
      })
      .catch(() => {
        setCustomer(null);
        setLoading(false);
      });
    fetchProvinces().then(setProvinces);
  }, [id]);

  // Khi chọn tỉnh, load quận/huyện
  useEffect(() => {
    if (customer && customer.addressProvince) {
      fetchDistricts(customer.addressProvince).then(setDistricts);
    }
  }, [customer?.addressProvince]);

  // Khi chọn quận, load phường/xã
  useEffect(() => {
    if (customer && customer.addressDistrict) {
      fetchWards(customer.addressDistrict).then(setWards);
    }
  }, [customer?.addressDistrict]);

  const handleChange = (name, value) => {
    setCustomer((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await adminUpdateUser(customer.id, customer);
      alert("Cập nhật thành công!");
      navigate("/customers");
    } catch (err) {
      alert("Cập nhật thất bại!");
    }
  };

  if (loading || !customer) {
    return (
      <div
        className="dashboard-layout"
        style={{ fontFamily: "'Quicksand', sans-serif" }}
      >
        <Navbar />
        <Sidebar />
        <main className="dashboard-content view-product-main">
          <div style={{ padding: 40, textAlign: "center" }}>
            Đang tải dữ liệu...
          </div>
        </main>
      </div>
    );
  }

  return (
    <div
      className="dashboard-layout"
      style={{ fontFamily: "'Quicksand', sans-serif" }}
    >
      <Navbar />
      <Sidebar />
      <main className="dashboard-content view-product-main">
        <div className="product-header">
          <h1>Chỉnh sửa khách hàng</h1>
          <div className="product-subtitle">
            <span
              className="subtitle"
              style={{ cursor: "pointer" }}
              onClick={() => navigate("/customers")}
            >
              Khách hàng
            </span>
            <span className="subtitle subtitle-sep">{">"}</span>
            <span className="subtitle active">Chỉnh sửa khách hàng</span>
          </div>
        </div>
        <form className="vp-content" onSubmit={handleSubmit}>
          {/* Cột trái: Thông tin cơ bản */}
          <div className="view-product-col">
            <div className="vp-box-info">
              <h2>Thông tin khách hàng</h2>
              <div className="vp-form-group">
                <Input type="text" label="ID" value={customer.id} disabled />
              </div>
              <div className="vp-form-group">
                <Input
                  type="text"
                  label="Tên đăng nhập"
                  value={customer.username || ""}
                  onChange={(e) => handleChange("username", e.target.value)}
                  required
                />
              </div>
              <div className="vp-form-group">
                <Input
                  type="text"
                  label="Họ tên"
                  value={customer.fullName || ""}
                  onChange={(e) => handleChange("fullName", e.target.value)}
                  required
                />
              </div>
              <div className="vp-form-group">
                <Input
                  type="text"
                  label="Email"
                  value={customer.email || ""}
                  onChange={(e) => handleChange("email", e.target.value)}
                  required
                />
              </div>
              <div className="vp-form-group">
                <Input
                  type="text"
                  label="Số điện thoại"
                  value={customer.phoneNumber || ""}
                  onChange={(e) => handleChange("phoneNumber", e.target.value)}
                  required
                />
              </div>
              <div className="vp-form-group">
                <Input
                  type="date"
                  label="Ngày sinh"
                  value={
                    customer.birthday ? customer.birthday.slice(0, 10) : ""
                  }
                  onChange={(e) => handleChange("birthday", e.target.value)}
                />
              </div>
            </div>
          </div>
          {/* Cột phải: Địa chỉ, vai trò, trạng thái, avatar, nút lưu/thoát */}
          <div className="view-product-col">
            <div className="vp-box-info">
              <h2>Thông tin bổ sung</h2>
              <div className="vp-form-group">
                <label className="input-label">Tỉnh/Thành phố</label>
                <select
                  className="input"
                  value={customer.addressProvince || ""}
                  onChange={(e) =>
                    handleChange("addressProvince", e.target.value)
                  }
                  required
                >
                  <option value="">Chọn tỉnh/thành</option>
                  {provinces.map((p) => (
                    <option key={p.code} value={p.code}>
                      {p.name}
                    </option>
                  ))}
                </select>
              </div>
              <div className="vp-form-group">
                <label className="input-label">Quận/Huyện</label>
                <select
                  className="input"
                  value={customer.addressDistrict || ""}
                  onChange={(e) =>
                    handleChange("addressDistrict", e.target.value)
                  }
                  required
                  disabled={!customer.addressProvince}
                >
                  <option value="">Chọn quận/huyện</option>
                  {districts.map((d) => (
                    <option key={d.code} value={d.code}>
                      {d.name}
                    </option>
                  ))}
                </select>
              </div>
              <div className="vp-form-group">
                <label className="input-label">Phường/Xã</label>
                <select
                  className="input"
                  value={customer.addressWard || ""}
                  onChange={(e) => handleChange("addressWard", e.target.value)}
                  required
                  disabled={!customer.addressDistrict}
                >
                  <option value="">Chọn phường/xã</option>
                  {wards.map((w) => (
                    <option key={w.code} value={w.code}>
                      {w.name}
                    </option>
                  ))}
                </select>
              </div>
              <div className="vp-form-group">
                <Input
                  type="text"
                  label="Địa chỉ chi tiết"
                  value={customer.addressDetail || ""}
                  onChange={(e) =>
                    handleChange("addressDetail", e.target.value)
                  }
                />
              </div>
              <div className="vp-form-group">
                <label className="input-label">Quyền người dùng</label>
                <select
                  className="input"
                  value={customer.role || "user"}
                  onChange={(e) => handleChange("role", e.target.value)}
                  required
                >
                  <option value="user">Khách hàng</option>
                  <option value="admin">Quản trị viên</option>
                </select>
              </div>
              <div className="vp-form-group">
                <Input
                  type="text"
                  label="Trạng thái"
                  value={customer.verified ? "Đã xác thực" : "Chưa xác thực"}
                  disabled
                />
              </div>
              <div className="vp-form-group" style={{ textAlign: "center" }}>
                <img
                  src={customer.avatar || defaultAvatar}
                  alt="Avatar"
                  style={{
                    width: 100,
                    height: 100,
                    borderRadius: "50%",
                    objectFit: "cover",
                    border: "1px solid #eee",
                    background: "#fafafa",
                  }}
                />
                <input
                  type="file"
                  accept="image/*"
                  style={{ marginTop: 8 }}
                  onChange={(e) => {
                    if (e.target.files && e.target.files[0]) {
                      handleChange("avatar", e.target.files[0]);
                    }
                  }}
                />
              </div>
            </div>
            <div className="vp-box-actions vp-box-actions-alone">
              <Button
                variant="secondary"
                type="button"
                onClick={() => navigate("/customers")}
              >
                Thoát
              </Button>
              <Button
                variant="primary"
                type="submit"
                style={{ marginLeft: 12 }}
              >
                Lưu
              </Button>
            </div>
          </div>
        </form>
      </main>
    </div>
  );
};

export default EditCustomer;

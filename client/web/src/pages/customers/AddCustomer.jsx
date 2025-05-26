import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../../components/layout/Navbar";
import Sidebar from "../../components/layout/Sidebar";
import Input from "../../components/common/Input";
import Button from "../../components/common/Button";
import defaultAvatar from "../../assets/images/default-avatar.jpg";
import "../../styles/addcustomer.css";
import {
  fetchProvinces,
  fetchDistricts,
  fetchWards,
  peekNextUserId,
  adminAddUser,
  adminUpdateUser,
} from "../../services/authService";

const AddCustomer = () => {
  const navigate = useNavigate();
  const [customer, setCustomer] = useState({
    username: "",
    fullName: "",
    email: "",
    phoneNumber: "",
    birthday: "",
    addressProvince: "",
    addressDistrict: "",
    addressWard: "",
    addressDetail: "",
    role: "user",
    verified: false,
    avatar: "",
  });

  // State cho dropdown địa chỉ
  const [provinces, setProvinces] = useState([]);
  const [districts, setDistricts] = useState([]);
  const [wards, setWards] = useState([]);
  const [nextId, setNextId] = useState("");

  // Lấy danh sách tỉnh
  useEffect(() => {
    fetchProvinces().then(setProvinces);
    peekNextUserId()
      .then((data) => setNextId(data.id || ""))
      .catch(() => setNextId(""));
  }, []);

  // Khi chọn tỉnh, lấy danh sách quận/huyện
  useEffect(() => {
    if (customer.addressProvince) {
      fetchDistricts(customer.addressProvince).then((ds) =>
        setDistricts(ds || [])
      );
      setCustomer((prev) => ({
        ...prev,
        addressDistrict: "",
        addressWard: "",
      }));
      setWards([]);
    }
    // eslint-disable-next-line
  }, [customer.addressProvince]);

  // Khi chọn quận/huyện, lấy danh sách phường/xã
  useEffect(() => {
    if (customer.addressDistrict) {
      fetchWards(customer.addressDistrict).then((ws) => setWards(ws || []));
      setCustomer((prev) => ({ ...prev, addressWard: "" }));
    }
    // eslint-disable-next-line
  }, [customer.addressDistrict]);

  // Xử lý thay đổi input
  const handleChange = (name, value) => {
    setCustomer((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const formData = new FormData();
    // Thêm các trường text
    Object.keys(customer).forEach((key) => {
      if (key !== "avatar") formData.append(key, customer[key]);
    });
    // Thêm file ảnh nếu có
    if (customer.avatar && typeof customer.avatar !== "string") {
      formData.append("avatar", customer.avatar);
    }
    try {
      await adminAddUser(formData); // Chỉ thêm mới, không cần kiểm tra isEdit
      navigate("/customers");
    } catch (err) {
      alert("Lưu thất bại!");
    }
  };

  return (
    <div
      className="dashboard-layout"
      style={{ fontFamily: "'Quicksand', sans-serif" }}
    >
      <Navbar />
      <Sidebar />
      <main className="dashboard-content view-product-main">
        <div className="product-header">
          <h1>Thêm khách hàng</h1>
          <div className="product-subtitle">
            <span
              className="subtitle"
              style={{ cursor: "pointer" }}
              onClick={() => navigate("/customers")}
            >
              Khách hàng
            </span>
            <span className="subtitle subtitle-sep">{">"}</span>
            <span className="subtitle active">Thêm khách hàng</span>
          </div>
        </div>
        <form className="vp-content" onSubmit={handleSubmit}>
          {/* Cột trái: Thông tin cơ bản */}
          <div className="view-product-col">
            <div className="vp-box-info">
              <h2>Thông tin khách hàng</h2>
              <div className="vp-form-group">
                <Input
                  type="text"
                  label="Mã khách hàng"
                  value={nextId || "Đang tải..."}
                  disabled
                />
              </div>
              <div className="vp-form-group">
                <Input
                  type="text"
                  label="Tên đăng nhập"
                  value={customer.username}
                  onChange={(e) => handleChange("username", e.target.value)}
                  required
                />
              </div>
              <div className="vp-form-group">
                <Input
                  type="text"
                  label="Họ tên"
                  value={customer.fullName}
                  onChange={(e) => handleChange("fullName", e.target.value)}
                  required
                />
              </div>
              <div className="vp-form-group">
                <Input
                  type="email"
                  label="Email"
                  value={customer.email}
                  onChange={(e) => handleChange("email", e.target.value)}
                  required
                />
              </div>
              <div className="vp-form-group">
                <Input
                  type="text"
                  label="Số điện thoại"
                  value={customer.phoneNumber}
                  onChange={(e) => handleChange("phoneNumber", e.target.value)}
                  required
                />
              </div>
              <div className="vp-form-group">
                <Input
                  type="date"
                  label="Ngày sinh"
                  value={customer.birthday}
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
                  value={customer.addressProvince}
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
                  value={customer.addressDistrict}
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
                  value={customer.addressWard}
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
                  value={customer.addressDetail}
                  onChange={(e) =>
                    handleChange("addressDetail", e.target.value)
                  }
                />
              </div>
              <div className="vp-form-group">
                <label className="input-label">Quyền người dùng</label>
                <select
                  className="input"
                  value={customer.role}
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
                  src={
                    customer.avatar
                      ? typeof customer.avatar === "string"
                        ? customer.avatar
                        : URL.createObjectURL(customer.avatar)
                      : defaultAvatar
                  }
                  alt="Avatar"
                  style={{
                    width: 100,
                    height: 100,
                    borderRadius: "50%",
                    objectFit: "cover",
                    border: "1px solid #eee",
                    background: "#fafafa",
                    marginBottom: 12,
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

export default AddCustomer;

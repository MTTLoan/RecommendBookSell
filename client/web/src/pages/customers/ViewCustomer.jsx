import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Navbar from "../../components/layout/Navbar";
import Sidebar from "../../components/layout/Sidebar";
import Input from "../../components/common/Input";
import Button from "../../components/common/Button";
import defaultAvatar from "../../assets/images/default-avatar.jpg";
import "../../styles/viewcustomer.css";
import {
  fetchCustomerDetail,
  fetchProvinces,
  fetchDistricts,
  fetchWards,
} from "../../services/authService";

const ViewCustomer = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [customer, setCustomer] = useState(null);
  const [loading, setLoading] = useState(true);

  // State cho dữ liệu địa chỉ
  const [provinceName, setProvinceName] = useState("");
  const [districtName, setDistrictName] = useState("");
  const [wardName, setWardName] = useState("");

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

  useEffect(() => {
    if (customer && customer.addressProvince) {
      fetchDistricts(customer.addressProvince).then(setDistricts);
    }
  }, [customer?.addressProvince]);

  useEffect(() => {
    if (customer && customer.addressDistrict) {
      fetchWards(customer.addressDistrict).then(setWards);
    }
  }, [customer?.addressDistrict]);

  useEffect(() => {
    if (customer && provinces.length > 0) {
      const province = provinces.find(
        (p) => String(p.code) === String(customer.addressProvince)
      );
      setProvinceName(province ? province.name : "");
    }
  }, [customer, provinces]);

  useEffect(() => {
    if (customer && districts.length > 0) {
      const district = districts.find(
        (d) => String(d.code) === String(customer.addressDistrict)
      );
      setDistrictName(district ? district.name : "");
    }
  }, [customer, districts]);

  useEffect(() => {
    if (customer && wards.length > 0) {
      const ward = wards.find(
        (w) => String(w.code) === String(customer.addressWard)
      );
      setWardName(ward ? ward.name : "");
    }
  }, [customer, wards]);

  if (loading) {
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

  if (!customer) {
    return (
      <div
        className="dashboard-layout"
        style={{ fontFamily: "'Quicksand', sans-serif" }}
      >
        <Navbar />
        <Sidebar />
        <main className="dashboard-content view-product-main">
          <div style={{ padding: 40, textAlign: "center" }}>
            Không tìm thấy khách hàng.
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
          <h1>Khách hàng</h1>
          <div className="product-subtitle">
            <span
              className="subtitle"
              style={{ cursor: "pointer" }}
              onClick={() => navigate("/customers")}
            >
              Khách hàng
            </span>
            <span className="subtitle subtitle-sep">{">"}</span>
            <span className="subtitle active">Xem khách hàng</span>
          </div>
        </div>
        <div className="vp-content">
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
                  disabled
                />
              </div>
              <div className="vp-form-group">
                <Input
                  type="text"
                  label="Họ tên"
                  value={customer.fullName || ""}
                  disabled
                />
              </div>
              <div className="vp-form-group">
                <Input
                  type="text"
                  label="Email"
                  value={customer.email || ""}
                  disabled
                />
              </div>
              <div className="vp-form-group">
                <Input
                  type="text"
                  label="Số điện thoại"
                  value={
                    customer.phoneNumber ? String(customer.phoneNumber) : ""
                  }
                  disabled
                />
              </div>
              <div className="vp-form-group">
                <Input
                  type="text"
                  label="Ngày sinh"
                  value={
                    customer.birthday
                      ? new Date(customer.birthday).toLocaleDateString()
                      : ""
                  }
                  disabled
                />
              </div>
            </div>
          </div>
          {/* Cột phải: Địa chỉ, vai trò, trạng thái, avatar, nút thoát */}
          <div className="view-product-col">
            <div className="vp-box-info">
              <h2>Thông tin bổ sung</h2>
              <div className="vp-form-group">
                <Input
                  type="text"
                  label="Tỉnh/Thành phố"
                  value={provinceName}
                  disabled
                />
              </div>
              <div className="vp-form-group">
                <Input
                  type="text"
                  label="Quận/Huyện"
                  value={districtName}
                  disabled
                />
              </div>
              <div className="vp-form-group">
                <Input
                  type="text"
                  label="Phường/Xã"
                  value={wardName}
                  disabled
                />
              </div>
              <div className="vp-form-group">
                <Input
                  type="text"
                  label="Địa chỉ chi tiết"
                  value={customer.addressDetail || ""}
                  disabled
                />
              </div>
              <div className="vp-form-group">
                <Input
                  type="text"
                  label="Vai trò"
                  value={customer.role || ""}
                  disabled
                />
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
              </div>
            </div>
            <div className="vp-box-actions vp-box-actions-alone">
              <Button
                variant="secondary"
                onClick={() => navigate("/customers")}
              >
                Thoát
              </Button>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default ViewCustomer;

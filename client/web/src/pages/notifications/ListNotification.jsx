import React, { useState, useEffect } from "react";
import Navbar from "../../components/layout/Navbar";
import Sidebar from "../../components/layout/Sidebar";
import Table from "../../components/layout/Table";
import Popup from "../../components/common/Popup";
import "../../styles/listnotification.css";
import {
  fetchNotifications,
  addNotification,
  updateNotification,
  deleteNotification,
} from "../../services/notificationService";
import { fetchAllCustomers } from "../../services/authService"; // Hàm lấy danh sách khách hàng

const TITLE_OPTIONS = [
  {
    value: "packing",
    label: "Đặt hàng thành công!",
    content: (orderId) =>
      `Đơn hàng #${orderId} của bạn đang được đóng gói. Vui lòng chờ trong giây lát.`,
  },
  {
    value: "waiting",
    label: "Đơn hàng đã sẵn sàng để giao!",
    content: (orderId) =>
      `Đơn hàng #${orderId} đã sẵn sàng và đang chờ giao đến bạn.`,
  },
  {
    value: "delivered",
    label: "Đơn hàng đã được giao thành công!",
    content: (orderId) =>
      `Đơn hàng #${orderId} đã được giao thành công. Cảm ơn bạn đã mua hàng!`,
  },
  {
    value: "returned",
    label: "Yêu cầu trả hàng đã được ghi nhận.",
    content: (orderId) => `Đơn hàng #${orderId} đang trong quá trình trả hàng.`,
  },
  {
    value: "returnedorder",
    label: "Trả hàng thành công",
    content: (orderId) => `Trả hàng thành công cho đơn hànghàng #${orderId}.`,
  },
  {
    value: "cancelled",
    label: "Đơn hàng của bạn đã được hủy",
    content: (orderId) =>
      `Đơn hàng #${orderId} đã bị hủy. Nếu có thắc mắc, vui lòng liên hệ hỗ trợ.`,
  },
  {
    value: "register_success",
    label: "Đăng ký thành công",
    content: () =>
      `Bạn đã tạo tài khoản thành công. Chào mừng bạn đến với hệ thống!`,
  },
  {
    value: "review",
    label: "Đánh giá sách",
    content: (orderId) =>
      `Bạn hãy đánh giá cho đơn hàng #${orderId} để nhận ưu đãi từ hệ thống.`,
  },
  {
    value: "system",
    label: "Thông báo hệ thống",
    content: () => "",
  },
];

const ListNotification = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [customerLoading, setCustomerLoading] = useState(false);
  const [showAdd, setShowAdd] = useState(false);
  const [showEdit, setShowEdit] = useState(false);
  const [showDelete, setShowDelete] = useState(false);
  const [showView, setShowView] = useState(false);
  const [selectedNotification, setSelectedNotification] = useState(null);
  const [newNotification, setNewNotification] = useState({
    customerId: "",
    customerName: "",
    title: "",
    content: "",
  });
  const [customers, setCustomers] = useState([]);
  const [customerFilter, setCustomerFilter] = useState("");
  const [filteredCustomers, setFilteredCustomers] = useState([]);
  const [showTitleDropdown, setShowTitleDropdown] = useState(false);
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [searchValue, setSearchValue] = useState("");
  const [error, setError] = useState("");

  const filteredNotifications = notifications.filter((noti) =>
    noti.customerName?.toLowerCase().includes(searchValue.toLowerCase())
  );

  useEffect(() => {
    loadData();
    loadCustomers();

    const handleClickOutside = (e) => {
      if (!e.target.className?.includes("dropdown-input")) {
        setDropdownOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const data = await fetchNotifications();
      setNotifications(data || []);
    } catch {
      setNotifications([]);
    }
    setLoading(false);
  };

  const loadCustomers = async () => {
    setCustomerLoading(true);
    try {
      const data = await fetchAllCustomers();
      console.log("Loaded customers:", data); // Log dữ liệu
      if (!data || data.length === 0) {
        setError("Không tìm thấy khách hàng nào. Vui lòng kiểm tra dữ liệu.");
      }
      setCustomers(data);
      setFilteredCustomers(data);
    } catch (error) {
      console.error("Error loading customers:", error);
      setError("Lỗi khi tải danh sách khách hàng: " + error.message);
      setCustomers([]);
      setFilteredCustomers([]);
    }
    setCustomerLoading(false);
  };

  // Thêm thông báo
  const handleAddNotification = async () => {
    if (
      !newNotification.customerId ||
      !newNotification.title ||
      !newNotification.content
    ) {
      setError(
        "Vui lòng điền đầy đủ thông tin: Tên khách hàng, Tiêu đề, Nội dung"
      );
      return;
    }
    try {
      await addNotification({
        userId: Number(newNotification.customerId),
        title: newNotification.title,
        message: newNotification.content,
      });
      setShowAdd(false);
      setNewNotification({
        customerId: "",
        customerName: "",
        title: "",
        content: "",
      });
      setCustomerFilter("");
      setError("");
      loadData();
    } catch (error) {
      console.error("Error adding notification:", error);
      setError(
        "Lỗi khi thêm thông báo: " +
          (error.response?.data?.message || error.message)
      );
    }
  };

  // Sửa thông báo
  const handleEditNotification = async () => {
    await updateNotification(selectedNotification.id, {
      content: selectedNotification.content,
    });
    setShowEdit(false);
    setSelectedNotification(null);
    loadData();
  };

  // Xóa thông báo
  const handleDeleteNotification = async () => {
    await deleteNotification(selectedNotification.id);
    setShowDelete(false);
    setSelectedNotification(null);
    loadData();
  };

  // Lọc khách hàng khi nhập
  const handleCustomerFilter = (value) => {
    setCustomerFilter(value);
    setFilteredCustomers(
      customers.filter((c) =>
        c.fullName.toLowerCase().includes(value.toLowerCase())
      )
    );
  };

  // Khi chọn loại tiêu đề, tự động điền nội dung mẫu
  const handleTitleChange = (value) => {
    setNewNotification({
      ...newNotification,
      title: value,
      content:
        value === "system"
          ? ""
          : TITLE_OPTIONS.find((opt) => opt.value === value)?.content("") || "",
    });
  };

  const columns = [
    {
      key: "id",
      label: "Mã thông báo",
      render: (noti) => noti.id,
    },
    {
      key: "customerName",
      label: "Tên khách hàng",
      render: (noti) => noti.customerName || "Ẩn",
    },
    {
      key: "title",
      label: "Tiêu đề",
      render: (noti) =>
        TITLE_OPTIONS.find((opt) => opt.value === noti.title)?.label ||
        noti.title,
      filters: TITLE_OPTIONS.map((opt) => ({
        text: opt.label,
        value: opt.label,
      })),
      onFilter: (value, record) => record.title === value,
    },
    {
      key: "actions",
      label: "Hành động",
      render: (noti) => (
        <div className="actions">
          <span
            className="material-symbols-outlined action-icon"
            title="Xem"
            onClick={() => {
              setSelectedNotification(noti);
              setShowView(true);
            }}
            style={{ cursor: "pointer" }}
          >
            visibility
          </span>
          <span
            className="material-symbols-outlined action-icon"
            title="Sửa"
            onClick={() => {
              setSelectedNotification(noti);
              setShowEdit(true);
            }}
            style={{ cursor: "pointer" }}
          >
            edit_square
          </span>
          <span
            className="material-symbols-outlined action-icon"
            title="Xóa"
            onClick={() => {
              setSelectedNotification(noti);
              setShowDelete(true);
            }}
            style={{ cursor: "pointer" }}
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
          <h1>Thông báo</h1>
          <div className="product-subtitle">
            <span className="subtitle active">Thông báo</span>
          </div>
        </div>
        {loading ? (
          <div style={{ padding: 40, textAlign: "center" }}>
            Đang tải dữ liệu...
          </div>
        ) : (
          <Table
            title=""
            data={filteredNotifications}
            columns={columns}
            showHeader
            showSearch
            showFilter={true}
            showDownload={false}
            showAddButton={true}
            addButtonText="Thêm thông báo"
            showCheckbox={false}
            showSort={true}
            onAdd={() => setShowAdd(true)}
            searchValue={searchValue}
            setSearchValue={setSearchValue}
          />
        )}

        {/* Popup Thêm */}
        <Popup
          open={showAdd}
          title="Thêm thông báo"
          titleColor="success"
          inputs={[
            {
              label: "Tên khách hàng",
              name: "customerId",
              type: "custom",
              render: (
                <div
                  className="dropdown-select"
                  style={{ position: "relative" }}
                >
                  <input
                    type="text"
                    placeholder="Nhập tên khách hàng để tìm"
                    value={
                      newNotification.customerId
                        ? newNotification.customerName
                        : customerFilter
                    }
                    onChange={(e) => {
                      const value = e.target.value;
                      setCustomerFilter(value);
                      // Nếu đang chọn khách thì xóa chọn để cho phép nhập lại
                      if (newNotification.customerId) {
                        setNewNotification((prev) => ({
                          ...prev,
                          customerId: "",
                          customerName: "",
                        }));
                      }
                      handleCustomerFilter(value);
                      setDropdownOpen(true);
                    }}
                    className="dropdown-input"
                    autoComplete="off"
                    onFocus={() => {
                      setDropdownOpen(true);
                      handleCustomerFilter(customerFilter);
                    }}
                    style={{ cursor: "pointer" }}
                  />
                  {dropdownOpen && !newNotification.customerId && (
                    <div className="dropdown-list">
                      {filteredCustomers.length > 0 ? (
                        filteredCustomers.map((c) => (
                          <div
                            key={c.id}
                            className={`dropdown-item${
                              newNotification.customerId === String(c.id)
                                ? " selected"
                                : ""
                            }`}
                            onClick={() => {
                              setNewNotification((prev) => ({
                                ...prev,
                                customerId: String(c.id),
                                customerName: c.fullName,
                              }));
                              setCustomerFilter(""); // reset filter khi đã chọn
                              setDropdownOpen(false);
                            }}
                          >
                            {c.fullName}
                          </div>
                        ))
                      ) : (
                        <div className="dropdown-empty">
                          Không tìm thấy khách hàng
                        </div>
                      )}
                    </div>
                  )}
                  {newNotification.customerId && (
                    <span
                      className="dropdown-clear"
                      style={{
                        position: "absolute",
                        right: 10,
                        top: 10,
                        cursor: "pointer",
                        color: "#d32f2f",
                        fontWeight: "bold",
                        fontSize: 18,
                      }}
                      title="Bỏ chọn"
                      onClick={() => {
                        setNewNotification((prev) => ({
                          ...prev,
                          customerId: "",
                          customerName: "",
                        }));
                        setCustomerFilter("");
                        setDropdownOpen(true);
                      }}
                    >
                      ×
                    </span>
                  )}
                </div>
              ),
            },
            // Các input khác (Tiêu đề, Nội dung) giữ nguyên
            {
              label: "Tiêu đề",
              name: "title",
              type: "custom",
              render: (
                <div className="dropdown-select">
                  <input
                    type="text"
                    placeholder="Chọn loại tiêu đề"
                    value={
                      TITLE_OPTIONS.find(
                        (opt) => opt.value === newNotification.title
                      )?.label || ""
                    }
                    readOnly
                    className="dropdown-input"
                    onFocus={() => setShowTitleDropdown(true)}
                    onClick={() => setShowTitleDropdown(true)}
                  />
                  {showTitleDropdown && (
                    <div className="dropdown-list">
                      {TITLE_OPTIONS.map((opt) => (
                        <div
                          key={opt.value}
                          className={`dropdown-item${
                            newNotification.title === opt.value
                              ? " selected"
                              : ""
                          }`}
                          onClick={() => {
                            handleTitleChange(opt.value);
                            setShowTitleDropdown(false);
                          }}
                        >
                          {opt.label}
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              ),
            },
            {
              label: "Nội dung",
              name: "content",
              type: "textarea",
              placeholder: "Nội dung",
              value: newNotification.content || "",
            },
          ]}
          onInputChange={(name, value) =>
            setNewNotification({ ...newNotification, [name]: value })
          }
          onClose={() => setShowAdd(false)}
          onConfirm={handleAddNotification}
          confirmText="Thêm"
          confirmColor="success"
          cancelText="Hủy"
          cancelColor="gray"
        />

        {/* Popup Sửa */}
        <Popup
          open={showEdit && selectedNotification}
          title="Sửa thông báo"
          titleColor="info"
          inputs={[
            {
              label: "Tên khách hàng",
              name: "customerName",
              value: selectedNotification?.customerName || "",
              style: {
                background: "#f5f5f5",
                color: "#888",
                cursor: "not-allowed",
              },
              disabled: true,
            },
            {
              label: "Tiêu đề",
              name: "title",
              value: selectedNotification?.title || "",
              style: {
                background: "#f5f5f5",
                color: "#888",
                cursor: "not-allowed",
              },
              disabled: true,
            },
            {
              label: "Nội dung",
              name: "content",
              type: "textarea",
              placeholder: "Nội dung",
              value: selectedNotification?.content || "",
            },
          ]}
          onInputChange={(name, value) =>
            setSelectedNotification({ ...selectedNotification, [name]: value })
          }
          onClose={() => setShowEdit(false)}
          onConfirm={handleEditNotification}
          confirmText="Lưu"
          confirmColor="info"
          cancelText="Hủy"
          cancelColor="gray"
        />

        {/* Popup Xem */}
        <Popup
          open={showView && selectedNotification}
          title="Chi tiết thông báo"
          titleColor="info"
          content={
            <div style={{ wordBreak: "break-word" }}>
              <div>
                <b>Tên khách hàng:</b> {selectedNotification?.customerName}
              </div>
              <div>
                <b>Tiêu đề:</b> {selectedNotification?.title}
              </div>
              <div style={{ marginTop: 8 }}>
                <b>Nội dung:</b>
              </div>
              <div
                style={{
                  whiteSpace: "pre-line",
                  marginTop: 4,
                  wordBreak: "break-word",
                }}
              >
                {selectedNotification?.content}
              </div>
            </div>
          }
          showCancel={false}
          confirmText="Đóng"
          confirmColor="info"
          onClose={() => setShowView(false)}
          onConfirm={() => setShowView(false)}
        />

        {/* Popup Xóa */}
        <Popup
          open={showDelete && selectedNotification}
          title="Xác nhận xóa thông báo"
          titleColor="error"
          content={
            selectedNotification
              ? `Bạn có chắc muốn xóa thông báo "${selectedNotification.title}" không?`
              : ""
          }
          contentColor="error"
          onClose={() => setShowDelete(false)}
          onConfirm={handleDeleteNotification}
          confirmText="Xóa"
          confirmColor="error"
          cancelText="Hủy"
          cancelColor="gray"
        />
      </main>
    </div>
  );
};

export default ListNotification;

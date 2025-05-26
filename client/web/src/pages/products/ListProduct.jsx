import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../../components/layout/Navbar";
import Sidebar from "../../components/layout/Sidebar";
import Table from "../../components/layout/Table";
import { fetchBooks, deleteBook } from "../../services/bookService";
import "../../styles/listproduct.css";
import Popup from "../../components/common/Popup";

const PRICE_FILTERS = [
  { text: "Dưới 100.000", value: "lt100" },
  { text: "100.000 - 300.000", value: "100-300" },
  { text: "300.000 - 500.000", value: "300-500" },
  { text: "Trên 500.000", value: "gt500" },
];

const FILTER_OPTIONS = [
  ...PRICE_FILTERS,
  { text: "Còn hàng", value: "instock" },
  { text: "Hết hàng", value: "outstock" },
];

const ListProduct = () => {
  const [searchValue, setSearchValue] = useState("");
  const [deleteId, setDeleteId] = useState(null);
  const [products, setProducts] = useState([]);
  const [allProducts, setAllProducts] = useState([]);
  const [filterValue, setFilterValue] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showDeleteSuccess, setShowDeleteSuccess] = useState(false);
  const [deleteSuccessTimeout, setDeleteSuccessTimeout] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    setLoading(true);
    fetchBooks()
      .then((books) => {
        const mapped = books.map((b) => ({
          id: b.id,
          name: b.name,
          image: b.images?.[0]?.url || "",
          price: b.price + " VND",
          quantity: b.stockQuantity,
          dateAdded: new Date(b.createdAt).toLocaleDateString("vi-VN"),
          status: b.stockQuantity > 0 ? "Còn hàng" : "Hết hàng",
        }));
        setProducts(mapped);
        setAllProducts(mapped);
      })
      .catch((err) => {
        setProducts([]);
        setAllProducts([]);
      })
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    let filtered = allProducts;

    // Lọc theo tên
    if (searchValue) {
      filtered = filtered.filter((p) =>
        p.name.toLowerCase().includes(searchValue.toLowerCase())
      );
    }

    // Lọc theo filterValue (giá hoặc tình trạng)
    if (filterValue) {
      if (["lt100", "100-300", "300-500", "gt500"].includes(filterValue)) {
        filtered = filtered.filter((record) => {
          const price = Number(String(record.price).replace(/[^\d]/g, ""));
          if (filterValue === "lt100") return price < 100000;
          if (filterValue === "100-300")
            return price >= 100000 && price <= 300000;
          if (filterValue === "300-500")
            return price > 300000 && price <= 500000;
          if (filterValue === "gt500") return price > 500000;
          return true;
        });
      } else if (filterValue === "instock" || filterValue === "outstock") {
        filtered = filtered.filter((record) =>
          filterValue === "instock"
            ? record.status === "Còn hàng"
            : record.status === "Hết hàng"
        );
      }
    }

    setProducts(filtered);
  }, [searchValue, filterValue, allProducts]);

  const handleAddProduct = () => {
    navigate("/products/add");
  };

  const handleViewProduct = (id) => {
    navigate(`/products/view/${id}`);
  };

  const handleEditProduct = (id) => {
    navigate(`/products/edit/${id}`);
  };

  const handleDeleteProduct = (id) => {
    setDeleteId(id);
  };

  const confirmDelete = async () => {
    setLoading(true);
    try {
      await deleteBook(deleteId);
      // Sau khi xóa, fetch lại danh sách
      const books = await fetchBooks();
      const mapped = books.map((b) => ({
        id: b.id,
        name: b.name,
        image: b.images?.[0]?.url || "",
        price: b.price + " VND",
        quantity: b.stockQuantity,
        dateAdded: new Date(b.createdAt).toLocaleDateString("vi-VN"),
        status: b.stockQuantity > 0 ? "Còn hàng" : "Hết hàng",
      }));
      setProducts(mapped);
      setAllProducts(mapped);
      setShowDeleteSuccess(true);
      const timeout = setTimeout(() => {
        setShowDeleteSuccess(false);
      }, 3000);
      setDeleteSuccessTimeout(timeout);
    } catch (err) {
      alert("Xóa sách thất bại!");
    }
    setDeleteId(null);
    setLoading(false);
  };

  useEffect(() => {
    return () => {
      if (deleteSuccessTimeout) clearTimeout(deleteSuccessTimeout);
    };
  }, [deleteSuccessTimeout]);

  const cancelDelete = () => {
    setDeleteId(null);
  };

  const columns = [
    {
      key: "name",
      label: "Sản phẩm",
      width: "35%",
      sorter: (a, b) => a.id - b.id,
      render: (product) => (
        <div className="product-info">
          <img
            src={product.image}
            alt={product.name}
            className="product-image"
          />
          <div className="product-details">
            <p className="product-name">{product.name}</p>
          </div>
        </div>
      ),
    },
    {
      key: "price",
      label: "Giá",
      width: "20%",
      filters: FILTER_OPTIONS,
      onFilter: (value, record) => {
        const price = Number(String(record.price).replace(/[^\d]/g, ""));
        if (["lt100", "100-300", "300-500", "gt500"].includes(value)) {
          if (value === "lt100") return price < 100000;
          if (value === "100-300") return price >= 100000 && price <= 300000;
          if (value === "300-500") return price > 300000 && price <= 500000;
          if (value === "gt500") return price > 500000;
        } else if (value === "instock") {
          return record.status === "Còn hàng";
        } else if (value === "outstock") {
          return record.status === "Hết hàng";
        }
        return true;
      },
    },
    { key: "quantity", label: "Số lượng", width: "10%" },
    { key: "dateAdded", label: "Ngày thêm", width: "15%" },
    {
      key: "status",
      label: "Tình trạng",
      width: "20%",
      render: (row) => (
        <span
          className={
            row.status === "Còn hàng"
              ? "status-instock"
              : row.status === "Hết hàng"
              ? "status-outstock"
              : "status-other"
          }
        >
          {row.status}
        </span>
      ),
    },
    {
      key: "actions",
      label: "Hành động",
      width: "10%",
      render: (row) => (
        <div className="actions">
          <span
            className="material-symbols-outlined action-icon"
            title="Xem chi tiết"
            onClick={() => handleViewProduct(row.id)}
            style={{ cursor: "pointer" }}
          >
            visibility
          </span>
          <span
            className="material-symbols-outlined action-icon"
            title="Chỉnh sửa"
            onClick={() => handleEditProduct(row.id)}
            style={{ cursor: "pointer" }}
          >
            edit_square
          </span>
          <span
            className="material-symbols-outlined action-icon"
            title="Xóa"
            onClick={() => handleDeleteProduct(row.id)}
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
          <h1>Sản phẩm</h1>
          <p className="product-subtitle">
            <span className="subtitle active">Sản phẩm</span>
          </p>
        </div>
        {loading && (
          <div className="table-loading">
            <span>Đang tải dữ liệu...</span>
          </div>
        )}
        {/* Bảng danh sách sản phẩm */}
        {!loading && (
          <Table
            title=""
            data={products}
            columns={columns}
            onAdd={handleAddProduct}
            showHeader={true}
            showSearch={true}
            searchValue={searchValue}
            setSearchValue={setSearchValue}
            showFilter={true}
            filterOptions={FILTER_OPTIONS} // Truyền filterOptions vào Table
            showDownload={true}
            showAddButton={true}
            showCheckbox={false}
            showSort={true}
            addButtonText="Thêm sản phẩm"
            downloadButtonText="Xuất file"
            filterValue={filterValue}
            setFilterValue={setFilterValue}
          />
        )}
        {deleteId && (
          <Popup
            open={!!deleteId}
            title="Xác nhận xóa sản phẩm"
            titleColor="error"
            content="Bạn có chắc muốn xóa sản phẩm này không?"
            contentColor="error"
            confirmText="Xóa"
            cancelText="Hủy"
            onConfirm={confirmDelete}
            onCancel={cancelDelete}
            onClose={cancelDelete}
            hideConfirm={false}
            hideCancel={false}
            confirmColor="error"
            cancelColor="gray"
          />
        )}
        {showDeleteSuccess && (
          <Popup
            open={showDeleteSuccess}
            title="Thành công"
            titleColor="success"
            content="Xóa sản phẩm thành công!"
            contentColor="success"
            showCancel={false}
            showConfirm={false}
          />
        )}
      </main>
    </div>
  );
};

export default ListProduct;

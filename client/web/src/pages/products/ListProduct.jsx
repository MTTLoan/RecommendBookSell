import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../../components/layout/Navbar";
import Sidebar from "../../components/layout/Sidebar";
import Table from "../../components/layout/Table";
import { fetchBooks, deleteBook } from "../../services/bookService";
import "../../styles/listproduct.css";

const PRICE_FILTERS = [
  { text: "Dưới 100.000", value: "lt100" },
  { text: "100.000 - 300.000", value: "100-300" },
  { text: "300.000 - 500.000", value: "300-500" },
  { text: "Trên 500.000", value: "gt500" },
];

const STATUS_OPTIONS = [
  { text: "Còn hàng", value: "Còn hàng" },
  { text: "Hết hàng", value: "Hết hàng" },
];

const ListProduct = () => {
  const [selectedProducts, setSelectedProducts] = useState([]);
  const [searchValue, setSearchValue] = useState("");
  const [deleteId, setDeleteId] = useState(null);
  const [products, setProducts] = useState([]);
  const [allProducts, setAllProducts] = useState([]);
  const [priceFilter, setPriceFilter] = useState(null);
  const [loading, setLoading] = useState(true);
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

    // Lọc theo giá
    if (priceFilter) {
      filtered = filtered.filter((record) => {
        const price = Number(String(record.price).replace(/[^\d]/g, ""));
        if (priceFilter === "lt100") return price < 100000;
        if (priceFilter === "100-300")
          return price >= 100000 && price <= 300000;
        if (priceFilter === "300-500") return price > 300000 && price <= 500000;
        if (priceFilter === "gt500") return price > 500000;
        return true;
      });
    }

    setProducts(filtered);
  }, [searchValue, priceFilter, allProducts]);

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
    } catch (err) {
      alert("Xóa sách thất bại!");
    }
    setDeleteId(null);
    setLoading(false);
  };

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
      width: "10%",
      filters: PRICE_FILTERS,
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
      filters: STATUS_OPTIONS,
      onFilter: (value, record) => record.status === value,
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
            showDownload={true}
            showAddButton={true}
            showCheckbox={false}
            showSort={true}
            addButtonText="Thêm sản phẩm"
            downloadButtonText="Xuất file"
            filterValue={priceFilter}
            setFilterValue={setPriceFilter}
          />
        )}
        {deleteId && (
          <div className="modal-backdrop">
            <div className="modal-confirm">
              <h3>Xác nhận xóa sản phẩm</h3>
              <p>Bạn có chắc muốn xóa sản phẩm này không?</p>
              <div
                style={{ margin: "12px 0", color: "#d32f2f", fontWeight: 500 }}
              >
                {selectedProducts.name}
              </div>
              <div className="modal-actions">
                <button className="btn-cancel" onClick={cancelDelete}>
                  Hủy
                </button>
                <button className="btn-delete" onClick={confirmDelete}>
                  Xóa
                </button>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

export default ListProduct;

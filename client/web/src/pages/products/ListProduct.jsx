import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../../components/layout/Navbar';
import Sidebar from '../../components/layout/Sidebar';
import Table from '../../components/layout/Table';
import { fetchBooks, deleteBook } from '../../services/bookService';
import '../../styles/listproduct.css';

const ListProduct = () => {
  const [selectedProducts, setSelectedProducts] = useState([]);
  const [deleteId, setDeleteId] = useState(null);
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    setLoading(true);
    fetchBooks()
      .then((books) => {
        setProducts(
          books.map((b) => ({
            id: b.id,
            name: b.name,
            image: b.images?.[0]?.url || '',
            price: b.price + ' VND',
            quantity: b.stockQuantity,
            dateAdded: new Date(b.createdAt).toLocaleDateString('vi-VN'),
            status: b.stockQuantity > 0 ? 'Còn hàng' : 'Hết hàng',
          }))
        );
      })
      .catch((err) => {
        console.error("Lỗi fetchBooks:", err);
        setProducts([]);
      })
      .finally(() => setLoading(false));
  }, []);
  
  const handleSelectProduct = (id) => {
    setSelectedProducts((prevSelected) =>
      prevSelected.includes(id)
        ? prevSelected.filter((productId) => productId !== id)
        : [...prevSelected, id]
    );
  };

  const handleAddProduct = () => {
    navigate('/products/add');
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
    setProducts(
      books.map((b) => ({
        id: b.id,
        name: b.name,
        image: b.images?.[0]?.url || '',
        price: b.price + ' VND',
        quantity: b.stockQuantity,
        dateAdded: new Date(b.createdAt).toLocaleDateString('vi-VN'),
        status: b.stockQuantity > 0 ? 'Còn hàng' : 'Hết hàng',
      }))
    );
  } catch (err) {
    alert('Xóa sách thất bại!');
  }
  setDeleteId(null);
  setLoading(false);
};

  const cancelDelete = () => {
    setDeleteId(null);
  };

  const columns = [
    {
      key: 'name',
      label: 'Sản phẩm',
      width: '35%',
      sorter: (a, b) => a.id - b.id,
      render: (product) => (
        <div className="product-info">
          <img src={product.image} alt={product.name} className="product-image" />
          <div className="product-details">
            <p className="product-id">{product.id}</p>
            <p className="product-name">{product.name}</p>
          </div>
        </div>
      ),
    },
    { key: 'price', label: 'Giá', width: '10%' },
    { key: 'quantity', label: 'Số lượng', width: '10%' },
    { key: 'dateAdded', label: 'Ngày thêm', width: '15%' },
    {
    key: 'status',
    label: 'Tình trạng',
    width: '20%',
    render: (row) => (
      <span
        className={
          row.status === 'Còn hàng'
            ? 'status-instock'
            : row.status === 'Hết hàng'
            ? 'status-outstock'
            : 'status-other'
        }
      >
        {row.status}
      </span>
    ),
  },
    {
      key: 'actions',
      label: 'Hành động',
      width: '10%',
      render: (row) => (
        <div className="actions">
          <span
            className="material-symbols-outlined action-icon"
            title="Xem chi tiết"
            onClick={() => handleViewProduct(row.id)}
            style={{ cursor: 'pointer' }}
          >
            visibility
          </span>
          <span
            className="material-symbols-outlined action-icon"
            title="Chỉnh sửa"
            onClick={() => handleEditProduct(row.id)}
            style={{ cursor: 'pointer' }}
          >
            edit_square
          </span>
          <span
            className="material-symbols-outlined action-icon"
            title="Xóa"
            onClick={() => handleDeleteProduct(row.id)}
            style={{ cursor: 'pointer'}}
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
            <div className="spinner"></div>
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
          showFilter={true}
          showDownload={true}
          showAddButton={true}
          showCheckbox={true}
          showSort={true}
          addButtonText="Thêm sản phẩm" 
          downloadButtonText="Xuất file" 
        />
        )}
        {deleteId && (
          <div className="modal-backdrop">
            <div className="modal-confirm">
              <h3>Xác nhận xóa sản phẩm</h3>
              <p>Bạn có chắc muốn xóa sản phẩm này không?</p>
              <div className="modal-actions">
                <button className="btn-cancel" onClick={cancelDelete}>Hủy</button>
                <button className="btn-delete" onClick={confirmDelete}>Xóa</button>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

export default ListProduct;
import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Navbar from '../../components/layout/Navbar';
import Sidebar from '../../components/layout/Sidebar';
import Input from '../../components/common/Input';
import Button from '../../components/common/Button';
import defaultAvatar from '../../assets/images/default-avatar.jpg';
import '../../styles/viewproduct.css';
import { fetchBookDetail } from '../../services/bookService';
import { fetchCategories } from '../../services/categoryService';


const ViewProduct = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [product, setProduct] = useState(null);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedImage, setSelectedImage] = useState(null);
  const [showImageModal, setShowImageModal] = useState(false);

  useEffect(() => {
    setLoading(true);
    Promise.all([
      fetchBookDetail(id),
      fetchCategories()
    ])
      .then(([book, cats]) => {
        setProduct(book);
        setCategories(cats || []);
        if (book && book.images && book.images.length > 0) {
          const firstImg = typeof book.images[0] === 'string' ? book.images[0] : book.images[0].url || defaultAvatar;
          setSelectedImage(firstImg);
        } else {
          setSelectedImage(defaultAvatar);
        }
      })
      .catch(() => {
        setProduct(null);
        setCategories([]);
        setSelectedImage(defaultAvatar);
      })
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) {
    return (
      <div className="dashboard-layout" style={{ fontFamily: "'Quicksand', sans-serif" }}>
        <Navbar />
        <Sidebar />
        <main className="dashboard-content view-product-main">
          <div style={{ padding: 40, textAlign: 'center' }}>Đang tải dữ liệu...</div>
        </main>
      </div>
    );
  }

  if (!product) {
    return (
      <div className="dashboard-layout" style={{ fontFamily: "'Quicksand', sans-serif" }}>
        <Navbar />
        <Sidebar />
        <main className="dashboard-content view-product-main">
          <div style={{ padding: 40, textAlign: 'center' }}>Không tìm thấy sản phẩm.</div>
        </main>
      </div>
    );
  }

   // Lấy tên danh mục từ categories
  const categoryName =
    categories.find(
      (cat) =>
        cat.id === product.categoryId
    )?.name || '';

  let authorDisplay = 'Unknown';
  if (Array.isArray(product.author) && product.author.length > 0) {
    authorDisplay = product.author.join(', ');
  } else if (typeof product.author === 'string' && product.author.trim() !== '') {
    authorDisplay = product.author;
  }

    const images = (product.images && product.images.length > 0)
    ? product.images.map(img => typeof img === 'string' ? img : img.url || defaultAvatar)
    : [defaultAvatar];

  return (
    <div className="dashboard-layout" style={{ fontFamily: "'Quicksand', sans-serif" }}>
      <Navbar />
      <Sidebar />
      <main className="dashboard-content view-product-main">
        <div className="product-header">
          <h1>Sản phẩm</h1>
          <p className="product-subtitle">
            <span
              className="subtitle"
              style={{ cursor: 'pointer' }}
              onClick={() => navigate('/products')}
            >
              Sản phẩm
            </span>
            <span className="subtitle subtitle-sep">{'>'}</span>
            <span className="subtitle active">Xem sản phẩm</span>
          </p>
        </div>
        <div className="vp-content">
          {/* Cột trái: Thông tin sản phẩm */}
          <div className="view-product-col">
            <div className="vp-box-info">
              <h2>Thông tin sản phẩm</h2>
              <div className="vp-form-group">
                <Input type="text" label="Mã sản phẩm" value={product.id} placeholder="Mã sản phẩm" required disabled/>
              </div>
              <div className="vp-form-group">
                <Input type="text" label="Tên sản phẩm" value={product.name} placeholder="Tên sản phẩm" required disabled />
              </div>
              <div className="vp-form-group">
                <Input
                  label= "Danh mục"
                  name="category"
                  type="text"
                  value={categoryName}
                  disabled
                  style={{
                    color: '#888',
                    fontFamily: "'Quicksand', sans-serif",
                    cursor: 'not-allowed',
                  }}
                />
              </div>
              <div className="vp-form-group">
                <Input
                  type="text"
                  label="Tác giả"
                  value={authorDisplay}
                  placeholder="Tác giả"
                  required
                  disabled
                />
              </div>
              <div className="vp-form-row">
                <div className="vp-form-group half">
                  <Input type="number" label="Giá" value={product.price} placeholder="Giá" required disabled />
                </div>
                <div className="vp-form-group half">
                  <Input type="number" label="Số lượng" name="quantity" value={product.quantity ?? product.stockQuantity ?? ''} placeholder="Số lượng" required disabled />
                </div>
              </div>
            </div>
          </div>
          {/* Cột phải: Mô tả, hình ảnh, nút thoát */}
          <div className="view-product-col">
            <div className="vp-box-desc">
              <h2>Mô tả</h2>
              <textarea
                value={product.description || ''}
                disabled
                rows={6}
                className="vp-input-disabled vp-box-desc-textarea"
                style={{ fontFamily: "'Quicksand', sans-serif" }}
              />
            </div>
            <div className="vp-box-images">
              <h2>Hình ảnh sản phẩm</h2>
              <div className="vp-images-list">
                {images.map((img, idx) => (
                  <img
                    key={idx}
                    src={img}
                    alt={`Ảnh sản phẩm ${idx + 1}`}
                    className="vp-product-image-thumb"
                    style={{
                      border: selectedImage === img ? '2px solid #3fbf48' : '1px solid #eee',
                      cursor: 'pointer'
                    }}
                    onClick={() => {
                      setSelectedImage(img);
                      setShowImageModal(true);
                    }}
                  />
                ))}
              </div>
            </div>
            <div className="vp-box-actions vp-box-actions-alone">
              <Button
                variant="secondary"
                onClick={() => navigate('/products')}
              >
                Thoát
              </Button>
            </div>
          </div>
        </div>
        {/* === MODAL HIỂN THỊ ẢNH LỚN === */}
        {showImageModal && (
          <div
            style={{
              position: 'fixed',
              top: 0, left: 0, right: 0, bottom: 0,
              background: 'rgba(0,0,0,0.7)',
              zIndex: 9999,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center'
            }}
            onClick={() => setShowImageModal(false)}
          >
            <img
              src={selectedImage}
              alt="Ảnh phóng to"
              style={{
                maxWidth: '90vw',
                maxHeight: '90vh',
                borderRadius: 8,
                background: '#fff',
                boxShadow: '0 2px 16px rgba(0,0,0,0.3)'
              }}
              onClick={e => e.stopPropagation()}
            />
          </div>
        )}
      </main>
    </div>
  );
};

export default ViewProduct;
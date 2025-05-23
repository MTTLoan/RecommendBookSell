import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../../components/layout/Navbar';
import Sidebar from '../../components/layout/Sidebar';
import Input from '../../components/common/Input';
import Button from '../../components/common/Button';
import '../../styles/addproduct.css';
import { addBook, peekNextBookId } from '../../services/bookService';
import { fetchCategories } from '../../services/categoryService';

const getInitialProduct = () => ({
  id: '',
  name: '',
  categoryId: '',
  authors: [],
  price: '',
  quantity: '',
  description: '',
  images: [],
});

const AddProduct = () => {
  const [product, setProduct] = useState(getInitialProduct());
  const [images, setImages] = useState([]);
  const [description, setDescription] = useState('');
  const [categories, setCategories] = useState([]);
  const [loadingId, setLoadingId] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    fetchCategories().then((cats) => {
      setCategories(cats || []);
      if (cats && cats.length > 0) {
        setProduct((prev) => ({
          ...prev,
          categoryId: cats[0].id,
        }));
      }
    });
    // Lấy mã sản phẩm mới
    peekNextBookId().then((data) => {
      setProduct((prev) => ({
        ...prev,
      id: data.id || '',
    }));
    setLoadingId(false);
  });
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setProduct((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleCategoryChange = (e) => {
    setProduct((prev) => ({
      ...prev,
      categoryId: e.target.value,
    }));
  };

  const handleDescriptionChange = (e) => {
    setDescription(e.target.value);
    setProduct((prev) => ({
      ...prev,
      description: e.target.value,
    }));
  };

  const handleAuthorsChange = (e) => {
    setProduct((prev) => ({
      ...prev,
      authors: e.target.value.split(',').map((s) => s.trim()).filter(Boolean),
    }));
  };

  const handleImageUpload = (e) => {
    const files = Array.from(e.target.files);
    const newImages = files.map((file) => URL.createObjectURL(file));
    setImages((prev) => [...prev, ...newImages]);
    setProduct((prev) => ({
      ...prev,
      images: [...prev.images, ...newImages],
    }));
  };

  const handleRemoveImage = (idx) => {
    const newImages = images.filter((_, i) => i !== idx);
    setImages(newImages);
    setProduct((prev) => ({
      ...prev,
      images: newImages,
    }));
  };

  const handleAdd = async (e) => {
    e.preventDefault();
    try {
      await addBook({
        name: product.name,
        description: product.description,
        images: product.images,
        price: Number(product.price),
        stockQuantity: Number(product.quantity),
        categoryId: Number(product.categoryId),
        author: product.authors,
      });
      alert('Thêm sản phẩm thành công!');
      setProduct(getInitialProduct());
      setImages([]);
      setDescription('');
      navigate('/products');
    } catch (err) {
      alert('Thêm sản phẩm thất bại!');
    }
  };

  const handleCancel = () => {
    navigate('/products');
  };
  

  return (
    <div className="dashboard-layout" style={{ fontFamily: "'Quicksand', sans-serif" }}>
      <Navbar />
      <Sidebar />
      <main className="dashboard-content add-product-main">
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
            <span className="subtitle active">Thêm sản phẩm</span>
          </p>
        </div>
        <form className="add-product-content" onSubmit={handleAdd}>
          {/* Cột trái: Thông tin sản phẩm */}
          <div className="add-product-col">
            <div className="ad-box-info">
              <h2>Thông tin sản phẩm</h2>
              <div className="ad-form-group">
                <Input
                  label="Mã sản phẩm"
                  type="text"
                  name="id"
                  value={loadingId ? 'Đang lấy...' : product.id}
                  placeholder="Mã sản phẩm"
                  required
                  disabled
                />
              </div>
              <div className="ad-form-group">
                <Input
                  label="Tên sản phẩm"
                  type="text"
                  name="name"
                  value={product.name}
                  placeholder="Tên sản phẩm"
                  required
                  onChange={handleChange}
                />
              </div>
              <div className="ad-form-group">
                <label>Danh mục</label>
                <select
                  value={product.categoryId}
                  className="ad-input-disabled"
                  style={{
                    color: '#333',
                    fontFamily: "'Quicksand', sans-serif",
                    width: '100%',
                    fontSize: 15,
                    padding: '10px 14px',
                    borderRadius: 12,
                    border: '1px solid #ddd',
                    background: '#f8f8f8',
                    height: 42,
                  }}
                  onChange={handleCategoryChange}
                  required
                >
                  {categories.map((cat) => (
                    <option key={cat.id} value={cat.id}>{cat.name}</option>
                  ))}
                </select>
              </div>
              <div className="ad-form-group">
                <Input
                  label="Tác giả"
                  type="text"
                  name="authors"
                  value={product.authors.join(', ')}
                  placeholder="Tác giả (phân cách bởi dấu phẩy)"
                  required
                  onChange={handleAuthorsChange}
                />
              </div>
              <div className="ad-form-row">
                <div className="ad-form-group half">
                  <Input
                    label="Giá"
                    type="number"
                    name="price"
                    value={product.price}
                    placeholder="Giá"
                    required
                    onChange={handleChange}
                  />
                </div>
                <div className="ad-form-group half">
                  <Input
                    label="Số lượng"
                    type="number"
                    name="quantity"
                    value={product.quantity}
                    placeholder="Số lượng"
                    required
                    onChange={handleChange}
                  />
                </div>
              </div>
            </div>
          </div>
          {/* Cột phải: Mô tả, hình ảnh, nút thao tác */}
          <div className="add-product-col">
            <div className="ad-box-desc">
              <h2>Mô tả</h2>
              <textarea
                value={description}
                onChange={handleDescriptionChange}
                rows={6}
                className="ad-input-disabled ad-box-desc-textarea"
                style={{ fontFamily: "'Quicksand', sans-serif", color: '#333' }}
                required
              />
            </div>
            <div className="ad-box-images">
              <h2>Hình ảnh sản phẩm</h2>
              <div className="ad-images-list">
                {images.map((img, idx) => (
                 <div key={idx} style={{ position: 'relative', display: 'inline-block' }}>
                  <img
                    src={img}
                    alt={`Ảnh sản phẩm ${idx + 1}`}
                    className="ad-product-image-thumb"
                  />
                  <button
                    type="button"
                    className="ad-remove-image-btn"
                    onClick={() => handleRemoveImage(idx)}
                    title="Xóa ảnh"
                  >
                  <span className="material-symbols-outlined" style={{ fontSize: 18, color: '#d32f2f' }}>close</span>
                  </button>
                </div>
              ))}
            {/* Nút dấu cộng để thêm ảnh */}
              <label className="ad-add-image-btn" style={{
                width: 100, height: 100, border: '2px dashed #3fbf48', borderRadius: 8,
                display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer',
                background: '#fafafa', color: '#3fbf48', fontSize: 36, marginBottom: 0, marginRight: 0
              }}>
              <span className="material-symbols-outlined">add</span>
              <input
                type="file"
                accept="image/*"
                multiple
                onChange={handleImageUpload}
                style={{ display: 'none' }}
              />
              </label>
            </div>
          </div>
            <div className="ad-box-actions ad-box-actions-alone" style={{ display: 'flex', justifyContent: 'flex-end', gap: 16 }}>
              <Button
                variant="secondary"
                type="button"
                onClick={handleCancel}
              >
                Hủy
              </Button>
              <Button
                variant="primary"
                type="submit"
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

export default AddProduct;
import React, { useState, useEffect } from 'react';
import Navbar from '../../components/layout/Navbar';
import Sidebar from '../../components/layout/Sidebar';
import Table from '../../components/layout/Table';
import '../../styles/listcategory.css';
import { useNavigate } from 'react-router-dom';
import { fetchCategories, searchCategories, addCategory, updateCategory, deleteCategory, fetchCategoryStats } from '../../services/categoryService';

const ListCategory = () => {
  const navigate = useNavigate();
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showAdd, setShowAdd] = useState(false);
  const [showEdit, setShowEdit] = useState(false);
  const [showDelete, setShowDelete] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [newCategory, setNewCategory] = useState({ name: '', imageUrl: '' });
  const [searchValue, setSearchValue] = useState(""); // Thêm state này

  useEffect(() => {
    loadData();
  }, []);

const loadData = async () => {
  setLoading(true);
  try {
    const [cats, statsRes] = await Promise.all([
      fetchCategories(),
      fetchCategoryStats()
    ]);
    const statsMap = {};
    (statsRes.stats || []).forEach(s => {
      statsMap[s._id] = s.quantity;
    });
    setCategories(
      (cats || []).map(cat => ({
        ...cat,
        id: Number(cat.id || cat._id),
        bookCount: statsMap[cat.id] || 0
      }))
    );
  } catch {
    setCategories([]);
  }
  setLoading(false);
};

  // Thêm danh mục
  const handleAddCategory = async () => {
    await addCategory(newCategory);
    setShowAdd(false);
    setNewCategory({ name: '', description: '', imageUrl: '' });
    loadData();
  };

  // Sửa danh mục
  const handleEditCategory = async () => {
    await updateCategory(Number(selectedCategory.id), { name: selectedCategory.name, description: selectedCategory.description });
    setShowEdit(false);
    setSelectedCategory(null);
    loadData();
  };

  // Xóa danh mục
  const handleDeleteCategory = async () => {
    await deleteCategory(Number(selectedCategory.id));
    setShowDelete(false);
    setSelectedCategory(null);
    loadData();
  };

const handleSearch = async (query) => {
  setSearchValue(query);
  setLoading(true);
  try {
    if (!query) {
      await loadData();
    } else {
      const cats = await searchCategories(query);
      const statsRes = await fetchCategoryStats();
      const statsMap = {};
      (statsRes.stats || []).forEach(s => {
        statsMap[s._id] = s.quantity;
      });
      setCategories(
        (cats || []).map(cat => ({
          ...cat,
          id: Number(cat.id || cat._id),
          bookCount: statsMap[cat.id] || 0
        }))
      );
    }
  } catch {
    setCategories([]);
  }
  setLoading(false);
};

  const columns = [
    {
      key: 'name',
      label: 'Danh mục',
      render: (cat) => (
        <div className="category-info" style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <img
            src={cat.imageUrl || 'https://via.placeholder.com/40x40?text=No+Img'}
            alt={cat.name}
            style={{ width: 40, height: 40, objectFit: 'cover', borderRadius: 8, border: '1px solid #eee' }}
          />
          <div>
            <div style={{ fontWeight: 600 }}>{cat.name}</div>
          </div>
        </div>
      ),
    },
  {
    key: 'bookCount',
    label: 'Số lượng sách',
    render: (cat) => cat.bookCount
  },
    {
      key: 'actions',
      label: 'Hành động',
      render: (cat) => (
        <div className="actions">
          <span className="material-symbols-outlined action-icon" title="Xem"
          onClick={() => navigate(`/categories/view/${cat.id}`)}>visibility</span>
          <span className="material-symbols-outlined action-icon" title="Sửa"
            onClick={() => { setSelectedCategory(cat); setShowEdit(true); }}>edit_square</span>
          <span className="material-symbols-outlined action-icon" title="Xóa"
            onClick={() => { setSelectedCategory(cat); setShowDelete(true); }}>delete</span>
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
          <h1>Danh mục</h1>
          <p className="product-subtitle">
            <span className="subtitle active">Danh mục</span>
          </p>
        </div>
        {loading ? (
          <div style={{ padding: 40, textAlign: 'center' }}>Đang tải dữ liệu...</div>
        ) : (
        <Table
            title=""
            data={categories}
            columns={columns}
            showHeader
            showSearch
            showFilter
            showDownload
            showAddButton={true}
            showCheckbox={true}
            showSort={true}
            addButtonText="Thêm danh mục"
            downloadButtonText="Xuất file"
            onAdd={() => setShowAdd(true)}
            searchValue={searchValue}
            setSearchValue={setSearchValue}
            onSearch={handleSearch}
          />
        )}
      </main>

            {/* Popup Thêm */}
      {showAdd && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>Thêm danh mục</h3>
            <input
              type="text"
              placeholder="Tên danh mục"
              value={newCategory.name}
              onChange={e => setNewCategory({ ...newCategory, name: e.target.value })}
            />
            <input
              type="text"
              placeholder="Mô tả"
              value={newCategory.description}
              onChange={e => setNewCategory({ ...newCategory, description: e.target.value })}
            />
            <input
              type="text"
              placeholder="Link ảnh (tuỳ chọn)"
              value={newCategory.imageUrl}
              onChange={e => setNewCategory({ ...newCategory, imageUrl: e.target.value })}
            />
            <div style={{ marginTop: 16 }}>
              <button onClick={handleAddCategory}>Lưu</button>
              <button onClick={() => setShowAdd(false)}>Hủy</button>
            </div>
          </div>
        </div>
      )}

        {/* Popup Sửa */}
      {showEdit && selectedCategory && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>Sửa danh mục</h3>
            <input
              type="text"
              value={selectedCategory.name}
              onChange={e => setSelectedCategory({ ...selectedCategory, name: e.target.value })}
              placeholder="Tên danh mục"
            />
            <input
              type="text"
              value={selectedCategory.description || ''}
              onChange={e => setSelectedCategory({ ...selectedCategory, description: e.target.value })}
              placeholder="Mô tả"
            />
            <div style={{ marginTop: 16 }}>
              <button onClick={handleEditCategory}>Lưu</button>
              <button onClick={() => setShowEdit(false)}>Hủy</button>
            </div>
          </div>
        </div>
      )}

        {/* Popup Xóa */}
      {showDelete && selectedCategory && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>Bạn có chắc muốn xóa danh mục này?</h3>
            <div style={{ margin: '12px 0', color: '#d32f2f', fontWeight: 500 }}>
              {selectedCategory.name}
              <div style={{ fontSize: 13, color: '#888', marginTop: 4 }}>{selectedCategory.description}</div>
            </div>
            <div style={{ marginTop: 16 }}>
              <button onClick={handleDeleteCategory}>Xóa</button>
              <button onClick={() => setShowDelete(false)}>Hủy</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ListCategory;
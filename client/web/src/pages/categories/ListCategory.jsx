import React, { useState, useEffect } from "react";
import Navbar from "../../components/layout/Navbar";
import Sidebar from "../../components/layout/Sidebar";
import Table from "../../components/layout/Table";
import Popup from "../../components/common/Popup";
import "../../styles/listcategory.css";
import { useNavigate } from "react-router-dom";
import {
  fetchCategories,
  addCategory,
  updateCategory,
  deleteCategory,
  fetchCategoryStats,
} from "../../services/categoryService";

const ListCategory = () => {
  const navigate = useNavigate();
  const [categories, setCategories] = useState([]);
  const [allCategories, setAllCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showAdd, setShowAdd] = useState(false);
  const [showEdit, setShowEdit] = useState(false);
  const [showDelete, setShowDelete] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [newCategory, setNewCategory] = useState({ name: "", imageUrl: "" });
  const [searchValue, setSearchValue] = useState("");

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const [cats, statsRes] = await Promise.all([
        fetchCategories(),
        fetchCategoryStats(),
      ]);
      const statsMap = {};
      (statsRes.stats || []).forEach((s) => {
        statsMap[s._id] = s.quantity;
      });
      const mapped = (cats || []).map((cat) => ({
        ...cat,
        id: Number(cat.id || cat._id),
        bookCount: statsMap[cat.id] || 0,
      }));
      setCategories(mapped);
      setAllCategories(mapped);
    } catch {
      setCategories([]);
      setAllCategories([]);
    }
    setLoading(false);
  };

  useEffect(() => {
    if (!searchValue) {
      setCategories(allCategories);
    } else {
      setCategories(
        allCategories.filter((cat) =>
          (cat.name || "").toLowerCase().includes(searchValue.toLowerCase())
        )
      );
    }
  }, [searchValue, allCategories]);

  // Thêm danh mục
  const handleAddCategory = async () => {
    await addCategory(newCategory);
    setShowAdd(false);
    setNewCategory({ name: "", description: "", imageUrl: "" });
    loadData();
  };

  // Sửa danh mục
  const handleEditCategory = async () => {
    await updateCategory(Number(selectedCategory.id), {
      name: selectedCategory.name,
      description: selectedCategory.description,
    });
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

  const columns = [
    {
      key: "name",
      label: "Danh mục",
      render: (cat) => (
        <div
          className="category-info"
          style={{ display: "flex", alignItems: "center", gap: 12 }}
        >
          <img
            src={
              cat.imageUrl || "https://via.placeholder.com/40x40?text=No+Img"
            }
            alt={cat.name}
            style={{
              width: 40,
              height: 40,
              objectFit: "cover",
              borderRadius: 8,
              border: "1px solid #eee",
            }}
          />
          <div>
            <div style={{ fontWeight: 600 }}>{cat.name}</div>
          </div>
        </div>
      ),
    },
    {
      key: "bookCount",
      label: "Số lượng sách",
      render: (cat) => cat.bookCount,
    },
    {
      key: "actions",
      label: "Hành động",
      render: (cat) => (
        <div className="actions">
          <span
            className="material-symbols-outlined action-icon"
            title="Xem"
            onClick={() => navigate(`/categories/view/${cat.id}`)}
          >
            visibility
          </span>
          <span
            className="material-symbols-outlined action-icon"
            title="Sửa"
            onClick={() => {
              setSelectedCategory(cat);
              setShowEdit(true);
            }}
          >
            edit_square
          </span>
          <span
            className="material-symbols-outlined action-icon"
            title="Xóa"
            onClick={() => {
              setSelectedCategory(cat);
              setShowDelete(true);
            }}
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
          <h1>Danh mục</h1>
          <p className="product-subtitle">
            <span className="subtitle active">Danh mục</span>
          </p>
        </div>
        {loading ? (
          <div style={{ padding: 40, textAlign: "center" }}>
            Đang tải dữ liệu...
          </div>
        ) : (
          <Table
            title=""
            data={categories}
            columns={columns}
            showHeader
            showSearch
            showFilter={false}
            showDownload
            showAddButton={true}
            showCheckbox={false}
            showSort={true}
            addButtonText="Thêm danh mục"
            downloadButtonText="Xuất file"
            onAdd={() => setShowAdd(true)}
            searchValue={searchValue}
            setSearchValue={setSearchValue}
          />
        )}

        <Popup
          open={showAdd}
          title="Thêm danh mục"
          titleColor="success"
          inputs={[
            {
              label: "Tên danh mục",
              name: "name",
              placeholder: "Tên danh mục",
              value: newCategory.name,
            },
            {
              label: "Mô tả",
              name: "description",
              type: "textarea",
              placeholder: "Mô tả",
              value: newCategory.description || "",
            },
          ]}
          showImageUpload={true}
          imageUrl={newCategory.imageUrl}
          onImageChange={(e) => {
            const file = e.target.files[0];
            if (file) {
              const reader = new FileReader();
              reader.onload = (ev) =>
                setNewCategory({ ...newCategory, imageUrl: ev.target.result });
              reader.readAsDataURL(file);
            }
          }}
          onInputChange={(name, value) =>
            setNewCategory({ ...newCategory, [name]: value })
          }
          onClose={() => setShowAdd(false)}
          onConfirm={handleAddCategory}
          confirmText="Thêm"
          confirmColor="success"
          cancelText="Hủy"
          cancelColor="gray"
        />

        <Popup
          open={showEdit && selectedCategory}
          title="Sửa danh mục"
          titleColor="info"
          inputs={[
            {
              label: "Tên danh mục",
              name: "name",
              placeholder: "Tên danh mục",
              value: selectedCategory?.name || "",
            },
            {
              label: "Mô tả",
              name: "description",
              type: "textarea",
              placeholder: "Mô tả",
              value: selectedCategory?.description || "",
            },
          ]}
          showImageUpload={true}
          imageUrl={selectedCategory?.imageUrl}
          onImageChange={(e) => {
            const file = e.target.files[0];
            if (file) {
              const reader = new FileReader();
              reader.onload = (ev) =>
                setSelectedCategory({
                  ...selectedCategory,
                  imageUrl: ev.target.result,
                });
              reader.readAsDataURL(file);
            }
          }}
          children={
            selectedCategory?.imageUrl && (
              <button
                style={{
                  margin: "8px 0 0 0",
                  background: "#fff",
                  color: "#d32f2f",
                  border: "1px solid #d32f2f",
                  borderRadius: 8,
                  padding: "4px 12px",
                  cursor: "pointer",
                  fontFamily: "Quicksand, sans-serif",
                  fontSize: 14,
                }}
                onClick={() =>
                  setSelectedCategory({ ...selectedCategory, imageUrl: "" })
                }
                type="button"
              >
                Xóa ảnh
              </button>
            )
          }
          onInputChange={(name, value) =>
            setSelectedCategory({ ...selectedCategory, [name]: value })
          }
          onClose={() => setShowEdit(false)}
          onConfirm={handleEditCategory}
          confirmText="Lưu"
          confirmColor="info"
          cancelText="Hủy"
          cancelColor="gray"
        />

        <Popup
          open={showDelete && selectedCategory}
          title="Xác nhận xóa danh mục"
          titleColor="error"
          content={
            selectedCategory
              ? `Bạn có chắc muốn xóa danh mục "${selectedCategory.name}" không?`
              : ""
          }
          contentColor="error"
          onClose={() => setShowDelete(false)}
          onConfirm={handleDeleteCategory}
          confirmText="Xóa"
          confirmColor="error"
          cancelText="Hủy"
          cancelColor="gray"
        />
      </main>
    </div>
  );
};

export default ListCategory;

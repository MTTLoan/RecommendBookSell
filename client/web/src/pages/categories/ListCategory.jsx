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
  const [newCategory, setNewCategory] = useState({ name: "", description: "" });
  const [searchValue, setSearchValue] = useState("");
  const [imageFile, setImageFile] = useState(null);
  const [editImageFile, setEditImageFile] = useState(null);
  const [previewImage, setPreviewImage] = useState("");
  const [editPreviewImage, setEditPreviewImage] = useState("");

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

  // Reset form khi đóng popup thêm
  const resetAddForm = () => {
    setNewCategory({ name: "", description: "" });
    setImageFile(null);
    setPreviewImage("");
  };

  // Reset form khi đóng popup sửa
  const resetEditForm = () => {
    setSelectedCategory(null);
    setEditImageFile(null);
    setEditPreviewImage("");
  };

  // Xử lý thay đổi ảnh trong popup Add
  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setImageFile(file);
      const reader = new FileReader();
      reader.onload = (event) => {
        setPreviewImage(event.target.result);
      };
      reader.readAsDataURL(file);
    }
  };

  // Xử lý thay đổi ảnh trong popup Edit
  const handleEditImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setEditImageFile(file);
      const reader = new FileReader();
      reader.onload = (event) => {
        setEditPreviewImage(event.target.result);
      };
      reader.readAsDataURL(file);
    }
  };

  // Thêm danh mục
  const handleAddCategory = async () => {
    try {
      // Kiểm tra dữ liệu đầu vào
      if (!newCategory.name || newCategory.name.trim() === "") {
        alert("Vui lòng nhập tên danh mục");
        return;
      }

      const data = {
        name: newCategory.name,
        description: newCategory.description || "",
        imageFile: imageFile,
      };

      console.log("Sending data:", data);
      await addCategory(data);
      setShowAdd(false);
      resetAddForm();
      loadData();
    } catch (error) {
      console.error("Lỗi thêm danh mục:", error);
      console.error("Error response:", error.response?.data);
      alert(
        "Có lỗi xảy ra khi thêm danh mục: " +
          (error.response?.data?.msg || error.message)
      );
    }
  };

  // Sửa danh mục
  const handleEditCategory = async () => {
    try {
      const data = {
        name: selectedCategory.name,
        description: selectedCategory.description,
        imageFile: editImageFile,
        removeImage: selectedCategory.removeImage,
      };
      await updateCategory(Number(selectedCategory.id), data);
      setShowEdit(false);
      resetEditForm();
      loadData();
    } catch (error) {
      console.error("Lỗi sửa danh mục:", error);
    }
  };

  // Xóa danh mục
  const handleDeleteCategory = async () => {
    try {
      await deleteCategory(Number(selectedCategory.id));
      setShowDelete(false);
      setSelectedCategory(null);
      loadData();
    } catch (error) {
      console.error("Lỗi xóa danh mục:", error);
    }
  };

  // Xóa ảnh trong popup Edit
  const handleRemoveEditImage = () => {
    setSelectedCategory({
      ...selectedCategory,
      imageUrl: "",
      removeImage: true,
    });
    setEditImageFile(null);
    setEditPreviewImage("");
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
              setSelectedCategory({
                ...cat,
                removeImage: false,
              });
              setEditPreviewImage("");
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

        {/* Popup Thêm danh mục */}
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
          imageUrl={previewImage}
          onImageChange={handleImageChange}
          onInputChange={(name, value) =>
            setNewCategory({ ...newCategory, [name]: value })
          }
          onClose={() => {
            setShowAdd(false);
            resetAddForm();
          }}
          onConfirm={handleAddCategory}
          confirmText="Thêm"
          confirmColor="success"
          cancelText="Hủy"
          cancelColor="gray"
        />

        {/* Popup Sửa danh mục */}
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
          imageUrl={
            editPreviewImage ||
            (selectedCategory?.removeImage ? "" : selectedCategory?.imageUrl)
          }
          onImageChange={handleEditImageChange}
          children={
            (selectedCategory?.imageUrl && !selectedCategory?.removeImage) ||
            editPreviewImage ? (
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
                onClick={handleRemoveEditImage}
                type="button"
              >
                Xóa ảnh
              </button>
            ) : null
          }
          onInputChange={(name, value) =>
            setSelectedCategory({ ...selectedCategory, [name]: value })
          }
          onClose={() => {
            setShowEdit(false);
            resetEditForm();
          }}
          onConfirm={handleEditCategory}
          confirmText="Lưu"
          confirmColor="info"
          cancelText="Hủy"
          cancelColor="gray"
        />

        {/* Popup Xóa danh mục */}
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

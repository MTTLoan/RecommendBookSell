import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Navbar from "../../components/layout/Navbar";
import Sidebar from "../../components/layout/Sidebar";
import Table from "../../components/layout/Table";
import { fetchCategories } from "../../services/categoryService";
import { fetchBooks } from "../../services/bookService";
import "../../styles/viewcategory.css";

const ViewCategory = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [category, setCategory] = useState(null);
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    Promise.all([fetchCategories(), fetchBooks()])
      .then(([cats, books]) => {
        const cat = (cats || []).find(
          (c) => String(c.id || c._id) === String(id)
        );
        setCategory(cat);
        const filtered = (books || []).filter(
          (b) => String(b.categoryId) === String(cat.id || cat._id)
        );
        setProducts(filtered);
      })
      .finally(() => setLoading(false));
  }, [id]);

  const columns = [
    {
      key: "name",
      label: "Tên sản phẩm",
      render: (prod) => prod.name,
    },
    {
      key: "image",
      label: "Hình ảnh",
      render: (prod) => (
        <img
          src={
            prod.images?.[0]?.url ||
            prod.images?.[0] ||
            "https://via.placeholder.com/40x40?text=No+Img"
          }
          alt={prod.name}
          style={{
            width: 40,
            height: 40,
            objectFit: "cover",
            borderRadius: 8,
            border: "1px solid #eee",
          }}
        />
      ),
    },
    {
      key: "stockQuantity",
      label: "Số lượng",
      render: (prod) => prod.stockQuantity ?? prod.quantity,
    },
  ];

  return (
    <div className="dashboard-layout">
      <Navbar />
      <Sidebar />
      <main className="dashboard-content">
        <div className="product-header">
          <h1>Danh mục</h1>
          <p className="product-subtitle">
            <span
              className="subtitle"
              style={{ cursor: "pointer" }}
              onClick={() => navigate("/categories")}
            >
              Danh mục
            </span>
            <span className="subtitle subtitle-sep">{">"}</span>
            <span className="subtitle active">{category?.name || ""}</span>
          </p>
        </div>
        {loading ? (
          <div style={{ padding: 40, textAlign: "center" }}>
            Đang tải dữ liệu...
          </div>
        ) : (
          <>
            <Table
              title=""
              data={products}
              columns={columns}
              showHeader
              showSearch={false}
              showFilter={false}
              showDownload={false}
              showAddButton={false}
              showCheckbox={false}
              showSort={true}
              style={{ padding: 0 }}
            />
            <div style={{ marginTop: 32, textAlign: "center", width: "10%" }}>
              <button onClick={() => navigate("/categories")}>Thoát</button>
            </div>
          </>
        )}
      </main>
    </div>
  );
};

export default ViewCategory;

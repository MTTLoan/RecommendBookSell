import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../../components/layout/Navbar";
import Sidebar from "../../components/layout/Sidebar";
import Table from "../../components/layout/Table";
import { fetchBooks, searchNameBooks } from "../../services/bookService";
import { fetchReviewStatsForBooks } from "../../services/reviewService";
import "../../styles/listreview.css";

const ListReview = () => {
  const [searchValue, setSearchValue] = useState("");
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const [allBooks, setAllBooks] = useState([]);

  const loadBooks = async () => {
    setLoading(true);
    try {
      const bookList = await fetchBooks();
      const stats = await fetchReviewStatsForBooks(bookList.map((b) => b.id));
      const merged = bookList.map((b) => {
        const stat = stats.find((s) => s.bookId === b.id) || {
          reviewCount: 0,
          avgRating: 0,
        };
        return {
          id: b.id,
          name: b.name,
          reviewCount: stat.reviewCount,
          avgRating: stat.avgRating,
        };
      });
      setAllBooks(merged);
      setBooks(merged);
    } catch (err) {
      setAllBooks([]);
      setBooks([]);
    }
    setLoading(false);
  };

  useEffect(() => {
    loadBooks();
  }, []);

  useEffect(() => {
    if (!searchValue) {
      setBooks(allBooks);
    } else {
      setBooks(
        allBooks.filter((b) =>
          b.name.toLowerCase().includes(searchValue.toLowerCase())
        )
      );
    }
  }, [searchValue, allBooks]);

  const handleView = (id) => {
    navigate(`/reviews/detail/${id}`);
  };

  const columns = [
    {
      key: "name",
      label: "Tên sản phẩm",
      width: "50%",
      render: (row) => <span>{row.name}</span>,
    },
    {
      key: "reviewCount",
      label: "Số lượt đánh giá",
      width: "20%",
      render: (row) => <span>{row.reviewCount}</span>,
    },
    {
      key: "avgRating",
      label: "Điểm trung bình",
      width: "20%",
      render: (row) =>
        row.reviewCount > 0 ? (
          <span>{row.avgRating.toFixed(1)} / 5</span>
        ) : (
          <span>Chưa có</span>
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
            onClick={() => handleView(row.id)}
            style={{ cursor: "pointer" }}
          >
            visibility
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
          <h1>Đánh giá</h1>
          <p className="product-subtitle">
            <span className="subtitle active">Đánh giá</span>
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
            data={books}
            columns={columns}
            showHeader={true}
            showSearch={true}
            searchValue={searchValue}
            setSearchValue={setSearchValue}
            onSearch={(v) => setSearchValue(v)}
            showFilter={false}
            showDownload={false}
            showAddButton={false}
            showCheckbox={false}
            showSort={true}
          />
        )}
      </main>
    </div>
  );
};

export default ListReview;

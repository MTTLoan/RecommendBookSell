import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Navbar from "../../components/layout/Navbar";
import Sidebar from "../../components/layout/Sidebar";
import Table from "../../components/layout/Table";
import { fetchBookDetail } from "../../services/bookService";
import { fetchReviewsByBookId } from "../../services/reviewService";
import "../../styles/detailreview.css";

const DetailReview = () => {
  const { id } = useParams(); // id là id sách
  const navigate = useNavigate();
  const [book, setBook] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    Promise.all([fetchBookDetail(id), fetchReviewsByBookId(id)])
      .then(([bookData, reviewList]) => {
        setBook(bookData);
        setReviews(reviewList || []);
      })
      .finally(() => setLoading(false));
  }, [id]);

  const columns = [
    {
      key: "username",
      label: "Tên người dùng",
      render: (review) => review.userName || review.username || "Ẩn danh",
    },
    {
      key: "rating",
      label: "Số điểm",
      render: (review) => review.rating,
    },
    {
      key: "content",
      label: "Nội dung đánh giá",
      render: (review) => review.comment,
    },
  ];

  return (
    <div className="dashboard-layout">
      <Navbar />
      <Sidebar />
      <main className="dashboard-content">
        <div className="product-header">
          <h1>Chi tiết đánh giá</h1>
          <p className="product-subtitle">
            <span
              className="subtitle"
              style={{ cursor: "pointer" }}
              onClick={() => navigate("/reviews")}
            >
              Đánh giá
            </span>
            <span className="subtitle subtitle-sep">{">"}</span>
            <span className="subtitle active">{book?.name || ""}</span>
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
              data={reviews}
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
              <button onClick={() => navigate("/reviews")}>Thoát</button>
            </div>
          </>
        )}
      </main>
    </div>
  );
};

export default DetailReview;

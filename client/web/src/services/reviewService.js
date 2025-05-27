import axios from "axios";
import { API_BASE_URL } from "../utils/constants";

// Lấy thống kê review cho nhiều sách (idList: mảng id sách)
export const fetchReviewStatsForBooks = async (idList) => {
  if (!idList || idList.length === 0) return [];
  const token = localStorage.getItem("token");
  const headers = token ? { Authorization: `Bearer ${token}` } : {};
  const res = await axios.post(
    `${API_BASE_URL}/reviews/stats`,
    { bookIds: idList },
    { headers }
  );
  return res.data.stats || [];
};

export const fetchReviewsByBookId = async (bookId) => {
  const token = localStorage.getItem("token");
  const headers = token ? { Authorization: `Bearer ${token}` } : {};
  const res = await axios.get(`${API_BASE_URL}/reviews/book/${bookId}`, {
    headers,
  });
  return res.data.reviews || [];
};

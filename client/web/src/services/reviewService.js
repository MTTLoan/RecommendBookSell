import axios from 'axios';
import { API_BASE_URL } from '../utils/constants';

// Lấy thống kê review cho nhiều sách (idList: mảng id sách)
export const fetchReviewStatsForBooks = async (idList) => {
  if (!idList || idList.length === 0) return [];
  const res = await axios.post(`${API_BASE_URL}/reviews/stats`, { bookIds: idList });
  return res.data.stats || [];
};

export const fetchReviewsByBookId = async (bookId) => {
  const res = await axios.get(`${API_BASE_URL}/reviews/book/${bookId}`);
  return res.data.reviews || [];
};
import axios from 'axios';
import { API_BASE_URL } from '../utils/constants';

// Lấy tất cả thông báo cho admin (kèm tên khách hàng)
export const fetchNotifications = async () => {
  const token = localStorage.getItem('token');
  const res = await axios.get(`${API_BASE_URL}/notifications/admin`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  return res.data;
};

export const addNotification = async (data) => {
  const token = localStorage.getItem('token');
  const res = await axios.post(`${API_BASE_URL}/notifications`, data, {
    headers: { Authorization: `Bearer ${token}` }
  });
  return res.data;
};

export const updateNotification = async (id, data) => {
  const token = localStorage.getItem('token');
  const res = await axios.put(`${API_BASE_URL}/notifications/${id}`, data, {
    headers: { Authorization: `Bearer ${token}` }
  });
  return res.data;
};

export const deleteNotification = async (id) => {
  const token = localStorage.getItem('token');
  const res = await axios.delete(`${API_BASE_URL}/notifications/${id}`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  return res.data;
};
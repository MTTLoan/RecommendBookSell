import axios from "axios";
import { API_BASE_URL } from "../utils/constants";

// Lấy tất cả thông báo cho admin (kèm tên khách hàng)
export const fetchNotifications = async () => {
  try {
    const res = await axios.get(`${API_BASE_URL}/notifications/admin`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    });
    console.log("Fetched notifications:", res.data); // Log dữ liệu
    return res.data;
  } catch (error) {
    console.error(
      "Error fetching notifications:",
      error.response?.data || error.message
    );
    throw error;
  }
};

export const addNotification = async (data) => {
  try {
    const res = await axios.post(
      `${API_BASE_URL}/notifications`,
      {
        userId: Number(data.userId), // Đảm bảo userId là Number
        title: data.title,
        message: data.message,
        orderId: data.orderId || null,
      },
      {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      }
    );
    console.log("Added notification:", res.data); // Log kết quả
    return res.data;
  } catch (error) {
    console.error(
      "Error adding notification:",
      error.response?.data || error.message
    );
    throw error;
  }
};

export const updateNotification = async (id, data) => {
  try {
    const res = await axios.put(
      `${API_BASE_URL}/notifications/admin/${id}`,
      { message: data.content }, // Ánh xạ content sang message
      {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      }
    );
    return res.data;
  } catch (error) {
    console.error(
      "Error updating notification:",
      error.response?.data || error.message
    );
    throw error;
  }
};

export const deleteNotification = async (id) => {
  const res = await axios.delete(`${API_BASE_URL}/notifications/${id}`, {
    headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
  });
  return res.data;
};

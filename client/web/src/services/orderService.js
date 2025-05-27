import axios from "axios";
import { API_BASE_URL } from "../utils/constants";

export const fetchAllOrders = async () => {
  const token = localStorage.getItem("token");
  const headers = token ? { Authorization: `Bearer ${token}` } : {};
  const res = await axios.get(`${API_BASE_URL}/orders`, { headers });
  return res.data;
};

export const searchOrders = async (query) => {
  const token = localStorage.getItem("token");
  const headers = token ? { Authorization: `Bearer ${token}` } : {};
  return await axios.get(
    `${API_BASE_URL}/orders/search?q=${encodeURIComponent(query)}`,
    { headers }
  );
};

export const updateOrderStatus = async (id, status) => {
  const token = localStorage.getItem("token");
  const headers = token ? { Authorization: `Bearer ${token}` } : {};
  const res = await axios.put(
    `${API_BASE_URL}/orders/admin/${id}/status`,
    { status },
    { headers }
  );
  return res.data;
};

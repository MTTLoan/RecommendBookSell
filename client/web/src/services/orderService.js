import axios from "axios";
import { API_BASE_URL } from "../utils/constants";

export const fetchAllOrders = async () => {
  const res = await axios.get(`${API_BASE_URL}/orders`);
  return res.data;
};

export const searchOrders = async (query) => {
  return await axios.get(
    `${API_BASE_URL}/orders/search?q=${encodeURIComponent(query)}`
  );
};

export const updateOrderStatus = async (id, status) => {
  const res = await axios.put(
    `${API_BASE_URL}/orders/admin/${id}/status`,
    { status },
    {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    }
  );
  return res.data;
};

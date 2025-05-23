import axios from 'axios';
import { API_BASE_URL } from '../utils/constants';

export const fetchAllOrders = async () => {
  const res = await axios.get(`${API_BASE_URL}/orders`);
  return res.data;
};

export const searchOrders = async (query) => {
  return await axios.get(`${API_BASE_URL}/orders/search?q=${encodeURIComponent(query)}`);
};
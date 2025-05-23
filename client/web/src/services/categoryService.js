import axios from 'axios';
import { API_BASE_URL } from '../utils/constants';

export const fetchCategories = async () => {
  const res = await axios.get(`${API_BASE_URL}/categories/all-categories`);
  return res.data.categories;
};

export const fetchCategoryStats = async () => {
  const res = await axios.get(`${API_BASE_URL}/categories/category-stats`);
  return res.data;
};

export const addCategory = async (data) => {
  const res = await axios.post(`${API_BASE_URL}/categories/add-category`, data);
  return res.data.category;
};

export const updateCategory = async (id, data) => {
  const res = await axios.put(`${API_BASE_URL}/categories/update-category/${Number(id)}`, data);
  return res.data.category;
};

export const deleteCategory = async (id) => {
  const res = await axios.delete(`${API_BASE_URL}/categories/delete-category/${Number(id)}`);
  return res.data.category;
};

export const searchCategories = async (query) => {
  const res = await axios.get(`${API_BASE_URL}/categories/search?q=${encodeURIComponent(query)}`);
  return res.data.categories || [];
};
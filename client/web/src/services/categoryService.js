import axios from "axios";
import { API_BASE_URL } from "../utils/constants";

// Helper để lấy headers nếu có token
const getAuthHeaders = () => {
  const token = localStorage.getItem("token");
  return token ? { Authorization: `Bearer ${token}` } : {};
};

export const fetchCategories = async () => {
  const res = await axios.get(`${API_BASE_URL}/categories`, {
    headers: getAuthHeaders(),
  });
  return res.data.categories || res.data;
};

export const fetchCategoryStats = async () => {
  const res = await axios.get(`${API_BASE_URL}/categories/stats`, {
    headers: getAuthHeaders(),
  });
  return res.data;
};

export const addCategory = async (data) => {
  try {
    const formData = new FormData();
    formData.append("name", data.name || "");
    formData.append("description", data.description || "");
    if (data.imageFile) {
      formData.append("avatar", data.imageFile);
    }

    const res = await axios.post(`${API_BASE_URL}/categories`, formData, {
      headers: { "Content-Type": "multipart/form-data", ...getAuthHeaders() },
    });
    return res.data.category || res.data;
  } catch (error) {
    console.error("Service error:", error.response?.data || error.message);
    throw error;
  }
};

export const updateCategory = async (id, data) => {
  const formData = new FormData();
  formData.append("name", data.name);
  formData.append("description", data.description || "");
  if (data.removeImage) {
    formData.append("removeImage", "true");
  }
  if (data.imageFile) {
    formData.append("avatar", data.imageFile);
  }
  const res = await axios.put(
    `${API_BASE_URL}/categories/${Number(id)}`,
    formData,
    { headers: { "Content-Type": "multipart/form-data", ...getAuthHeaders() } }
  );
  return res.data.category || res.data;
};

export const deleteCategory = async (id) => {
  const res = await axios.delete(`${API_BASE_URL}/categories/${Number(id)}`, {
    headers: getAuthHeaders(),
  });
  return res.data.category || res.data;
};

export const searchCategories = async (query) => {
  const res = await axios.get(
    `${API_BASE_URL}/categories/search?q=${encodeURIComponent(query)}`,
    { headers: getAuthHeaders() }
  );
  return res.data.categories || res.data;
};

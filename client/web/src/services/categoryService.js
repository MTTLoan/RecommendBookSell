import axios from "axios";
import { API_BASE_URL } from "../utils/constants";

export const fetchCategories = async () => {
  const token = localStorage.getItem("token");
  const headers = token ? { Authorization: `Bearer ${token}` } : {};
  const res = await axios.get(`${API_BASE_URL}/categories/all-categories`, {
    headers,
  });
  return res.data.categories;
};

export const fetchCategoryStats = async () => {
  const token = localStorage.getItem("token");
  const headers = token ? { Authorization: `Bearer ${token}` } : {};
  const res = await axios.get(`${API_BASE_URL}/categories/category-stats`, {
    headers,
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

    console.log("FormData being sent:");
    for (let [key, value] of formData.entries()) {
      console.log(key, value);
    }

    const token = localStorage.getItem("token");
    const headers = {
      "Content-Type": "multipart/form-data",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    };
    const res = await axios.post(
      `${API_BASE_URL}/categories/add-category`,
      formData,
      { headers }
    );
    return res.data.category;
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
  const token = localStorage.getItem("token");
  const headers = {
    "Content-Type": "multipart/form-data",
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
  const res = await axios.put(
    `${API_BASE_URL}/categories/update-category/${Number(id)}`,
    formData,
    { headers }
  );
  return res.data.category;
};

export const deleteCategory = async (id) => {
  const token = localStorage.getItem("token");
  const headers = token ? { Authorization: `Bearer ${token}` } : {};
  const res = await axios.delete(
    `${API_BASE_URL}/categories/delete-category/${Number(id)}`,
    { headers }
  );
  return res.data.category;
};

export const searchCategories = async (query) => {
  const token = localStorage.getItem("token");
  const headers = token ? { Authorization: `Bearer ${token}` } : {};
  const res = await axios.get(
    `${API_BASE_URL}/categories/search?q=${encodeURIComponent(query)}`,
    { headers }
  );
  return res.data.categories || [];
};

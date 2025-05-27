import axios from "axios";
import { API_BASE_URL } from "../utils/constants";

export const fetchCategories = async () => {
  const res = await axios.get(`${API_BASE_URL}/categories/all-categories`);
  return res.data.categories;
};

export const fetchCategoryStats = async () => {
  const res = await axios.get(`${API_BASE_URL}/categories/category-stats`);
  return res.data;
};

export const addCategory = async (data) => {
  try {
    const formData = new FormData();
    formData.append("name", data.name || "");
    formData.append("description", data.description || "");

    // Nếu có file ảnh (từ input file)
    if (data.imageFile) {
      formData.append("avatar", data.imageFile);
    }

    console.log("FormData being sent:");
    for (let [key, value] of formData.entries()) {
      console.log(key, value);
    }

    const res = await axios.post(
      `${API_BASE_URL}/categories/add-category`,
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      }
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

  // Nếu có yêu cầu xóa ảnh
  if (data.removeImage) {
    formData.append("removeImage", "true");
  }

  // Nếu có file ảnh mới
  if (data.imageFile) {
    formData.append("avatar", data.imageFile);
  }

  const res = await axios.put(
    `${API_BASE_URL}/categories/update-category/${Number(id)}`,
    formData,
    {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    }
  );
  return res.data.category;
};

export const deleteCategory = async (id) => {
  const res = await axios.delete(
    `${API_BASE_URL}/categories/delete-category/${Number(id)}`
  );
  return res.data.category;
};

export const searchCategories = async (query) => {
  const res = await axios.get(
    `${API_BASE_URL}/categories/search?q=${encodeURIComponent(query)}`
  );
  return res.data.categories || [];
};

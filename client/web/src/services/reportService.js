import axios from "axios";
import { API_BASE_URL } from "../utils/constants";

// Định nghĩa URL cơ bản cho các API reports
const REPORTS_API_URL = "http://localhost:5000/api/reports";

export const fetchRevenueStats = async (month, year) => {
  const res = await axios.get(
    `${REPORTS_API_URL}/stats/revenue?month=${month}&year=${year}`,
    {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("token")}`, // Sử dụng localStorage
      },
    }
  );
  return res.data;
};

export const fetchClickStats = async (month, year) => {
  const res = await axios.get(
    `${REPORTS_API_URL}/stats/clicks?month=${month}&year=${year}`,
    {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("token")}`, // Sử dụng localStorage
      },
    }
  );
  return res.data;
};

export const fetchAddToCartStats = async (month, year) => {
  const res = await axios.get(
    `${REPORTS_API_URL}/stats/add-to-cart?month=${month}&year=${year}`,
    {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("token")}`, // Sử dụng localStorage
      },
    }
  );
  return res.data;
};

export const fetchPurchaseStats = async (month, year) => {
  const res = await axios.get(
    `${REPORTS_API_URL}/stats/purchases?month=${month}&year=${year}`,
    {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("token")}`, // Sử dụng localStorage
      },
    }
  );
  return res.data;
};

export const fetchTopProducts = async (month, year) => {
  const res = await axios.get(
    `${REPORTS_API_URL}/top-products?month=${month}&year=${year}`,
    {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    }
  );
  return res.data;
};

export const fetchRevenueChartData = async (month, year) => {
  const res = await axios.get(
    `${REPORTS_API_URL}/chart/revenue?month=${month}&year=${year}`,
    {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    }
  );
  return res.data;
};

export const fetchCategoryRevenueChartData = async (month, year) => {
  const res = await axios.get(
    `${REPORTS_API_URL}/chart/category-revenue?month=${month}&year=${year}`,
    {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    }
  );
  return res.data;
};

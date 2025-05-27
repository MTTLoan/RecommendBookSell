import axios from "axios";
import { API_BASE_URL } from "../utils/constants";

const REPORTS_API_URL = "http://localhost:5000/api/reports";

const getHeaders = () => ({
  "Content-Type": "application/json",
  Authorization: `Bearer ${localStorage.getItem("token")}`,
});

export const fetchRevenueStats = async (month, year) => {
  const query =
    month === null ? `year=${year}` : `month=${month + 1}&year=${year}`;
  const res = await axios.get(`${REPORTS_API_URL}/stats/revenue?${query}`, {
    headers: getHeaders(),
  });
  return res.data;
};

export const fetchClickStats = async (month, year) => {
  const query =
    month === null ? `year=${year}` : `month=${month + 1}&year=${year}`;
  const res = await axios.get(`${REPORTS_API_URL}/stats/clicks?${query}`, {
    headers: getHeaders(),
  });
  return res.data;
};

export const fetchAddToCartStats = async (month, year) => {
  const query =
    month === null ? `year=${year}` : `month=${month + 1}&year=${year}`;
  const res = await axios.get(`${REPORTS_API_URL}/stats/add-to-cart?${query}`, {
    headers: getHeaders(),
  });
  return res.data;
};

export const fetchPurchaseStats = async (month, year) => {
  const query =
    month === null ? `year=${year}` : `month=${month + 1}&year=${year}`;
  const res = await axios.get(`${REPORTS_API_URL}/stats/purchases?${query}`, {
    headers: getHeaders(),
  });
  return res.data;
};

export const fetchTopProducts = async (month, year) => {
  const query =
    month === null ? `year=${year}` : `month=${month + 1}&year=${year}`;
  const res = await axios.get(`${REPORTS_API_URL}/top-products?${query}`, {
    headers: getHeaders(),
  });
  return res.data;
};

export const fetchRevenueChartData = async (month, year) => {
  const query =
    month === null ? `year=${year}` : `month=${month + 1}&year=${year}`;
  const res = await axios.get(`${REPORTS_API_URL}/chart/revenue?${query}`, {
    headers: getHeaders(),
  });
  return res.data;
};

export const fetchCategoryRevenueChartData = async (month, year) => {
  const query =
    month === null ? `year=${year}` : `month=${month + 1}&year=${year}`;
  const res = await axios.get(
    `${REPORTS_API_URL}/chart/category-revenue?${query}`,
    {
      headers: getHeaders(),
    }
  );
  return res.data;
};

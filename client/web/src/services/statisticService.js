import axios from "axios";
import { API_BASE_URL } from "../utils/constants";

export const fetchDashboardStats = async () => {
  const res = await axios.get(`${API_BASE_URL}/dashboard/stats`);
  return res.data;
};
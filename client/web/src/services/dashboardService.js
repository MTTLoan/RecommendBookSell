import axios from 'axios';
import { API_BASE_URL } from '../utils/constants';

const DASHBOARD_API_URL = `${API_BASE_URL}/dashboard`;

const getDashboardData = async () => {
  try {
    const response = await axios.get(DASHBOARD_API_URL);
    return response.data;
  } catch (error) {
    console.error('Error fetching dashboard data:', error);
    throw error; // Re-throw the error for the caller to handle
  }
};

const dashboardService = {
  getDashboardData,
};

export default dashboardService;
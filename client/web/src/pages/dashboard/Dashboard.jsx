import Sidebar from '../../components/layout/Sidebar';
import Navbar from '../../components/layout/Navbar';
import Breadcrumb from '../../components/common/Breadcrumb';
import RevenueChart from '../../components/dashboard/RevenueChart';
import OverviewWidgets from '../../components/dashboard/OverviewWidgets';
import TopProductsTable from '../../components/dashboard/TopProductsTable';
import dashboardService from '../../services/dashboardService';
import { useEffect, useState } from 'react';
import '../../styles/dashboard.css';

export default function Dashboard() {
  const [statistics, setStatistics] = useState(null);

  useEffect(() => {
    dashboardService.getDashboardData().then(setStatistics);
  }, []);

  return (
    <div className="dashboard-container">
      <Sidebar />
      <div className="main-content">
        <Navbar />
        <Breadcrumb title="Tổng quan" paths={['Tổng quan']} />
        <div className="dashboard-content">
          <RevenueChart />
          <OverviewWidgets />
          <TopProductsTable />
        </div>
      </div>
    </div>
  );
}

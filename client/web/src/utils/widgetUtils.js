// Hàm tính mô tả dựa trên monthFilter và giá trị thay đổi
export const getWidgetDescription = (changeValue, monthFilter, periodLabel = "tháng") => {
  const absChange = Math.abs(changeValue);
  const isIncrease = changeValue >= 0;
  const comparisonPeriod = monthFilter === null || monthFilter === undefined ? "năm trước" : `${periodLabel} trước`;

  if (changeValue === 0) {
    return `Không thay đổi so với ${comparisonPeriod}`;
  }
  return `${isIncrease ? "Tăng" : "Giảm"} ${absChange}% so với ${comparisonPeriod}`;
};

// Hàm tạo dữ liệu widget cho một mục (ví dụ: Doanh thu, Khách hàng, ...)
export const createWidgetData = (title, value, changeValue, monthFilter) => {
  return [
    title,
    value,
    `${Math.abs(changeValue)}%`,
    getWidgetDescription(changeValue, monthFilter),
  ];
};

// Hàm tạo toàn bộ dữ liệu widget cho worksheet
export const generateWidgetDataForExport = (stats, monthFilter) => {
  const widgetHeaders = ["Tiêu đề", "Giá trị", "Phần trăm thay đổi", "Mô tả"];
  const widgetData = [
    createWidgetData(
      "Doanh thu",
      stats.revenue?.toLocaleString("vi-VN") + " VND" || "0 VND",
      stats.revenueChange || 0,
      monthFilter
    ),
    createWidgetData(
      "Khách hàng",
      stats.customers || 0,
      stats.customersChange || 0,
      monthFilter
    ),
    createWidgetData(
      "Đơn hàng",
      stats.orders || 0,
      stats.ordersChange || 0,
      monthFilter
    ),
    createWidgetData(
      "Sản phẩm",
      stats.products || 0,
      stats.productsChange || 0,
      monthFilter
    ),
  ];
  return [widgetHeaders, ...widgetData];
};

// Hàm tạo dữ liệu widget hiển thị trên giao diện (trả về object thay vì mảng)
export const getWidgetProps = (title, value, changeValue, monthFilter) => {
  return {
    title,
    value,
    percentage: Math.abs(changeValue),
    isIncrease: changeValue >= 0,
    description: getWidgetDescription(changeValue, monthFilter),
  };
};
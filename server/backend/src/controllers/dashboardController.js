// const db = require('../config/db'); // Sequelize hoặc Knex tùy bạn dùng
// const { Op } = require('sequelize');
// const { Statistics, Orders, Users } = require('../models');

// exports.getDashboardData = async (req, res) => {
//   try {
//     const today = new Date();
//     const week = getWeekNumber(today);
//     const month = today.getMonth() + 1;
//     const year = today.getFullYear();

//     // Biểu đồ doanh thu 30 ngày (demo: lấy theo ngày)
//     const chartData = await Statistics.findAll({
//       where: { year },
//       order: [['date', 'ASC']],
//       attributes: ['date', 'totalRevenue']
//     });

//     // 4 widgets chính
//     const latest = await Statistics.findOne({ where: { year, week }, order: [['date', 'DESC']] });
//     const previous = await Statistics.findOne({ where: { year, week: week - 1 } });

//     const percentChange = (cur, prev) => {
//       if (!prev || prev === 0) return 0;
//       return +(((cur - prev) / prev) * 100).toFixed(1);
//     };

//     const widgets = [
//       {
//         label: 'Tổng doanh thu',
//         value: `${latest?.totalRevenue?.toLocaleString()} đ`,
//         change: percentChange(latest?.totalRevenue, previous?.totalRevenue),
//       },
//       {
//         label: 'Khách hàng mới',
//         value: await Users.count({
//           where: {
//             createdAt: {
//               [Op.gte]: startOfWeek(today),
//               [Op.lt]: endOfWeek(today)
//             }
//           }
//         }),
//         change: 1.5, // hoặc tính % từ tuần trước
//       },
//       {
//         label: 'Đơn hàng mới',
//         value: latest?.totalOrders || 0,
//         change: percentChange(latest?.totalOrders, previous?.totalOrders),
//       },
//       {
//         label: 'Sản phẩm đã bán',
//         value: await getTotalProductsSold(week, year),
//         change: -1.5 // giả định
//       }
//     ];

//     // Sản phẩm bán chạy
//     const topProducts = await getTopProductsSold(5, week, year);

//     res.json({ chartData, widgets, topProducts });
//   } catch (err) {
//     console.error(err);
//     res.status(500).json({ error: 'Internal server error' });
//   }
// };

// function getWeekNumber(date) {
//   const firstDayOfYear = new Date(date.getFullYear(), 0, 1);
//   const days = Math.floor((date - firstDayOfYear) / (24 * 60 * 60 * 1000));
//   return Math.ceil((days + firstDayOfYear.getDay() + 1) / 7);
// }

// function startOfWeek(d) {
//   const date = new Date(d);
//   const day = date.getDay();
//   const diff = date.getDate() - day + (day === 0 ? -6 : 1);
//   return new Date(date.setDate(diff));
// }

// function endOfWeek(d) {
//   const date = startOfWeek(d);
//   return new Date(date.setDate(date.getDate() + 7));
// }

// async function getTotalProductsSold(week, year) {
//   const orders = await Orders.findAll({
//     where: db.Sequelize.where(db.Sequelize.fn('YEARWEEK', db.Sequelize.col('orderDate'), 1), `${year}${week}`),
//     attributes: ['items']
//   });

//   let total = 0;
//   orders.forEach(order => {
//     const items = JSON.parse(order.items || '[]');
//     items.forEach(item => total += item.quantity);
//   });

//   return total;
// }

// async function getTopProductsSold(limit, week, year) {
//   const orders = await Orders.findAll({
//     where: db.Sequelize.where(db.Sequelize.fn('YEARWEEK', db.Sequelize.col('orderDate'), 1), `${year}${week}`),
//     attributes: ['items']
//   });

//   const countMap = {};

//   orders.forEach(order => {
//     const items = JSON.parse(order.items || '[]');
//     items.forEach(item => {
//       if (!countMap[item.bookId]) {
//         countMap[item.bookId] = { quantity: 0, unitPrice: item.unitPrice };
//       }
//       countMap[item.bookId].quantity += item.quantity;
//     });
//   });

//   const sorted = Object.entries(countMap)
//     .map(([bookId, data]) => ({
//       id: bookId,
//       sold: data.quantity,
//       price: data.unitPrice
//     }))
//     .sort((a, b) => b.sold - a.sold)
//     .slice(0, limit);

//   // Nếu bạn có bảng Book, join vào để lấy tên
//   return sorted.map(p => ({
//     name: `Sản phẩm #${p.id}`,
//     price: p.price.toLocaleString(),
//     sold: p.sold
//   }));
// }

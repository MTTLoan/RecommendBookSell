import { useState } from 'react';
import * as XLSX from 'xlsx';
import '../../styles/topProductsTable.css'; 

export default function TopProductsTable({ products }) {
    const [filter, setFilter] = useState(10); // Default filter is top 10

    const handleFilterChange = (e) => {
        setFilter(Number(e.target.value));
    };

    const handleDownloadExcel = () => {
        const filteredProducts = products.slice(0, filter);
        const worksheet = XLSX.utils.json_to_sheet(
            filteredProducts.map((p) => ({
                'Sản phẩm': p.name,
                Giá: `${p.price} VND`,
                'Lượt bán': p.sold,
            }))
        );
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, 'Top Products');
        XLSX.writeFile(workbook, 'top_products.xlsx');
    };

    return (
        <div className="top-products">
            <div className="header">
                <h3>Sản phẩm bán chạy</h3>
                <div className="actions">
                    <select value={filter} onChange={handleFilterChange}>
                        <option value={5}>Top 5</option>
                        <option value={10}>Top 10</option>
                        <option value={20}>Top 20</option>
                        <option value={50}>Top 50</option>
                        <option value={100}>Top 100</option>
                    </select>
                    <span
                        className="material-symbols-outlined download-icon"
                        onClick={handleDownloadExcel}
                        title="Download Excel"
                    >
                        download
                    </span>
                </div>
            </div>
            <table>
                <thead>
                    <tr>
                        <th>Sản phẩm</th>
                        <th>Giá</th>
                        <th>Lượt bán</th>
                    </tr>
                </thead>
                <tbody>
                    {products?.slice(0, filter).map((p, i) => (
                        <tr key={i}>
                            <td>{p.name}</td>
                            <td>{p.price} VND</td>
                            <td>{p.sold}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}
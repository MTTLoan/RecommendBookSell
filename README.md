# RecommendBookSell

Nền tảng bán sách trực tuyến có tích hợp gợi ý sản phẩm dựa trên lịch sử đánh giá người dùng.

## Tổng quan kiến trúc

Dự án được chia thành 3 phần chính:

- **Frontend (React):** giao diện admin tại `client/web` và khách hàng tại `client/mobile`.
- **Backend API (Node.js + Express + MongoDB):** xử lý nghiệp vụ người dùng, sách, đơn hàng, giỏ hàng, báo cáo… tại `server/backend`.
- **AI Recommendation (Python):** mô hình gợi ý sách sử dụng `scikit-learn` (KNN + cosine similarity) tại `server/ai-backend`.

## Tính năng nổi bật

- Đăng ký/đăng nhập, xác thực JWT.
- Quản lý sách, danh mục, người dùng.
- Quản lý giỏ hàng, đơn hàng, đánh giá.
- Gửi OTP/email xác thực tài khoản.
- Upload ảnh lên AWS S3.
- Dashboard và báo cáo bán hàng.
- Gợi ý sách theo:
  - sách tương tự (book-based)
  - người dùng (user-based từ lịch sử rating)

## Cấu trúc thư mục

```text
RecommendBookSell/
├── client/
│   └── web/                 # React app
├── server/
│   ├── backend/             # Node.js/Express API
│   └── ai-backend/          # Python
```

## Yêu cầu môi trường

- **Node.js** >= 18
- **npm** >= 9
- **Python** >= 3.10
- **MongoDB** (local hoặc MongoDB Atlas)
- AWS credentials để upload ảnh S3

## Cài đặt & chạy dự án

### Bước 1: Clone và cài dependencies

```bash
git clone <repo-url>
cd RecommendBookSell
```

#### Frontend

```bash
cd client/web
npm install
```

#### Backend Node.js

```bash
cd ../../server/backend
npm install
```

#### AI Backend Python

```bash
cd ../ai-backend
python -m venv .venv
source .venv/bin/activate    # Windows: .venv\Scripts\activate
pip install -r requirements.txt
```

### Bước 2: Chạy từng service

#### Terminal A - Node.js API

```bash
cd server/backend
npm start
```

#### Terminal B - React app

```bash
cd client/web
npm start
```

## Các endpoint chính (Node API)

Base URL: `/api`

- `/auth`
- `/otp`
- `/verify_email`
- `/forgot_password`
- `/books`
- `/categories`
- `/notifications`
- `/orders`
- `/reviews`
- `/carts`
- `/user`
- `/dashboard`
- `/recommendations`
- `/reports`

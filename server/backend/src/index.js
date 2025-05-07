import dotenv from "dotenv";
import express from "express";
import cors from "cors";
import { connectDB } from "./config/db.js";
import authRoutes from "./routes/auth.js";
import otpRoutes from "./routes/otp.js";
import verifyEmailRoutes from "./routes/verifyEmail.js";
import { errorHandler } from "./middleware/errorHandler.js";

dotenv.config();
const app = express();

app.use(cors({
  origin: ['http://localhost:3000', 'http://127.0.0.1:3000'], // Cho phép cả localhost và 127.0.0.1
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization'],
  credentials: true,
}));

// app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

connectDB();

// login, register, logout routes
app.use("/api/auth", authRoutes);

// otp routes
app.use("/api/otp", otpRoutes);
app.use("/api/verify_email", verifyEmailRoutes);

app.use(errorHandler);

// const PORT = process.env.PORT || 5000;
// app.listen(PORT, () => console.log(`Server running on port ${PORT}`));

// tui dùng 5000/api/... á
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
  console.log(`API URL: http://localhost:${PORT}/api`);
});

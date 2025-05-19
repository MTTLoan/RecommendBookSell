import dotenv from "dotenv";
import express from "express";
import cors from "cors";
import { connectDB } from "./config/db.js";
import authRoutes from "./routes/auth.js";
import otpRoutes from "./routes/otp.js";
import orderRoutes from "./routes/orderRoutes.js";
import notificationRoutes from "./routes/notification.js";
import verifyEmailRoutes from "./routes/verifyEmail.js";
import { errorHandler } from "./middleware/errorHandler.js";
import forgotPasswordRoutes from "./routes/forgotPassword.js";
import bookRoutes from "./routes/book.js";

dotenv.config();
const app = express();

app.use(cors());
app.use(express.json());

connectDB();

// login, register, logout routes
app.use("/api/auth", authRoutes);

// otp routes
app.use("/api/otp", otpRoutes);
app.use("/api/verify_email", verifyEmailRoutes);

// forgotPassword routes
app.use("/api/forgot_password", forgotPasswordRoutes);

// book routes
app.use("/api/books", bookRoutes);


// Sử dụng routes
app.use("/api/orders", orderRoutes);

app.use(errorHandler);

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));

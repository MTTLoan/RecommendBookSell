import express from "express";
import dotenv from "dotenv";
import cors from "cors";
import connectDB from "./config/db.js";

dotenv.config();

const app = express();

// Middleware
app.use(cors()); // Cho phép frontend gọi API
app.use(express.json()); // Parse JSON body

// Kết nối MongoDB
connectDB();

// Route cơ bản để kiểm tra
app.get("/", (req, res) => {
  res.json({ message: "Welcome to the Ecommerce API" });
});

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));

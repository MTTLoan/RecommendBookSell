import express from "express";
import { registerController } from "../controllers/authController.js";
import { registerValidator } from "../middleware/userMiddleware.js";

const router = express.Router();

router.post("/register", registerValidator, registerController);

export default router;

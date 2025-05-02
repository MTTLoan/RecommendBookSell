import express from "express";
import {
  logoutController,
  registerController,
} from "../controllers/authController.js";
import { registerValidator } from "../middleware/userMiddleware.js";

const router = express.Router();

router.post("/register", registerValidator, registerController);
router.post("/logout", logoutController);

export default router;

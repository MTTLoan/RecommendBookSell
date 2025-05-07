import express from "express";
import {
  logoutController,
  loginController,
  registerController,
  forgotPasswordController,
  resetPasswordController,
} from "../controllers/authController.js";
import {
  registerValidator,
  loginValidator,
} from "../middleware/userMiddleware.js";
import { rateLimitForgotPassword } from "../middleware/rateLimiter.js";


const router = express.Router();

router.post("/register", registerValidator, registerController);
router.post("/login", loginValidator, loginController);
router.post("/logout", logoutController);
router.post("/forgot-password", rateLimitForgotPassword, forgotPasswordController);
router.post("/reset-password", resetPasswordController);

export default router;

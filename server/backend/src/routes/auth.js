import express from "express";
import {
  logoutController,
  loginController,
  registerController,
  loginWithGoogle,
} from "../controllers/authController.js";
import {
  registerValidator,
  loginValidator,
} from "../middleware/userMiddleware.js";

const router = express.Router();

router.post("/register", registerValidator, registerController);
router.post("/login", loginValidator, loginController);
router.post("/login/google", loginWithGoogle);
router.post("/logout", logoutController);

export default router;

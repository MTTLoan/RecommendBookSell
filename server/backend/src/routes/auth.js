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
import user_jwt from "../middleware/user_jwt.js";

const router = express.Router();

router.post("/register", registerValidator, registerController);
router.post("/login", loginValidator, loginController);

router.post("/googleauth", loginWithGoogle);
router.post("/logout", user_jwt, logoutController);

export default router;

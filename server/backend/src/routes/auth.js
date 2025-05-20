
import express from "express";
import {
  logoutController,
  loginController,
  registerController,
  googleAuthController,
  changePasswordController,
  getProfileController,
  updateProfileController
} from "../controllers/authController.js";
import {
  registerValidator,
  loginValidator,
  changePasswordValidator,
  updateProfileValidator
} from "../middleware/userMiddleware.js";
import user_jwt from "../middleware/user_jwt.js";

const router = express.Router();

router.post("/register", registerValidator, registerController);
router.post("/login", loginValidator, loginController);
router.post("/googleauth", googleAuthController);
router.post("/logout", user_jwt, logoutController);
router.post("/change-password", user_jwt, changePasswordValidator, changePasswordController);
router.get("/profile", user_jwt, getProfileController);
router.put('/update-profile', user_jwt, updateProfileValidator, updateProfileController);

export default router;

import express from "express";
import {
  logoutController,
  loginController,
  registerController,
  googleAuthController,
  changePasswordController,
  getProfileController
} from "../controllers/authController.js";
import {
  registerValidator,
  loginValidator,
  changePasswordValidator
} from "../middleware/userMiddleware.js";
import user_jwt from "../middleware/user_jwt.js";

const router = express.Router();

router.post("/register", registerValidator, registerController);
router.post("/login", loginValidator, loginController);
router.post("/googleauth", googleAuthController);
router.post("/logout", user_jwt, logoutController);
router.post("/change-password", changePasswordValidator, async (req, res) => {
  const { email, oldPassword, newPassword, confirmPassword } = req.body;

  if (newPassword !== confirmPassword) {
    return res.status(400).json({ message: "Mật khẩu mới và xác nhận mật khẩu không khớp." });
  }

  try {
    const result = await changePasswordController({ email, oldPassword, newPassword });
    res.status(200).json(result);
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
});

router.get("/profile", getProfileController);


export default router;
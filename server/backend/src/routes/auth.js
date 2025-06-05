import express from "express";
import {
  logoutController,
  loginController,
  registerController,
  googleAuthController,
  changePasswordController,
  getProfileController,
  updateProfileController,
  uploadAvatarController,
  updateProfileWithAvatarController,
} from "../controllers/authController.js";
import {
  registerValidator,
  loginValidator,
  changePasswordValidator,
  updateProfileValidator,
} from "../middleware/userMiddleware.js";
import user_jwt from "../middleware/user_jwt.js";
import uploadAvatar from "../middleware/uploadToS3.js";

const router = express.Router();

// Route đăng ký người dùng
router.post("/register", registerValidator, registerController);
// Route đăng nhập người dùng
router.post("/login", loginValidator, loginController);
// Route xác thực đăng nhập bằng Google
router.post("/googleauth", googleAuthController);
// Route đăng xuất người dùng
router.post("/logout", user_jwt, logoutController);
// Route thay đổi mật khẩu
router.post(
  "/change-password",
  user_jwt,
  changePasswordValidator,
  changePasswordController
);
// Route để lấy thông tin người dùng
router.get("/profile", user_jwt, getProfileController);
// Route để cập nhật thông tin người dùng
router.put(
  "/update-profile",
  user_jwt,
  updateProfileValidator,
  updateProfileController
);
// Route để tải lên ảnh đại diện
router.post("/upload-avatar", user_jwt, uploadAvatar, uploadAvatarController);
// Route để cập nhật thông tin người dùng với ảnh đại diện
router.put(
  "/update-profile-with-avt",
  user_jwt,
  uploadAvatar,
  updateProfileWithAvatarController
);

export default router;

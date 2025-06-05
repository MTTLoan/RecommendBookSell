import user_jwt from "../middleware/user_jwt.js";
import express from "express";
import {
  adminSearchUsersController,
  adminUpdateUserController,
  adminDeleteUserController,
  adminGetAllUsersController,
  adminGetUserDetailController,
  adminAddUserController,
  peekNextUserId,
} from "../controllers/userController.js";
import uploadAvatar from "../middleware/uploadToS3.js";

const router = express.Router();

// Route để lấy ID người dùng tiếp theo
router.get("/peek-next-id", peekNextUserId);
// Route để tim kiếm người dùng
router.get("/search", user_jwt, adminSearchUsersController);
// Route để xóa người dùng
router.delete("/:id", user_jwt, adminDeleteUserController);
// Route để lấy chi tiết người dùng
router.get("/:id", user_jwt, adminGetUserDetailController);
// Route để lấy tất cả người dùng
router.get("/", user_jwt, adminGetAllUsersController);
// Route để thêm người dùng mới và cập nhật người dùng
router.post("/", uploadAvatar, adminAddUserController);
// Route để cập nhật thông tin người dùng
router.put("/:id", uploadAvatar, adminUpdateUserController);

export default router;

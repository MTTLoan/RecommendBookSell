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

// lấy ID người dùng tiếp theo
router.get("/id", peekNextUserId);
// tìm kiếm người dùng theo tên
router.get("/search", user_jwt, adminSearchUsersController);
// lấy chi tiết người dùng theo ID
router.get("/:id", user_jwt, adminGetUserDetailController);
// lấy tất cả người dùng
router.get("/", user_jwt, adminGetAllUsersController);
// thêm người dùng mới (có upload ảnh)
router.post("/", user_jwt, uploadAvatar, adminAddUserController);
// sửa thông tin người dùng (có upload ảnh)
router.put("/:id", user_jwt, uploadAvatar, adminUpdateUserController);
// xóa người dùng theo ID
router.delete("/:id", user_jwt, adminDeleteUserController);

export default router;

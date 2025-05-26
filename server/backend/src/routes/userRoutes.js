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

router.get("/peek-next-id", peekNextUserId);
router.get("/search", user_jwt, adminSearchUsersController);
router.delete("/:id", user_jwt, adminDeleteUserController);
router.get("/:id", user_jwt, adminGetUserDetailController);
router.get("/", user_jwt, adminGetAllUsersController);
router.post("/", uploadAvatar, adminAddUserController);
router.put("/:id", uploadAvatar, adminUpdateUserController);

export default router;

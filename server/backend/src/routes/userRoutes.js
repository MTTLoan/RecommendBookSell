
import express from "express";
import {
  adminUpdateUserController, 
  adminDeleteUserController,
  adminGetAllUsersController
} from "../controllers/userController.js";

const router = express.Router();

router.put("/admin/user/:id", user_jwt, adminUpdateUserController);
router.delete("/admin/user/:id", user_jwt, adminDeleteUserController);
// router.get("/admin/users", user_jwt, adminGetAllUsersController);
router.get("/admin/users", adminGetAllUsersController);

export default router;
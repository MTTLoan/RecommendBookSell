import user_jwt from "../middleware/user_jwt.js";
import express from "express";
import {
  adminSearchUsersController,
  adminUpdateUserController, 
  adminDeleteUserController,
  adminGetAllUsersController
} from "../controllers/userController.js";

const router = express.Router();

router.get('/search', user_jwt, adminSearchUsersController);
router.put("/:id", user_jwt, adminUpdateUserController);
router.delete("/:id", user_jwt, adminDeleteUserController);
router.get("/", user_jwt, adminGetAllUsersController);

export default router;
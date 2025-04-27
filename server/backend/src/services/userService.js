import mongoose from "mongoose";
import User from "../models/User.js";

export const userService = {
  users: mongoose.model("User", User.schema),
};

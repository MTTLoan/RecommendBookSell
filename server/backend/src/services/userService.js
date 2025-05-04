import mongoose from "mongoose";
import User from "../models/User.js";

export const userService = {
  User: mongoose.model("User", User.schema),
};

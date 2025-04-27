import User from "../models/User.js";
import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";
import { userService } from "../services/userService.js";

export const registerController = async (req, res) => {
  const { username, fullName, email, phoneNumber, password } = req.body;
  try {
    let user = await userService.users.findOne({ username });
    if (user)
      return res.status(400).json({ message: "Tên tài khoản đã tồn tại" });

    user = new User({
      username,
      fullName,
      email,
      phoneNumber,
      password: await bcrypt.hash(password, 10),
      role: "user",
    });
    await user.save();

    const token = jwt.sign(
      { id: user._id, role: user.role },
      process.env.JWT_SECRET,
      { expiresIn: "1h" }
    );

    res.status(201).json({
      token,
      user: {
        id: user._id,
        username: user.username,
        fullName: user.fullName,
        email: user.email,
        phoneNumber: user.phoneNumber,
        role: user.role,
      },
    });
  } catch (error) {
    throw new Error("Đăng ký thất bại");
  }
};

export const logoutController = async (req, res) => {
  try {
    // Vì dùng JWT, không cần xóa token ở server
    // Client sẽ tự xóa token ở phía client
    res.status(200).json({ message: "Đăng xuất thành công" });
  } catch (error) {
    throw new Error("Đăng xuất thất bại");
  }
};

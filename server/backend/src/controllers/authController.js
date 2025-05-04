import User from "../models/User.js";
import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";
import { userService } from "../services/userService.js";
import { sendVerificationOTPEmail } from "./email_verificationController.js";

export const registerController = async (req, res) => {
  const { username, fullName, email, phoneNumber, password } = req.body;
  try {
    let user = await userService.User.findOne({ username });
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

    // gửi otp xác nhận tài khoản
    await sendVerificationOTPEmail(email);

    const token = jwt.sign(
      { id: user._id, role: user.role },
      process.env.JWT_SECRET,
      { expiresIn: "1h" }
    );

    res.status(201).json({
      token,
      user: user,
    });
  } catch (error) {
    // throw new Error("Đăng ký thất bại");
    throw new Error("Đăng ký thất bại: " + error.message);
  }
};

export const loginController = async (req, res) => {
  let { email, password } = req.body;

  password = password.trim();

  try {
    let user = await User.findOne({ email: email });

    if (!user) {
      if (!user) {
        return res.status(400).json({
          success: false,
          error_server: false,
          msg: "Username or Email not exists!",
        });
      }
    }

    // check verify account
    if (!user.verified) {
      return res.status(400).json({
        success: false,
        error_server: false,
        verified: false,
        email: user.email,
        msg: "Email hasn't been verified yet. Check your inbox!",
      });
    }

    const isMatch = await bcrypt.compare(password, user.password);

    if (!isMatch) {
      return res.status(400).json({
        success: false,
        error_server: false,
        msg: "Password is invalid!",
      });
    }

    // lưu token đăng nhập thành công
    const payload = {
      user: {
        id: user.id,
        role: user.role,
      },
    };

    await user.save();

    // tạo token
    const token = jwt.sign(payload, process.env.JWT_SECRET, {
      expiresIn: "1h",
    });

    return res.status(200).json({
      success: true,
      msg: "User logged in!",
      user_id: user.id,
      token: token,
      user: user,
    });
  } catch (err) {
    console.log(err.message);

    return res.status(500).json({
      success: false,
      error_server: true,
      msg: "Server Error!",
    });
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

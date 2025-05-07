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

// Google login controller
import { OAuth2Client } from "google-auth-library";
const client = new OAuth2Client(process.env.GOOGLE_CLIENT_ID);
const JWT_SECRET = process.env.JWT_SECRET;

export const loginWithGoogle = async (req, res) => {
  const { email, idToken } = req.body;
  console.log("Received request:", { email, idToken });

  try {
    const ticket = await client.verifyIdToken({
      idToken,
      audience: process.env.GOOGLE_CLIENT_ID,
    });
    const payload = ticket.getPayload();
    console.log("Token payload:", payload);

    if (payload.email !== email) {
      console.log("Email mismatch:", {
        payloadEmail: payload.email,
        requestEmail: email,
      });
      return res.status(401).json({ message: "Email không trùng khớp" });
    }

    let user = await User.findOne({ email });
    if (!user) {
      user = new User({
        email,
        fullName: payload.name,
        avatar: payload.picture,
        googleId: payload.sub,
      });
      await user.save();
      console.log("Created new user:", user);
    } else {
      console.log("Found existing user:", user);
    }

    const token = jwt.sign({ id: user._id, email: user.email }, JWT_SECRET, {
      expiresIn: "7d",
    });

    res.json({
      user: {
        id: user._id,
        fullName: user.fullName,
        email: user.email,
        token,
      },
    });
  } catch (err) {
    console.error("Google login error:", err);
    res.status(401).json({ message: "Xác thực Google thất bại" });
  }
};

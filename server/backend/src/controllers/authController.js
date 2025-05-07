import User from "../models/User.js";
import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";
import crypto from "crypto";
import nodemailer from "nodemailer";
import PasswordReset from "../models/PasswordReset.js";


export const registerController = async (req, res) => {
  const { username, fullName, email, phoneNumber, password } = req.body;
  try {
    let user = await User.findOne({ username });
    if (user) return res.status(400).json({ message: "Tên tài khoản đã tồn tại" });

    user = new User({
      username,
      fullName,
      email,
      phoneNumber,
      password: await bcrypt.hash(password, 10),
      role: "user",
    });
    await user.save();

    const token = jwt.sign({ id: user._id, role: user.role }, process.env.JWT_SECRET, {
      expiresIn: "1h",
    });

    res.status(201).json({
      token,
      user: user,
    });
  } catch (error) {
    res.status(500).json({ message: "Đăng ký thất bại: " + error.message });
  }
};

export const loginController = async (req, res) => {
  const { username, password } = req.body;
  console.log('Login attempt:', { username }); // Log thông tin đăng nhập

  try {
    // Tìm user theo username
    const user = await User.findOne({
      $or: [{ username: username }],
    });

    console.log('User found:', user ? 'Yes' : 'No'); // Log kết quả tìm kiếm

    if (!user) {
      return res.status(400).json({ message: "Tên đăng nhập không tồn tại." });
    }

    // Kiểm tra mật khẩu
    const isMatch = await bcrypt.compare(password, user.password);
    console.log('Password match:', isMatch ? 'Yes' : 'No'); // Log kết quả so sánh mật khẩu

    if (!isMatch) {
      return res.status(400).json({ message: "Mật khẩu không chính xác." });
    }

    // Tạo token
    const token = jwt.sign({ id: user._id, role: user.role }, process.env.JWT_SECRET, {
      expiresIn: "1h",
    });

    // Trả về thông tin user (không bao gồm mật khẩu)
    const userResponse = {
      _id: user._id,
      username: user.username,
      email: user.email,
      fullName: user.fullName,
      role: user.role,
    };

    res.status(200).json({
      token,
      user: userResponse,
    });
  } catch (error) {
    console.error('Login error:', error); // Log lỗi nếu có
    res.status(500).json({ message: "Lỗi máy chủ: " + error.message });
  }
};

export const forgotPasswordController = async (req, res) => {
  console.log("Request body:", req.body);
  const { email } = req.body;

  try {
    console.log("Received email from frontend:", email);
    const user = await User.findOne({ email });
    console.log("User found:", user);
    if (!user) {
      return res.status(404).json({ message: "Email không tồn tại." });
    }

    // Tạo token và lưu vào bảng PasswordResets
    const resetToken = crypto.randomBytes(32).toString("hex");
    const expiresAt = Date.now() + 3600000; // Token hết hạn sau 1 giờ

    const passwordReset = new PasswordReset({
      userId: user._id,
      token: resetToken,
      expiresAt,
    });
    await passwordReset.save();

    // Gửi email
    const transporter = nodemailer.createTransport({
      service: "Gmail",
      auth: {
        user: process.env.AUTH_EMAIL,
        pass: process.env.AUTH_PASS,
      },
    });

    const resetUrl = `${process.env.CLIENT_URL}/auth/reset-password?token=${resetToken}`;

    await transporter.sendMail({
      to: email,
      subject: "Khôi phục mật khẩu",
      html: `<p>Nhấn vào liên kết sau để khôi phục mật khẩu:</p><a href="${resetUrl}">${resetUrl}</a>`,
    });

    res.json({ message: "Email khôi phục mật khẩu đã được gửi." });
  } catch (error) {
    console.error("Forgot password error:", error); 
    res.status(500).json({ message: "Lỗi máy chủ: " + error.message });
  }
};

export const resetPasswordController = async (req, res) => {
    const { token, password } = req.body;
  
    try {
      const passwordReset = await PasswordReset.findOne({
        token,
        expiresAt: { $gt: Date.now() }, // Token chưa hết hạn
      });
  
      if (!passwordReset) {
        return res.status(400).json({ message: "Token không hợp lệ hoặc đã hết hạn." });
      }
  
      const user = await User.findById(passwordReset.userId);
      if (!user) {
        return res.status(404).json({ message: "Người dùng không tồn tại." });
      }
  
      // Cập nhật mật khẩu
      user.password = await bcrypt.hash(password, 10);
      await user.save();
  
      // Xóa yêu cầu khôi phục mật khẩu sau khi sử dụng
      await PasswordReset.deleteOne({ _id: passwordReset._id });
  
      res.json({ message: "Mật khẩu đã được cập nhật." });
    } catch (error) {
      res.status(500).json({ message: "Lỗi máy chủ: " + error.message });
    }
  };

  export const logoutController = async (req, res) => {
    try {
      // Nếu bạn sử dụng token JWT, không cần làm gì trên server
      // Chỉ cần client xóa token khỏi localStorage hoặc cookie
      res.status(200).json({ message: "Đăng xuất thành công." });
    } catch (error) {
      res.status(500).json({ message: "Lỗi máy chủ: " + error.message });
    }
  };


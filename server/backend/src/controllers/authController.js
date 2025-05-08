import User from "../models/User.js";
import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";
import { userService } from "../services/userService.js";
import { sendVerificationOTPEmail } from "./email_verificationController.js";

export const registerController = async (req, res) => {
  const { username, fullName, email, phoneNumber, password } = req.body;
  try {
    // Kiểm tra xem username đã tồn tại chưa
    let user = await User.findOne({ username });
    if (user) {
      return res.status(400).json({ message: "Tên tài khoản đã tồn tại" });
    }

    // Tạo người dùng mới
    user = new User({
      username,
      fullName,
      email,
      phoneNumber,
      password: await bcrypt.hash(password, 10),
      role: "user",
    });
    await user.save();

    // Gửi OTP xác nhận tài khoản
    await sendVerificationOTPEmail(email);

    // Tạo token
    const token = jwt.sign(
      { id: user._id, role: user.role },
      process.env.JWT_SECRET,
      { expiresIn: "1h" }
    );

    // Lưu token vào trường `token` trong cơ sở dữ liệu
    user.token = token;
    await user.save();

    // Trả về phản hồi
    res.status(201).json({
      success: true,
      msg: "Đăng ký thành công!",
      token,
      user,
    });
  } catch (error) {
    console.error("Lỗi đăng ký:", error.message);
    res.status(500).json({
      success: false,
      msg: "Đăng ký thất bại! Lỗi server.",
    });
  }
};

export const loginController = async (req, res) => {
  let { identifier, password } = req.body; // `identifier` có thể là username hoặc email
  password = password.trim();

  try {
    // Tìm người dùng bằng username hoặc email
    let user = await User.findOne({
      $or: [{ username: identifier }, { email: identifier }],
    });

    if (!user) {
      return res.status(400).json({
        success: false,
        error_server: false,
        msg: "Tên tài khoản hoặc email không tồn tại!",
      });
    }

    // Kiểm tra xem tài khoản đã được xác thực chưa
    if (!user.verified) {
      return res.status(400).json({
        success: false,
        error_server: false,
        verified: false,
        email: user.email,
        msg: "Email chưa được xác thực. Vui lòng kiểm tra hộp thư của bạn!",
      });
    }

    // Kiểm tra mật khẩu
    const isMatch = await bcrypt.compare(password, user.password);

    if (!isMatch) {
      return res.status(400).json({
        success: false,
        error_server: false,
        msg: "Mật khẩu không chính xác!",
      });
    }

    // Tạo payload cho JWT
    const payload = {
      user: {
        id: user.id,
        role: user.role,
      },
    };

    // Tạo token
    const token = jwt.sign(payload, process.env.JWT_SECRET, {
      expiresIn: "1h",
    });

    // Lưu token vào trường `token` trong cơ sở dữ liệu
    user.token = token;
    await user.save();

    return res.status(200).json({
      success: true,
      msg: "Đăng nhập thành công!",
      user_id: user.id,
      token: token,
      user: user,
    });
  } catch (err) {
    console.log(err.message);

    return res.status(500).json({
      success: false,
      error_server: true,
      msg: "Lỗi máy chủ!",
    });
  }
};

export const logoutController = async (req, res) => {
  try {
    const user = await User.findById(req.user.id);

    if (!user) {
      return res.status(404).json({
        success: false,
        msg: "Người dùng không tồn tại hoặc đã đăng xuất.",
      });
    }

    res.status(200).json({ message: "Đăng xuất thành công!" });
  } catch (error) {
    console.error(error.message);
    res.status(500).json({
      success: false,
      msg: "Đăng xuất thất bại! Lỗi server.",
    });
  }
};

export const googleAuth = async (req, res) => {
  let { googleId, email, fullName, photoUrl } = req.body;

  googleId = googleId.trim();
  email = email.trim();
  fullName = fullName.trim();
  photoUrl = photoUrl.trim();

  try {
    let isNewAccount = false;
    // Tìm người dùng bằng googleId hoặc email
    let user = await User.findOne({ $or: [{ googleId }, { email }] });

    if (!user) {
      // Kiểm tra email trùng lặp
      const emailExists = await User.findOne({ email });
      if (emailExists) {
        return res.status(400).json({
          success: false,
          msg: "Email đã được sử dụng bởi tài khoản khác!",
        });
      }

      // Tạo người dùng mới
      user = new User({
        googleId,
        email,
        fullName,
        photoUrl,
        provider: "google",
        verified: true,
        role: "user",
      });
      isNewAccount = true;
      await user.save();
    }

    // Tạo payload cho JWT
    const payload = {
      user: {
        id: user.id,
        role: user.role,
      },
    };

    // Tạo JWT token
    const token = jwt.sign(payload, process.env.JWT_SECRET, {
      expiresIn: "1h",
    });

    // Lưu token vào user
    user.token = token;
    await user.save();

    return res.status(200).json({
      success: true,
      msg: "Đăng nhập Google thành công!",
      isNewAccount,
      token,
      user_id: user.id,
      user,
    });
  } catch (err) {
    console.error("Lỗi đăng nhập Google:", err.message);
    return res.status(500).json({
      success: false,
      msg: "Lỗi máy chủ khi đăng nhập Google!",

    });
  }
};


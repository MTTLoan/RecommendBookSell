import User from "../models/User.js";
import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";
import { sendVerificationOTPEmail } from "./email_verificationController.js";
import { hashData, verifyHashedData } from "../util/hashData.js";
import { deleteS3File } from "../middleware/uploadToS3.js";



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
      verified: false,
      addressProvince: null,
      addressDistrict: null,
      addressWard: null,
      addressDetail: null,
      birthday: null,
      avatar: null,
      token: null,
    });
    await user.save();

    // Gửi OTP xác nhận tài khoản
    await sendVerificationOTPEmail(email);

    // Tạo token
    const token = jwt.sign(
      { id: user.id, role: user.role },
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
    console.log('Password match:', isMatch ? 'Yes' : 'No'); // Log kết quả so sánh mật khẩu

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
    const user = await User.findOne({ id: req.user.id });

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

export const googleAuthController = async (req, res) => {
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
      // Generate a unique username
      let username;
      let usernameExists;
      let attempts = 0;
      do {
        username = email.split("@")[0] + Math.random().toString(36).slice(-4);
        usernameExists = await User.findOne({ username });
        attempts++;
        if (attempts > 5) {
          throw new Error("Không thể tạo tên người dùng duy nhất");
        }
      } while (usernameExists);

      // Log user creation data
      const userData = {
        googleId,
        email,
        fullName,
        photoUrl,
        username,
        phoneNumber: "0000000000", // Temporary placeholder
        password: Math.random().toString(36).slice(-8), // Temporary password
        verified: true,
        role: "user",
      };
      console.log("Tạo người dùng mới:", userData);

      // Create a new user
      user = new User(userData);

      await user.save();
      isNewAccount = true;
    } else if (user.googleId && user.googleId !== googleId) {
      return res.status(400).json({
        success: false,
        msg: "Email đã được liên kết với một tài khoản Google khác!",
      });
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
      msg: isNewAccount
        ? "Tạo tài khoản Google thành công!"
        : "Đăng nhập Google thành công!",
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

export const changePasswordController = async (req, res) => {
  try {
    const { oldPassword, newPassword, confirmPassword } = req.body;

    // Kiểm tra mật khẩu xác nhận
    if (newPassword !== confirmPassword) {
      return res.status(400).json({
        success: false,
        msg: "Mật khẩu xác nhận không khớp.",
      });
    }

    // Tìm người dùng
    const user = await User.findOne({ id: req.user.id });
    if (!user) {
      return res.status(404).json({
        success: false,
        msg: "Không tìm thấy tài khoản.",
      });
    }

    // Kiểm tra mật khẩu cũ
    const isValid = await verifyHashedData(oldPassword, user.password);
    if (!isValid) {
      return res.status(400).json({
        success: false,
        msg: "Mật khẩu cũ không đúng.",
      });
    }

    // Cập nhật mật khẩu mới
    const hashedPassword = await hashData(newPassword);
    user.password = hashedPassword;
    await user.save();

    return res.status(200).json({
      success: true,
      msg: "Mật khẩu đã được thay đổi thành công.",
    });
  } catch (error) {
    console.error("Lỗi thay đổi mật khẩu:", error.message);
    return res.status(500).json({
      success: false,
      msg: "Lỗi server khi thay đổi mật khẩu.",
    });
  }
};

// Lấy thông tin hồ sơ người dùng
export const getProfileController = async (req, res) => {
  try {
    const user = await User.findOne({ id: req.user.id });

    if (!user) {
      return res.status(404).json({ message: "Không tìm thấy tài khoản." });
    }

    return res.status(200).json({
      message: "Lấy thông tin hồ sơ thành công.",
      user,
    });
  } catch (error) {
    return res.status(500).json({ message: error.message || "Lỗi server." });
  }
};

export const updateProfileController = async (req, res) => {
  try {
    const {
      fullName,
      phoneNumber,
      addressProvince,
      addressDistrict,
      addressWard,
      addressDetail,
      birthday,
      gender,
    } = req.body;

    // Tìm người dùng
    const user = await User.findOne({ id: req.user.id });
    if (!user) {
      return res.status(404).json({
        success: false,
        msg: "Không tìm thấy tài khoản.",
      });
    }

    // Cập nhật các trường
    if (fullName !== undefined) user.fullName = fullName;
    if (phoneNumber !== undefined) user.phoneNumber = phoneNumber;
    if (addressProvince !== undefined) user.addressProvince = addressProvince;
    if (addressDistrict !== undefined) user.addressDistrict = addressDistrict;
    if (addressWard !== undefined) user.addressWard = addressWard;
    if (addressDetail !== undefined) user.addressDetail = addressDetail;
    if (birthday !== undefined)
      user.birthday = birthday ? new Date(birthday) : null;
    if (gender !== undefined) user.gender = gender;

    user.updatedAt = new Date();

    // Lưu người dùng
    await user.save();

    return res.status(200).json({
      success: true,
      msg: "Cập nhật hồ sơ thành công!",
      user,
    });
  } catch (error) {
    console.error("Lỗi cập nhật hồ sơ:", error.message);
    return res.status(500).json({
      success: false,
      msg: "Cập nhật hồ sơ thất bại! Lỗi server.",
    });
  }
};

export const uploadAvatarController = async (req, res) => {
  try {
    if (!req.file || !req.file.location) {
      console.log("No file or location found in req.file");
      return res.status(400).json({ success: false, msg: "Không có file avatar được gửi lên." });
    }

    const user = await User.findOne({ id: req.user.id });
    if (!user) {
      return res.status(404).json({ success: false, msg: "Không tìm thấy tài khoản." });
    }

    // Nếu user đã có avatar cũ, xóa khỏi S3
    if (user.avatar) {
      await deleteS3File(user.avatar);
    }

    // Lưu avatar mới
    user.avatar = req.file.location;
    user.updatedAt = new Date();
    await user.save();

    return res.status(200).json({ success: true, msg: "Cập nhật avatar thành công!", avatar: user.avatar });
  } catch (error) {
    console.error("Lỗi cập nhật avatar:", error.message, error.stack);
    return res.status(500).json({ success: false, msg: "Cập nhật avatar thất bại! Lỗi server." });
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


  export const updateProfileWithAvatarController = async (req, res) => {
  try {
    // Multer middleware đã upload file avatar lên S3, req.file.location có URL ảnh mới
    const {
      username,
      fullName,
      birthday,
    } = req.body;

    const user = await User.findOne({ id: req.user.id });
    if (!user) {
      return res.status(404).json({ success: false, msg: "Không tìm thấy tài khoản." });
    }

    // Xóa avatar cũ nếu có và có file mới
    if (req.file && user.avatar) {
      await deleteS3File(user.avatar);
      user.avatar = req.file.location; // url ảnh mới
    }

    if (username !== undefined) user.username = username;
    if (fullName !== undefined) user.fullName = fullName;
    if (birthday !== undefined) user.birthday = birthday ? new Date(birthday) : null;

    user.updatedAt = new Date();

    await user.save();

    return res.status(200).json({
      success: true,
      msg: "Cập nhật hồ sơ thành công!",
      user,
    });
  } catch (error) {
    console.error("Lỗi cập nhật hồ sơ:", error.message);
    return res.status(500).json({
      success: false,
      msg: "Cập nhật hồ sơ thất bại! Lỗi server.",
    });
  }
};

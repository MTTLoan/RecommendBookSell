import User from "../models/User.js";
import Order from "../models/Order.js";
import bcrypt from "bcryptjs";
import { deleteS3File } from "../middleware/uploadToS3.js";

// Lấy mã user tiếp theo
export const peekNextUserId = async (req, res) => {
  try {
    const lastUser = await User.findOne({}).sort({ id: -1 });
    const nextId = lastUser ? lastUser.id + 1 : 1;
    return res.json({ id: nextId });
  } catch (error) {
    console.error("Lỗi lấy mã user tiếp theo:", error.message);
    res
      .status(500)
      .json({ success: false, msg: "Lỗi server khi lấy mã user tiếp theo." });
  }
};

// Lấy thông tin người dùng hiện tại
export const adminSearchUsersController = async (req, res) => {
  try {
    if (req.user.role !== "admin") {
      return res.status(403).json({
        success: false,
        msg: "Bạn không có quyền thực hiện thao tác này.",
      });
    }
    const { q } = req.query;
    if (!q) {
      return res
        .status(400)
        .json({ success: false, msg: "Thiếu từ khóa tìm kiếm." });
    }
    const users = await User.find({
      role: "user",
      $or: [
        { username: { $regex: q, $options: "i" } },
        { fullName: { $regex: q, $options: "i" } },
      ],
    }).select("-password");
    return res.status(200).json({ success: true, users });
  } catch (error) {
    console.error("Lỗi tìm kiếm user:", error.message);
    res
      .status(500)
      .json({ success: false, msg: "Lỗi server khi tìm kiếm user." });
  }
};

// Lấy danh sách tất cả người dùng (chỉ cho admin)
export const adminGetAllUsersController = async (req, res) => {
  try {
    if (req.user.role !== "admin") {
      return res.status(403).json({
        success: false,
        msg: "Bạn không có quyền thực hiện thao tác này.",
      });
    }
    const users = await User.find({ role: "user" }).select("-password");

    // Lấy danh sách userId
    const userIds = users.map((u) => u.id);

    // Lấy tổng doanh thu và số đơn hàng cho từng user
    const ordersAgg = await Order.aggregate([
      { $match: { userId: { $in: userIds } } },
      {
        $group: {
          _id: "$userId",
          revenue: { $sum: "$totalAmount" },
          orderCount: { $sum: 1 },
        },
      },
    ]);

    const orderMap = {};
    ordersAgg.forEach((o) => {
      orderMap[o._id] = { revenue: o.revenue, orderCount: o.orderCount };
    });

    const usersWithStats = users.map((u) => ({
      ...u.toObject(),
      revenue: orderMap[u.id]?.revenue || 0,
      orderCount: orderMap[u.id]?.orderCount || 0,
    }));

    return res.status(200).json({ success: true, users: usersWithStats });
  } catch (error) {
    console.error("Lỗi lấy danh sách user:", error.message);
    res
      .status(500)
      .json({ success: false, msg: "Lỗi server khi lấy danh sách user." });
  }
};

// Thêm người dùng mới (chỉ cho admin)
export const adminAddUserController = async (req, res) => {
  try {
    const {
      username,
      fullName,
      email,
      phoneNumber,
      password,
      birthday,
      addressProvince,
      addressDistrict,
      addressWard,
      addressDetail,
      role,
      verified,
    } = req.body;

    if (!username || !fullName || !email || !phoneNumber) {
      return res.status(400).json({
        success: false,
        msg: "Tên đăng nhập, họ tên, email và số điện thoại là bắt buộc.",
      });
    }

    if (!password || password.length < 6) {
      return res.status(400).json({
        success: false,
        msg: "Mật khẩu là bắt buộc và phải từ 6 ký tự trở lên.",
      });
    }

    const existingUser = await User.findOne({
      $or: [{ username }, { email }],
    });

    if (existingUser) {
      return res.status(400).json({
        success: false,
        msg:
          existingUser.username === username
            ? "Tên đăng nhập đã tồn tại."
            : "Email đã tồn tại.",
      });
    }

    let avatarUrl = null;
    if (req.file && req.file.location) {
      avatarUrl = req.file.location; // S3 URL
      console.log("Avatar uploaded to:", avatarUrl);
    } else {
      console.log("No avatar file uploaded");
    }

    const hashedPassword = await bcrypt.hash(password, 10);

    const userData = {
      username: username.trim(),
      fullName: fullName.trim(),
      email: email.trim().toLowerCase(),
      phoneNumber: phoneNumber.trim(),
      password: hashedPassword,
      role: role || "user",
      verified: verified === "true" || verified === true || false,
    };

    if (birthday && birthday.trim()) {
      userData.birthday = new Date(birthday);
    }

    if (addressProvince && addressProvince.trim()) {
      userData.addressProvince = parseInt(addressProvince);
    }

    if (addressDistrict && addressDistrict.trim()) {
      userData.addressDistrict = parseInt(addressDistrict);
    }

    if (addressWard && addressWard.trim()) {
      userData.addressWard = parseInt(addressWard);
    }

    if (addressDetail && addressDetail.trim()) {
      userData.addressDetail = addressDetail.trim();
    }

    if (avatarUrl) {
      userData.avatar = avatarUrl;
    }

    console.log("Creating user with data:", {
      ...userData,
      password: "[HIDDEN]",
    });

    try {
      const Counter = mongoose.model("Counter");
      const counter = await Counter.findOneAndUpdate(
        { _id: "userId" },
        { $inc: { seq: 1 } },
        { new: true, upsert: true }
      );
      userData.id = counter.seq;
      console.log("Generated ID:", userData.id);
    } catch (counterError) {
      console.error("Counter error, using fallback:", counterError);
      const lastUser = await User.findOne({}).sort({ id: -1 });
      userData.id = lastUser ? lastUser.id + 1 : 1;
      console.log("Fallback ID:", userData.id);
    }

    const user = new User(userData);
    await user.save();

    const userResponse = user.toObject();
    delete userResponse.password;

    console.log("User created successfully:", userResponse);

    res.status(201).json({
      success: true,
      user: userResponse,
      msg: "Thêm người dùng thành công!",
    });
  } catch (error) {
    console.error("Error in adminAddUserController:", error);
    console.error("Error stack:", error.stack);

    if (error.code === 11000) {
      const field = Object.keys(error.keyPattern)[0];
      return res.status(400).json({
        success: false,
        msg: `${
          field === "username"
            ? "Tên đăng nhập"
            : field === "email"
            ? "Email"
            : "Trường"
        } đã tồn tại.`,
      });
    }

    if (error.name === "ValidationError") {
      const messages = Object.values(error.errors).map((err) => err.message);
      return res.status(400).json({
        success: false,
        msg: `Lỗi validation: ${messages.join(", ")}`,
      });
    }

    res.status(500).json({
      success: false,
      msg: "Lỗi server khi thêm người dùng.",
      error: process.env.NODE_ENV === "development" ? error.message : undefined,
    });
  }
};

export const adminUpdateUserController = async (req, res) => {
  try {
    const { fullName, ...otherFields } = req.body;
    let updateData = { fullName, ...otherFields };
    let user = await User.findOne({ id: req.params.id });
    if (!user) {
      return res
        .status(404)
        .json({ success: false, msg: "Không tìm thấy user." });
    }
    if (req.file && req.file.location) {
      if (user.avatar) {
        await deleteS3File(user.avatar);
      }
      updateData.avatar = req.file.location;
    }
    user = await User.findOneAndUpdate({ id: req.params.id }, updateData, {
      new: true,
    });
    res.status(200).json({ success: true, user });
  } catch (error) {
    res.status(500).json({ success: false, msg: "Lỗi server" });
  }
};

// Xóa người dùng (chỉ cho admin)
export const adminDeleteUserController = async (req, res) => {
  try {
    if (req.user.role !== "admin") {
      return res.status(403).json({
        success: false,
        msg: "Bạn không có quyền thực hiện thao tác này.",
      });
    }
    const userId = parseInt(req.params.id);

    const user = await User.findOneAndDelete({ id: userId, role: "user" });
    if (!user) {
      return res
        .status(404)
        .json({ success: false, msg: "Không tìm thấy user để xóa." });
    }

    return res
      .status(200)
      .json({ success: true, msg: "Xóa user thành công!", user });
  } catch (error) {
    console.error("Lỗi xóa user:", error.message);
    res
      .status(500)
      .json({ success: false, msg: "Xóa user thất bại! Lỗi server." });
  }
};

// Lấy chi tiết người dùng (chỉ cho admin)
export const adminGetUserDetailController = async (req, res) => {
  try {
    if (req.user.role !== "admin") {
      return res.status(403).json({
        success: false,
        msg: "Bạn không có quyền thực hiện thao tác này.",
      });
    }
    const userId = parseInt(req.params.id);
    const user = await User.findOne({ id: userId, role: "user" }).select(
      "-password"
    );
    if (!user) {
      return res
        .status(404)
        .json({ success: false, msg: "Không tìm thấy user." });
    }
    return res.status(200).json({ success: true, user });
  } catch (error) {
    console.error("Lỗi lấy chi tiết user:", error.message);
    res
      .status(500)
      .json({ success: false, msg: "Lỗi server khi lấy chi tiết user." });
  }
};

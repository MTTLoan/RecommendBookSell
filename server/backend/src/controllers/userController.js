import User from "../models/User.js";
import Order from "../models/Order.js";

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

export const adminAddUserController = async (req, res) => {
  try {
    const { username, fullName, ...otherFields } = req.body;
    let avatarUrl = null;
    if (req.file && req.file.location) {
      avatarUrl = req.file.location; // Link ảnh trên S3
    }
    const user = new User({
      username,
      fullName,
      ...otherFields,
      avatar: avatarUrl,
    });
    await user.save();
    res.status(201).json({ success: true, user });
  } catch (error) {
    res.status(500).json({ success: false, msg: "Lỗi server" });
  }
};

export const adminUpdateUserController = async (req, res) => {
  try {
    const { fullName, ...otherFields } = req.body;
    let updateData = { fullName, ...otherFields };
    if (req.file && req.file.location) {
      updateData.avatar = req.file.location;
    }
    const user = await User.findOneAndUpdate(
      { id: req.params.id },
      updateData,
      { new: true }
    );
    res.status(200).json({ success: true, user });
  } catch (error) {
    res.status(500).json({ success: false, msg: "Lỗi server" });
  }
};

// Xóa user (chỉ cho admin)
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

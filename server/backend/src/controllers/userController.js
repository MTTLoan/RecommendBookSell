import User from "../models/User.js";

export const adminSearchUsersController = async (req, res) => {
  try {
    if (req.user.role !== "admin") {
      return res.status(403).json({ success: false, msg: "Bạn không có quyền thực hiện thao tác này." });
    }
    const { q } = req.query;
    if (!q) {
      return res.status(400).json({ success: false, msg: "Thiếu từ khóa tìm kiếm." });
    }
    const users = await User.find({
      role: "user",
      $or: [
        { username: { $regex: q, $options: "i" } },
        { fullName: { $regex: q, $options: "i" } }
      ]
    }).select("-password");
    return res.status(200).json({ success: true, users });
  } catch (error) {
    console.error("Lỗi tìm kiếm user:", error.message);
    res.status(500).json({ success: false, msg: "Lỗi server khi tìm kiếm user." });
  }
};

export const adminGetAllUsersController = async (req, res) => {
  try {
    if (req.user.role !== "admin") {
      return res.status(403).json({ success: false, msg: "Bạn không có quyền thực hiện thao tác này." });
    }
    const users = await User.find({ role: "user" }).select("-password");
    return res.status(200).json({ success: true, users });
  } catch (error) {
    console.error("Lỗi lấy danh sách user:", error.message);
    res.status(500).json({ success: false, msg: "Lỗi server khi lấy danh sách user." });
  }
};

// Sửa thông tin user (chỉ cho admin)
export const adminUpdateUserController = async (req, res) => {
  try {
    // Kiểm tra quyền admin
    if (req.user.role !== "admin") {
      return res.status(403).json({ success: false, msg: "Bạn không có quyền thực hiện thao tác này." });
    }
    const userId = parseInt(req.params.id);
    const { fullName, phoneNumber, email, addressProvince, addressDistrict, addressWard, addressDetail, birthday, avatar } = req.body;

    const user = await User.findOne({ id: userId, role: "user" });
    if (!user) {
      return res.status(404).json({ success: false, msg: "Không tìm thấy user." });
    }

    // Cập nhật các trường
    if (fullName !== undefined) user.fullName = fullName;
    if (phoneNumber !== undefined) user.phoneNumber = phoneNumber;
    if (email !== undefined) user.email = email;
    if (addressProvince !== undefined) user.addressProvince = addressProvince;
    if (addressDistrict !== undefined) user.addressDistrict = addressDistrict;
    if (addressWard !== undefined) user.addressWard = addressWard;
    if (addressDetail !== undefined) user.addressDetail = addressDetail;
    if (birthday !== undefined) user.birthday = birthday ? new Date(birthday) : null;
    if (avatar !== undefined) user.avatar = avatar;

    user.updatedAt = new Date();
    await user.save();

    return res.status(200).json({ success: true, msg: "Cập nhật user thành công!", user });
  } catch (error) {
    console.error("Lỗi cập nhật user:", error.message);
    res.status(500).json({ success: false, msg: "Cập nhật user thất bại! Lỗi server." });
  }
};

// Xóa user (chỉ cho admin)
export const adminDeleteUserController = async (req, res) => {
  try {
    if (req.user.role !== "admin") {
      return res.status(403).json({ success: false, msg: "Bạn không có quyền thực hiện thao tác này." });
    }
    const userId = parseInt(req.params.id);

    const user = await User.findOneAndDelete({ id: userId, role: "user" });
    if (!user) {
      return res.status(404).json({ success: false, msg: "Không tìm thấy user để xóa." });
    }

    return res.status(200).json({ success: true, msg: "Xóa user thành công!", user });
  } catch (error) {
    console.error("Lỗi xóa user:", error.message);
    res.status(500).json({ success: false, msg: "Xóa user thất bại! Lỗi server." });
  }
};

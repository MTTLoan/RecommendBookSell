import User from "../models/User.js";
import { sendOTP, verifyOTP, deleteOTP } from "../controllers/OTPController.js";
import { hashData } from "../util/hashData.js";

const sendPasswordResetOTP = async (email) => {
  try {
    // check if an account exists
    const existingUser = await User.findOne({ email });
    if (!existingUser) {
      throw Error("Không tìm thấy tài khoản nào với email đã cung cấp.");
    }

    const otpDetails = {
      email,
      subject: "BOOKPROJECT: Đặt Lại Mật Khẩu",
      message: "Đặt lại mật khẩu của bạn bằng mã OTP bên dưới.",
      duration: 15, // 15 phút hiệu lực cho OTP
    };

    const createOTP = await sendOTP(otpDetails);
    return createOTP;
  } catch (error) {
    throw error;
  }
};

const verifyPasswordResetOTP = async ({ email, otp }) => {
  try {
    const validOTP = await verifyOTP({ email, otp });
    if (!validOTP) {
      throw Error("Mã OTP không hợp lệ. Vui lòng kiểm tra email của bạn.");
    }
    return true;
  } catch (error) {
    throw error;
  }
};

const resetPassword = async ({ email, newPassword, otp }) => {
  try {
    // verify OTP first
    const isOTPValid = await verifyPasswordResetOTP({ email, otp });
    if (!isOTPValid) {
      throw Error("Xác thực OTP thất bại.");
    }

    // hash the new password
    const hashedPassword = await hashData(newPassword);

    // update the user's password
    await User.updateOne({ email }, { password: hashedPassword });

    // delete OTP record after successful reset
    await deleteOTP(email);

    return { message: "Mật khẩu đã được đặt lại thành công." };
  } catch (error) {
    throw error;
  }
};

export { sendPasswordResetOTP, verifyPasswordResetOTP, resetPassword };

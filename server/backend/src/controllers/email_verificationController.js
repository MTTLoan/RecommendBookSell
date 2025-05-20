import User from "../models/User.js";
import { sendOTP, verifyOTP, deleteOTP } from "../controllers/OTPController.js";

const verifyUserEmail = async ({ email, otp }) => {
  try {
    const validOTP = await verifyOTP({ email, otp });
    if (!validOTP) {
      throw Error(
        "Mã xác thực không hợp lệ. Vui lòng kiểm tra hộp thư của bạn."
      );
    }

    // update user.verify -> true
    await User.updateOne({ email }, { verified: true });

    await deleteOTP(email);
    return;
  } catch (error) {
    throw error;
  }
};

const sendVerificationOTPEmail = async (email) => {
  try {
    // check if an account exists
    const existingUser = await User.findOne({ email });
    if (!existingUser) {
      throw Error("Không tìm thấy tài khoản nào với email đã cung cấp.");
    }

    const otpDetails = {
      email,
      subject: "BOOKPROJECT: Xác Thực Email",
      message: "Xác thực email BOOKPROJECT của bạn bằng mã bên dưới.",
      duration: 15,
    };

    const createOTP = await sendOTP(otpDetails);
    return createOTP;
  } catch (error) {
    throw error;
  }
};

export { sendVerificationOTPEmail, verifyUserEmail };

import User from "../models/User.js";
import { sendOTP, verifyOTP, deleteOTP } from "../controllers/OTPController.js";
import { hashData } from "../util/hashData.js";


const sendPasswordResetOTP = async (email) => {
  try {
    // check if an account exists
    const existingUser = await User.findOne({ email });
    if (!existingUser) {
      throw Error("No account found for the provided email.");
    }

    const otpDetails = {
      email,
      subject: "BOOKPROJECT: Password Reset",
      message: "Reset your password with the code below.",
      duration: 15,  // 15 minutes duration for OTP
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
      throw Error("Invalid OTP. Check your email for the correct code.");
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
      throw Error("OTP verification failed.");
    }

    // hash the new password
    const hashedPassword = await hashData(newPassword);

    // update the user's password
    await User.updateOne({ email }, { password: hashedPassword });

    // delete OTP record after successful reset
    await deleteOTP(email);

    return { message: "Password successfully reset." };
  } catch (error) {
    throw error;
  }
};

export { sendPasswordResetOTP, verifyPasswordResetOTP, resetPassword };

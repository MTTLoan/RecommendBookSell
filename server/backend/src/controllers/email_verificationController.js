import User from "../models/User.js";
import { sendOTP, verifyOTP, deleteOTP } from "../controllers/OTPController.js";

const verifyUserEmail = async ({ email, otp }) => {
  try {
    const validOTP = await verifyOTP({ email, otp });
    if (!validOTP) {
      throw Error("Invalid code passed. Check your inbox.");
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
      throw Error("There's no account for the provided email.");
    }

    const otpDetails = {
      email,
      subject: "BOOKPROJECT: Email Verification",
      message: "Verify BO email with the code below.",
      duration: 15,
    };

    const createOTP = await sendOTP(otpDetails);
    return createOTP;
  } catch (error) {
    throw error;
  }
};

export { sendVerificationOTPEmail, verifyUserEmail };

import OTP from "../models/OTP.js";
import nodemailer from "nodemailer";
import { hashData, verifyHashedData } from "../util/hashData.js";
import htmlEmailVerify from "../util/getHtmlEmail.js";

// verifyOTP
const verifyOTP = async ({ email, otp }) => {
  try {
    if (!(email && otp)) throw Error("Vui lòng cung cấp email và mã OTP.");

    // ensure otp record exists
    const matchedOTPRecord = await OTP.findOne({
      email,
    });

    const { expiresAt } = matchedOTPRecord;

    // checking for expired code
    if (expiresAt < Date.now()) {
      await OTP.deleteOne({ email });
      throw Error("Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới.");
    }

    // not expired yet, verify value
    const hashedOTP = matchedOTPRecord.otp;
    const validOTP = await verifyHashedData(otp, hashedOTP);

    return true;
  } catch (error) {
    throw error;
  }
};

// deleteOTP
const deleteOTP = async (email) => {
  try {
    await OTP.deleteOne({ email });
  } catch (error) {
    throw error;
  }
};

// send OTP
const sendOTP = async ({ email, subject, message, duration = 10 }) => {
  try {
    if (!(email && subject && message)) {
      throw Error("Vui lòng cung cấp email, tiêu đề và nội dung tin nhắn.");
    }

    // clear any old record
    await OTP.deleteOne({ email });

    // create random OTP
    let generateOTP = `${Math.floor(1000 + Math.random() * 9000)}`;

    // send mail
    const AUTH_EMAIL = process.env.AUTH_EMAIL;
    const AUTH_PASS = process.env.AUTH_PASS;

    let transporter = nodemailer.createTransport({
      service: "gmail",
      host: "smtp.gmail.com",
      port: 587,
      secure: false,
      auth: {
        user: AUTH_EMAIL,
        pass: AUTH_PASS,
      },
    });

    // test transporter
    transporter.verify((error, success) => {
      if (error) {
        console.log(error);
      } else {
        console.log("Sẵn sàng gửi tin nhắn.");
        console.log(success);
      }
    });

    // send email
    const mailOptions = {
      from: AUTH_EMAIL,
      to: email,
      subject: subject,
      text: "Xin chào BOOKPROJECT!",
      html: await htmlEmailVerify(generateOTP, duration),
    };
    await transporter.sendMail(mailOptions);

    // save otp record
    const hashedOTP = await hashData(generateOTP);
    const newOTP = await new OTP({
      email,
      otp: hashedOTP,
      createdAt: Date.now(),
      expiresAt: Date.now() + duration * 60 * 1000,
    });

    const createdOTPRecord = await newOTP.save();

    return createdOTPRecord;
  } catch (error) {
    throw error;
  }
};

export { sendOTP, verifyOTP, deleteOTP };

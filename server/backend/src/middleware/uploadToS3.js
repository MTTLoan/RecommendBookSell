import AWS from "aws-sdk";
import multer from "multer";
import multerS3 from "multer-s3";
import dotenv from "dotenv";
import AWS from "aws-sdk";
import multer from "multer";
import multerS3 from "multer-s3";
import dotenv from "dotenv";

dotenv.config();

const s3 = new AWS.S3({
  accessKeyId: process.env.AWS_ACCESS_KEY,
  secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY,
  region: "ap-southeast-1",
  region: "ap-southeast-1",
});

const uploadAvatar = multer({
  storage: multerS3({
    s3: s3,
    bucket: "upload-avatar-473",
    bucket: "upload-avatar-473",
    key: function (req, file, cb) {
      console.log("File received:", file.originalname);
      // Lưu vào thư mục avatars
      const fileName = `avatars/${Date.now()}-${file.originalname}`;
      cb(null, fileName);
    },
    contentType: multerS3.AUTO_CONTENT_TYPE,
    // acl: "public-read", // Removed to fix S3 ACL error
  }),
  limits: { fileSize: 5 * 1024 * 1024 },
}).single("avatar");
export default uploadAvatar;
  limits: { fileSize: 5 * 1024 * 1024 }, // 5MB limit
  fileFilter: (req, file, cb) => {
    console.log("File filter - mimetype:", file.mimetype);
    // Chỉ cho phép file ảnh
    if (file.mimetype.startsWith("image/")) {
      cb(null, true);
    } else {
      cb(new Error("Chỉ cho phép upload file ảnh"), false);
    }
  },
}).single("avatar");

// Wrap middleware để bắt lỗi
const uploadAvatarWithErrorHandling = (req, res, next) => {
  uploadAvatar(req, res, (err) => {
    if (err) {
      console.error("Upload error:", err);
      return res.status(400).json({
        success: false,
        msg: `Lỗi upload: ${err.message}`,
      });
    }
    console.log("Upload successful, continuing...");
    next();
  });
};

export default uploadAvatarWithErrorHandling;

const deleteS3File = async (fileUrl) => {
  // Tách key từ URL (sau bucket name)
  const bucket = "upload-avatar-473";
  const url = new URL(fileUrl);
  const key = decodeURIComponent(url.pathname).substring(1); // bỏ dấu `/` đầu
  const bucket = "upload-avatar-473";

  try {
    const url = new URL(fileUrl);
    const key = decodeURIComponent(url.pathname).substring(1); // bỏ dấu `/` đầu

    await s3
      .deleteObject({
        Bucket: bucket,
        Key: key,
      })
      .promise();
    console.log(`Đã xóa ảnh cũ: ${key}`);
  } catch (err) {
    console.error("Lỗi khi xóa ảnh cũ trên S3:", err.message);
    console.error("Lỗi khi xóa ảnh cũ trên S3:", err.message);
  }
};
export { deleteS3File };

export { deleteS3File };

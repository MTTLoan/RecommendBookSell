import AWS from "aws-sdk";
import multer from "multer";
import multerS3 from "multer-s3";
import dotenv from "dotenv";

dotenv.config();

const s3 = new AWS.S3({
  region: "us-east-1",
});

const uploadAvatar = multer({
  storage: multerS3({
    s3: s3,
    bucket: "upload-file-10",
    key: function (req, file, cb) {
      console.log("File received:", file.originalname);
      // Lưu vào thư mục avatars
      const fileName = `avatars/${Date.now()}-${file.originalname}`;
      cb(null, fileName);
    },
    contentType: multerS3.AUTO_CONTENT_TYPE,
  }),
  limits: { fileSize: 5 * 1024 * 1024 },
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
  const bucket = "upload-file-10";
  const url = new URL(fileUrl);
  const key = decodeURIComponent(url.pathname).substring(1);

  try {
    const url = new URL(fileUrl);
    const key = decodeURIComponent(url.pathname).substring(1);

    await s3
      .deleteObject({
        Bucket: bucket,
        Key: key,
      })
      .promise();
    console.log(`Đã xóa ảnh cũ: ${key}`);
  } catch (err) {
    console.error("Lỗi khi xóa ảnh cũ trên S3:", err.message);
  }
};
export { deleteS3File };

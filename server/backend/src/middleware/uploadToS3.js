import AWS from 'aws-sdk';
import multer from 'multer';
import multerS3 from 'multer-s3';
import dotenv from 'dotenv';
dotenv.config();

const s3 = new AWS.S3({
  accessKeyId: process.env.AWS_ACCESS_KEY,
  secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY,
  region: 'ap-southeast-1',
});

const uploadAvatar = multer({
  storage: multerS3({
    s3: s3,
    bucket: 'upload-avatar-473',
    key: function (req, file, cb) {
      console.log("File received:", file.originalname);
      const fileName = `avatars/${Date.now()}-${file.originalname}`;
      cb(null, fileName);
    },
  }),
  limits: { fileSize: 5 * 1024 * 1024 },
}).single('avatar');
export default uploadAvatar;

const deleteS3File = async (fileUrl) => {
  // Tách key từ URL (sau bucket name)
  const bucket = 'upload-avatar-473';
  const url = new URL(fileUrl);
  const key = decodeURIComponent(url.pathname).substring(1); // bỏ dấu `/` đầu

  try {
    await s3
      .deleteObject({
        Bucket: bucket,
        Key: key,
      })
      .promise();
    console.log(`Đã xóa ảnh cũ: ${key}`);
  } catch (err) {
    console.error('Lỗi khi xóa ảnh cũ trên S3:', err.message);
  }
};
export {deleteS3File };

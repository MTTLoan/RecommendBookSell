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

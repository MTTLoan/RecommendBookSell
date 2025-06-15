import AWS from "aws-sdk";
import multer from "multer";
import multerS3 from "multer-s3";
import dotenv from "dotenv";

dotenv.config();

const s3 = new AWS.S3({
  region: "us-east-1",
});

// Middleware upload nhiều ảnh cho sản phẩm
const uploadProductImages = multer({
  storage: multerS3({
    s3: s3,
    bucket: "upload-file-10",
    key: function (req, file, cb) {
      const fileName = `products/${Date.now()}-${file.originalname}`;
      cb(null, fileName);
    },
    contentType: multerS3.AUTO_CONTENT_TYPE,
  }),
  limits: { fileSize: 5 * 1024 * 1024 },
  fileFilter: (req, file, cb) => {
    if (file.mimetype.startsWith("image/")) {
      cb(null, true);
    } else {
      cb(new Error("Chỉ cho phép upload file ảnh"), false);
    }
  },
}).array("image", 10);

const uploadProductImagesWithErrorHandling = (req, res, next) => {
  uploadProductImages(req, res, (err) => {
    if (err) {
      return res.status(400).json({
        success: false,
        msg: `Lỗi upload: ${err.message}`,
      });
    }
    next();
  });
};

export default uploadProductImagesWithErrorHandling;

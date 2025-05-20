import { body, validationResult } from "express-validator";
import { userService } from "../services/userService.js";

// Schema chung cho các trường
const usernameSchema = {
  notEmpty: { errorMessage: "Tên tài khoản là bắt buộc" },
  isString: { errorMessage: "Tên tài khoản phải là chuỗi" },
  trim: true,
  isLength: {
    options: { min: 3, max: 50 },
    errorMessage: "Tên tài khoản phải từ 3 đến 50 ký tự",
  },
};

const fullNameSchema = {
  notEmpty: { errorMessage: "Họ và tên là bắt buộc" },
  isString: { errorMessage: "Họ và tên phải là chuỗi" },
  trim: true,
  isLength: {
    options: { min: 1, max: 100 },
    errorMessage: "Họ và tên phải từ 1 đến 100 ký tự",
  },
};

const emailSchema = {
  notEmpty: { errorMessage: "Email là bắt buộc" },
  isEmail: { errorMessage: "Email không đúng định dạng" },
  trim: true,
};

const phoneNumberSchema = {
  notEmpty: { errorMessage: "Số điện thoại là bắt buộc" },
  isString: { errorMessage: "Số điện thoại phải là chuỗi" },
  trim: true,
  matches: {
    options: /^[0-9]{10,11}$/,
    errorMessage: "Số điện thoại phải là số và có 10-11 chữ số",
  },
};

const passwordSchema = {
  notEmpty: { errorMessage: "Mật khẩu là bắt buộc" },
  isString: { errorMessage: "Mật khẩu phải là chuỗi" },
  trim: true,
  isLength: {
    options: { min: 8, max: 50 },
    errorMessage: "Mật khẩu phải từ 8 đến 50 ký tự",
  },
  isStrongPassword: {
    options: {
      minLength: 8,
      minLowercase: 1,
      minUppercase: 1,
      minNumbers: 1,
      minSymbols: 1,
    },
    errorMessage:
      "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt",
  },
};

// Hàm validate tổng quát
const validate = (schema) => {
  const validationMiddleware = [
    ...Object.keys(schema).map((key) =>
      body(key).customSanitizer((value) => {
        if (schema[key].trim && typeof value === "string") {
          return value.trim();
        }
        return value;
      })
    ),
    ...Object.keys(schema).map((key) => {
      const fieldSchema = schema[key];
      let validator = body(key);

      if (fieldSchema.notEmpty)
        validator = validator
          .notEmpty()
          .withMessage(fieldSchema.notEmpty.errorMessage);
      if (fieldSchema.isString)
        validator = validator
          .isString()
          .withMessage(fieldSchema.isString.errorMessage);
      if (fieldSchema.isEmail)
        validator = validator
          .isEmail()
          .withMessage(fieldSchema.isEmail.errorMessage);
      if (fieldSchema.isLength)
        validator = validator
          .isLength(fieldSchema.isLength.options)
          .withMessage(fieldSchema.isLength.errorMessage);
      if (fieldSchema.isStrongPassword)
        validator = validator
          .isStrongPassword(fieldSchema.isStrongPassword.options)
          .withMessage(fieldSchema.isStrongPassword.errorMessage);
      if (fieldSchema.matches)
        validator = validator
          .matches(fieldSchema.matches.options)
          .withMessage(fieldSchema.matches.errorMessage);
      if (fieldSchema.custom)
        validator = validator.custom(fieldSchema.custom.options);

      return validator;
    }),
    (req, res, next) => {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({ errors: errors.array() });
      }
      next();
    },
  ];

  return validationMiddleware;
};

// Validator cho đăng ký
export const registerValidator = validate({
  username: usernameSchema,
  fullName: fullNameSchema,
  email: {
    ...emailSchema,
    custom: {
      options: async (value) => {
        const user = await userService.User.findOne({ email: value });
        if (user) {
          throw new Error("Email đã tồn tại");
        }
        return true;
      },
    },
  },
  phoneNumber: {
    ...phoneNumberSchema,
    custom: {
      options: async (value) => {
        const user = await userService.User.findOne({ phoneNumber: value });
        if (user) {
          throw new Error("Số điện thoại đã tồn tại");
        }
        return true;
      },
    },
  },
  password: passwordSchema,
  confirm_password: {
    notEmpty: { errorMessage: "Xác nhận mật khẩu là bắt buộc" },
    isString: { errorMessage: "Xác nhận mật khẩu phải là chuỗi" },
    trim: true,
    isLength: {
      options: { min: 8, max: 50 },
      errorMessage: "Xác nhận mật khẩu phải từ 8 đến 50 ký tự",
    },
    isStrongPassword: {
      options: {
        minLength: 8,
        minLowercase: 1,
        minUppercase: 1,
        minNumbers: 1,
        minSymbols: 1,
      },
      errorMessage:
        "Xác nhận mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt",
    },
    custom: {
      options: (value, { req }) => {
        if (value !== req.body.password) {
          throw new Error("Xác nhận mật khẩu không khớp với mật khẩu");
        }
        return true;
      },
    },
  },
});

// Validator cho đăng |nhập
export const loginValidator = validate({
  identifier: {
    notEmpty: { errorMessage: "Tên tài khoản hoặc email là bắt buộc" },
    isString: { errorMessage: "Tên tài khoản hoặc email phải là chuỗi" },
    trim: true,
  },
  password: {
    notEmpty: { errorMessage: "Mật khẩu là bắt buộc" },
    isString: { errorMessage: "Mật khẩu phải là chuỗi" },
    trim: true,
  },
});

// Validator quên mật khẩu
export const forgotPasswordRequestValidator = validate({
  email: emailSchema,
});

export const resetPasswordValidator = validate({
  email: emailSchema,
  otp: {
    notEmpty: { errorMessage: "OTP là bắt buộc" },
    isNumeric: { errorMessage: "OTP chỉ được chứa số" },
    isLength: {
      options: { min: 4, max: 4 },
      errorMessage: "OTP phải gồm đúng 4 chữ số",
    },
  },
  newPassword: passwordSchema,
});

// Validator cho thay đổi mật khẩu
export const changePasswordValidator = validate({
  oldPassword: {
    notEmpty: { errorMessage: "Mật khẩu cũ là bắt buộc" },
    isString: { errorMessage: "Mật khẩu cũ phải là chuỗi" },
    trim: true,
  },
  newPassword: passwordSchema,
  confirmPassword: {
    notEmpty: { errorMessage: "Xác nhận mật khẩu là bắt buộc" },
    isString: { errorMessage: "Xác nhận mật khẩu phải là chuỗi" },
    trim: true,
    custom: {
      options: (value, { req }) => {
        if (value !== req.body.newPassword) {
          throw new Error("Xác nhận mật khẩu không khớp với mật khẩu mới");
        }
        return true;
      },
    },
  },
});

// Validator cho cập nhật hồ sơ
export const updateProfileValidator = validate({
  fullName: { ...fullNameSchema, optional: true },
  phoneNumber: {
    ...phoneNumberSchema,
    optional: true,
    custom: {
      options: async (value, { req }) => {
        if (value) {
          const user = await userService.User.findOne({ phoneNumber: value });
          if (user && user.id !== req.user.id) {
            throw new Error("Số điện thoại đã tồn tại");
          }
        }
        return true;
      },
    },
  },
});
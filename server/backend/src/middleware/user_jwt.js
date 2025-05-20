import jwt from "jsonwebtoken";

const userJwtMiddleware = async (req, res, next) => {
  const token = req.header("Authorization");

  if (!token) {
    return res.status(401).json({
      msg: "Không có token, quyền truy cập bị từ chối",
    });
  }

  try {
    // Nếu token có dạng "Bearer <token>", thì cần tách chuỗi:
    const pureToken = token.startsWith("Bearer ")
      ? token.slice(7).trim()
      : token;

    // Verify JWT: kiểm tra tính hợp lệ
    const decoded = jwt.verify(pureToken, process.env.JWT_SECRET); // Sửa từ jwtUserSecret thành JWT_SECRET
    req.user = decoded.user;
    next();
  } catch (err) {
    console.log("Lỗi middleware: " + err.message);
    res.status(401).json({
      msg: "Token không hợp lệ",
    });
  }
};

export default userJwtMiddleware;

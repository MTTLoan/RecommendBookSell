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

    // verify JWT: kiểm tra tính hợp lệ
    // process.env.jwtUserSecret là chuỗi bí mật mã hoá
    jwt.verify(pureToken, process.env.jwtUserSecret, (err, decoded) => {
      if (err) {
        res.status(401).json({
          msg: "Token không hợp lệ",
        });
      } else {
        req.user = decoded.user;
        next();
      }
    });
  } catch (err) {
    console.log("Lỗi middleware: " + err);
    res.status(500).json({
      msg: "Lỗi máy chủ",
    });
  }
};

export default userJwtMiddleware;

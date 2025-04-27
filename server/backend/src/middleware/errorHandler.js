export const errorHandler = (err, req, res, next) => {
  console.error(err.stack);
  res.status(err.status || 500).json({
    message: err.message || "Lỗi server",
    error: process.env.NODE_ENV === "development" ? err.stack : undefined,
  });
};

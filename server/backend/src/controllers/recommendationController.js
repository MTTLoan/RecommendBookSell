import { exec } from "child_process";
import { fileURLToPath } from "url";
import { dirname, resolve } from "path";
import Book from "../models/Book.js";

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

// Đường dẫn tới script Python
const pythonScriptPath = resolve(__dirname, "../../../ai-backend/src/main.py");
// Đường dẫn tới Python trong môi trường ảo
const pythonExec = resolve(
  __dirname,
  "../../../ai-backend/venv/Scripts/python.exe"
);

const getRecommendations = async (req, res) => {
  const userId = req.user?.id;
  if (!userId) {
    return res
      .status(400)
      .json({ success: false, msg: "Không tìm thấy userId trong token" });
  }

  console.log(
    `Calling Python script: ${pythonExec} ${pythonScriptPath} --user ${userId}`
  );

  exec(
    `"${pythonExec}" "${pythonScriptPath}" --user ${userId}`,
    async (error, stdout, stderr) => {
      if (error) {
        console.error(`Error: ${error.message}`);
        return res.status(500).json({
          success: false,
          msg: "Lỗi khi gọi script gợi ý",
          error: error.message,
        });
      }
      if (stderr) {
        console.error(`Stderr: ${stderr}`);
        return res
          .status(500)
          .json({ success: false, msg: "Lỗi từ script gợi ý", error: stderr });
      }

      try {
        const result = JSON.parse(stdout);
        if (result.error) {
          return res.status(400).json({
            success: false,
            msg: "Lỗi từ script gợi ý",
            error: result.error,
          });
        }

        // Lấy danh sách bookId từ kết quả của script Python
        const bookIds = result.recommended_books.map((book) => book.id);

        // Truy vấn MongoDB để lấy thông tin chi tiết của các sách
        const books = await Book.find({ id: { $in: bookIds } }).lean();

        // Sắp xếp books theo thứ tự của bookIds
        const sortedBooks = bookIds
          .map((id) => books.find((book) => book.id === id))
          .filter((book) => book);

        // Định dạng response
        res.status(200).json({
          success: true,
          msg: "Lấy danh sách sách đề xuất thành công.",
          book: sortedBooks,
        });
      } catch (parseError) {
        console.error(`Parse Error: ${parseError.message}`);
        res.status(500).json({
          success: false,
          msg: "Lỗi khi phân tích kết quả từ script",
          error: parseError.message,
        });
      }
    }
  );
};

const getRecommendationsByBook = async (req, res) => {
  const { bookId } = req.params;
  if (!bookId) {
    return res
      .status(400)
      .json({ success: false, msg: "Vui lòng cung cấp bookId" });
  }

  console.log(
    `Calling Python script: ${pythonExec} ${pythonScriptPath} --book ${bookId}`
  );

  exec(
    `"${pythonExec}" "${pythonScriptPath}" --book ${bookId}`,
    async (error, stdout, stderr) => {
      if (error) {
        console.error(`Error: ${error.message}`);
        return res.status(500).json({
          success: false,
          msg: "Lỗi khi gọi script gợi ý",
          error: error.message,
        });
      }
      if (stderr) {
        console.error(`Stderr: ${stderr}`);
        return res
          .status(500)
          .json({ success: false, msg: "Lỗi từ script gợi ý", error: stderr });
      }

      try {
        const result = JSON.parse(stdout);
        if (result.error) {
          return res.status(400).json({
            success: false,
            msg: "Lỗi từ script gợi ý",
            error: result.error,
          });
        }

        // Lấy danh sách bookId từ kết quả của script Python
        const bookIds = result.recommended_books.map((book) => book.id);

        // Truy vấn MongoDB để lấy thông tin chi tiết của các sách
        const books = await Book.find({ id: { $in: bookIds } }).lean();

        // Sắp xếp books theo thứ tự của bookIds
        const sortedBooks = bookIds
          .map((id) => books.find((book) => book.id === id))
          .filter((book) => book);

        // Định dạng response
        res.status(200).json({
          success: true,
          msg: "Lấy danh sách sách đề xuất thành công.",
          book: sortedBooks,
        });
      } catch (parseError) {
        console.error(`Parse Error: ${parseError.message}`);
        res.status(500).json({
          success: false,
          msg: "Lỗi khi phân tích kết quả từ script",
          error: parseError.message,
        });
      }
    }
  );
};

export { getRecommendations, getRecommendationsByBook };

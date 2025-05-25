// backend/src/routes/recommendationRoutes.js
import express from "express";
import { exec } from "child_process";
import { fileURLToPath } from "url";
import { dirname, resolve } from "path";
const router = express.Router();

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

// Đường dẫn tới script Python (đi lên hai cấp rồi vào ai-backend)
const pythonScriptPath = resolve(__dirname, "../../../ai-backend/src/main.py");
// Đường dẫn tới Python trong môi trường ảo
const pythonExec = resolve(
  __dirname,
  "../../../ai-backend/venv/Scripts/python.exe"
); // Sử dụng python.exe trên Windows

router.get("/:userId", (req, res) => {
  const userId = req.params.userId;
  console.log(
    `Calling Python script: ${pythonExec} ${pythonScriptPath} ${userId}`
  );

  exec(
    `"${pythonExec}" "${pythonScriptPath}" ${userId}`,
    (error, stdout, stderr) => {
      if (error) {
        console.error(`Error: ${error.message}`);
        return res.status(500).json({ error: "Lỗi khi gọi script gợi ý" });
      }
      if (stderr) {
        console.error(`Stderr: ${stderr}`);
        return res.status(500).json({ error: "Lỗi từ script gợi ý" });
      }

      try {
        const result = JSON.parse(stdout);
        if (result.error) {
          return res.status(400).json(result);
        }
        res.status(200).json(result);
      } catch (parseError) {
        console.error(`Parse Error: ${parseError.message}`);
        res.status(500).json({ error: "Lỗi khi phân tích kết quả từ script" });
      }
    }
  );
});

export default router;

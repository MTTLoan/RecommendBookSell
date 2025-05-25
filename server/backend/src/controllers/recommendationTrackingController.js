import RecommendationTracking from "../models/RecommendationTracking.js";
import Counter from "../models/Counter.js";

export const recordClick = async (req, res) => {
  console.log("Request received:", req.headers["authorization"], req.query);
  const { id: userId } = req.user;
  const { bookId } = req.query;

  try {
    if (!userId) {
      console.error("userId is undefined, req.user:", req.user);
      return res
        .status(400)
        .json({ success: false, msg: "userId không hợp lệ" });
    }
    if (!bookId || isNaN(parseInt(bookId))) {
      return res
        .status(400)
        .json({ success: false, msg: "bookId không hợp lệ" });
    }

    // Tăng giá trị counter và lấy id mới
    const counter = await Counter.findOneAndUpdate(
      { _id: "recommendationTrackingId" },
      { $inc: { seq: 1 } },
      { new: true, upsert: true }
    );
    const newId = counter.seq;

    // Tạo bản ghi mới với id tự động tăng
    await new RecommendationTracking({
      id: newId,
      userId,
      bookId: parseInt(bookId),
      action: "click",
    }).save();

    console.log(
      `Recorded click for user ${userId} on book ${bookId} with id ${newId}`
    );
    res.status(200).json({ success: true, msg: "Ghi nhận click thành công" });
  } catch (error) {
    console.error("Error recording click:", error);
    res.status(500).json({
      success: false,
      msg: "Lỗi khi ghi nhận click",
      error: error.message,
    });
  }
};

export default { recordClick };

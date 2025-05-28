import RecommendationTracking from "../models/RecommendationTracking.js";
import Counter from "../models/Counter.js";

const getNextSequence = async (name) => {
  const counter = await Counter.findOneAndUpdate(
    { _id: name },
    { $inc: { seq: 1 } },
    { new: true, upsert: true }
  );
  return counter.seq;
};

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

export const recordAddToCart = async (req, res) => {
  try {
    const userId = req.user.id;
    const { bookId, cartId } = req.body;

    if (!bookId || isNaN(bookId) || !cartId || isNaN(cartId)) {
      return res.status(400).json({ message: "Invalid bookId or cartId" });
    }

    const tracking = new RecommendationTracking({
      id: await getNextSequence("recommendationTrackingId"),
      userId,
      bookId: parseInt(bookId),
      action: "add_to_cart",
      timestamp: new Date(),
      cartId: parseInt(cartId),
    });

    await tracking.save();
    return res
      .status(200)
      .json({ message: "Add to cart recorded successfully" });
  } catch (error) {
    console.error("Error recording add to cart:", error);
    return res.status(500).json({ message: "Server error" });
  }
};

export const recordPurchase = async (req, res) => {
  try {
    const userId = req.user.id;
    const { bookId, orderId } = req.body;

    if (!bookId || isNaN(bookId) || !orderId || isNaN(orderId)) {
      return res.status(400).json({ message: "Invalid bookId or orderId" });
    }

    const tracking = new RecommendationTracking({
      id: await getNextSequence("recommendationTrackingId"),
      userId,
      bookId: parseInt(bookId),
      action: "purchase",
      timestamp: new Date(),
      orderId: parseInt(orderId),
    });

    await tracking.save();
    return res.status(200).json({ message: "Purchase recorded successfully" });
  } catch (error) {
    console.error("Error recording purchase:", error);
    return res.status(500).json({ message: "Server error" });
  }
};

export default { recordClick, recordAddToCart, recordPurchase };

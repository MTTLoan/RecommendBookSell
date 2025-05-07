import bcrypt from "bcryptjs";

const hashData = async (data, saltRounds = 10) => {
  try {
    const hashedData = await bcryptjs.hash(data, saltRounds);
    return hashedData;
  } catch (error) {
    throw error;
  }
};

const verifyHashedData = async (unhashed, hashed) => {
  try {
    const match = await bcryptjs.compare(unhashed, hashed);
    return match;
  } catch (error) {
    throw error;
  }
};

export { hashData, verifyHashedData };

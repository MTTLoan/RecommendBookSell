import mongoose from "mongoose";

const UserSchema = new mongoose.Schema(
  {
    id: { type: Number, required: true, unique: true }, // Maps to int [pk]
    username: { type: String, required: true, unique: true },
    fullName: { type: String, required: true },
    email: { type: String, required: true, unique: true },
    phoneNumber: { type: String, required: true }, // Removed unique
    addressProvince: { type: Number, required: false }, // int
    addressDistrict: { type: Number, required: false }, // int
    addressWard: { type: Number, required: false }, // int
    addressDetail: { type: String, required: false }, // varchar
    password: { type: String, required: true },
    role: { type: String, default: "user" }, // Removed enum to allow any varchar
    birthday: { type: Date, required: false }, // Maps to date
    avatar: { type: String, required: false }, // Maps to avatar varchar
    token: { type: String, required: false }, // Maps to token varchar
  },
  {
    timestamps: { createdAt: "createdAt", updatedAt: "updatedAt" }, // Auto-manage createdAt, updatedAt
  }
);

// Virtual to expose _id as id for API compatibility
// UserSchema.virtual("id").get(function () {
//   return this._id;
// });

// Ensure virtuals are included in JSON output
UserSchema.set("toJSON", { virtuals: true });

export default mongoose.model("User", UserSchema);

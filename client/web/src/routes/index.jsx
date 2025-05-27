import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import { isAuthenticated } from "../services/authService";

// Auth Pages
import Login from "../pages/auth/Login";
import ForgotPassword from "../pages/auth/ForgotPassword";
import ResetPassword from "../pages/auth/ResetPassword";

// Dashboard Page
import Dashboard from "../pages/dashboard/Dashboard";

// Product Pages
import ListProduct from "../pages/products/ListProduct";
import AddProduct from "../pages/products/AddProduct";
import EditProduct from "../pages/products/EditProduct";
import ViewProduct from "../pages/products/ViewProduct";

// Order Pages
import ListOrder from "../pages/orders/ListOrder";
import EditOrder from "../pages/orders/EditOrder";
import ViewOrder from "../pages/orders/ViewOrder";

// Customer Pages
import ListCustomer from "../pages/customers/ListCustomer";
import AddCustomer from "../pages/customers/AddCustomer";
import EditCustomer from "../pages/customers/EditCustomer";
import ViewCustomer from "../pages/customers/ViewCustomer";

// Category Pages
import ListCategory from "../pages/categories/ListCategory";
import ViewCategory from "../pages/categories/ViewCategory";

// Notification Pages
import ListNotification from "../pages/notifications/ListNotification";

// Reviews Page
import ListReview from "../pages/reviews/ListReview";
import DetailReview from "../pages/reviews/DetailReview";

// Settings Page
import Settings from "../pages/settings/Settings";

// Reports Page
import Reports from "../pages/reports/Reports";

// Protected Route wrapper
const ProtectedRoute = ({ children }) => {
  if (!isAuthenticated()) {
    return <Navigate to="/auth/login" replace />;
  }
  return children;
};

// Public Route wrapper (redirects to dashboard if already logged in)
const PublicRoute = ({ children }) => {
  if (isAuthenticated()) {
    return <Navigate to="/dashboard" replace />;
  }
  return children;
};

const AppRoutes = () => {
  return (
    <Routes>
      {/* Auth Routes */}
      <Route
        path="/auth/login"
        element={
          <PublicRoute>
            <Login />
          </PublicRoute>
        }
      />
      <Route
        path="/auth/forgot-password"
        element={
          <PublicRoute>
            <ForgotPassword />
          </PublicRoute>
        }
      />
      <Route
        path="/auth/reset-password"
        element={
          <PublicRoute>
            <ResetPassword />
          </PublicRoute>
        }
      />

      {/* Protected Routes */}
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <Dashboard />
          </ProtectedRoute>
        }
      />

      {/* Product Routes */}
      <Route
        path="/products"
        element={
          <ProtectedRoute>
            <ListProduct />
          </ProtectedRoute>
        }
      />
      <Route
        path="/products/add"
        element={
          <ProtectedRoute>
            <AddProduct />
          </ProtectedRoute>
        }
      />
      <Route
        path="/products/edit/:id"
        element={
          <ProtectedRoute>
            <EditProduct />
          </ProtectedRoute>
        }
      />
      <Route
        path="/products/view/:id"
        element={
          <ProtectedRoute>
            <ViewProduct />
          </ProtectedRoute>
        }
      />

      {/* Order Routes */}
      <Route
        path="/orders"
        element={
          <ProtectedRoute>
            <ListOrder />
          </ProtectedRoute>
        }
      />
      <Route
        path="/orders/edit/:id"
        element={
          <ProtectedRoute>
            <EditOrder />
          </ProtectedRoute>
        }
      />
      <Route
        path="/orders/view/:id"
        element={
          <ProtectedRoute>
            <ViewOrder />
          </ProtectedRoute>
        }
      />

      {/* Customer Routes */}
      <Route
        path="/customers"
        element={
          <ProtectedRoute>
            <ListCustomer />
          </ProtectedRoute>
        }
      />
      <Route
        path="/customers/add"
        element={
          <ProtectedRoute>
            <AddCustomer />
          </ProtectedRoute>
        }
      />
      <Route
        path="/customers/edit/:id"
        element={
          <ProtectedRoute>
            <EditCustomer />
          </ProtectedRoute>
        }
      />
      <Route
        path="/customers/view/:id"
        element={
          <ProtectedRoute>
            <ViewCustomer />
          </ProtectedRoute>
        }
      />

      {/* Category Routes */}
      <Route
        path="/categories"
        element={
          <ProtectedRoute>
            <ListCategory />
          </ProtectedRoute>
        }
      />
      <Route
        path="/categories/view/:id"
        element={
          <ProtectedRoute>
            <ViewCategory />
          </ProtectedRoute>
        }
      />
      {/* Notification Routes */}
      <Route
        path="/notifications"
        element={
          <ProtectedRoute>
            <ListNotification />
          </ProtectedRoute>
        }
      />
      {/* Reviews Routes */}
      <Route
        path="/reviews"
        element={
          <ProtectedRoute>
            <ListReview />
          </ProtectedRoute>
        }
      />
      <Route
        path="/reviews/detail/:id"
        element={
          <ProtectedRoute>
            <DetailReview />
          </ProtectedRoute>
        }
      />

      {/* Settings Route */}
      <Route
        path="/settings"
        element={
          <ProtectedRoute>
            <Settings />
          </ProtectedRoute>
        }
      />

      {/* Reports Route */}
      <Route
        path="/reports"
        element={
          <ProtectedRoute>
            <Reports />
          </ProtectedRoute>
        }
      />

      {/* Redirect root to dashboard if authenticated, otherwise to login */}
      <Route
        path="/"
        element={
          isAuthenticated() ? (
            <Navigate to="/dashboard" replace />
          ) : (
            <Navigate to="/auth/login" replace />
          )
        }
      />

      {/* 404 - Not Found */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};

export default AppRoutes;

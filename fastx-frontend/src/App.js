import React, { useContext } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";

import { AuthProvider, AuthContext } from "./context/AuthContext";
import Layout from "./Pages/Layout";
import HomePage from "./Pages/HomePage";
import OperatorDashboard from "./Pages/OperatorDashboard";
import PassengerDashboard from "./Pages/PassengerDashboard";
import AdminDashboard from "./Pages/AdminDashboard";
import SelectSeats from "./Pages/Passenger/SelectSeats";





function AppRoutes() {
  const { role } = useContext(AuthContext);

  return (
    <Layout>
    <Routes>
      <Route path="/" element={<HomePage />} />
    <Route path="/select-seat/:routeId" element={<SelectSeats />} />

      <Route
        path="/operator"
        element={role === "OPERATOR" ? <OperatorDashboard /> : <Navigate to="/" />}
      />
      <Route
        path="/passenger"
        element={role === "PASSENGER" ? <PassengerDashboard /> : <Navigate to="/" />}
      />
      <Route
        path="/admin"
        element={role === "ADMIN" ? <AdminDashboard /> : <Navigate to="/" />}
      />
    </Routes>
    </Layout>
  );
}

function App() {
  return (
    <AuthProvider>
      <Router>
        <AppRoutes />
      </Router>
    </AuthProvider>
  );
}

export default App;

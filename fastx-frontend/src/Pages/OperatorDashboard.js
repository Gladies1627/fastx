import React, { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import "./OperatorDashboard.css";
import OperatorRoutesTab from "./Operator/OperatorRoutesTab";
import OperatorBookings from "./Operator/OperatorBookingsTab";

const OperatorDashboard = () => {
  const { logout, user } = useContext(AuthContext);
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState("routes");
  const username = user?.name || "Operator";

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  return (
    <div className="operator-dashboard-wrapper">
      {/* Logout button at top-right */}
      <button className="logout-btn-global" onClick={handleLogout}>
        <i className="bi bi-box-arrow-right me-1" />
        Logout
      </button>

      {/* Welcome text in the center top */}
      <div className="welcome-box text-center">
        <h1 className="welcome-heading">Welcome, {username}</h1>
        <p className="welcome-subtext">Manage your routes and bookings below</p>
      </div>
      <div className="tab-container px-4 animated-tab-container">
        {[
          { key: "routes", label: "Manage Routes" },
          { key: "bookings", label: "View Bookings" },
        ].map(({ key, label }) => (
          <button
            key={key}
            onClick={() => setActiveTab(key)}
            className={`btn mx-2 ${activeTab === key ? "btn-primary" : "btn-outline-primary"}`}
          >
            {label}
          </button>
        ))}
      </div>
        {activeTab === "routes" && <OperatorRoutesTab />}
        {activeTab === "bookings" && <OperatorBookings />}
      </div>
   
  );
};

export default OperatorDashboard;

import React, { useState } from "react";
import SearchBuses from "./Passenger/SearchBuses";
import MyBookings from "./Passenger/MyBookings";
import Cancellations from "./Passenger/Cancellations";
import '../Pages/PassengerDashboard.css';


function PassengerDashboard() {
  const [activeTab, setActiveTab] = useState("search");
  const userId = localStorage.getItem("userId");
  const userName = localStorage.getItem("username");
  

  return (
    
    <div className="passenger-page">
    <div className="container mt-4 passenger-dashboard">
      {/* Header */}
<div className="d-flex justify-content-end mb-4">
  <button
    className="btn btn-danger"
    onClick={() => {
      localStorage.clear();
      window.location.href = "/";
    }}
  >
    Logout
  </button>
</div>
<div className="text-center my-4">
  <h2 className="welcome-text">Welcome, {userName}</h2>
</div>

      {/* Tab Navigation */}
      <ul className="nav nav-tabs">
        <li className="nav-item">
          <button
            className={`nav-link ${activeTab === "search" ? "active" : ""}`}
            onClick={() => setActiveTab("search")}
          >
            Search Buses
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link ${activeTab === "bookings" ? "active" : ""}`}
            onClick={() => setActiveTab("bookings")}
          >
            My Bookings
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link ${activeTab === "cancellations" ? "active" : ""}`}
            onClick={() => setActiveTab("cancellations")}
          >
            Cancellations
          </button>
        </li>
      </ul>

      <div className="tab-content mt-4">
        {activeTab === "search" && <SearchBuses />}
        {activeTab === "bookings" && <MyBookings />}
        {activeTab === "cancellations" && (
  <Cancellations passengerId={userId} />
)}

      </div>
    </div>
    </div>
    
  );
}

export default PassengerDashboard;

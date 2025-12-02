import React, { useEffect, useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import AdminUsersTab from "./Admin/AdminUsersTab";
import AdminRoutesTab from "./Admin/AdminRoutesTab";
import AdminBookingsTab from "./Admin/AdminBookingsTab";
import AdminCancellationsTab from "./Admin/AdminCancellationsTab";
import { AuthContext } from "../context/AuthContext";
import "../Pages/AdminDashboard.css";

function AdminDashboard() {
  const [activeTab, setActiveTab] = useState("users");
  const [dashboardData, setDashboardData] = useState(null);
  const navigate = useNavigate();
  const { token } = useContext(AuthContext);

  const fetchDashboardData = async () => {
    try {
      const res = await fetch("https://fastx-backend-ilxf.onrender.com/api/admin/dashboard", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      const data = await res.json();
      setDashboardData(data);
    } catch (err) {
      console.error("Failed to fetch dashboard data:", err);
    }
  };

  useEffect(() => {
    fetchDashboardData();
  }, []);

  if (!dashboardData) return <div className="text-center mt-5">Loading...</div>;

  return (
    <div className="admin-dashboard-bg">
      {/* Top right logout */}
      <button
        className="logout-fixed-btn btn btn-danger"
        onClick={() => {
          localStorage.clear();
          navigate("/");
        }}
      >
        Logout
      </button>

      {/* Center heading */}
      <h2 className="admin-heading-center">Welcome, Admin</h2>

      {/* Glassy container */}
      <div className="dashboard-container">
        <ul className="nav nav-tabs justify-content-center mb-4">
          {["users", "routes", "bookings", "cancellations"].map((tab) => (
            <li className="nav-item" key={tab}>
              <button
                className={`nav-link ${activeTab === tab ? "active" : ""}`}
                onClick={() => setActiveTab(tab)}
              >
                {tab.charAt(0).toUpperCase() + tab.slice(1)}
              </button>
            </li>
          ))}
        </ul>

        <div className="tab-content">
          {activeTab === "users" && (
            <AdminUsersTab users={dashboardData.users} fetchUsers={fetchDashboardData} />
          )}
          {activeTab === "routes" && <AdminRoutesTab busRoutes={dashboardData.busRoutes} />}
          {activeTab === "bookings" && <AdminBookingsTab bookings={dashboardData.bookings || []} />}
          {activeTab === "cancellations" && (
            <AdminCancellationsTab cancellations={dashboardData.cancellations || []} />
          )}
        </div>
      </div>
    </div>
  );
}

export default AdminDashboard;

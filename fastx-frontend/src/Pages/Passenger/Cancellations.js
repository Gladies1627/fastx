import React, { useEffect, useState, useContext } from "react";
import { AuthContext } from "../../context/AuthContext";
import "./Cancellations.css";

function Cancellations() {
  const [cancellations, setCancellations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const { token } = useContext(AuthContext);

  const fetchCancellations = async () => {
    const passengerId = localStorage.getItem("userId");
    if (!passengerId || !token) {
      setError("User not authenticated.");
      setLoading(false);
      return;
    }

    try {
      const res = await fetch(`https://fastx-backend-ilxf.onrender.com/api/passenger/cancellations`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!res.ok) throw new Error("Failed to fetch cancellations.");
      const data = await res.json();
      setCancellations(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCancellations();
  }, []);

  const filteredCancellations = cancellations.filter((c) => {
    const search = searchTerm.toLowerCase();
    return (
      c.bookingId.toString().includes(search) ||
      c.reason?.toLowerCase().includes(search) ||
      new Date(c.cancelledAt).toLocaleDateString("en-IN").includes(search) ||
      c.route?.origin?.toLowerCase().includes(search) ||
      c.route?.destination?.toLowerCase().includes(search)
    );
  });

  if (loading) return <p>Loading cancellations...</p>;
  if (error) return <p className="error-text">❌ {error}</p>;

  if (cancellations.length === 0) {
    return <div className="no-data-alert">No cancellations found.</div>;
  }

  return (
    <div className="bookings-container">
      <h2>🗑️ My Cancellations</h2>

      <div className="text-end mb-3">
        <input
          type="text"
          className="form-control w-50"
          placeholder="Search by route, reason, date..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          style={{ marginLeft: "auto", marginRight: "0" }}
        />
      </div>

      <table className="booking-table">
        <thead>
          <tr>
            <th>Booking ID</th>
            <th>Route</th>
            <th>Seats</th>
            <th>Travel Date</th>
            <th>Original (₹)</th>
            <th>Refund (₹)</th>
            <th>Cancelled At</th>
            <th>Reason</th>
          </tr>
        </thead>
        <tbody>
          {filteredCancellations.map((c) => (
            <tr key={c.id}>
              <td>{c.bookingId}</td>
              <td>
                {c.route
                  ? `${c.route.origin} → ${c.route.destination}`
                  : <span style={{ color: "#c00" }}>Route Unavailable</span>}
              </td>
              <td>{c.seatNumbers?.join(", ")}</td>
              <td>
                {new Date(c.travelDate).toLocaleDateString("en-IN", {
                  day: "2-digit",
                  month: "short",
                  year: "numeric",
                })}
              </td>
              <td>{c.originalAmount}</td>
              <td>{c.refundAmount}</td>
              <td className="text-muted">
                {new Date(c.cancelledAt).toLocaleString("en-IN", {
                  day: "2-digit",
                  month: "short",
                  year: "numeric",
                  hour: "2-digit",
                  minute: "2-digit",
                })}
              </td>
              <td>{c.reason}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default Cancellations;

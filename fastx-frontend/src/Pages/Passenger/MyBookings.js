import React, { useEffect, useState } from "react";
import "./MyBookings.css";

const MyBookings = () => {
  const userId = localStorage.getItem("userId");
  const rawToken = localStorage.getItem("token");
  const token = rawToken.startsWith("Bearer ") ? rawToken : `Bearer ${rawToken}`;

  const [bookings, setBookings] = useState([]);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");

 const filteredBookings = bookings.filter((booking) => {
  const searchLower = searchTerm.toLowerCase();
  const normalizedStatus = booking.status.toLowerCase() === "confirmed" ? "valid" : booking.status.toLowerCase();

  return (
    booking.id.toString().includes(searchLower) ||
    normalizedStatus.includes(searchLower) ||
    booking.seats.some(seat => seat.seatNumber.toLowerCase().includes(searchLower)) ||
    booking.route?.origin?.toLowerCase().includes(searchLower) ||
    booking.route?.destination?.toLowerCase().includes(searchLower) ||
    new Date(booking.travelDate).toLocaleDateString("en-IN").includes(searchLower)
  );
});


  const fetchBookings = async () => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/passenger/all-bookings/${userId}`,
        {
          headers: { Authorization: token },
        }
      );

      if (!response.ok) throw new Error(`Server error: ${response.status}`);

      const data = await response.json();
      setBookings(data);
    } catch (err) {
      console.error("🚨 Error fetching bookings:", err.message);
      setError("Failed to load bookings");
    }
  };

  useEffect(() => {
    fetchBookings();
  }, []);

  const handleCancel = async (bookingId) => {
    const reason = prompt("Why are you cancelling this booking?");
    if (!reason) return;

    try {
      const response = await fetch(
        `http://localhost:8080/api/passenger/cancel-booking/${bookingId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: token,
          },
          body: JSON.stringify({ reason }),
        }
      );

      if (!response.ok) throw new Error("Failed to cancel booking");

      alert("Booking cancelled successfully.");
      fetchBookings();
    } catch (err) {
      alert("❌ Error cancelling booking: " + err.message);
    }
  };

  const handleDownload = async (bookingId) => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/passenger/ticket/${bookingId}`,
        {
          headers: { Authorization: token },
        }
      );

      if (!response.ok) throw new Error("Failed to download ticket");

      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `Ticket_${bookingId}.pdf`;
      document.body.appendChild(a);
      a.click();
      a.remove();
    } catch (err) {
      alert("❌ Error downloading ticket: " + err.message);
    }
  };

  return (
    <div className="bookings-container">
      <h2>🧾 My Bookings</h2>
      {error && <p className="error-text">{error}</p>}

      {bookings.length === 0 ? (
        <div className="no-data-alert">No bookings found.</div>
      ) : (
        <>
          <div className="text-end mb-3">
            <input
              type="text"
              className="form-control w-50"
              placeholder="Search by route, seat, status, date..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              style={{ marginLeft: "auto", marginRight: "0" }}
            />
          </div>

          <table className="booking-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Route</th>
                <th>Seats</th>
                <th>Travel Date</th>
                <th>Total (₹)</th>
                <th>Actions</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {filteredBookings.map((booking) => (
                <tr key={booking.id}>
                  <td>{booking.id}</td>
                  <td>
                    {`${booking.route.origin} → ${booking.route.destination}`}
                  </td>
                  <td>
                    {booking.seats.map((seat) => seat.seatNumber).join(", ")}
                  </td>
                  <td>
                    {new Date(booking.travelDate).toLocaleDateString("en-IN", {
                      day: "2-digit",
                      month: "short",
                      year: "numeric",
                    })}
                  </td>
                  <td>{booking.totalAmount}</td>
                  <td>
                    <button
                      className="btn cancel-btn"
                      onClick={() => handleCancel(booking.id)}
                      disabled={booking.status === "CANCELLED"}
                    >
                      ❌ Cancel
                    </button>
                    <button
                      className="btn download-btn"
                      onClick={() => handleDownload(booking.id)}
                      disabled={booking.status === "CANCELLED"}
                    >
                      ⬇️ Download
                    </button>
                  </td>
                  <td>
                    {booking.status === "CANCELLED" ? (
                      <span className="text-muted fst-italic">Cancelled</span>
                    ) : (
                      <span className="text-success fw-semibold">Valid</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </>
      )}
    </div>
  );
};

export default MyBookings;

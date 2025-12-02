import React, { useEffect, useState, useContext } from "react";
import { AuthContext } from "../../context/AuthContext";
import "./OperatorBookings.css"; // make sure this path is correct

const OperatorBookings = () => {
  const { token } = useContext(AuthContext);
  const [bookings, setBookings] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");

  const BASE_URL = "https://fastx-backend-ilxf.onrender.com/api/operator";

  useEffect(() => {
    const fetchBookings = async () => {
      try {
        const res = await fetch(`${BASE_URL}/bookings`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        const data = await res.json();
        setBookings(data);
      } catch (err) {
        console.error("Error fetching bookings:", err);
      }
    };

    fetchBookings();
  }, [token]);

  const filteredBookings = bookings.filter((b) => {
    const term = searchTerm.toLowerCase();
    return (
      b.passenger?.name?.toLowerCase().includes(term) ||
      b.passenger?.email?.toLowerCase().includes(term) ||
      b.route?.busName?.toLowerCase().includes(term) ||
      b.route?.origin?.toLowerCase().includes(term) ||
      b.route?.destination?.toLowerCase().includes(term) ||
      b.seats?.some((s) => s.seatNumber.toLowerCase().includes(term)) ||
      new Date(b.bookingDate).toLocaleString("en-IN", {
        dateStyle: "medium",
        timeStyle: "short",
      }).toLowerCase().includes(term) ||
      b.payment?.paymentMode?.toLowerCase().includes(term) ||
      b.payment?.paymentStatus?.toLowerCase().includes(term) ||
      b.status?.toLowerCase().includes(term)
    );
  });

  return (
    <div className="operator-bookings-container">
      <h3 className="mb-4 text-white">All Bookings on Your Buses</h3>

      <div className="search-box mb-3 d-flex justify-content-end">
        <input
          type="text"
          className="form-control w-50"
          placeholder="🔍 Search by passenger, email, bus, seat, payment..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>

      <table className="table table-bordered table-hover table-striped">
        <thead className="table-dark">
          <tr>
            <th>#</th>
            <th>Passenger</th>
            <th>Email</th>
            <th>Bus</th>
            <th>Route</th>
            <th>Seats</th>
            <th>Booking Time</th>
            <th>Payment</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          {filteredBookings.length === 0 ? (
            <tr>
              <td colSpan="9" className="text-center text-muted">
                No bookings found.
              </td>
            </tr>
          ) : (
            filteredBookings.map((b, idx) => (
              <tr key={b.id}>
                <td>{idx + 1}</td>
                <td>{b.passenger?.name}</td>
                <td>{b.passenger?.email}</td>
                <td>{b.route?.busName}</td>
                <td>
                  {b.route?.origin} → {b.route?.destination}
                </td>
                <td>{b.seats?.map((s) => s.seatNumber).join(", ")}</td>
                <td>
                  {new Date(b.bookingDate).toLocaleString("en-IN", {
                    dateStyle: "medium",
                    timeStyle: "short",
                  })}
                </td>
                <td>
                  {b.payment
                    ? `${b.payment.paymentMode} (${b.payment.paymentStatus})`
                    : "N/A"}
                </td>
                <td>
                  {b.status === "CANCELLED" ? (
                    <span className="badge bg-danger">Cancelled</span>
                  ) : (
                    <span className="badge bg-success">Confirmed</span>
                  )}
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
};

export default OperatorBookings;

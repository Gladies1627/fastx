import React, { useState } from "react";

function AdminBookingsTab({ bookings }) {
  const safeBookings = Array.isArray(bookings) ? bookings : [];
  const [searchTerm, setSearchTerm] = useState("");

  const filteredBookings = safeBookings.filter((b) => {
    const term = searchTerm.toLowerCase();
    return (
      b.passengerName?.toLowerCase().includes(term) ||
      b.passengerEmail?.toLowerCase().includes(term) ||
      b.route?.origin?.toLowerCase().includes(term) ||
      b.route?.destination?.toLowerCase().includes(term) ||
      b.seatNumbers?.some((s) => s.toLowerCase().includes(term)) ||
      b.status?.toLowerCase().includes(term) ||
      new Date(b.travelDate).toLocaleString("en-IN").toLowerCase().includes(term)
    );
  });

  return (
    <div>
      <h4>All Bookings</h4>

      <div className="mb-3 d-flex justify-content-end">
        <input
          type="text"
          className="form-control w-50"
          placeholder="🔍 Search by passenger, route, seat, status..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>

      {filteredBookings.length === 0 ? (
        <p className="text-muted">No bookings found.</p>
      ) : (
        <table className="table table-bordered table-striped">
          <thead className="table-dark">
            <tr>
              <th>ID</th>
              <th>Passenger</th>
              <th>Route</th>
              <th>Seat Numbers</th>
              <th>Travel Date</th>
              <th>Status</th>
              <th>Amount</th>
            </tr>
          </thead>
          <tbody>
            {filteredBookings.map((b) => (
              <tr key={b.id}>
                <td>{b.id}</td>
                <td>
                  {b.passengerName
                    ? `${b.passengerName} (${b.passengerEmail})`
                    : "N/A"}
                </td>
                <td>
                  {b.route
                    ? `${b.route.origin} → ${b.route.destination}`
                    : "N/A"}
                </td>
                <td>
                  {b.seatNumbers && b.seatNumbers.length > 0
                    ? b.seatNumbers.join(", ")
                    : "N/A"}
                </td>
                <td>
                  {new Date(b.travelDate).toLocaleString("en-IN", {
                    dateStyle: "medium",
                    timeStyle: "short",
                  })}
                </td>
                <td>{b.status}</td>
                <td>₹{b.totalAmount}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default AdminBookingsTab;

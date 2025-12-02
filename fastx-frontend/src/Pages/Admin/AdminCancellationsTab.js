import React, { useState } from "react";

function AdminCancellationsTab({ cancellations }) {
  const safeList = Array.isArray(cancellations) ? cancellations : [];
  const [searchTerm, setSearchTerm] = useState("");

  const filteredList = safeList.filter((c) => {
    const term = searchTerm.toLowerCase();
    return (
      c.bookingId?.toString().includes(term) ||
      c.travelDate?.toLowerCase().includes(term) ||
      new Date(c.cancelledAt).toLocaleString("en-IN").toLowerCase().includes(term) ||
      c.refundAmount?.toString().includes(term)
    );
  });

  return (
    <div>
      <h4>All Cancellations</h4>

      <div className="mb-3 d-flex justify-content-end">
        <input
          type="text"
          className="form-control w-50"
          placeholder="🔍 Search by Booking ID, Date, Refund..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>

      {filteredList.length === 0 ? (
        <p className="text-muted">No cancellations found.</p>
      ) : (
        <table className="table table-bordered table-striped">
          <thead className="table-dark">
            <tr>
              <th>ID</th>
              <th>Booking ID</th>
              <th>Travel Date</th>
              <th>Cancelled At</th>
              <th>Refunded Amount</th>
            </tr>
          </thead>
          <tbody>
            {filteredList.map((c) => (
              <tr key={c.id}>
                <td>{c.id}</td>
                <td>{c.bookingId}</td>
                <td>{c.travelDate}</td>
                <td>
                  {new Date(c.cancelledAt).toLocaleString("en-IN", {
                    day: "2-digit",
                    month: "short",
                    year: "numeric",
                    hour: "numeric",
                    minute: "2-digit",
                    hour12: true,
                  })}
                </td>
                <td>₹{c.refundAmount}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default AdminCancellationsTab;

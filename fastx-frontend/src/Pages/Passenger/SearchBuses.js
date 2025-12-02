import React, { useState, useContext } from "react";
import { AuthContext } from "../../context/AuthContext";
import "./SearchBuses.css";

function SearchBuses() {
  const [origin, setOrigin] = useState("");
  const [destination, setDestination] = useState("");
  const [routes, setRoutes] = useState([]);
  const [searched, setSearched] = useState(false);
  const [filterTerm, setFilterTerm] = useState("");
  const { token } = useContext(AuthContext);

  const handleSearch = async (e) => {
    e.preventDefault();
    setSearched(true);

    try {
      const res = await fetch(
        `https://fastx-backend-ilxf.onrender.com/api/passenger/search?origin=${origin}&destination=${destination}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      if (res.ok) {
        const data = await res.json();
        setRoutes(data);
      } else {
        alert("Failed to fetch buses.");
        setRoutes([]);
      }
    } catch (err) {
      alert("Error: " + err.message);
      setRoutes([]);
    }
  };

  const filteredRoutes = routes
    .filter((route) => route.status !== "CANCELLED")
    .filter((route) => {
      const term = filterTerm.toLowerCase();
      return (
        route.busName?.toLowerCase().includes(term) ||
        route.busNumber?.toLowerCase().includes(term) ||
        route.busType?.toLowerCase().includes(term) ||
        route.origin?.toLowerCase().includes(term) ||
        route.destination?.toLowerCase().includes(term) ||
        route.farePerSeat?.toString().includes(term) ||
        route.totalSeats?.toString().includes(term) ||
        route.amenities?.toLowerCase().includes(term) ||
        route.status?.toLowerCase().includes(term) ||
        new Date(route.departureTime)
          .toLocaleString("en-IN", {
            dateStyle: "medium",
            timeStyle: "short",
          })
          .toLowerCase()
          .includes(term) ||
        new Date(route.arrivalTime)
          .toLocaleString("en-IN", {
            dateStyle: "medium",
            timeStyle: "short",
          })
          .toLowerCase()
          .includes(term)
      );
    });

  return (
    <div className="search-buses-container">
      {/* Search Form */}
      <form onSubmit={handleSearch} className="row g-3 mb-4 bus-search-form">
        <div className="col">
          <input
            type="text"
            className="form-control"
            placeholder="From"
            value={origin}
            onChange={(e) => setOrigin(e.target.value)}
            required
          />
        </div>
        <div className="col">
          <input
            type="text"
            className="form-control"
            placeholder="To"
            value={destination}
            onChange={(e) => setDestination(e.target.value)}
            required
          />
        </div>
        <div className="col">
          <button type="submit" className="btn btn-primary w-100">
            Search
          </button>
        </div>
      </form>

      {searched && routes.length === 0 && (
        <div className="no-routes-message">
          No buses available for this route.
        </div>
      )}

      {routes.length > 0 && (
        <>
          <div className="mb-3 d-flex justify-content-end">
            <input
              type="text"
              className="form-control w-50 route-filter-input"
              placeholder="🔍 Filter by any field..."
              value={filterTerm}
              onChange={(e) => setFilterTerm(e.target.value)}
            />
          </div>
          <div className="table-responsive">
            <table className="table table-bordered bus-search-table">
              <thead className="table-dark text-center">
                <tr>
                  <th>Bus</th>
                  <th>Number</th>
                  <th>Type</th>
                  <th>From</th>
                  <th>To</th>
                  <th>Departure</th>
                  <th>Arrival</th>
                  <th>Fare</th>
                  <th>Seats</th>
                  <th>Amenities</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {filteredRoutes.map((route) => (
                  <tr key={route.id}>
                    <td>{route.busName}</td>
                    <td>{route.busNumber}</td>
                    <td>{route.busType.replace(/_/g, " ")}</td>
                    <td>{route.origin}</td>
                    <td>{route.destination}</td>
                    <td>
                      {new Date(route.departureTime).toLocaleString("en-IN", {
                        dateStyle: "medium",
                        timeStyle: "short",
                      })}
                    </td>
                    <td>
                      {new Date(route.arrivalTime).toLocaleString("en-IN", {
                        dateStyle: "medium",
                        timeStyle: "short",
                      })}
                    </td>
                    <td>₹{route.farePerSeat}</td>
                    <td>{route.totalSeats}</td>
                    <td>{route.amenities || "—"}</td>
                    <td>
                      {route.status === "ACTIVE" ? (
                        <span className="text-success fw-semibold">Active</span>
                      ) : (
                        <span className="text-danger">Cancelled</span>
                      )}
                    </td>
                    <td>
                      <a
                        href={`/select-seat/${route.id}`}
                        className="btn btn-success btn-sm"
                      >
                        Book
                      </a>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}
    </div>
  );
}

export default SearchBuses;

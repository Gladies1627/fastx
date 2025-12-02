import React, { useState, useContext } from "react";
import axios from "axios";
import { AuthContext } from "../../context/AuthContext";

function AdminRoutesTab({ busRoutes }) {
  const [routes, setRoutes] = useState(busRoutes || []);
  const [editingRoute, setEditingRoute] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const { token } = useContext(AuthContext);

  const BASE_URL = "https://fastx-backend-ilxf.onrender.com/api/admin";

  const fetchRoutes = async () => {
    try {
      const res = await axios.get(`${BASE_URL}/dashboard`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setRoutes(res.data.busRoutes);
    } catch (err) {
      console.error("Failed to refresh routes", err);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm("Are you sure you want to delete this route?")) {
      try {
        await axios.delete(`${BASE_URL}/route/${id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        await fetchRoutes();
      } catch (err) {
        alert("Error deleting route: " + (err.response?.data || err.message));
      }
    }
  };

  const handleEditSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.put(`${BASE_URL}/route`, editingRoute, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const updated = routes.map((r) =>
        r.id === editingRoute.id ? editingRoute : r
      );
      setRoutes(updated);
      setEditingRoute(null);
    } catch (err) {
      alert("Failed to update route: " + (err.response?.data || err.message));
    }
  };

  const filteredRoutes = routes.filter((route) => {
    const term = searchTerm.toLowerCase();
    return (
      route.busName?.toLowerCase().includes(term) ||
      route.busType?.toLowerCase().includes(term) ||
      route.origin?.toLowerCase().includes(term) ||
      route.destination?.toLowerCase().includes(term) ||
      route.status?.toLowerCase().includes(term)
    );
  });

  return (
    <div>
      <h4>All Bus Routes</h4>

      <div className="mb-3 d-flex justify-content-end">
        <input
          type="text"
          className="form-control w-50"
          placeholder="🔍 Search routes..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>

      <table className="table table-bordered table-striped">
        <thead className="table-dark">
          <tr>
            <th>ID</th>
            <th>Bus Name</th>
            <th>Bus Type</th>
            <th>From → To</th>
            <th>Departure</th>
            <th>Arrival</th>
            <th>Fare</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {filteredRoutes.map((route) => (
            <tr
              key={route.id}
              className={route.status === "CANCELLED" ? "table-secondary text-muted" : ""}
            >
              <td>{route.id}</td>
              <td>{route.busName}</td>
              <td>{route.busType}</td>
              <td>{route.origin} → {route.destination}</td>
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
              <td>
                {route.status === "CANCELLED" ? (
                  <span className="badge bg-danger">Cancelled</span>
                ) : (
                  <>
                    <button
                      className="btn btn-sm btn-primary me-2"
                      onClick={() => setEditingRoute({ ...route })}
                    >
                      Edit
                    </button>
                    <button
                      className="btn btn-sm btn-danger"
                      onClick={() => handleDelete(route.id)}
                    >
                      Delete
                    </button>
                  </>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Edit Form */}
      {editingRoute && (
        <form className="mt-4 border p-3" onSubmit={handleEditSubmit}>
          <h5>Edit Route #{editingRoute.id}</h5>
          <input
            type="text"
            className="form-control mb-2"
            placeholder="Bus Name"
            value={editingRoute.busName}
            onChange={(e) =>
              setEditingRoute({ ...editingRoute, busName: e.target.value })
            }
          />
          <input
            type="text"
            className="form-control mb-2"
            placeholder="Bus Type"
            value={editingRoute.busType}
            onChange={(e) =>
              setEditingRoute({ ...editingRoute, busType: e.target.value })
            }
          />
          <input
            type="text"
            className="form-control mb-2"
            placeholder="Origin"
            value={editingRoute.origin}
            onChange={(e) =>
              setEditingRoute({ ...editingRoute, origin: e.target.value })
            }
          />
          <input
            type="text"
            className="form-control mb-2"
            placeholder="Destination"
            value={editingRoute.destination}
            onChange={(e) =>
              setEditingRoute({ ...editingRoute, destination: e.target.value })
            }
          />
          <input
            type="text"
            className="form-control mb-2"
            placeholder="Departure Time"
            value={editingRoute.departureTime}
            onChange={(e) =>
              setEditingRoute({ ...editingRoute, departureTime: e.target.value })
            }
          />
          <input
            type="text"
            className="form-control mb-2"
            placeholder="Arrival Time"
            value={editingRoute.arrivalTime}
            onChange={(e) =>
              setEditingRoute({ ...editingRoute, arrivalTime: e.target.value })
            }
          />
          <input
            type="number"
            className="form-control mb-2"
            placeholder="Fare per Seat"
            value={editingRoute.farePerSeat}
            onChange={(e) =>
              setEditingRoute({
                ...editingRoute,
                farePerSeat: parseFloat(e.target.value),
              })
            }
          />
          <div className="d-flex gap-2">
            <button className="btn btn-success">Save</button>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setEditingRoute(null)}
            >
              Cancel
            </button>
          </div>
        </form>
      )}
    </div>
  );
}

export default AdminRoutesTab;

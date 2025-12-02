import React, { useState, useEffect, useContext, useCallback } from "react";
import { AuthContext } from "../../context/AuthContext";
import { useNavigate } from "react-router-dom";
import "./OperatorRoutesTab.css";

const OperatorRoutesTab = () => {
  const navigate = useNavigate();
  const { token, userId: operatorId } = useContext(AuthContext);

  const [routes, setRoutes] = useState([]);
  const [editMode, setEditMode] = useState(false);
  const [editingRouteId, setEditingRouteId] = useState(null);
  const [activeTab, setActiveTab] = useState("add");
  const [searchTerm, setSearchTerm] = useState("");

  const [form, setForm] = useState({
    busName: "",
    busNumber: "",
    origin: "",
    destination: "",
    departureTime: "",
    arrivalTime: "",
    totalSeats: "",
    farePerSeat: "",
    busType: "",
    amenities: "",
  });

  const fetchRoutes = useCallback(async () => {
    try {
      const response = await fetch("http://localhost:8080/api/operator/routes", {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!response.ok) throw new Error("Failed to fetch routes");

      const text = await response.text();
      const data = text ? JSON.parse(text) : [];
      setRoutes(data);
    } catch (err) {
      console.error("❌ Failed to fetch routes:", err);
      setRoutes([]);
    }
  }, [token]);

  useEffect(() => {
    if (!token || !operatorId) {
      navigate("/login");
    } else {
      fetchRoutes();
    }
  }, [token, operatorId, navigate, fetchRoutes]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const url = editMode
      ? `http://localhost:8080/api/operator/update/${editingRouteId}`
      : `http://localhost:8080/api/operator/add-route`;

    const method = editMode ? "PUT" : "POST";

    try {
      const res = await fetch(url, {
        method,
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(form),
      });

      const resultText = await res.text();
      let result = {};
      try {
        result = JSON.parse(resultText);
      } catch {}

      if (res.ok) {
        alert(editMode ? "Route updated successfully!" : "Route added successfully!");
        setForm({
          busName: "",
          busNumber: "",
          origin: "",
          destination: "",
          departureTime: "",
          arrivalTime: "",
          totalSeats: "",
          farePerSeat: "",
          busType: "",
          amenities: "",
        });
        setEditMode(false);
        setEditingRouteId(null);
        fetchRoutes();
      } else {
        alert("Failed to save route: " + (result.message || "Unknown error"));
      }
    } catch (err) {
      alert("Error: " + err.message);
    }
  };

  const handleDelete = async (routeId) => {
    if (!window.confirm("Are you sure you want to delete this route?")) return;
    try {
      await fetch(`http://localhost:8080/api/operator/delete/${routeId}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });
      fetchRoutes();
    } catch (err) {
      console.error("Failed to delete route:", err);
    }
  };

  const handleEdit = (route) => {
    setForm({
      busName: route.busName,
      busNumber: route.busNumber,
      origin: route.origin,
      destination: route.destination,
      departureTime: route.departureTime?.slice(0, 16),
      arrivalTime: route.arrivalTime?.slice(0, 16),
      totalSeats: route.totalSeats,
      farePerSeat: route.farePerSeat,
      busType: route.busType,
      amenities: route.amenities,
    });
    setEditMode(true);
    setEditingRouteId(route.id);
    window.scrollTo(0, 0);
  };

  const filteredRoutes = routes.filter((r) => {
    const term = searchTerm.toLowerCase();
    return (
      r.busNumber.toLowerCase().includes(term) ||
      r.origin.toLowerCase().includes(term) ||
      r.destination.toLowerCase().includes(term) ||
      r.busType.toLowerCase().includes(term) ||
      r.status.toLowerCase().includes(term) ||
      r.totalSeats.toString().includes(term) ||
      r.farePerSeat.toString().includes(term) ||
      new Date(r.departureTime)
        .toLocaleString("en-IN", { dateStyle: "medium", timeStyle: "short" })
        .toLowerCase()
        .includes(term) ||
      new Date(r.arrivalTime)
        .toLocaleString("en-IN", { dateStyle: "medium", timeStyle: "short" })
        .toLowerCase()
        .includes(term)
    );
  });

  return (
    <div className="operator-routes-container">
      {/* Tabs */}
      <div className="routes-tab-buttons">
        <button
          className={`btn me-2 ${editMode || activeTab === "add" ? "btn-primary" : "btn-outline-primary"}`}
          onClick={() => {
            setActiveTab("add");
            setEditMode(false);
            setEditingRouteId(null);
            setForm({
              busName: "",
              busNumber: "",
              origin: "",
              destination: "",
              departureTime: "",
              arrivalTime: "",
              totalSeats: "",
              farePerSeat: "",
              busType: "",
              amenities: "",
            });
          }}
        >
          ➕ Add Route
        </button>
        <button
          className={`btn ${activeTab === "manage" && !editMode ? "btn-primary" : "btn-outline-primary"}`}
          onClick={() => {
            setActiveTab("manage");
            setEditMode(false);
          }}
        >
          ✏️ Edit / Delete Routes
        </button>
      </div>

      {/* Form */}
      {(activeTab === "add" || editMode) && (
        <div className="row justify-content-center">
          <form onSubmit={handleSubmit} className="row g-3 col-md-10 col-lg-8">
            {[{ name: "busName", label: "Bus Name" },
              { name: "busNumber", label: "Bus Number" },
              { name: "origin", label: "Origin" },
              { name: "destination", label: "Destination" },
              { name: "departureTime", label: "Departure Time", type: "datetime-local" },
              { name: "arrivalTime", label: "Arrival Time", type: "datetime-local" },
              { name: "totalSeats", label: "Total Seats", type: "number" },
              { name: "farePerSeat", label: "Fare Per Seat", type: "number" },
              { name: "amenities", label: "Amenities (comma-separated)" }
            ].map(({ name, label, type = "text" }) => (
              <div className="col-md-6" key={name}>
                <label>{label}</label>
                <input
                  name={name}
                  value={form[name]}
                  onChange={handleChange}
                  type={type}
                  className="form-control"
                  required
                />
              </div>
            ))}

            <div className="col-md-6">
              <label>Bus Type</label>
              <select
                name="busType"
                value={form.busType}
                onChange={handleChange}
                className="form-control"
                required
              >
                <option value="">-- Select --</option>
                <option value="SEATER_AC">Seater AC</option>
                <option value="SEATER_NON_AC">Seater Non-AC</option>
                <option value="SLEEPER_AC">Sleeper AC</option>
                <option value="SLEEPER_NON_AC">Sleeper Non-AC</option>
              </select>
            </div>

            <div className="col-12">
              <button className="btn btn-primary w-100">
                {editMode ? "Update Route" : "Save Route"}
              </button>
            </div>

            {editMode && (
              <div className="col-12">
                <button
                  type="button"
                  className="btn btn-secondary w-100 mt-2"
                  onClick={() => {
                    setEditMode(false);
                    setEditingRouteId(null);
                    setForm({
                      busName: "",
                      busNumber: "",
                      origin: "",
                      destination: "",
                      departureTime: "",
                      arrivalTime: "",
                      totalSeats: "",
                      farePerSeat: "",
                      busType: "",
                      amenities: "",
                    });
                  }}
                >
                  Cancel Edit
                </button>
              </div>
            )}
          </form>
        </div>
      )}

      {/* Table + Filter */}
      {activeTab === "manage" && !editMode && (
        <>
          <h4 className="text-white mb-3">All Routes</h4>

          <div className="mb-3 d-flex justify-content-end">
            <input
              type="text"
              className="form-control w-50"
              placeholder="🔍 Search by bus number, location, fare, date..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>

          <div className="table-responsive">
            <table className="table routes-table table-sm">
              <thead>
                <tr>
                  <th>#</th>
                  <th>Bus Name</th>
                  <th>Bus Number</th>
                  <th>Origin</th>
                  <th>Destination</th>
                  <th>Departure</th>
                  <th>Arrival</th>
                  <th>Fare</th>
                  <th>Seats</th>
                  <th>Type</th>
                  <th>Amenities</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredRoutes.map((r, idx) => (
                  <tr key={r.id}>
                    <td>{idx + 1}</td>
                    <td className="text-white">{r.busName}</td>
                    <td>{r.busNumber}</td>
                    <td>{r.origin}</td>
                    <td>{r.destination}</td>
                    <td>
                      {new Date(r.departureTime).toLocaleString("en-IN", {
                        dateStyle: "medium",
                        timeStyle: "short",
                      })}
                    </td>
                    <td>
                      {new Date(r.arrivalTime).toLocaleString("en-IN", {
                        dateStyle: "medium",
                        timeStyle: "short",
                      })}
                    </td>
                    <td>₹{r.farePerSeat}</td>
                    <td>{r.totalSeats}</td>
                    <td>{r.busType}</td>
                    <td>{r.amenities}</td>
                    <td>
                      {r.status === "CANCELLED" ? (
                        <span className="badge bg-danger">Cancelled</span>
                      ) : (
                        <span className="badge bg-success">Active</span>
                      )}
                    </td>
                    <td>
                      {r.status === "CANCELLED" ? (
                        <span className="text-muted">N/A</span>
                      ) : (
                        <>
                          <button
                            className="btn btn-warning btn-sm me-2"
                            onClick={() => handleEdit(r)}
                          >
                            Edit
                          </button>
                          <button
                            className="btn btn-danger btn-sm"
                            onClick={() => handleDelete(r.id)}
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
          </div>
        </>
      )}
    </div>
  );
};

export default OperatorRoutesTab;

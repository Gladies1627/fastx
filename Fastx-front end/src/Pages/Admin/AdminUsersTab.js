import React, { useContext, useState } from "react";
import { AuthContext } from "../../context/AuthContext";

function AdminUsersTab({ users, fetchUsers }) {
  const safeUsers = Array.isArray(users) ? users : [];
  const { token } = useContext(AuthContext);
  const [searchTerm, setSearchTerm] = useState("");

  const BASE_URL = "https://fastx-backend-ilxf.onrender.com/api/admin";

  const handleDeleteUser = async (userId, role) => {
    if (role === "ADMIN") {
      alert("❌ Cannot delete Admin users.");
      return;
    }

    const confirmDelete = window.confirm("Are you sure you want to delete this user?");
    if (!confirmDelete) return;

    try {
      const response = await fetch(`${BASE_URL}/user/${userId}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`
        },
      });

      if (!response.ok) {
        const errText = await response.text();
        throw new Error(errText || "Failed to delete user");
      }

      alert("✅ User deleted successfully.");
      fetchUsers(); // 🔄 Refresh after deletion
    } catch (err) {
      alert("❌ Error deleting user: " + err.message);
    }
  };

  const filteredUsers = safeUsers.filter((u) => {
    const term = searchTerm.toLowerCase();
    return (
      u.name?.toLowerCase().includes(term) ||
      u.email?.toLowerCase().includes(term) ||
      u.contactNumber?.toLowerCase().includes(term) ||
      u.gender?.toLowerCase().includes(term) ||
      u.role?.toLowerCase().includes(term) ||
      u.status?.toLowerCase().includes(term)
    );
  });

  return (
    <div>
      <h4>All Users</h4>

      <div className="mb-3 d-flex justify-content-end">
        <input
          type="text"
          className="form-control w-50"
          placeholder="🔍 Search users..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>

      {filteredUsers.length === 0 ? (
        <p className="text-muted">No users found.</p>
      ) : (
        <table className="table table-bordered table-striped">
          <thead className="table-dark">
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Contact</th>
              <th>Gender</th>
              <th>Role</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredUsers.map((u) => (
              <tr
                key={u.id}
                className={u.status === "DELETED" ? "table-secondary text-muted" : ""}
              >
                <td>{u.id}</td>
                <td style={u.status === "DELETED" ? { textDecoration: "line-through" } : {}}>
                  {u.name || "N/A"}
                </td>
                <td>{u.email || "N/A"}</td>
                <td>{u.contactNumber || "N/A"}</td>
                <td>{u.gender || "N/A"}</td>
                <td>{u.role || "N/A"}</td>
                <td>
                  {u.status === "DELETED" ? (
                    <span className="badge bg-danger">Deleted</span>
                  ) : (
                    <span className="badge bg-success">Active</span>
                  )}
                </td>
                <td>
                  <button
                    className="btn btn-sm btn-danger"
                    onClick={() => handleDeleteUser(u.id, u.role)}
                    disabled={u.role === "ADMIN" || u.status === "DELETED"}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default AdminUsersTab;

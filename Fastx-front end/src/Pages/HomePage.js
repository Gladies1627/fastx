import React, { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import "../Pages/HomePage.css";
import "../Pages/Layout.css";

function HomePage() {
  const [formType, setFormType] = useState("register");
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();

  const [registerData, setRegisterData] = useState({
    name: "",
    email: "",
    password: "",
    contactNumber: "",
    gender: "",
    address: "",
    role: "",
  });

  const [loginData, setLoginData] = useState({
    name: "",
    password: "",
  });

  const [loading, setLoading] = useState(false);

  const handleRegisterChange = (e) => {
    const { name, value } = e.target;
    setRegisterData((prev) => ({ ...prev, [name]: value }));
  };

  const handleLoginChange = (e) => {
    const { name, value } = e.target;
    setLoginData((prev) => ({ ...prev, [name]: value }));
  };

  const handleRegisterSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const response = await fetch(
        "https://fastx-backend-ilxf.onrender.com/auth/register",
        {
          method: "POST",
          mode: "cors",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(registerData),
        }
      );

      const responseBody = await response.text();
      if (response.ok) {
        alert("✅ Registration successful!");
        setFormType("login");
      } else {
        alert(responseBody);
      }
    } catch (err) {
      alert("❗ Error: " + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleLoginSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const response = await fetch(
        "https://fastx-backend-ilxf.onrender.com/auth/login",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(loginData),
        }
      );

      if (!response.ok) throw new Error("Login failed");

      const data = await response.json();
      if (data.status === "DELETED") {
        alert("❌ Your account has been deleted by the admin.");
        return;
      }

      login(data.role, data.token, data.id, data.name);

      switch (data.role) {
        case "ADMIN":
          navigate("/admin");
          break;
        case "PASSENGER":
          navigate("/passenger");
          break;
        case "OPERATOR":
          navigate("/operator");
          break;
        default:
          alert("🚨 Unknown role! Can't navigate.");
      }
    } catch (err) {
      alert(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="home-page-bg">
      <div className="home-wrapper">
        <div className="quote-box text-center">
          <h1 className="fastx-quote">
            "Your seat, your journey , your vibe  
            <h2> — ride with FastX"</h2>
          </h1>
        </div>

        <div className="home-container">
          <div className="home-card">
            <div className="text-center mb-3">
              <button
                className={`btn btn-outline-purple btn-toggle ${
                  formType === "register" ? "active" : ""
                }`}
                onClick={() => setFormType("register")}
              >
                Register
              </button>

              <button
                className={`btn btn-outline-purple btn-toggle ${
                  formType === "login" ? "active" : ""
                }`}
                onClick={() => setFormType("login")}
              >
                Login
              </button>
            </div>

            {formType === "register" ? (
              <form onSubmit={handleRegisterSubmit}>
                <h4 className="mb-3">Registration</h4>
                <input
                  name="name"
                  className="form-control mb-2"
                  placeholder="Name"
                  onChange={handleRegisterChange}
                  required
                />
                <input
                  name="email"
                  type="email"
                  className="form-control mb-2"
                  placeholder="Email"
                  onChange={handleRegisterChange}
                  required
                />
                <input
                  name="password"
                  type="password"
                  className="form-control mb-2"
                  placeholder="Password"
                  onChange={handleRegisterChange}
                  required
                />
                <input
                  name="contactNumber"
                  className="form-control mb-2"
                  placeholder="Contact Number"
                  onChange={handleRegisterChange}
                />

                <div className="form-group">
                  <label className="form-label">Gender:</label>
                  <br />
                  {["Male", "Female", "Other"].map((gender) => (
                    <div
                      key={gender}
                      className="form-check form-check-inline mb-2"
                    >
                      <input
                        className="form-check-input"
                        type="radio"
                        name="gender"
                        value={gender}
                        onChange={handleRegisterChange}
                        required
                      />
                      <label className="form-check-label">{gender}</label>
                    </div>
                  ))}
                </div>

                <input
                  name="address"
                  className="form-control mb-2"
                  placeholder="Address"
                  onChange={handleRegisterChange}
                />
                <select
                  name="role"
                  className="form-control mb-3"
                  onChange={handleRegisterChange}
                  required
                >
                  <option value="">Select Role</option>
                  <option value="PASSENGER">Passenger</option>
                  <option value="OPERATOR">Operator</option>
                </select>
                <button
                  type="submit"
                  className="btn-outline-purple w-100"
                  disabled={loading}
                >
                  {loading ? "Registering..." : "Register"}
                </button>
              </form>
            ) : (
              <form onSubmit={handleLoginSubmit}>
                <h4 className="mb-3">Login</h4>
                <input
                  name="name"
                  type="text"
                  className="form-control mb-2"
                  placeholder="Username"
                  onChange={handleLoginChange}
                  required
                />
                <input
                  name="password"
                  type="password"
                  className="form-control mb-3"
                  placeholder="Password"
                  onChange={handleLoginChange}
                  required
                />
                <button
                  type="submit"
                  className="btn-outline-purple w-100"
                  disabled={loading}
                >
                  {loading ? "Logging in..." : "Login"}
                </button>
              </form>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default HomePage;

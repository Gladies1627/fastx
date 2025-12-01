import React, { createContext, useState, useEffect } from "react";

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [role, setRole] = useState(localStorage.getItem("role"));
  const [token, setToken] = useState(localStorage.getItem("token"));
  const [userId, setUserId] = useState(localStorage.getItem("userId"));
  const [userName, setUserName] = useState(localStorage.getItem("username"));

const login = (role, token, userId, name) => {
  setRole(role);
  setToken(token);
  setUserId(userId);
  setUserName(name);
  localStorage.setItem("role", role);
  localStorage.setItem("token", token);
  localStorage.setItem("userId", userId);
  localStorage.setItem("username", name); // 👈 here
};

  const logout = () => {
    setRole(null);
    setToken(null);
    setUserId(null);
    setUserName(null);
    localStorage.clear();
  };

  return (
    <AuthContext.Provider value={{ role, token, userId,login, logout,user: {
    name: userName
  } }}>
      {children}
    </AuthContext.Provider>
  );
};

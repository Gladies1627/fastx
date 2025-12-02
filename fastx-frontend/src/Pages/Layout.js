import React from "react";
import { useLocation } from "react-router-dom";
import "../Pages/Layout.css";

function Layout({ children }) {
  const location = useLocation();
  const showLogoAndTitle = true;


  const hideFooterRoutes = [
    "/select-seat",
    "/confirm-booking",
    "/booking-confirmed",
    "/cancel-booking",
    "/admin" 
  ];

  const shouldShowFooter = !hideFooterRoutes.some((route) =>
    location.pathname.startsWith(route)
  );

  return (
    <div className="layout-bg-wrapper">
      <div className="page-wrapper">
        {showLogoAndTitle && (
          <div className="layout-header">
            <div className="logo"></div>
            <h1 className="app-title">FastX</h1>
          </div>
        )}
        <main className="main-content">{children}</main>
      </div>

      {shouldShowFooter && (
        <footer className="home-footer">
          <div className="footer-container">
            <div className="footer-section">
              <h5>Help & Support</h5>
              <ul>
                <li><a href="#">FAQs</a></li>
                <li><a href="#">Booking Guide</a></li>
                <li><a href="#">Cancellation Policy</a></li>
              </ul>
            </div>
            <div className="footer-section">
              <h5>Contact Us</h5>
              <p>Email: <a href="mailto:support@fastx.com">support@fastx.com</a></p>
              <p>Phone: +91-98765-43210</p>
            </div>
          </div>
        </footer>
      )}
    </div>
  );
}

export default Layout;

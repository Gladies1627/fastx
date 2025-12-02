// src/Pages/Passenger/SelectSeats.js
import React, { useEffect, useState, useContext } from "react";
import { useParams, useNavigate } from "react-router-dom";
import "../../App.css";
import { AuthContext } from "../../context/AuthContext";
import './SeatSelector.css'; 

function SelectSeats() {
  const { routeId } = useParams();
  const [seats, setSeats] = useState([]);
  const [selectedSeatIds, setSelectedSeatIds] = useState([]);
  const [paymentMode, setPaymentMode] = useState("");
  const navigate = useNavigate();
  const { token, userId } = useContext(AuthContext);

  useEffect(() => {
    const fetchSeats = async () => {
      try {
        const res = await fetch(`https://fastx-backend-ilxf.onrender.com/api/passenger/seats/${routeId}`, {
          headers: {
            Authorization: `Bearer ${token} `,
          },
        });
        const data = await res.json();
        setSeats(data);
      } catch (err) {
        console.error("Failed to fetch seats →", err);
        alert("Failed to fetch seats");
      }
    };

    fetchSeats();
  }, [routeId, token]);

  const toggleSeat = (seatId) => {
    if (selectedSeatIds.includes(seatId)) {
      setSelectedSeatIds(selectedSeatIds.filter((id) => id !== seatId));
    } else {
      setSelectedSeatIds([...selectedSeatIds, seatId]);
    }
  };

  const handleBooking = async () => {
    if (selectedSeatIds.length === 0) {
      alert("Please select at least one seat");
      return;
    }

    if (!paymentMode) {
      alert("Please select a payment method");
      return;
    }

    try {
      const queryParams = new URLSearchParams({
        passengerId: userId,
        routeId,
        paymentMode,
      });
      selectedSeatIds.forEach((id) => queryParams.append("seatIds", id));

      const res = await fetch(`https://fastx-backend-ilxf.onrender.com/api/passenger/book?${queryParams.toString()}`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (res.ok) {
        const booking = await res.json();
        alert("Booking successful!");
        navigate("/booking-confirmed", { state: { booking } }); 
      } else {
        const error = await res.text();
        alert("Booking failed: " + error);
      }
    } catch (err) {
      alert("Booking error: " + err.message);
    }
  };

  return (
    <div className="select-seat-page">
      <h2 className="seat-title">Select Your Seats</h2>

      <div className="seat-legend-container">
        <div className="legend-item">
          <span className="legend-box legend-available" /> Available
        </div>
        <div className="legend-item">
          <span className="legend-box legend-booked-male" /> Booked (Male)
        </div>
        <div className="legend-item">
          <span className="legend-box legend-booked-female" /> Booked (Female)
        </div>
        <div className="legend-item">
          <span className="legend-box legend-selected" /> Your Selection
        </div>
      </div>

      <div className="seat-grid mt-4">
        {Array.from({ length: Math.ceil(seats.length / 4) }, (_, rowIdx) => {
          const rowSeats = seats.slice(rowIdx * 4, rowIdx * 4 + 4);

          const leftSide = rowSeats.slice(0, 2);
          const rightSide = rowSeats.slice(2, 4);

          return (
            <div key={rowIdx} className="seat-row">
              <div className="seat-side">
                {leftSide.map((seat) => {
                  const isBooked = seat.booked;
                  const isSelected = selectedSeatIds.includes(seat.id);
                  let seatClass = "seat";
                  if (isBooked) {
                    seatClass += " booked";
                    if (seat.bookedByGender === "FEMALE") seatClass += " female-seat";
                    else if (seat.bookedByGender === "MALE") seatClass += " male-seat";
                  } else if (isSelected) {
                    seatClass += " selected";
                  }
                  return (
                    <div
                      key={seat.id}
                      className={seatClass}
                      onClick={() => !isBooked && toggleSeat(seat.id)}
                      title={
                        isBooked
                          ? `Booked (${seat.bookedByGender})`
                          : "Click to select"
                      }
                    >
                      {seat.seatNumber}
                    </div>
                  );
                })}
              </div>

              <div className="aisle-space" />

              <div className="seat-side">
                {rightSide.map((seat) => {
                  const isBooked = seat.booked;
                  const isSelected = selectedSeatIds.includes(seat.id);
                  let seatClass = "seat";
                  if (isBooked) {
                    seatClass += " booked";
                    if (seat.bookedByGender === "FEMALE") seatClass += " female-seat";
                    else if (seat.bookedByGender === "MALE") seatClass += " male-seat";
                  } else if (isSelected) {
                    seatClass += " selected";
                  }
                  return (
                    <div
                      key={seat.id}
                      className={seatClass}
                      onClick={() => !isBooked && toggleSeat(seat.id)}
                      title={
                        isBooked
                          ? `Booked (${seat.bookedByGender})`
                          : "Click to select"
                      }
                    >
                      {seat.seatNumber}
                    </div>
                  );
                })}
              </div>
            </div>
          );
        })}
      </div>

      <div className="payment-section mt-4">
        <label className="form-label"><strong>Payment Mode:</strong></label>
        <select
          className="form-select mb-3"
          value={paymentMode}
          onChange={(e) => setPaymentMode(e.target.value)}
          required
        >
          <option value="">-- Select Payment Method --</option>
          <option value="UPI">UPI</option>
          <option value="CREDIT_CARD">Credit Card</option>
          <option value="DEBIT_CARD">Debit Card</option>
          <option value="NET_BANKING">Net Banking</option>
        </select>

        <div className="button-group">
          <button className="btn btn-success me-3" onClick={handleBooking}>
            Confirm Booking
          </button>
          <button className="btn btn-secondary" onClick={() => navigate("/passenger")}>
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
}

export default SelectSeats;

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>FastX | Home</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

    <style>
        body {
            margin: 00;
            padding: 0;
            background-color: #ffe6e6;
            background-image: url('images/bus.png');
            background-repeat: no-repeat;
            background-position: right center;
            background-size: 100% auto;
            height: 80vh;
            font-family: 'Segoe UI', sans-serif;
        }

       .form-container {
   			width: 40%;
   			margin: 5% 0 0 5%;
   			padding: 10px;
    		background-color: rgba(255, 255, 255, 0.4); 
    		border-radius: 12px;
    		box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
    		backdrop-filter: blur(15px); 
		}


        .form-section {
            display: none;
        }

        h2 {
            font-weight: bold;
        }

        .btn-toggle {
            width: 48%;
        }

        .form-check-inline {
            margin-right: 10px;
        }
    </style>
</head>
<body>
<head>
<link href="https://fonts.googleapis.com/css2?family=Pacifico&display=swap" rel="stylesheet">
<link href="https://fonts.googleapis.com/css2?family=Fredoka&display=swap" rel="stylesheet">

  <style>
    h1 {
        font-family: 'Pacifico', cursive;
        font-size: 2.5rem;
        font-weight: 500;
        color: #000000;
    }
  </style>
  
</head>

<body>
 <h1 class="text-center mt-4 mb-3">FastX 🚌</h1>
   <h3 class="text-center mb-3">Bus Ticket Booking App</h3>

</body>

<div class="form-container">
   

    <div class="text-center mb-1">
        <button class="btn btn-outline-primary btn-toggle" onclick="showForm('register')">Register</button>
        <button class="btn btn-outline-success btn-toggle" onclick="showForm('login')">Login</button>
    </div>

    <!-- Registration Form -->
    <div id="registerForm" class="form-section">
        <h4 class="mb-3">Registration</h4>
        <form action="register" method="post">
            <input type="text" name="name" class="form-control mb-2" placeholder="Name" required>
            <input type="email" name="email" class="form-control mb-2" placeholder="Email" required>
            <input type="password" name="password" class="form-control mb-2" placeholder="Password" required>
            <input type="text" name="contactNumber" class="form-control mb-2" placeholder="Contact Number">

            <label class="form-label">Gender:</label><br>
            <div class="form-check form-check-inline mb-2">
                <input class="form-check-input" type="radio" name="gender" value="Male" required>
                <label class="form-check-label">Male</label>
            </div>
            <div class="form-check form-check-inline mb-2">
                <input class="form-check-input" type="radio" name="gender" value="Female">
                <label class="form-check-label">Female</label>
            </div>
            <div class="form-check form-check-inline mb-2">
                <input class="form-check-input" type="radio" name="gender" value="Other">
                <label class="form-check-label">Other</label>
            </div>

            <input type="text" name="address" class="form-control mb-2" placeholder="Address">

            <select name="role" class="form-control mb-3" required>
                <option value="" disabled selected>Select Role</option>
                <option value="PASSENGER">Passenger</option>
                <option value="OPERATOR">Operator</option>
            </select>

            <button type="submit" class="btn btn-primary w-100">Register</button>
        </form>
    </div>

    <!-- Login Form -->
    <div id="loginForm" class="form-section">
        <h4 class="mb-3">Login</h4>
        <form action="login" method="post">
            <input type="text" name="name" class="form-control mb-2" placeholder="Username" required>
            <input type="password" name="password" class="form-control mb-3" placeholder="Password" required>
            <button type="submit" class="btn btn-success w-100">Login</button>
        </form>
    </div>
</div>

<script>
    function showForm(formType) {
        document.getElementById("registerForm").style.display = (formType === "register") ? "block" : "none";
        document.getElementById("loginForm").style.display = (formType === "login") ? "block" : "none";
    }

    showForm("register");
</script>

</body>
</html>

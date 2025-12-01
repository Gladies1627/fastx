<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <h2>User Login</h2>
    
    <form action="login" method="post">
        <input type="text" name="name" placeholder="Name" class="form-control mb-2" required />
        <input type="password" name="password" placeholder="Password" class="form-control mb-2" required />
        <button type="submit" class="btn btn-primary">Login</button>
    </form>

    <c:if test="${not empty errorMsg}">
        <div class="alert alert-danger mt-3">${errorMsg}</div>
    </c:if>
</div>
</body>
</html>

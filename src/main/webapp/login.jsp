<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login Acquario</title>
<style>
    body { font-family: sans-serif; background: #eef2f3; display: flex; justify-content: center; padding-top: 50px; }
    .card { background: white; padding: 20px; border-radius: 10px; shadow: 0 4px 8px rgba(0,0,0,0.1); width: 300px; }
    input { width: 90%; margin-bottom: 15px; padding: 10px; }
    button { width: 100%; padding: 10px; background: #007bff; color: white; border: none; cursor: pointer; }
</style>
</head>
<body>

<div class="card">
    <h2>Login Sistema</h2>
    
    <%-- Messaggio di errore dinamico se il login fallisce --%>
    <% String errore = (String) request.getAttribute("errore"); 
       if(errore != null) { %>
        <p style="color:red;"><%= errore %></p>
    <% } %>

    <form action="LoginServlet" method="post">
        <input type="text" name="username" placeholder="Username" required><br>
        <input type="password" name="password" placeholder="Password" required><br>
        <button type="submit">Accedi</button>
    </form>
</div>

</body>
</html>
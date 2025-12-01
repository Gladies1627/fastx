package com.fastx.busbooking.dto;

public class LoginResponse {
    private String token;
    private String role;
    private Integer id;
    private String status;
    private String name;// ✅ Add this

    public LoginResponse(String token, String role, Integer id, String status,String name) {
        this.token = token;
        this.role = role;
        this.id = id;
        this.status = status;
        this.name = name;
        
    }

    // Getters & Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

package com.example.temp_project;

public abstract class User {

    String role;

    public User(String r)
    {
        this.role = r;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isRole()
    {
        if (getRole().equals("user")) return true; // User role
        return false; // Moderator role
    }
}


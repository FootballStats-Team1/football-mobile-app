package com.example.temp_project;

public class User {

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

    public boolean isRole() // TRUE == USER FALSE == MODERATOR (poiotita logismikou)
    {
        if (this.role.equals("user")) return true;
        return false;
    }
}


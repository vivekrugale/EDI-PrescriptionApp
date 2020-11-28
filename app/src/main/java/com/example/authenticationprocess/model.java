package com.example.authenticationprocess;

public class model {
   String fullname,role;

    model()
    {

    }

    public model(String fullname, String role) {
        this.fullname = fullname;
        this.role = role;
    }


    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

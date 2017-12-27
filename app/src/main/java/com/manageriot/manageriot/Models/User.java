package com.manageriot.manageriot.Models;

/**
 * Created by wondochoung on 12/22/17.
 */

public class User {
    String id;
    String last_name;
    String first_name;
    String email;
    String phone;
    int role_id;
    String deleted_at;
    String created_at;
    String updated_at;


    public User(String id,
                String last_name,
                String first_name,
                String email,
                String phone,
                int role_id,
                String deleted_at,
                String created_at,
                String updated_at) {
        this.id=id;
        this.last_name=last_name;
        this.first_name=first_name;
        this.phone=phone;
        this.email=email;
        this.role_id = role_id;
        this.deleted_at = deleted_at;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public String getId() {
        return id;
    }

    public String getLastName() {
        return last_name;
    }
    public String getFirstName() {
        return last_name;
    }
    public String getEmail() {
        return email;
    }
    public String getPhone() { return phone; }
    public int getRoleId() {
        return role_id;
    }
    public String getDeletedAt() {
        return deleted_at;
    }
    public String getCreatedAt() { return created_at; }
    public String getUpdatedAt() {
        return updated_at;
    }


}

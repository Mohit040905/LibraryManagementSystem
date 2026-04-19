package com.library.model;

import java.time.LocalDate;

/**
 * Model class representing a library Member.
 */
public class Member {

    private int memberId;
    private String name;
    private String email;
    private String phone;
    private LocalDate joinedOn;

    // Constructor for registering a new member
    public Member(String name, String email, String phone) {
        this.name     = name;
        this.email    = email;
        this.phone    = phone;
        this.joinedOn = LocalDate.now();
    }

    // Full constructor (used when reading from DB)
    public Member(int memberId, String name, String email, String phone, LocalDate joinedOn) {
        this.memberId = memberId;
        this.name     = name;
        this.email    = email;
        this.phone    = phone;
        this.joinedOn = joinedOn;
    }

    // Getters
    public int       getMemberId() { return memberId; }
    public String    getName()     { return name; }
    public String    getEmail()    { return email; }
    public String    getPhone()    { return phone; }
    public LocalDate getJoinedOn() { return joinedOn; }

    // Setters
    public void setMemberId(int memberId) { this.memberId = memberId; }

    @Override
    public String toString() {
        return String.format(
            "| %-4d | %-25s | %-30s | %-13s | Joined: %s |",
            memberId, name, email, phone, joinedOn
        );
    }
}

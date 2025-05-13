package com.example.minesweeper;

public class Invitation {
    public String fromUID;
    public String matchID;

    // Default constructor (required by Firebase)
    public Invitation() {}

    public Invitation(String fromUID, String matchID) {
        this.fromUID = fromUID;
        this.matchID = matchID;
    }
}

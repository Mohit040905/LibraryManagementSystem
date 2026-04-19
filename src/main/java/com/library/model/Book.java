package com.library.model;

import java.time.LocalDate;

/**
 * Model class representing a Book in the library.
 * Encapsulates all book-related fields with getters and setters.
 */
public class Book {

    private int bookId;
    private String title;
    private String author;
    private String genre;
    private int totalCopies;
    private int availableCopies;
    private LocalDate addedOn;

    // Constructor for creating a new book (no ID yet)
    public Book(String title, String author, String genre, int totalCopies) {
        this.title           = title;
        this.author          = author;
        this.genre           = genre;
        this.totalCopies     = totalCopies;
        this.availableCopies = totalCopies;
        this.addedOn         = LocalDate.now();
    }

    // Full constructor (used when reading from DB)
    public Book(int bookId, String title, String author, String genre,
                int totalCopies, int availableCopies, LocalDate addedOn) {
        this.bookId          = bookId;
        this.title           = title;
        this.author          = author;
        this.genre           = genre;
        this.totalCopies     = totalCopies;
        this.availableCopies = availableCopies;
        this.addedOn         = addedOn;
    }

    // Getters
    public int       getBookId()          { return bookId; }
    public String    getTitle()           { return title; }
    public String    getAuthor()          { return author; }
    public String    getGenre()           { return genre; }
    public int       getTotalCopies()     { return totalCopies; }
    public int       getAvailableCopies() { return availableCopies; }
    public LocalDate getAddedOn()         { return addedOn; }

    // Setters
    public void setBookId(int bookId)                   { this.bookId = bookId; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    @Override
    public String toString() {
        return String.format(
            "| %-4d | %-35s | %-20s | %-12s | Copies: %d/%d |",
            bookId, title, author, genre, availableCopies, totalCopies
        );
    }
}

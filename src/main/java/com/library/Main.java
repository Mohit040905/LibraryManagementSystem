package com.library;

import com.library.model.Book;
import com.library.model.IssueRecord;
import com.library.model.Member;
import com.library.service.LibraryService;
import com.library.util.DBConnection;

import java.util.List;
import java.util.Scanner;

/**
 * Entry point for the Library Management System.
 * Provides a console-based menu-driven interface.
 */
public class Main {

    private static final LibraryService service = new LibraryService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        printBanner();
        boolean running = true;

        while (running) {
            printMainMenu();
            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1  -> bookMenu();
                case 2  -> memberMenu();
                case 3  -> issueReturnMenu();
                case 4  -> reportsMenu();
                case 0  -> running = false;
                default -> print("Invalid option. Please try again.");
            }
        }

        DBConnection.closeConnection();
        print("\nThank you for using the Library Management System. Goodbye!");
    }

    // ══════════════════════════════════════════════════════════════════
    // MENUS
    // ══════════════════════════════════════════════════════════════════

    private static void printMainMenu() {
        print("""
            \n╔══════════════════════════════════╗
            ║      LIBRARY MANAGEMENT SYSTEM   ║
            ╠══════════════════════════════════╣
            ║  1. Books                        ║
            ║  2. Members                      ║
            ║  3. Issue / Return               ║
            ║  4. Reports                      ║
            ║  0. Exit                         ║
            ╚══════════════════════════════════╝""");
    }

    // ── BOOK MENU ────────────────────────────────────────────────────

    private static void bookMenu() {
        print("""
            \n── BOOK MANAGEMENT ───────────────────
              1. Add New Book
              2. View All Books
              3. Search Books
              4. Delete Book
              0. Back""");

        int choice = readInt("Enter choice: ");
        switch (choice) {
            case 1 -> addBook();
            case 2 -> viewAllBooks();
            case 3 -> searchBooks();
            case 4 -> deleteBook();
            case 0 -> {}
            default -> print("Invalid option.");
        }
    }

    private static void addBook() {
        print("\n── ADD BOOK ──");
        String title  = readString("Title: ");
        String author = readString("Author: ");
        String genre  = readString("Genre: ");
        int copies    = readInt("Number of copies: ");
        print(service.addBook(title, author, genre, copies));
    }

    private static void viewAllBooks() {
        List<Book> books = service.getAllBooks();
        if (books.isEmpty()) { print("No books in the library yet."); return; }
        print("\n── ALL BOOKS (" + books.size() + ") ──────────────────────────────────────────────────────────────────");
        books.forEach(b -> print(b.toString()));
    }

    private static void searchBooks() {
        print("\nSearch by: 1. Title   2. Author");
        int by        = readInt("Choice: ");
        String field  = (by == 2) ? "author" : "title";
        String keyword = readString("Enter keyword: ");
        List<Book> results = service.searchBooks(keyword, field);
        if (results.isEmpty()) { print("No books found matching '" + keyword + "'"); return; }
        print("\n── SEARCH RESULTS ──");
        results.forEach(b -> print(b.toString()));
    }

    private static void deleteBook() {
        int id = readInt("\nEnter Book ID to delete: ");
        print(service.deleteBook(id));
    }

    // ── MEMBER MENU ──────────────────────────────────────────────────

    private static void memberMenu() {
        print("""
            \n── MEMBER MANAGEMENT ─────────────────
              1. Register New Member
              2. View All Members
              0. Back""");

        int choice = readInt("Enter choice: ");
        switch (choice) {
            case 1 -> registerMember();
            case 2 -> viewAllMembers();
            case 0 -> {}
            default -> print("Invalid option.");
        }
    }

    private static void registerMember() {
        print("\n── REGISTER MEMBER ──");
        String name  = readString("Full Name: ");
        String email = readString("Email: ");
        String phone = readString("Phone: ");
        print(service.registerMember(name, email, phone));
    }

    private static void viewAllMembers() {
        List<Member> members = service.getAllMembers();
        if (members.isEmpty()) { print("No members registered yet."); return; }
        print("\n── ALL MEMBERS (" + members.size() + ") ──");
        members.forEach(m -> print(m.toString()));
    }

    // ── ISSUE / RETURN MENU ──────────────────────────────────────────

    private static void issueReturnMenu() {
        print("""
            \n── ISSUE / RETURN ────────────────────
              1. Issue Book to Member
              2. Return Book
              3. View All Active Issues
              0. Back""");

        int choice = readInt("Enter choice: ");
        switch (choice) {
            case 1 -> issueBook();
            case 2 -> returnBook();
            case 3 -> viewActiveIssues();
            case 0 -> {}
            default -> print("Invalid option.");
        }
    }

    private static void issueBook() {
        print("\n── ISSUE BOOK ──");
        int bookId   = readInt("Book ID: ");
        int memberId = readInt("Member ID: ");
        print(service.issueBook(bookId, memberId));
    }

    private static void returnBook() {
        viewActiveIssues();
        if (service.getActiveIssues().isEmpty()) return;
        int recordId = readInt("\nEnter Record ID to return: ");
        print(service.returnBook(recordId));
    }

    private static void viewActiveIssues() {
        List<IssueRecord> issues = service.getActiveIssues();
        if (issues.isEmpty()) { print("No books are currently issued."); return; }
        print("\n── ACTIVE ISSUES (" + issues.size() + ") ──");
        issues.forEach(r -> print(r.toString()));
    }

    // ── REPORTS MENU ─────────────────────────────────────────────────

    private static void reportsMenu() {
        print("""
            \n── REPORTS ───────────────────────────
              1. Overdue Books
              2. Member Borrow History
              0. Back""");

        int choice = readInt("Enter choice: ");
        switch (choice) {
            case 1 -> {
                List<IssueRecord> overdue = service.getOverdueRecords();
                if (overdue.isEmpty()) { print("No overdue books. Great!"); }
                else { print("\n── OVERDUE BOOKS ──"); overdue.forEach(r -> print(r.toString())); }
            }
            case 2 -> {
                int memberId = readInt("Enter Member ID: ");
                List<IssueRecord> history = service.getMemberHistory(memberId);
                if (history.isEmpty()) { print("No records found for Member ID " + memberId); }
                else { print("\n── MEMBER HISTORY ──"); history.forEach(r -> print(r.toString())); }
            }
            case 0 -> {}
            default -> print("Invalid option.");
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // UTILITY HELPERS
    // ══════════════════════════════════════════════════════════════════

    private static void printBanner() {
        print("""
            ╔══════════════════════════════════════════╗
            ║   LIBRARY MANAGEMENT SYSTEM  v1.0        ║
            ║   Java + JDBC + MySQL                    ║
            ║   Developed by: Mohit Morande            ║
            ╚══════════════════════════════════════════╝""");
    }

    private static void print(String msg)  { System.out.println(msg); }

    private static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int val = Integer.parseInt(scanner.nextLine().trim());
                return val;
            } catch (NumberFormatException e) {
                print("Please enter a valid number.");
            }
        }
    }
}

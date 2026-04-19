package com.library.service;

import com.library.dao.BookDAO;
import com.library.dao.IssueRecordDAO;
import com.library.dao.MemberDAO;
import com.library.model.Book;
import com.library.model.IssueRecord;
import com.library.model.Member;

import java.util.List;

/**
 * Service layer containing all business logic.
 * This sits between the console UI (Main) and the DAO layer.
 * Validates inputs and enforces business rules before touching the DB.
 */
public class LibraryService {

    private final BookDAO        bookDAO;
    private final MemberDAO      memberDAO;
    private final IssueRecordDAO issueRecordDAO;

    public LibraryService() {
        this.bookDAO        = new BookDAO();
        this.memberDAO      = new MemberDAO();
        this.issueRecordDAO = new IssueRecordDAO();
    }

    // ══════════════════════════════════════════════════════════════════
    // BOOK OPERATIONS
    // ══════════════════════════════════════════════════════════════════

    public String addBook(String title, String author, String genre, int copies) {
        if (title.isBlank() || author.isBlank()) return "ERROR: Title and author cannot be empty.";
        if (copies < 1) return "ERROR: Number of copies must be at least 1.";

        Book book = new Book(title.trim(), author.trim(), genre.trim(), copies);
        if (bookDAO.addBook(book)) {
            return "SUCCESS: Book added with ID " + book.getBookId();
        }
        return "ERROR: Failed to add book.";
    }

    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }

    public List<Book> searchBooks(String keyword, String searchBy) {
        return searchBy.equalsIgnoreCase("author")
            ? bookDAO.searchByAuthor(keyword)
            : bookDAO.searchByTitle(keyword);
    }

    public String deleteBook(int bookId) {
        Book book = bookDAO.getBookById(bookId);
        if (book == null) return "ERROR: No book found with ID " + bookId;
        if (book.getTotalCopies() != book.getAvailableCopies()) {
            return "ERROR: Cannot delete — some copies of this book are currently issued.";
        }
        return bookDAO.deleteBook(bookId)
            ? "SUCCESS: Book deleted."
            : "ERROR: Failed to delete book.";
    }

    // ══════════════════════════════════════════════════════════════════
    // MEMBER OPERATIONS
    // ══════════════════════════════════════════════════════════════════

    public String registerMember(String name, String email, String phone) {
        if (name.isBlank() || email.isBlank()) return "ERROR: Name and email cannot be empty.";
        if (!email.contains("@")) return "ERROR: Invalid email format.";

        Member member = new Member(name.trim(), email.trim(), phone.trim());
        if (memberDAO.registerMember(member)) {
            return "SUCCESS: Member registered with ID " + member.getMemberId();
        }
        return "ERROR: Registration failed. Email may already be in use.";
    }

    public List<Member> getAllMembers() {
        return memberDAO.getAllMembers();
    }

    // ══════════════════════════════════════════════════════════════════
    // ISSUE / RETURN OPERATIONS
    // ══════════════════════════════════════════════════════════════════

    public String issueBook(int bookId, int memberId) {
        // Validate book
        Book book = bookDAO.getBookById(bookId);
        if (book == null)       return "ERROR: No book found with ID " + bookId;
        if (!book.isAvailable()) return "ERROR: No copies of '" + book.getTitle() + "' are available right now.";

        // Validate member
        Member member = memberDAO.getMemberById(memberId);
        if (member == null) return "ERROR: No member found with ID " + memberId;

        // Check duplicate issue
        if (issueRecordDAO.isMemberAlreadyIssuedBook(memberId, bookId)) {
            return "ERROR: " + member.getName() + " already has a copy of '" + book.getTitle() + "' issued.";
        }

        // Create the issue record and reduce available copies
        IssueRecord record = new IssueRecord(bookId, memberId);
        if (issueRecordDAO.issueBook(record) && bookDAO.updateAvailableCopies(bookId, -1)) {
            return String.format(
                "SUCCESS: '%s' issued to %s. Due date: %s",
                book.getTitle(), member.getName(), record.getDueDate()
            );
        }
        return "ERROR: Failed to issue book. Please try again.";
    }

    public String returnBook(int recordId) {
        List<IssueRecord> active = issueRecordDAO.getActiveIssues();
        IssueRecord target = active.stream()
            .filter(r -> r.getRecordId() == recordId)
            .findFirst()
            .orElse(null);

        if (target == null) {
            return "ERROR: No active issue found with Record ID " + recordId;
        }

        if (issueRecordDAO.returnBook(recordId) && bookDAO.updateAvailableCopies(target.getBookId(), 1)) {
            String overdueMsg = target.isOverdue() ? " (Note: This book was overdue!)" : "";
            return "SUCCESS: '" + target.getBookTitle() + "' returned successfully." + overdueMsg;
        }
        return "ERROR: Return operation failed.";
    }

    public List<IssueRecord> getActiveIssues() {
        return issueRecordDAO.getActiveIssues();
    }

    public List<IssueRecord> getOverdueRecords() {
        return issueRecordDAO.getOverdueRecords();
    }

    public List<IssueRecord> getMemberHistory(int memberId) {
        Member member = memberDAO.getMemberById(memberId);
        if (member == null) return List.of();
        return issueRecordDAO.getIssuesByMember(memberId);
    }
}

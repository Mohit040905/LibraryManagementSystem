package com.library.model;

import java.time.LocalDate;

/**
 * Model class representing a book issue/return transaction.
 */
public class IssueRecord {

    public enum Status { ISSUED, RETURNED }

    private int       recordId;
    private int       bookId;
    private int       memberId;
    private String    bookTitle;   // for display purposes
    private String    memberName;  // for display purposes
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Status    status;

    // Constructor for issuing a new book (14-day lending period)
    public IssueRecord(int bookId, int memberId) {
        this.bookId     = bookId;
        this.memberId   = memberId;
        this.issueDate  = LocalDate.now();
        this.dueDate    = LocalDate.now().plusDays(14);
        this.status     = Status.ISSUED;
    }

    // Full constructor (used when reading from DB)
    public IssueRecord(int recordId, int bookId, int memberId,
                       String bookTitle, String memberName,
                       LocalDate issueDate, LocalDate dueDate,
                       LocalDate returnDate, Status status) {
        this.recordId   = recordId;
        this.bookId     = bookId;
        this.memberId   = memberId;
        this.bookTitle  = bookTitle;
        this.memberName = memberName;
        this.issueDate  = issueDate;
        this.dueDate    = dueDate;
        this.returnDate = returnDate;
        this.status     = status;
    }

    // Getters
    public int       getRecordId()   { return recordId; }
    public int       getBookId()     { return bookId; }
    public int       getMemberId()   { return memberId; }
    public String    getBookTitle()  { return bookTitle; }
    public String    getMemberName() { return memberName; }
    public LocalDate getIssueDate()  { return issueDate; }
    public LocalDate getDueDate()    { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public Status    getStatus()     { return status; }

    public boolean isOverdue() {
        return status == Status.ISSUED && LocalDate.now().isAfter(dueDate);
    }

    @Override
    public String toString() {
        String returnInfo = (returnDate != null) ? returnDate.toString() : "Not returned";
        String overdueTag = isOverdue() ? " *** OVERDUE ***" : "";
        return String.format(
            "| RecordID: %-4d | Book: %-30s | Member: %-20s | Issued: %s | Due: %s | Return: %s | %s%s |",
            recordId, bookTitle, memberName, issueDate, dueDate, returnInfo, status, overdueTag
        );
    }
}

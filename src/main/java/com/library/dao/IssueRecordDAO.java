package com.library.dao;

import com.library.model.IssueRecord;
import com.library.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Issue/Return operations.
 */
public class IssueRecordDAO {

    private final Connection conn;

    public IssueRecordDAO() {
        this.conn = DBConnection.getConnection();
    }

    // ── ISSUE BOOK ────────────────────────────────────────────────────────────

    public boolean issueBook(IssueRecord record) {
        String sql = "INSERT INTO issue_records (book_id, member_id, issue_date, due_date, status) "
                   + "VALUES (?, ?, ?, ?, 'ISSUED')";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, record.getBookId());
            ps.setInt(2, record.getMemberId());
            ps.setDate(3, Date.valueOf(record.getIssueDate()));
            ps.setDate(4, Date.valueOf(record.getDueDate()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] issueBook: " + e.getMessage());
        }
        return false;
    }

    // ── RETURN BOOK ───────────────────────────────────────────────────────────

    public boolean returnBook(int recordId) {
        String sql = "UPDATE issue_records SET return_date = ?, status = 'RETURNED' "
                   + "WHERE record_id = ? AND status = 'ISSUED'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(java.time.LocalDate.now()));
            ps.setInt(2, recordId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] returnBook: " + e.getMessage());
        }
        return false;
    }

    // ── GET ALL ACTIVE ISSUES ─────────────────────────────────────────────────

    public List<IssueRecord> getActiveIssues() {
        return getIssues("WHERE ir.status = 'ISSUED'");
    }

    // ── GET HISTORY FOR A MEMBER ──────────────────────────────────────────────

    public List<IssueRecord> getIssuesByMember(int memberId) {
        return getIssues("WHERE ir.member_id = " + memberId);
    }

    // ── GET ALL OVERDUE RECORDS ───────────────────────────────────────────────

    public List<IssueRecord> getOverdueRecords() {
        return getIssues("WHERE ir.status = 'ISSUED' AND ir.due_date < CURDATE()");
    }

    // ── CHECK IF MEMBER ALREADY HAS THE BOOK ISSUED ───────────────────────────

    public boolean isMemberAlreadyIssuedBook(int memberId, int bookId) {
        String sql = "SELECT COUNT(*) FROM issue_records "
                   + "WHERE member_id = ? AND book_id = ? AND status = 'ISSUED'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ps.setInt(2, bookId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] isMemberAlreadyIssuedBook: " + e.getMessage());
        }
        return false;
    }

    // ── PRIVATE HELPER: shared JOIN query ────────────────────────────────────

    private List<IssueRecord> getIssues(String whereClause) {
        List<IssueRecord> records = new ArrayList<>();
        String sql = "SELECT ir.*, b.title AS book_title, m.name AS member_name "
                   + "FROM issue_records ir "
                   + "JOIN books b   ON ir.book_id   = b.book_id "
                   + "JOIN members m ON ir.member_id = m.member_id "
                   + whereClause
                   + " ORDER BY ir.issue_date DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Date returnDate = rs.getDate("return_date");
                records.add(new IssueRecord(
                    rs.getInt("record_id"),
                    rs.getInt("book_id"),
                    rs.getInt("member_id"),
                    rs.getString("book_title"),
                    rs.getString("member_name"),
                    rs.getDate("issue_date").toLocalDate(),
                    rs.getDate("due_date").toLocalDate(),
                    returnDate != null ? returnDate.toLocalDate() : null,
                    IssueRecord.Status.valueOf(rs.getString("status"))
                ));
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] getIssues: " + e.getMessage());
        }
        return records;
    }
}

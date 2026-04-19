package com.library.dao;

import com.library.model.Member;
import com.library.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Member operations.
 */
public class MemberDAO {

    private final Connection conn;

    public MemberDAO() {
        this.conn = DBConnection.getConnection();
    }

    // ── REGISTER ──────────────────────────────────────────────────────────────

    public boolean registerMember(Member member) {
        String sql = "INSERT INTO members (name, email, phone, joined_on) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            ps.setDate(4, Date.valueOf(member.getJoinedOn()));

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) member.setMemberId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // MySQL duplicate entry error code
                System.out.println("[ERROR] A member with this email already exists.");
            } else {
                System.err.println("[DAO ERROR] registerMember: " + e.getMessage());
            }
        }
        return false;
    }

    // ── GET ALL ───────────────────────────────────────────────────────────────

    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members ORDER BY name";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) members.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] getAllMembers: " + e.getMessage());
        }
        return members;
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────────

    public Member getMemberById(int memberId) {
        String sql = "SELECT * FROM members WHERE member_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] getMemberById: " + e.getMessage());
        }
        return null;
    }

    // ── PRIVATE HELPER ────────────────────────────────────────────────────────

    private Member mapRow(ResultSet rs) throws SQLException {
        return new Member(
            rs.getInt("member_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getDate("joined_on").toLocalDate()
        );
    }
}

package com.bloodlink.dao;

import com.bloodlink.model.BloodRequest;
import com.bloodlink.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BloodRequestDAO — all database operations for blood requests.
 */
public class BloodRequestDAO {

    /** Post a new blood request */
    public boolean create(BloodRequest req) throws SQLException {
        String sql = "INSERT INTO blood_requests (requester_id, blood_group, hospital_name, location, " +
                     "contact_phone, units_needed, urgency, notes) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1,    req.getRequesterId());
            ps.setString(2, req.getBloodGroup());
            ps.setString(3, req.getHospitalName());
            ps.setString(4, req.getLocation());
            ps.setString(5, req.getContactPhone());
            ps.setInt(6,    req.getUnitsNeeded());
            ps.setString(7, req.getUrgency() != null ? req.getUrgency() : "urgent");
            ps.setString(8, req.getNotes());

            return ps.executeUpdate() > 0;
        }
    }

    /** Get all active requests ordered by urgency */
    public List<BloodRequest> getActive() throws SQLException {
        String sql = "SELECT * FROM blood_requests WHERE status = 'active' " +
                     "ORDER BY FIELD(urgency,'critical','urgent','normal'), created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement()) {

            ResultSet rs = st.executeQuery(sql);
            List<BloodRequest> list = new ArrayList<>();
            while (rs.next()) list.add(mapRequest(rs));
            return list;
        }
    }

    /** Get all requests (admin view) */
    public List<BloodRequest> getAll() throws SQLException {
        String sql = "SELECT * FROM blood_requests ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement()) {

            ResultSet rs = st.executeQuery(sql);
            List<BloodRequest> list = new ArrayList<>();
            while (rs.next()) list.add(mapRequest(rs));
            return list;
        }
    }

    /** Mark a request as fulfilled or cancelled */
    public boolean updateStatus(int id, String status) throws SQLException {
        String sql = "UPDATE blood_requests SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Count active requests */
    public int countActive() throws SQLException {
        String sql = "SELECT COUNT(*) FROM blood_requests WHERE status = 'active'";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement()) {

            ResultSet rs = st.executeQuery(sql);
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /** Map ResultSet row → BloodRequest */
    private BloodRequest mapRequest(ResultSet rs) throws SQLException {
        BloodRequest r = new BloodRequest();
        r.setId(rs.getInt("id"));
        r.setRequesterId(rs.getInt("requester_id"));
        r.setBloodGroup(rs.getString("blood_group"));
        r.setHospitalName(rs.getString("hospital_name"));
        r.setLocation(rs.getString("location"));
        r.setContactPhone(rs.getString("contact_phone"));
        r.setUnitsNeeded(rs.getInt("units_needed"));
        r.setUrgency(rs.getString("urgency"));
        r.setNotes(rs.getString("notes"));
        r.setStatus(rs.getString("status"));
        r.setCreatedAt(rs.getString("created_at"));
        return r;
    }
}

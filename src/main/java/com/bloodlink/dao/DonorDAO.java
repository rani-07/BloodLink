package com.bloodlink.dao;

import com.bloodlink.model.Donor;
import com.bloodlink.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DonorDAO — all database operations for donors.
 */
public class DonorDAO {

    /** Register a new donor profile linked to a user */
    public boolean register(Donor donor) throws SQLException {
        String sql = "INSERT INTO donors (user_id, blood_group, age, gender, weight, city, state, pin_code, available) " +
                     "VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1,    donor.getUserId());
            ps.setString(2, donor.getBloodGroup());
            ps.setInt(3,    donor.getAge());
            ps.setString(4, donor.getGender());
            ps.setDouble(5, donor.getWeight());
            ps.setString(6, donor.getCity());
            ps.setString(7, donor.getState());
            ps.setString(8, donor.getPinCode());
            ps.setBoolean(9, true);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Search donors by blood group and/or city.
     * Both params are optional — pass null to skip that filter.
     */
    public List<Donor> search(String bloodGroup, String city) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT d.*, u.first_name, u.last_name, u.email, u.phone " +
            "FROM donors d JOIN users u ON d.user_id = u.id WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        if (bloodGroup != null && !bloodGroup.isEmpty()) {
            sql.append(" AND d.blood_group = ?");
            params.add(bloodGroup);
        }
        if (city != null && !city.isEmpty()) {
            sql.append(" AND LOWER(d.city) LIKE ?");
            params.add("%" + city.toLowerCase() + "%");
        }

        sql.append(" ORDER BY d.available DESC, d.total_donations DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            List<Donor> list = new ArrayList<>();
            while (rs.next()) list.add(mapDonor(rs));
            return list;
        }
    }

    /** Get one donor by donor ID */
    public Donor findById(int id) throws SQLException {
        String sql = "SELECT d.*, u.first_name, u.last_name, u.email, u.phone " +
                     "FROM donors d JOIN users u ON d.user_id = u.id WHERE d.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapDonor(rs) : null;
        }
    }

    /** Get donor by user ID (for logged-in user's own profile) */
    public Donor findByUserId(int userId) throws SQLException {
        String sql = "SELECT d.*, u.first_name, u.last_name, u.email, u.phone " +
                     "FROM donors d JOIN users u ON d.user_id = u.id WHERE d.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapDonor(rs) : null;
        }
    }

    /** Get all donors (for admin) */
    public List<Donor> getAll() throws SQLException {
        String sql = "SELECT d.*, u.first_name, u.last_name, u.email, u.phone " +
                     "FROM donors d JOIN users u ON d.user_id = u.id " +
                     "ORDER BY d.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            List<Donor> list = new ArrayList<>();
            while (rs.next()) list.add(mapDonor(rs));
            return list;
        }
    }

    /** Toggle donor availability (available / unavailable) */
    public boolean updateAvailability(int donorId, boolean available) throws SQLException {
        String sql = "UPDATE donors SET available = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, available);
            ps.setInt(2, donorId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Delete a donor by ID (admin) */
    public boolean delete(int donorId) throws SQLException {
        String sql = "DELETE FROM donors WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, donorId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Count total donors */
    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM donors";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement()) {

            ResultSet rs = st.executeQuery(sql);
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /** Map ResultSet row → Donor object */
    private Donor mapDonor(ResultSet rs) throws SQLException {
        Donor d = new Donor();
        d.setId(rs.getInt("id"));
        d.setUserId(rs.getInt("user_id"));
        d.setFirstName(rs.getString("first_name"));
        d.setLastName(rs.getString("last_name"));
        d.setEmail(rs.getString("email"));
        d.setPhone(rs.getString("phone"));
        d.setBloodGroup(rs.getString("blood_group"));
        d.setAge(rs.getInt("age"));
        d.setGender(rs.getString("gender"));
        d.setWeight(rs.getDouble("weight"));
        d.setCity(rs.getString("city"));
        d.setState(rs.getString("state"));
        d.setPinCode(rs.getString("pin_code"));
        d.setAvailable(rs.getBoolean("available"));
        d.setTotalDonations(rs.getInt("total_donations"));
        if (rs.getDate("last_donation") != null)
            d.setLastDonation(rs.getDate("last_donation").toString());
        if (rs.getDate("next_eligible") != null)
            d.setNextEligible(rs.getDate("next_eligible").toString());
        d.setCreatedAt(rs.getString("created_at"));
        return d;
    }
}

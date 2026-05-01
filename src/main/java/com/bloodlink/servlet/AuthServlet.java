package com.bloodlink.servlet;

import com.bloodlink.dao.DonorDAO;
import com.bloodlink.dao.UserDAO;
import com.bloodlink.model.Donor;
import com.bloodlink.model.User;
import com.bloodlink.util.JsonUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * AuthServlet — handles user registration and login.
 *
 * POST /api/auth/register  → register new user + donor profile
 * POST /api/auth/login     → login and start session
 * POST /api/auth/logout    → destroy session
 * GET  /api/auth/me        → return currently logged-in user
 */
@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {

    private final UserDAO  userDAO  = new UserDAO();
    private final DonorDAO donorDAO = new DonorDAO();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res) throws IOException {
        JsonUtil.sendSuccess(res, "OK"); // Handle CORS preflight
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String path = req.getPathInfo(); // e.g. "/login"

        try {
            switch (path) {
                case "/register" -> handleRegister(req, res);
                case "/login"    -> handleLogin(req, res);
                case "/logout"   -> handleLogout(req, res);
                default          -> JsonUtil.sendError(res, 404, "Endpoint not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JsonUtil.sendError(res, 500, "Database error: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String path = req.getPathInfo();
        try {
            if ("/me".equals(path)) handleMe(req, res);
            else JsonUtil.sendError(res, 404, "Endpoint not found");
        } catch (SQLException e) {
            JsonUtil.sendError(res, 500, "Database error");
        }
    }

    // ── REGISTER ─────────────────────────────────────────────────────────────
    private void handleRegister(HttpServletRequest req, HttpServletResponse res)
            throws IOException, SQLException {

        // Parse JSON body
        Map body = JsonUtil.parseBody(req, Map.class);

        // Validate required fields
        String email = (String) body.get("email");
        String password = (String) body.get("password");
        if (email == null || password == null || password.length() < 6) {
            JsonUtil.sendError(res, 400, "Email and password (min 6 chars) are required");
            return;
        }

        // Check if email already used
        if (userDAO.emailExists(email)) {
            JsonUtil.sendError(res, 409, "Email already registered");
            return;
        }

        // Build and save User
        User user = new User();
        user.setFirstName((String) body.getOrDefault("firstName", ""));
        user.setLastName((String) body.getOrDefault("lastName", ""));
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone((String) body.getOrDefault("phone", ""));
        user.setRole((String) body.getOrDefault("role", "donor"));

        boolean saved = userDAO.register(user);
        if (!saved) {
            JsonUtil.sendError(res, 500, "Registration failed");
            return;
        }

        // If role is donor, also save donor profile
        User savedUser = userDAO.login(email, password);
        if ("donor".equals(user.getRole()) && savedUser != null) {
            Donor donor = new Donor();
            donor.setUserId(savedUser.getId());
            donor.setBloodGroup((String) body.getOrDefault("bloodGroup", "O+"));
            donor.setCity((String) body.getOrDefault("city", ""));
            donor.setState((String) body.getOrDefault("state", ""));
            donor.setPinCode((String) body.getOrDefault("pinCode", ""));
            double age = body.get("age") != null ? ((Number) body.get("age")).doubleValue() : 0;
            donor.setAge((int) age);
            donor.setGender((String) body.getOrDefault("gender", ""));
            double weight = body.get("weight") != null ? ((Number) body.get("weight")).doubleValue() : 0;
            donor.setWeight(weight);
            donorDAO.register(donor);
        }

        JsonUtil.sendSuccess(res, "Registration successful! Welcome to BloodLink.");
    }

    // ── LOGIN ────────────────────────────────────────────────────────────────
    private void handleLogin(HttpServletRequest req, HttpServletResponse res)
            throws IOException, SQLException {

        Map body = JsonUtil.parseBody(req, Map.class);
        String email    = (String) body.get("email");
        String password = (String) body.get("password");

        if (email == null || password == null) {
            JsonUtil.sendError(res, 400, "Email and password are required");
            return;
        }

        User user = userDAO.login(email, password);
        if (user == null) {
            JsonUtil.sendError(res, 401, "Invalid email or password");
            return;
        }

        // Save user in session
        HttpSession session = req.getSession(true);
        session.setAttribute("userId", user.getId());
        session.setAttribute("userRole", user.getRole());
        session.setMaxInactiveInterval(60 * 60 * 24); // 24 hours

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login successful!");
        response.put("user", user);
        JsonUtil.sendJson(res, response);
    }

    // ── LOGOUT ───────────────────────────────────────────────────────────────
    private void handleLogout(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
        JsonUtil.sendSuccess(res, "Logged out successfully");
    }

    // ── ME (current user) ────────────────────────────────────────────────────
    private void handleMe(HttpServletRequest req, HttpServletResponse res)
            throws IOException, SQLException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            JsonUtil.sendError(res, 401, "Not logged in");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        User user = userDAO.findById(userId);
        if (user == null) {
            JsonUtil.sendError(res, 404, "User not found");
            return;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("user", user);
        JsonUtil.sendJson(res, response);
    }
}

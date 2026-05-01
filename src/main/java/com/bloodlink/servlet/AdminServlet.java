package com.bloodlink.servlet;

import com.bloodlink.dao.BloodRequestDAO;
import com.bloodlink.dao.DonorDAO;
import com.bloodlink.util.JsonUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * AdminServlet — admin-only dashboard stats endpoint.
 *
 * GET /api/admin/stats → total donors, active requests, etc.
 */
@WebServlet("/api/admin/*")
public class AdminServlet extends HttpServlet {

    private final DonorDAO        donorDAO   = new DonorDAO();
    private final BloodRequestDAO requestDAO = new BloodRequestDAO();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res) throws IOException {
        JsonUtil.sendSuccess(res, "OK");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // Check admin session
        HttpSession session = req.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            JsonUtil.sendError(res, 403, "Admin access required");
            return;
        }

        String path = req.getPathInfo();
        try {
            if ("/stats".equals(path)) {
                Map<String, Object> stats = new HashMap<>();
                stats.put("totalDonors",    donorDAO.countAll());
                stats.put("activeRequests", requestDAO.countActive());
                stats.put("livesSaved",     donorDAO.countAll() * 3); // estimate
                stats.put("success", true);
                JsonUtil.sendJson(res, stats);

            } else if ("/donors".equals(path)) {
                var donors = donorDAO.getAll();
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", true);
                resp.put("donors", donors);
                JsonUtil.sendJson(res, resp);

            } else if ("/requests".equals(path)) {
                var requests = requestDAO.getAll();
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", true);
                resp.put("requests", requests);
                JsonUtil.sendJson(res, resp);

            } else {
                JsonUtil.sendError(res, 404, "Admin endpoint not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JsonUtil.sendError(res, 500, "Database error: " + e.getMessage());
        }
    }
}

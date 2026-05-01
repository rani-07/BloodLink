package com.bloodlink.servlet;

import com.bloodlink.dao.BloodRequestDAO;
import com.bloodlink.model.BloodRequest;
import com.bloodlink.util.JsonUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BloodRequestServlet — manage urgent blood requests.
 *
 * GET  /api/requests         → get all active requests
 * GET  /api/requests/all     → get all requests (admin)
 * POST /api/requests         → post a new blood request
 * PUT  /api/requests/{id}    → update request status (fulfill/cancel)
 */
@WebServlet("/api/requests/*")
public class BloodRequestServlet extends HttpServlet {

    private final BloodRequestDAO requestDAO = new BloodRequestDAO();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res) throws IOException {
        JsonUtil.sendSuccess(res, "OK");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String path = req.getPathInfo();
        try {
            List<BloodRequest> requests;
            if ("/all".equals(path)) {
                requests = requestDAO.getAll();
            } else {
                requests = requestDAO.getActive();
            }
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("count", requests.size());
            resp.put("requests", requests);
            JsonUtil.sendJson(res, resp);
        } catch (SQLException e) {
            JsonUtil.sendError(res, 500, "Database error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            Map body = JsonUtil.parseBody(req, Map.class);

            // Validate required fields
            if (body.get("bloodGroup") == null || body.get("hospitalName") == null) {
                JsonUtil.sendError(res, 400, "bloodGroup and hospitalName are required");
                return;
            }

            BloodRequest request = new BloodRequest();
            request.setBloodGroup((String) body.get("bloodGroup"));
            request.setHospitalName((String) body.get("hospitalName"));
            request.setLocation((String) body.getOrDefault("location", ""));
            request.setContactPhone((String) body.getOrDefault("contactPhone", ""));
            request.setNotes((String) body.getOrDefault("notes", ""));
            request.setUrgency((String) body.getOrDefault("urgency", "urgent"));
            double units = body.get("unitsNeeded") != null ? ((Number) body.get("unitsNeeded")).doubleValue() : 1;
            request.setUnitsNeeded((int) units);

            // Link to logged-in user if available
            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute("userId") != null) {
                request.setRequesterId((int) session.getAttribute("userId"));
            }

            boolean saved = requestDAO.create(request);
            if (saved) JsonUtil.sendSuccess(res, "Blood request posted! Nearby donors have been notified.");
            else JsonUtil.sendError(res, 500, "Failed to post request");

        } catch (SQLException e) {
            e.printStackTrace();
            JsonUtil.sendError(res, 500, "Database error: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String path = req.getPathInfo();
        try {
            if (path != null && path.length() > 1) {
                int id = Integer.parseInt(path.substring(1));
                Map body = JsonUtil.parseBody(req, Map.class);
                String status = (String) body.get("status"); // fulfilled or cancelled
                if (status == null) {
                    JsonUtil.sendError(res, 400, "status field required");
                    return;
                }
                boolean updated = requestDAO.updateStatus(id, status);
                if (updated) JsonUtil.sendSuccess(res, "Request status updated to: " + status);
                else JsonUtil.sendError(res, 404, "Request not found");
            } else {
                JsonUtil.sendError(res, 400, "Request ID required");
            }
        } catch (NumberFormatException e) {
            JsonUtil.sendError(res, 400, "Invalid ID");
        } catch (SQLException e) {
            JsonUtil.sendError(res, 500, "Database error");
        }
    }
}

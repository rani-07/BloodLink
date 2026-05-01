package com.bloodlink.servlet;

import com.bloodlink.dao.DonorDAO;
import com.bloodlink.model.Donor;
import com.bloodlink.util.JsonUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DonorServlet — donor search and profile endpoints.
 *
 * GET  /api/donors              → get all donors (admin)
 * GET  /api/donors/search       → search by blood group & city
 * GET  /api/donors/{id}         → get single donor profile
 * GET  /api/donors/me           → get logged-in donor's own profile
 * PUT  /api/donors/availability → toggle availability
 * DELETE /api/donors/{id}       → delete donor (admin)
 */
@WebServlet("/api/donors/*")
public class DonorServlet extends HttpServlet {

    private final DonorDAO donorDAO = new DonorDAO();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res) throws IOException {
        JsonUtil.sendSuccess(res, "OK");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String path = req.getPathInfo(); // null, "/search", "/me", "/{id}"

        try {
            if (path == null || path.equals("/")) {
                // GET /api/donors — return all
                List<Donor> donors = donorDAO.getAll();
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", true);
                resp.put("count", donors.size());
                resp.put("donors", donors);
                JsonUtil.sendJson(res, resp);

            } else if (path.equals("/search")) {
                // GET /api/donors/search?bloodGroup=A%2B&city=Mumbai
                String bloodGroup = req.getParameter("bloodGroup");
                String city       = req.getParameter("city");
                List<Donor> donors = donorDAO.search(bloodGroup, city);
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", true);
                resp.put("count", donors.size());
                resp.put("donors", donors);
                JsonUtil.sendJson(res, resp);

            } else if (path.equals("/me")) {
                // GET /api/donors/me — logged-in user's donor profile
                HttpSession session = req.getSession(false);
                if (session == null || session.getAttribute("userId") == null) {
                    JsonUtil.sendError(res, 401, "Not logged in");
                    return;
                }
                int userId = (int) session.getAttribute("userId");
                Donor donor = donorDAO.findByUserId(userId);
                if (donor == null) {
                    JsonUtil.sendError(res, 404, "Donor profile not found");
                    return;
                }
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", true);
                resp.put("donor", donor);
                JsonUtil.sendJson(res, resp);

            } else {
                // GET /api/donors/{id}
                int id = Integer.parseInt(path.substring(1));
                Donor donor = donorDAO.findById(id);
                if (donor == null) {
                    JsonUtil.sendError(res, 404, "Donor not found");
                    return;
                }
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", true);
                resp.put("donor", donor);
                JsonUtil.sendJson(res, resp);
            }

        } catch (NumberFormatException e) {
            JsonUtil.sendError(res, 400, "Invalid donor ID");
        } catch (SQLException e) {
            e.printStackTrace();
            JsonUtil.sendError(res, 500, "Database error: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String path = req.getPathInfo();
        try {
            if ("/availability".equals(path)) {
                // PUT /api/donors/availability
                HttpSession session = req.getSession(false);
                if (session == null || session.getAttribute("userId") == null) {
                    JsonUtil.sendError(res, 401, "Not logged in");
                    return;
                }
                int userId = (int) session.getAttribute("userId");
                Donor donor = donorDAO.findByUserId(userId);
                if (donor == null) {
                    JsonUtil.sendError(res, 404, "Donor profile not found");
                    return;
                }
                Map body = JsonUtil.parseBody(req, Map.class);
                boolean available = (boolean) body.get("available");
                donorDAO.updateAvailability(donor.getId(), available);
                JsonUtil.sendSuccess(res, "Availability updated to: " + available);
            } else {
                JsonUtil.sendError(res, 404, "Endpoint not found");
            }
        } catch (SQLException e) {
            JsonUtil.sendError(res, 500, "Database error");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String path = req.getPathInfo();
        try {
            if (path != null && path.length() > 1) {
                int id = Integer.parseInt(path.substring(1));
                boolean deleted = donorDAO.delete(id);
                if (deleted) JsonUtil.sendSuccess(res, "Donor deleted");
                else JsonUtil.sendError(res, 404, "Donor not found");
            } else {
                JsonUtil.sendError(res, 400, "Donor ID required");
            }
        } catch (NumberFormatException e) {
            JsonUtil.sendError(res, 400, "Invalid ID");
        } catch (SQLException e) {
            JsonUtil.sendError(res, 500, "Database error");
        }
    }
}

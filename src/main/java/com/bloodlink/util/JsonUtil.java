package com.bloodlink.util;

import com.google.gson.Gson;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * JsonUtil — helper to send clean JSON responses from any servlet.
 */
public class JsonUtil {

    private static final Gson gson = new Gson();

    /** Send any object as a JSON response */
    public static void sendJson(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // Allow frontend (Live Server on port 5500) to call backend (Tomcat on 8080)
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.getWriter().write(gson.toJson(data));
    }

    /** Send a simple success message */
    public static void sendSuccess(HttpServletResponse response, String message) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        map.put("message", message);
        sendJson(response, map);
    }

    /** Send an error message with HTTP status code */
    public static void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("error", message);
        sendJson(response, map);
    }

    /** Parse incoming JSON request body into a Java object */
    public static <T> T parseBody(javax.servlet.http.HttpServletRequest request, Class<T> clazz) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }
        return gson.fromJson(sb.toString(), clazz);
    }
}

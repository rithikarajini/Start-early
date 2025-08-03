

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/AssignmentProgressServlet")
public class AssignmentProgressServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String jdbcURL = "jdbc:mysql://localhost:3306/start_early";
        String dbUser = "root";
        String dbPassword = "Rithika@14";

        response.setContentType("application/json");

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.getWriter().write("{\"error\": \"User not logged in\"}");
            return;
        }

        try (Connection conn = DriverManager.getConnection(jdbcURL, dbUser, dbPassword)) {
            String sql = "SELECT subject, score FROM assignment_submissions WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();

                JSONArray assignments = new JSONArray();

                while (rs.next()) {
                    JSONObject assignment = new JSONObject();
                    String subject = rs.getString("subject");
                    int score = rs.getInt("score");

                    int totalQuestions = subject.equals("English") ? 9 : 6; // English has 9, others have 6
                    int stars = (int) Math.floor((double) score / totalQuestions * 3); // Convert score to stars (max 3)

                    assignment.put("subject", subject);
                    assignment.put("stars", stars);
                    assignments.put(assignment);
                }

                response.getWriter().write(assignments.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"error\": \"Error retrieving progress\"}");
        }
    }
}

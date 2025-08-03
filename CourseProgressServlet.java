import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import org.json.JSONObject;

@WebServlet("/course_progress")
public class CourseProgressServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String jdbcURL = "jdbc:mysql://localhost:3306/start_early";
        String dbUser = "root";
        String dbPassword = "Rithika@14";

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "User not logged in");
            out.print(errorResponse);
            out.flush();
            return;
        }

        JSONObject progressData = new JSONObject();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(jdbcURL, dbUser, dbPassword)) {
                String[] subjects = { "English", "Tamil", "Hindi", "Maths" };

                String sql = "SELECT is_completed FROM assignment_submissions WHERE user_id = ? AND subject = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    for (String subject : subjects) {
                        pstmt.setInt(1, userId);
                        pstmt.setString(2, subject);
                        ResultSet rs = pstmt.executeQuery();

                        if (rs.next()) {
                            boolean isCompleted = rs.getBoolean("is_completed"); // TinyInt maps to Boolean
                            progressData.put(subject, isCompleted ? "Completed" : "Not Completed");
                        } else {
                            progressData.put(subject, "Not Completed");
                        }
                    }
                }
            }

            out.print(progressData);
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Database error: " + e.getMessage());
            out.print(errorResponse);
        }

        out.flush();
    }
}

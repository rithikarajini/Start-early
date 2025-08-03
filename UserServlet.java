

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import org.json.JSONObject;
import java.io.PrintWriter;

@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {
   
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Database connection variables
        String jdbcURL = "jdbc:mysql://localhost:3306/start_early";
        String dbUser = "root";
        String dbPassword = "Rithika@14";

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Get user ID from session
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            JSONObject error = new JSONObject();
            error.put("error", "User not logged in");
            out.print(error);
            out.flush();
            return;
        }

        try {
            // Connect to the database
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);

            // Fetch user data dynamically based on session user ID
            String query = "SELECT USERNAME, EMAIL FROM users WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();
            JSONObject user = new JSONObject();

            if (rs.next()) {
                user.put("USERNAME", rs.getString("USERNAME"));
                user.put("EMAIL", rs.getString("EMAIL"));
            } else {
                user.put("error", "User not found");
            }

            conn.close();
            out.print(user);

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("error", "Database error: " + e.getMessage());
            out.print(error);
        }

        out.flush();
    }
}


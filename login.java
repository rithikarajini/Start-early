import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.RequestDispatcher;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/login")
public class login extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String jdbcURL = "jdbc:mysql://localhost:3306/start_early";
        String dbUser = "root";
        String dbPassword = "Rithika@14";

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String sql = "SELECT id FROM users WHERE USERNAME = ? AND PASSWORD = ?";

        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            // Establish database connection
            try (Connection con = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, username);
                ps.setString(2, password);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int userId = rs.getInt("id");
                    // Create session and store user data
                    HttpSession session = request.getSession();
                    session.setAttribute("userId", userId);
                    session.setAttribute("username", username);

                    // Forward the request to HOME_PAGE.html
                    RequestDispatcher dispatcher = request.getRequestDispatcher("HOME_PAGE.html");
                    dispatcher.forward(request, response);
                } else {
                    // Login failed, forward to login page with error message
                	out.println("<html><body background=oops.jpg>");
                	out.println("<center><font color=black size=30>Oops!!!<br><br><font color=red size=6>Invalid username or password!!<br>");
                	out.println("<a href=login.html>Try again</a></center><audio autoplay><source src=uhhh.mp3 type=audio/mpeg></audio></body></html>");
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.setContentType("text/html");
            response.getWriter().println("<h3 style='color:red;'>Database error: " + e.getMessage() + "</h3>");
        }
    }
}

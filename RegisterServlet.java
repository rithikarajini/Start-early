import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String INSERT_QUERY = "INSERT INTO USERS(USERNAME, PASSWORD, EMAIL) VALUES(?, ?, ?)";
    private static final String GET_USER_ID_QUERY = "SELECT id FROM USERS WHERE USERNAME = ?";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        String jdbcURL = "jdbc:mysql://localhost:3306/start_early";
        String dbUser = "root";
        String dbPassword = "Rithika@14";

        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            try (Connection con = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
                 PreparedStatement psInsert = con.prepareStatement(INSERT_QUERY);
                 PreparedStatement psGetId = con.prepareStatement(GET_USER_ID_QUERY)) {

                // Insert new user
                psInsert.setString(1, username);
                psInsert.setString(2, password);
                psInsert.setString(3, email);
                int result = psInsert.executeUpdate();

                if (result == 1) {
                    // Retrieve user ID
                    psGetId.setString(1, username);
                    ResultSet rs = psGetId.executeQuery();

                    if (rs.next()) {
                        int userId = rs.getInt("id");

                        // Create session and store user info
                        HttpSession session = request.getSession();
                        session.setAttribute("userId", userId);
                        session.setAttribute("username", username);

                        // Forward to homepage
                        RequestDispatcher rd = request.getRequestDispatcher("HOME_PAGE.html");
                        rd.forward(request, response);
                    } else {
                        out.println("<h3 style='color:red;'>Error retrieving user data.</h3>");
                    }
                } else {
                    out.println("<h3 style='color:red;'>Registration failed. Please try again.</h3>");
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            out.println("<h3 style='color:red;'>Database error: " + e.getMessage() + "</h3>");
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doPost(request,response);
    }
}




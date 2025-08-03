import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import java.io.PrintWriter;
import org.json.JSONObject;

@WebServlet("/mathGradingServlet")
public class mathGradingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String jdbcURL = "jdbc:mysql://localhost:3306/start_early";
        String dbUser = "root";
        String dbPassword = "Rithika@14";

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

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
            int assignmentId = Integer.parseInt(request.getParameter("assignment_id"));
            String subject = "Maths";

            String[] userAnswers = {
                request.getParameter("q1"),
                request.getParameter("q2"),
                request.getParameter("q4"),
                request.getParameter("q5"),
                request.getParameter("q6"),
                request.getParameter("q7")
            };

            String[] correctAnswers = { "3", "5", "4", "10", "3", "2" };

            int score = 0;
            StringBuilder correctList = new StringBuilder();
            StringBuilder wrongList = new StringBuilder();

            for (int i = 0; i < correctAnswers.length; i++) {
                if (userAnswers[i] != null && userAnswers[i].equals(correctAnswers[i])) {
                    score++;
                    correctList.append(correctAnswers[i]).append(", ");
                } else {
                    wrongList.append(userAnswers[i] != null ? userAnswers[i] : "No Answer").append(", ");
                }
            }

            if (correctList.length() > 0) correctList.setLength(correctList.length() - 2);
            if (wrongList.length() > 0) wrongList.setLength(wrongList.length() - 2);

            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(jdbcURL, dbUser, dbPassword)) {
                String sql = "INSERT INTO assignment_submissions (user_id, assignment_id, subject, is_completed, score, correct_answers, wrong_answers, submitted_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, userId);
                    pstmt.setInt(2, assignmentId);
                    pstmt.setString(3, subject);  
                    pstmt.setInt(4, 1); 
                    pstmt.setInt(5, score);
                    pstmt.setString(6, correctList.toString());
                    pstmt.setString(7, wrongList.toString());

                    int rowsInserted = pstmt.executeUpdate();
                    JSONObject jsonResponse = new JSONObject();
                    if (rowsInserted > 0) {
                        jsonResponse.put("success", true);
                        jsonResponse.put("score", score);
                        jsonResponse.put("correct", correctList.toString());
                        jsonResponse.put("wrong", wrongList.toString());
                    } else {
                        jsonResponse.put("error", "Failed to save score.");
                    }

                    // Redirect to the result page
                    response.sendRedirect("result.html?score=" + score + "&correct=" + correctList + "&wrong=" + wrongList);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("error", "Error processing assignment: " + e.getMessage());
            out.print(error);
        }

        out.flush();
    }
}

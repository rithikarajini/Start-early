

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.json.JSONObject;
import java.io.PrintWriter;


@WebServlet("/getUsername")
public class GetUsernameServlet extends HttpServlet {
   
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject json = new JSONObject();
        json.put("username", (username != null) ? username : "Guest");

        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }
}



import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;

/**
 * Servlet implementation class chaining
 */
@WebServlet("/chaining")
public class chaining extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String srch=request.getParameter("search");
        if (srch != null) {
            RequestDispatcher rd = null;
		  if (srch.equalsIgnoreCase("Maths")) {
              rd = request.getRequestDispatcher("maths.html");
          } else if (srch.equalsIgnoreCase("Hindi")) {
              rd = request.getRequestDispatcher("hindi.html");
          } else if (srch.equalsIgnoreCase("English")) {
              rd = request.getRequestDispatcher("eng.html");
          } else if (srch.equalsIgnoreCase("Tamil")) {
              rd = request.getRequestDispatcher("tamil.html");
          }

          if (rd != null) {
              rd.forward(request, response); // Forward to the requested page
          } else {
              response.getWriter().println("<html><body background=oops.jpg><center><br><br><h1>Subject not found! Please check your search query.</h1></center>"
              		+ "<audio autoplay><source src=uhhh.mp3 type=audio/mpeg></audio></body></html>");
          }
      } else {
          response.getWriter().println("<h3>No search query provided!</h3>");
      }
	}

}

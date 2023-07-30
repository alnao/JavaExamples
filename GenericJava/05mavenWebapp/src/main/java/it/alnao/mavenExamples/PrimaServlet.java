package it.alnao.mavenExamples;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class PrimaServlet
 */
public class PrimaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 private static final String HTML_TOP = "<html><head><title>Primo esempio servlet: tabelline</title></head><body>";
	 private static final String HTML_BOTTOM = "</body></html>";
	 
	 private static final String TABLE_TOP = "<h3>Tabelline</h3><table width='80%'>";
	 private static final String TABLE_BOTTOM = "</table>";
    /**
     * Default constructor. 
     */
    public PrimaServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		request.setAttribute("nomeInRequest", "Alberto");
		request.getSession().setAttribute("cognomeInSessione","Nao");
		request.getRequestDispatcher("prova.jsp").forward(request, response);
		
		/*
	  response.setContentType("text/html");
	  PrintWriter out = response.getWriter();
	  out.println(HTML_TOP);
	  out.println(TABLE_TOP);
	  for (int i=2;i<=12;i++){
	    out.println("<tr><td><b>"+i+"</b></td>");
	    for (int j=2;j<=10;j++){
	      out.println("<td>"+(i*j)+"</td>");
	    }
	    out.println("</tr>");
	  } 
	  out.println(TABLE_BOTTOM);
	  out.println(HTML_BOTTOM);
	  */
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

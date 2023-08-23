package it.alnao.mavenExamples;
import java.util.Calendar;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.Tag;

public class TagCommentoSenzaParametri extends TagSupport implements Tag {
    private static final long serialVersionUID = 1L;

    public int doStartTag() throws JspException {
      //HttpSession session = pageContext.getSession();
      //pageContext.getRequest().setAttribute("Nome", valore);
      String s="\n\n<!-- prova tag AlNao.it -->\n\n" + Calendar.getInstance().getTime();
      try { 
        JspWriter out = pageContext.getOut();
        out.println(s); 
      } catch (Exception e) { 
        e.printStackTrace(); 
      } 
      return SKIP_BODY;
    }
   }
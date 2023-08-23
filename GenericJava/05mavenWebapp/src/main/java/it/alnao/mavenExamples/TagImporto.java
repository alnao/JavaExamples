package it.alnao.mavenExamples;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

public class TagImporto extends BodyTagSupport implements Tag{
	
	private static final long serialVersionUID=1L;
	public String getPositiveStyle() {
		return positiveStyle;
	}

	public void setPositiveStyle(String positiveStyle) {
		this.positiveStyle = positiveStyle;
	}

	public String getNegativeStyle() {
		return negativeStyle;
	}

	public void setNegativeStyle(String negativeStyle) {
		this.negativeStyle = negativeStyle;
	}

	private String positiveStyle = null;
	private String negativeStyle = null;
	private String bodyTag = null;
	public int doStartTag() throws JspException {
	  return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() throws JspException {
	   bodyTag = getBodyContent().getString().trim();
	   if (bodyTag != null) {
	   Number decimal;
	   DecimalFormat df = new DecimalFormat();
	   try {
		decimal = df.parse(bodyTag);
		} catch (ParseException e) {
			decimal=null;
			e.printStackTrace();
		}
	   if (decimal != null) {
	     if (decimal.doubleValue() > 0) { //positivo
	       bodyTag = "<span class=\"" + getPositiveStyle() + "\">";//+ bodyTag
	     } else {
	       if (decimal.doubleValue() < 0) { //negativo
	         bodyTag = "<span class=\"" + getNegativeStyle() + "\">";//+ bodyTag
	       } else { //zero
	         bodyTag = "<span class=\"defaultClass\">" ;//+ bodyTag
	       }
	     }
	   } else { //non è un numero
	     bodyTag = "<span>";
	   }
	   }
	   return (SKIP_BODY);
	 }

	 public int doEndTag() throws JspException {
	   bodyTag = bodyTag + "</span>";
	   try {
	     this.pageContext.getOut().print(this.bodyTag);
	   } catch (IOException ioe) {
	     throw new JspException(ioe);
	   }
	   return EVAL_PAGE;
	 }
}
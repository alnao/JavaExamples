package it.alnao.mavenExamples;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class PrimaAction extends Action  {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		PrimoForm loginForm = (PrimoForm) form;
		
		request.getSession().setAttribute(Globals.LOCALE_KEY, request.getLocale());
		
		if (loginForm.getUserName() == null || loginForm.getPassword() == null
				|| !loginForm.getUserName().equalsIgnoreCase("alnao")
				|| !loginForm.getPassword().equals("bellissimo")) {
			return mapping.findForward("failure");
		} else {
			request.setAttribute("nomeUtente", "Alberto Nao");
			PrimoBean b= new PrimoBean (loginForm.getUserName());
			request.setAttribute("oggetto", b);
			return mapping.findForward("success");
		}
	}
}

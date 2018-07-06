package controller.access;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.repackaged.org.joda.time.DateTimeZone;
import com.google.appengine.repackaged.org.joda.time.LocalDateTime;

import pmf.entity.*;
import model.entity.Access;
import model.entity.Resource;
import model.entity.User;

public class AccessControllerEdit extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		com.google.appengine.api.users.User uGoogle =UserServiceFactory.getUserService().getCurrentUser();
		if(uGoogle==null){
			RequestDispatcher dp= getServletContext().getRequestDispatcher("/WEB-INF/Views/Errors/error.jsp");
			dp.forward(request, response);
		}
		else{
			PersistenceManager accesoControlador=PMF.get().getPersistenceManager();
			String qUsers="select from "+ User.class.getName()+" where email=='"+uGoogle.getEmail()+"' && status==true";
			List<User> uSearch=(List<User>) accesoControlador.newQuery(qUsers).execute();
			if(uSearch.isEmpty()){	
				RequestDispatcher dp= getServletContext().getRequestDispatcher("/WEB-INF/Views/Errors/error2.jsp");
				dp.forward(request, response);
			}else{
				String query2="select from "+ Resource.class.getName()
						+" where url == '"+request.getServletPath()+"' && status==true";
				List <Resource> rSearch=(List<Resource>) accesoControlador.newQuery(query2).execute();
				if(rSearch.isEmpty()){
					RequestDispatcher dp= getServletContext().getRequestDispatcher("/WEB-INF/Views/Errors/error3.jsp");
					dp.forward(request, response);
				}
				else{
					String query3 = "select from "+Access.class.getName()
							+" where idRole == "+uSearch.get(0).getIdRole()+" && idUrl== "+rSearch.get(0).getId()+" && status==true";	
							List <Access> aSearch=(List<Access>) accesoControlador.newQuery(query3).execute();		
				if(aSearch.isEmpty()){
					RequestDispatcher dp= getServletContext().getRequestDispatcher("/WEB-INF/Views/Errors/error4.jsp");
					dp.forward(request, response);
				}else{
					accesoControlador.close();
				}
				}
			}
		}
		
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key k =	KeyFactory.createKey(Access.class.getSimpleName(), new Long(request.getParameter("id")).longValue());
		Access a = pm.getObjectById(Access.class, k);
		request.setAttribute("access", a);
		pm.close();
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/Views/Access/edit.jsp");
		dispatcher.forward(request, response);
		
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String status = request.getParameter("status");
	
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key k =	KeyFactory.createKey(Access.class.getSimpleName(), new Long(request.getParameter("id")).longValue());
		Access a = pm.getObjectById(Access.class, k);
		boolean prueba = false;
		if(status.equalsIgnoreCase("true")){
			prueba = true;
		}
		if(a.isStatus() != prueba){
			try {
				a.updateStatus(prueba);
			} finally {
				pm.close();
			}
		}
		response.sendRedirect("/access");
		pm.close();
	}
	
}

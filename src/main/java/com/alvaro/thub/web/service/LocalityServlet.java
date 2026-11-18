package com.alvaro.thub.web.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.alvaro.thub.model.LocalityDTO;
import com.alvaro.thub.service.LocalityService;
import com.alvaro.thub.service.impl.LocalityServiceImpl;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/locality")
public class LocalityServlet extends HttpServlet {       
 
	private LocalityService localityService = null;
	private Gson gson = null; 
	
    public LocalityServlet() {
        super();
        localityService = new LocalityServiceImpl();
        gson = new Gson();
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String idProvinciaStr = request.getParameter("provinceId");
		Long provinceId = Long.valueOf(idProvinciaStr);
		
		List<LocalityDTO> localidades = localityService.findByProvinceId(provinceId);		        
				
        String json = gson.toJson(localidades);
        
        response.setContentType("application/json");
		OutputStream os = response.getOutputStream();		
		os.write(json.getBytes());
		os.flush();
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

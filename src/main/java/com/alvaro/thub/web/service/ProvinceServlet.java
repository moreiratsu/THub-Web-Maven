package com.alvaro.thub.web.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.alvaro.thub.model.ProvinceDTO;
import com.alvaro.thub.service.ProvinceService;
import com.alvaro.thub.service.impl.ProvinceServiceImpl;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/province")
public class ProvinceServlet extends HttpServlet {

	private ProvinceService provinceService = null;
	private Gson gson = null;
	
    public ProvinceServlet() {
        super();
        provinceService = new ProvinceServiceImpl();
        gson = new Gson();
    }

 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String idPaisStr = request.getParameter("id-pais");
		Long idPais = Long.valueOf(idPaisStr);
		
		List<ProvinceDTO> provinces = provinceService.findByCountry(idPais);		        
				
        String json = gson.toJson(provinces);
        
        response.setContentType("application/json");
		OutputStream os = response.getOutputStream();		
		os.write(json.getBytes());
		os.flush();
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

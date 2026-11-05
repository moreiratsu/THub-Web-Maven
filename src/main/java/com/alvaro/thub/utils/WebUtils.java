package com.alvaro.thub.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileUploadException;
import org.apache.commons.fileupload2.jakarta.servlet5.JakartaServletDiskFileUpload;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Clase de utilidades para manejar solicitudes web, especialmente para la carga de archivos.
 */

public class WebUtils {
	
	public static byte[] leerBytes(HttpServletRequest request, String fileParameterName)
	        throws ServletException, IOException {

	    if (!JakartaServletDiskFileUpload.isMultipartContent(request)) {
	        throw new ServletException("La petición no es multipart/form-data.");
	    }

	    DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
	    JakartaServletDiskFileUpload upload =
	            new JakartaServletDiskFileUpload(factory);

	    // Límite de 2 MB para la foto y 3 MB para toda la petición.
	    upload.setMaxFileSize(2L * 1024 * 1024);
	    upload.setMaxSize(3L * 1024 * 1024);

	    try {
	        List<DiskFileItem> elementos = upload.parseRequest(request);

	        for (DiskFileItem elemento : elementos) {
	            if (!elemento.isFormField()
	                    && fileParameterName.equals(elemento.getFieldName())) {

	                if (elemento.getSize() == 0) {
	                    return null; // No se seleccionó ningún archivo.
	                }

	                try (InputStream entrada = elemento.getInputStream()) {
	                    return entrada.readAllBytes();
	                }
	            }
	        }

	        return null; // No se encontró un campo llamado "photo".

	    } catch (FileUploadException e) {
	        throw new ServletException("No se pudo procesar la subida.", e);
	    } catch (IOException e) {
	        throw new ServletException("No se pudieron leer los bytes de la foto.", e);
	    }
	}
}

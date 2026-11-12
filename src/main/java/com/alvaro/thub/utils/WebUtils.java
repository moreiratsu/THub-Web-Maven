package com.alvaro.thub.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;

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
	
	// metodo para leer los bytes de un archivo (solo los lee cuando hay un archivo en un formulario, si hubiera mas elementos en el formulario no funcionaria)
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
	
	
	// metodo que devuelve la extension de un archivo permitido. Si no lo esta devuelve null.COMPRUEBA EL NOMBRE, NO COMPRUEBA SI ES UNA IMAGEN
	public static String obtenerFormatoImagenValido(String nombreArchivo) {
	    if (nombreArchivo == null) {
	        return null;
	    }

	    int posicionPunto = nombreArchivo.lastIndexOf(".");

	    if (posicionPunto < 0) {
	        return null;
	    }

	    String formato = nombreArchivo
	            .substring(posicionPunto)
	            .toLowerCase(java.util.Locale.ROOT);

	    if (formato.equals(".png")
	            || formato.equals(".jpg")
	            || formato.equals(".jpeg")) {
	        return formato;
	    }

	    return null;
	}
	
	
	// ahora usando el metodo anterior tambien compruebo si es realmente una imagen (para evitar por ejemplo que alguien suba un texto con nomobre "imagen.png")
	public static String validarImagen(String nombreArchivo, byte[] bytes) throws IOException {
	    String formato = obtenerFormatoImagenValido(nombreArchivo);

	    if (formato == null || bytes == null || bytes.length == 0) {
	        return null;
	    }

	    try (ByteArrayInputStream entrada = new ByteArrayInputStream(bytes)) {
	        if (ImageIO.read(entrada) == null) {
	            return null;
	        }
	    }

	    return formato;
	}
}

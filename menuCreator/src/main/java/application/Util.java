package application;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Util {

	public static CatalogoResponse cargarCatalogo() throws StreamReadException, DatabindException, IOException {
		ObjectMapper mapper = new ObjectMapper(); 
		InputStream inputStream = Util.class.getResourceAsStream("/catalogProductos.json"); 
		if (inputStream == null) { 
			throw new FileNotFoundException("Resource not found: catalogProductos.json");        
		}
		
		CatalogoResponse catalogoResponse = mapper.readValue(inputStream, CatalogoResponse.class);
        // Mostrar los datos
        for (Catalogo catalogo : catalogoResponse.getCatalogo()) {
            System.out.println("Catalogo: " + catalogo.getCodigo() + " - " + catalogo.getNombre());
            for (Producto producto : catalogo.getProductos()) {
                System.out.println("  Producto: " + producto.getCodigo() + " - " + producto.getNombre() + " - Precio: " + producto.getPrecio());
            }
        }
        
        return catalogoResponse;
	}
	
	public static List<Catalogo> filterCatalog(List<Catalogo> catalogos, String query) {
	    if (query == null || query.isEmpty()) {
	        return catalogos; // Si no hay texto, devolver lista completa
	    }

	    return catalogos.stream()
	            .map(catalogo -> {
	                // Verificar si el nombre del catálogo coincide
	                boolean matchesCatalogName = catalogo.getNombre() != null && catalogo.getNombre().toLowerCase().contains(query.toLowerCase());
	                
	                if (matchesCatalogName) {
	                    // Si coincide el nombre del catálogo, devolverlo con todos sus productos
	                    return catalogo;
	                } else {
	                    // Si no coincide, filtrar los productos que coinciden
	                    List<Producto> filteredProducts = catalogo.getProductos().stream()
	                            .filter(
	                             p -> p.getNombre() != null && p.getNombre().toLowerCase().contains(query.toLowerCase())
	                            				||
	                             p.getCodigo() != null && p.getCodigo().toLowerCase().contains(query.toLowerCase())
	                             )
	                            .collect(Collectors.toList());

	                    // Si hay productos que coinciden, devolver un catálogo con solo esos productos
	                    if (!filteredProducts.isEmpty()) {
	                        Catalogo filteredCatalog = new Catalogo();
	                        filteredCatalog.setCodigo(catalogo.getCodigo());
	                        filteredCatalog.setNombre(catalogo.getNombre());
	                        filteredCatalog.setProductos(filteredProducts);
	                        return filteredCatalog;
	                    }
	                }
	                return null; // Si no coincide ni el catálogo ni los productos, ignorarlo
	            })
	            .filter(c -> c != null) // Eliminar catálogos nulos
	            .collect(Collectors.toList());
	}
	
}

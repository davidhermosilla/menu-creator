package application;

import java.util.List;

public class Catalogo {
    private String codigo;
    private String nombre;
    private List<Producto> productos;

    // Getters y Setters
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
	public String toString() {
		return "Catalogo [codigo=" + codigo + ", nombre=" + nombre + ", productos=" + productos + "]";
	}

	public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
}


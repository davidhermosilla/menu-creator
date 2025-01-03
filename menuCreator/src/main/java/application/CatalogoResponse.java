package application;

import java.util.List;

public class CatalogoResponse {
    private List<Catalogo> catalogo;

    // Getter y Setter
    public List<Catalogo> getCatalogo() {
        return catalogo;
    }

    public void setCatalogo(List<Catalogo> catalogo) {
        this.catalogo = catalogo;
    }
}

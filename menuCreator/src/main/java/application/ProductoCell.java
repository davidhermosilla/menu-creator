package application;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ProductoCell extends ListCell<Producto> {

    private HBox content;
    private Label nombre;
    private ComboBox<Integer> cantidadCombo;
    private Label total;
    
    public ProductoCell() {
        super();
        
        nombre = new Label();
        cantidadCombo = new ComboBox<>();
        for (int i = 1; i <= 10; i++) {
            cantidadCombo.getItems().add(i);
        }
        cantidadCombo.setValue(1);
        
        total = new Label();
        cantidadCombo.setOnAction(e -> updateTotal());
        
        VBox vBox = new VBox();
        HBox.setHgrow(vBox, Priority.ALWAYS);
        
        content = new HBox(nombre, cantidadCombo, total);
        content.setSpacing(10);
    }

    private void updateTotal() {
        Producto producto = getItem();
        if (producto != null) {
            double totalValue = cantidadCombo.getValue() * producto.getPrecio();
            total.setText(String.format("%.2f", totalValue));
        }
    }

    @Override
    protected void updateItem(Producto producto, boolean empty) {
        super.updateItem(producto, empty);
        if (empty || producto == null) {
            setGraphic(null);
        } else {
            nombre.setText(producto.getNombre());
            updateTotal();
            setGraphic(content);
        }
    }
}


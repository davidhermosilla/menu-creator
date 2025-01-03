package application;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class ProductController {

	public CatalogoResponse catalogoGlobal;

	public int iva = 21;
	
	@FXML
	private Accordion catalogAccordion;
	
	@FXML
	private ScrollPane scrollPaneCatalogId;

	@FXML
	private TextField fieldBuscar;
	
	@FXML
	private TextField totalSinIva;
	
	@FXML
	private TextField totalConIva;
	
	@FXML
	private VBox vBox;
	
	@FXML
    private TableView<Producto> productTable;

    @FXML
    private TableColumn<Producto, String> codeColumn;

    @FXML
    private TableColumn<Producto, String> nameColumn;

    @FXML
    private TableColumn<Producto, Double> priceColumn;
    
	@FXML
	public void initialize() {
		try {
	        // Configurar columnas del TableView
	        codeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCodigo()));
	        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
	        priceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrecio()).asObject());

	        // Configurar datos iniciales si es necesario
	        productTable.setItems(FXCollections.observableArrayList());
	        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	        
	        // Opcional: Establecer mínimos
	        codeColumn.setMinWidth(100);
	        nameColumn.setMinWidth(200);
	        priceColumn.setMinWidth(100);

			catalogoGlobal = Util.cargarCatalogo();
		} catch (IOException e) {
			e.printStackTrace();
		}
		loadCatalogo(null);
	}

    public void addProductToMenu(Producto producto) {
        productTable.getItems().add(producto);
    }
    
	@FXML
	private void handleDragDetected(MouseEvent event) {
		ListView<Producto> sourceList = (ListView<Producto>) event.getSource();
		Producto selectedItem = sourceList.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			Dragboard db = sourceList.startDragAndDrop(TransferMode.COPY);
			ClipboardContent content = new ClipboardContent();
			try {
				content.putString(selectedItem.toJSON());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			db.setContent(content);
			event.consume();
		}
	}

	@FXML
	private void handleDragOver(DragEvent event) {
		if (event.getGestureSource() instanceof ListView && event.getDragboard().hasString()) {
			event.acceptTransferModes(TransferMode.COPY);
		}
		event.consume();
	}

	@FXML
	private void handleDragDropped(DragEvent event) {
	    Dragboard db = event.getDragboard();
	    boolean success = false;
	    if (db.hasString()) {
	        try {
	            Producto newProducto = Producto.fromJSON(db.getString());
				// Crear el ContextMenu para eliminar elementos
				ContextMenu contextMenu = new ContextMenu();
				MenuItem deleteItem = new MenuItem("Eliminar");
				deleteItem.setOnAction(eventDelete -> {
					Producto selectedItem = productTable.getSelectionModel().getSelectedItem();
					if (selectedItem != null) {
						productTable.getItems().remove(selectedItem);
						
						double totalSinIvaSum = Double.parseDouble(totalSinIva.getText()!=null && !"".equals(totalSinIva.getText())?totalSinIva.getText():"0");
			            
			            totalSinIvaSum -= selectedItem.getPrecio();
			            
			            double valor = totalSinIvaSum;
			            double totalSinIvaSumRedondeado = Math.round(valor * 100.0) / 100.0;
			            
			            totalSinIva.setText(String.valueOf(totalSinIvaSumRedondeado));
			            
			            double totalConIvaSum = ((totalSinIvaSum*iva)/100)+totalSinIvaSum;
			            double totalConIvaSumRedondeado = Math.round(totalConIvaSum * 100.0) / 100.0;
			            
			            totalConIva.setText(String.valueOf(totalConIvaSumRedondeado));
					}
				});
				contextMenu.getItems().add(deleteItem);
				productTable.setContextMenu(contextMenu);
				
	            productTable.getItems().add(newProducto); // Añadir al TableView
	            
	            List<Catalogo> catalogoProducto = Util.filterCatalog(catalogoGlobal.getCatalogo(), newProducto.getCodigo());
	            System.out.println(catalogoProducto);
	            
	            double totalSinIvaSum = Double.parseDouble(totalSinIva.getText()!=null && !"".equals(totalSinIva.getText())?totalSinIva.getText():"0");
	            
	            totalSinIvaSum += newProducto.getPrecio();
	            
	            totalSinIva.setText(String.valueOf(totalSinIvaSum));
	            
	            double totalConIvaSum = ((totalSinIvaSum*iva)/100)+totalSinIvaSum;
	            double totalConIvaSumRedondeado = Math.round(totalConIvaSum * 100.0) / 100.0;
	            
	            totalConIva.setText(String.valueOf(totalConIvaSumRedondeado));
	            
	            success = true;
	        } catch (JsonProcessingException e) {
	            e.printStackTrace();
	        }
	    }
	    event.setDropCompleted(success);
	    event.consume();
	}
	
	
	@FXML
	private void handleDragDone(DragEvent event) {
		event.consume();
	}

	@FXML
	private void filterCatalog(KeyEvent event) {
		String query = fieldBuscar.getText();
        List<Catalogo> filteredCatalogs = Util.filterCatalog(catalogoGlobal.getCatalogo(), query);
        loadCatalogo(filteredCatalogs);
	}	
	
	@SuppressWarnings("unchecked")
	public void loadCatalogo(List<Catalogo> catalogo) {
		
		catalogAccordion.getPanes().clear();
		
		if (catalogo==null) {
			catalogo= catalogoGlobal.getCatalogo();
		}
		
		for (Catalogo catalogoItem : catalogo) {
			ListView<Producto> listView = new ListView<>();
			listView.setId(catalogoItem.getCodigo() + "List");

			listView.setOnDragDetected(this::handleDragDetected);
			listView.setOnDragOver(this::handleDragOver);
			ObservableList<Producto> items = FXCollections.observableArrayList();
			for (Producto producto : catalogoItem.getProductos()) {
				items.add(producto);
			}
			listView.setItems(items);

			// Configurar la celda personalizada para mostrar solo el nombre
			listView.setCellFactory(new Callback<>() {
				@Override
				public ListCell<Producto> call(ListView<Producto> param) {
					return new ListCell<>() {
						@Override
						protected void updateItem(Producto item, boolean empty) {
							super.updateItem(item, empty);
							if (empty || item == null) {
								setText(null);
							} else {
								setText(item.getNombre()); // Mostrar solo el nombre
							}
						}
					};
				}
			});

			TitledPane newPane = new TitledPane();
			newPane.setText(catalogoItem.getNombre());
			newPane.setContent(listView);
			newPane.setId(catalogoItem.getCodigo());

			catalogAccordion.getPanes().add(newPane);
			
		    // Forzar actualización del layout
//		    catalogAccordion.applyCss();
//		    catalogAccordion.layout();
//		    scrollPaneCatalogId.layout();
		}
		
	}
	
}

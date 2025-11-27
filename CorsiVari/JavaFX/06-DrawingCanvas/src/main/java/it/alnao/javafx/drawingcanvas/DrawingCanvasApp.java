package it.alnao.javafx.drawingcanvas;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Progetto 06 - Drawing Canvas
 * Concetto principale: Canvas e GraphicsContext
 * 
 * Applicazione per disegnare liberamente su un canvas
 * con diverse forme e colori.
 */
public class DrawingCanvasApp extends Application {
    
    private Canvas canvas;
    private GraphicsContext gc;
    private Color currentColor = Color.web("#0d6efd");
    private double brushSize = 5.0;
    private String currentTool = "Pen";

    @Override
    public void start(Stage primaryStage) {
        // Canvas per il disegno
        canvas = new Canvas(700, 500);
        gc = canvas.getGraphicsContext2D();
        
        // Riempi il canvas di bianco
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Bordo del canvas
        gc.setStroke(Color.web("#ced4da"));
        gc.setLineWidth(2);
        gc.strokeRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Setup mouse events
        setupMouseEvents();

        // Toolbar
        HBox toolbar = createToolbar();

        // Layout
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f8f9fa;");
        
        Label titleLabel = new Label("🎨 Drawing Canvas");
        titleLabel.setStyle(
            "-fx-font-size: 32px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #0d6efd;"
        );

        root.getChildren().addAll(titleLabel, toolbar, canvas);

        Scene scene = new Scene(root, 800, 650);
        primaryStage.setTitle("Drawing Canvas - Canvas API Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Setup eventi del mouse per disegnare
     */
    private void setupMouseEvents() {
        canvas.setOnMousePressed(e -> {
            gc.setStroke(currentColor);
            gc.setLineWidth(brushSize);
            gc.beginPath();
            gc.moveTo(e.getX(), e.getY());
            gc.stroke();
        });

        canvas.setOnMouseDragged(e -> {
            if ("Pen".equals(currentTool)) {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            } else if ("Eraser".equals(currentTool)) {
                double half = brushSize / 2;
                gc.clearRect(e.getX() - half, e.getY() - half, brushSize, brushSize);
            }
        });

        canvas.setOnMouseReleased(e -> {
            if ("Circle".equals(currentTool)) {
                gc.setFill(currentColor);
                gc.fillOval(e.getX() - 25, e.getY() - 25, 50, 50);
            } else if ("Rectangle".equals(currentTool)) {
                gc.setFill(currentColor);
                gc.fillRect(e.getX() - 30, e.getY() - 20, 60, 40);
            }
        });
    }

    /**
     * Crea la toolbar con strumenti
     */
    private HBox createToolbar() {
        HBox toolbar = new HBox(15);
        toolbar.setAlignment(Pos.CENTER);
        toolbar.setPadding(new Insets(10));
        toolbar.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);"
        );

        // Pulsanti strumenti
        Button penBtn = createToolButton("✏️ Penna", "Pen");
        Button eraserBtn = createToolButton("🧹 Gomma", "Eraser");
        Button circleBtn = createToolButton("⭕ Cerchio", "Circle");
        Button rectBtn = createToolButton("▭ Rettangolo", "Rectangle");

        // ColorPicker
        ColorPicker colorPicker = new ColorPicker(currentColor);
        colorPicker.setOnAction(e -> currentColor = colorPicker.getValue());

        // Slider dimensione pennello
        Label sizeLabel = new Label("Dimensione:");
        Slider sizeSlider = new Slider(1, 20, brushSize);
        sizeSlider.setShowTickMarks(true);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setMajorTickUnit(5);
        sizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
            brushSize = newVal.doubleValue()
        );

        // Pulsante cancella tutto
        Button clearBtn = new Button("🗑️ Cancella");
        clearBtn.setStyle(
            "-fx-background-color: #dc3545; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 8px 15px; " +
            "-fx-background-radius: 5px; " +
            "-fx-cursor: hand;"
        );
        clearBtn.setOnAction(e -> {
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setStroke(Color.web("#ced4da"));
            gc.setLineWidth(2);
            gc.strokeRect(0, 0, canvas.getWidth(), canvas.getHeight());
        });

        toolbar.getChildren().addAll(
            penBtn, eraserBtn, circleBtn, rectBtn, 
            new Separator(), colorPicker, 
            new Separator(), sizeLabel, sizeSlider, 
            new Separator(), clearBtn
        );

        return toolbar;
    }

    /**
     * Crea un pulsante strumento
     */
    private Button createToolButton(String text, String tool) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: #0d6efd; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 8px 15px; " +
            "-fx-background-radius: 5px; " +
            "-fx-cursor: hand;"
        );
        
        btn.setOnAction(e -> {
            currentTool = tool;
            System.out.println("Strumento selezionato: " + tool);
        });
        
        return btn;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package it.alnao.javafx.photocarousel.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.File;

/**
 * Visual card representing a photo in the carousel.
 * Supports standard mode, overlap 10% mode, height clamping, and centered vertical overflow.
 */
public class PhotoCard extends VBox {

    private final File imageFile;
    private final ImageView imageView;
    private final Label nameLabel;
    private final boolean overlap10;

    public PhotoCard(File file, double targetWidth, double maxHeight, boolean overlap10, boolean allowOverflow) {
        this.imageFile = file;
        this.overlap10 = overlap10;

        setAlignment(Pos.CENTER);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(overlap10 ? 12 : 15);
        dropShadow.setOffsetX(overlap10 ? 4 : 0);
        dropShadow.setOffsetY(5);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.4));
        setEffect(dropShadow);

        Image image = new Image(file.toURI().toString(), 0, 0, true, true);
        imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        if (overlap10) {
            setPadding(Insets.EMPTY);
            setStyle("-fx-background-color: transparent;");
            nameLabel = null;
            getChildren().add(imageView);
        } else {
            setPadding(new Insets(10));
            setStyle("-fx-background-color: #313244; -fx-background-radius: 12px; -fx-border-radius: 12px; -fx-border-color: #45475a; -fx-border-width: 1px;");

            nameLabel = new Label(file.getName());
            nameLabel.setStyle("-fx-text-fill: #cdd6f4; -fx-font-size: 12px; -fx-font-weight: 600; -fx-padding: 8 0 0 0;");
            nameLabel.setAlignment(Pos.CENTER);

            getChildren().addAll(imageView, nameLabel);
        }

        updateTargetSize(targetWidth, maxHeight, allowOverflow);
    }

    public void updateTargetSize(double targetWidth, double maxHeight, boolean allowOverflow) {
        if (targetWidth <= 0) return;

        double effectiveWidth = overlap10 ? targetWidth : Math.max(10, targetWidth - 20);

        if (allowOverflow) {
            // Mode "Permetti sbordo": Keep full target width, unconstrained height centered vertically
            imageView.setFitWidth(effectiveWidth);
            imageView.setFitHeight(0);
            setMaxHeight(Double.MAX_VALUE);
        } else {
            // Mode clamped height: Clamp width based on aspect ratio so height does not exceed maxHeight
            double effectiveMaxHeight = maxHeight > 0 ? (overlap10 ? maxHeight : Math.max(10, maxHeight - 45)) : 0;

            Image img = imageView.getImage();
            if (img != null && img.getWidth() > 0 && img.getHeight() > 0 && effectiveMaxHeight > 0) {
                double aspectRatio = img.getWidth() / img.getHeight();
                double maxAllowedWidth = effectiveMaxHeight * aspectRatio;

                if (effectiveWidth > maxAllowedWidth) {
                    effectiveWidth = maxAllowedWidth;
                }
            }

            if (effectiveMaxHeight > 0) {
                imageView.setFitHeight(effectiveMaxHeight);
                setMaxHeight(maxHeight);
            }
            imageView.setFitWidth(effectiveWidth);
        }

        if (nameLabel != null) {
            nameLabel.setMaxWidth(effectiveWidth);
        }
    }

    public File getImageFile() {
        return imageFile;
    }
}

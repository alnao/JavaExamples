package it.alnao.javafx.pathsgames;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PathsBookCardsApp extends Application {

    private static final int OPTION_COUNT = 4;

    private final Random random = new Random();

    private final List<GameCard> deck = List.of(
        new GameCard("Sentiero delle Ceneri", "Rischio", "Attraversi un bosco bruciato: guadagni visione, perdi energia.", "+2 Visione, -1 Energia"),
        new GameCard("Locanda del Corvo", "Supporto", "Ti fermi alla locanda e raccogli voci utili sui mercanti.", "+1 Informazione, +1 Riposo"),
        new GameCard("Ponte Spezzato", "Sfida", "Un ponte instabile: puoi passare veloce o trovare una via lunga.", "Tiro Agilita oppure +1 Tempo"),
        new GameCard("Mercato Segreto", "Scelta", "Tra banchi nascosti trovi oggetti rari ma costosi.", "Compra reliquia o conserva monete"),
        new GameCard("Cappella Silente", "Mistero", "Le candele si accendono da sole e rivelano simboli antichi.", "+1 Fato, evento futuro alterato"),
        new GameCard("Fiume Nebbioso", "Esplorazione", "La nebbia copre tutto: segui l'istinto o la corrente.", "Nuovo bivio narrativo"),
        new GameCard("Torre del Custode", "Evento", "Il custode offre una prova in cambio di accesso agli archivi.", "Sblocca conoscenza rara"),
        new GameCard("Radura dei Sussurri", "Arcano", "Le piante reagiscono alle tue domande con echi lontani.", "Visione parziale della mappa")
    );

    private final Label leftTitle = new Label();
    private final Label leftTag = new Label();
    private final Label leftDescription = new Label();
    private final Label leftOutcome = new Label();

    private final Label rightTitle = new Label();
    private final Label rightTag = new Label();
    private final Label rightDescription = new Label();
    private final Label rightOutcome = new Label();

    private final VBox optionsContainer = new VBox(10);
    private final Button executeButton = new Button("Esegui");

    private final List<OptionCardView> optionViews = new ArrayList<>();

    private VBox leftPage;

    private GameCard currentLeftCard;
    private GameCard selectedCard;
    private boolean turningPage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label title = new Label("Paths Book Cards");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Pagina sinistra: evento corrente. Pagina destra: opzioni selezionabili.");
        subtitle.getStyleClass().add("page-subtitle");

        VBox header = new VBox(2, title, subtitle);

        leftPage = buildPage("Scena Corrente", leftTitle, leftTag, leftDescription, leftOutcome);
        leftPage.getStyleClass().add("page-left");

        VBox rightPreview = buildPage("Anteprima Opzione", rightTitle, rightTag, rightDescription, rightOutcome);
        rightPreview.getStyleClass().add("page-right");
        rightPreview.getStyleClass().add("card-preview");

        Label optionsCaption = new Label("Opzioni disponibili");
        optionsCaption.getStyleClass().add("section-caption");

        Label hint = new Label("Suggerimento: clicca la card o la (i) per anteprima, poi premi Esegui.");
        hint.getStyleClass().add("hint-label");

        executeButton.getStyleClass().add("action-button");
        executeButton.setDisable(true);
        executeButton.setOnAction(event -> executeSelected());

        VBox rightControls = new VBox(12, rightPreview, optionsCaption, optionsContainer, hint, executeButton);
        rightControls.setFillWidth(true);
        VBox.setVgrow(optionsContainer, Priority.ALWAYS);

        VBox leftPanel = new VBox(leftPage);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightControls, Priority.ALWAYS);

        Region spine = new Region();
        spine.getStyleClass().add("book-spine");

        HBox bookPages = new HBox(20, leftPanel, spine, rightControls);
        bookPages.setAlignment(Pos.TOP_CENTER);

        VBox bookShell = new VBox(20, header, bookPages);
        bookShell.getStyleClass().add("book-shell");

        StackPane root = new StackPane(bookShell);
        root.setPadding(new Insets(22));

        Scene scene = new Scene(root, 1320, 820);
        scene.getStylesheets().add(Objects.requireNonNull(
            getClass().getResource("/it/alnao/javafx/pathsgames/book-cards.css")).toExternalForm());

        stage.setTitle("Paths Book Cards - JavaFX");
        stage.setScene(scene);
        stage.setMinWidth(1100);
        stage.setMinHeight(760);

        bootstrapState();

        stage.show();
    }

    private VBox buildPage(String caption, Label title, Label tag, Label description, Label outcome) {
        Label captionLabel = new Label(caption);
        captionLabel.getStyleClass().add("section-caption");

        title.getStyleClass().add("card-large-title");
        tag.getStyleClass().add("card-large-tag");
        description.getStyleClass().add("card-large-desc");
        description.setWrapText(true);

        Text divider = new Text("---------------");
        divider.setFill(Color.rgb(92, 51, 23, 0.45));

        Label outcomeLabel = new Label("Effetto:");
        outcomeLabel.getStyleClass().add("section-caption");

        outcome.getStyleClass().add("card-large-desc");

        VBox card = new VBox(10, title, tag, divider, description, outcomeLabel, outcome);
        card.getStyleClass().add("card-large");

        VBox page = new VBox(14, captionLabel, card);
        page.getStyleClass().add("page");
        VBox.setVgrow(card, Priority.ALWAYS);
        return page;
    }

    private void bootstrapState() {
        currentLeftCard = randomCard();
        renderLargeCard(leftTitle, leftTag, leftDescription, leftOutcome, currentLeftCard);
        refreshOptions();
    }

    private void refreshOptions() {
        selectedCard = null;
        executeButton.setDisable(true);
        optionViews.clear();
        optionsContainer.getChildren().clear();

        List<GameCard> options = randomDistinctCards(OPTION_COUNT, currentLeftCard);
        for (GameCard card : options) {
            OptionCardView cardView = createOptionCard(card);
            optionViews.add(cardView);
            optionsContainer.getChildren().add(cardView.node());
        }

        if (!options.isEmpty()) {
            setRightPreview(options.get(0));
        }
    }

    private OptionCardView createOptionCard(GameCard card) {
        Label title = new Label(card.title());
        title.getStyleClass().add("small-title");

        Label desc = new Label(card.shortDescription());
        desc.getStyleClass().add("small-desc");
        desc.setWrapText(true);

        VBox textBlock = new VBox(4, title, desc);

        Button infoButton = new Button("i");
        infoButton.getStyleClass().add("info-button");
        infoButton.setOnAction(e -> {
            e.consume();
            setRightPreview(card);
            markSelected(card);
        });

        HBox row = new HBox(10, textBlock, infoButton);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textBlock, Priority.ALWAYS);
        row.setPadding(new Insets(3));

        StackPane wrapper = new StackPane(row);
        wrapper.getStyleClass().add("card-small");
        wrapper.setPadding(new Insets(3));
        wrapper.setMaxWidth(Double.MAX_VALUE);

        attachHoverEffect(wrapper);

        wrapper.setOnMouseClicked(e -> {
            setRightPreview(card);
            markSelected(card);
        });

        return new OptionCardView(card, wrapper);
    }

    private void attachHoverEffect(StackPane node) {
        ScaleTransition grow = new ScaleTransition(Duration.millis(140), node);
        grow.setToX(1.035);
        grow.setToY(1.035);

        ScaleTransition shrink = new ScaleTransition(Duration.millis(140), node);
        shrink.setToX(1.0);
        shrink.setToY(1.0);

        DropShadow hoverGlow = new DropShadow();
        hoverGlow.setColor(Color.rgb(224, 176, 128, 0.55));
        hoverGlow.setRadius(22);
        hoverGlow.setSpread(0.2);

        node.setOnMouseEntered(e -> {
            shrink.stop();
            grow.playFromStart();
            node.setEffect(hoverGlow);
        });

        node.setOnMouseExited(e -> {
            grow.stop();
            shrink.playFromStart();
            node.setEffect(null);
        });
    }

    private void markSelected(GameCard card) {
        selectedCard = card;
        executeButton.setDisable(false);

        for (OptionCardView view : optionViews) {
            if (view.card().equals(card)) {
                if (!view.node().getStyleClass().contains("card-small-selected")) {
                    view.node().getStyleClass().add("card-small-selected");
                    playSelectPulse(view.node());
                }
            } else {
                view.node().getStyleClass().remove("card-small-selected");
            }
        }
    }

    private void playSelectPulse(StackPane node) {
        ScaleTransition up = new ScaleTransition(Duration.millis(90), node);
        up.setToX(1.06);
        up.setToY(1.06);

        ScaleTransition down = new ScaleTransition(Duration.millis(120), node);
        down.setToX(1.035);
        down.setToY(1.035);

        up.setOnFinished(e -> down.playFromStart());
        up.playFromStart();
    }

    private void setRightPreview(GameCard card) {
        renderLargeCard(rightTitle, rightTag, rightDescription, rightOutcome, card);
    }

    private void executeSelected() {
        if (selectedCard == null || turningPage) {
            return;
        }

        executeButton.setDisable(true);
        animatePageTurn(selectedCard);
    }

    private void animatePageTurn(GameCard nextCard) {
        turningPage = true;

        RotateTransition fold = new RotateTransition(Duration.millis(240), leftPage);
        fold.setAxis(Rotate.Y_AXIS);
        fold.setFromAngle(0);
        fold.setToAngle(-92);
        fold.setInterpolator(Interpolator.EASE_IN);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(240), leftPage);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.72);

        ParallelTransition phaseOut = new ParallelTransition(fold, fadeOut);

        RotateTransition unfold = new RotateTransition(Duration.millis(270), leftPage);
        unfold.setAxis(Rotate.Y_AXIS);
        unfold.setFromAngle(92);
        unfold.setToAngle(0);
        unfold.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(270), leftPage);
        fadeIn.setFromValue(0.72);
        fadeIn.setToValue(1.0);

        TranslateTransition settle = new TranslateTransition(Duration.millis(270), leftPage);
        settle.setFromX(-16);
        settle.setToX(0);

        ParallelTransition phaseIn = new ParallelTransition(unfold, fadeIn, settle);

        phaseOut.setOnFinished(e -> {
            currentLeftCard = nextCard;
            renderLargeCard(leftTitle, leftTag, leftDescription, leftOutcome, currentLeftCard);
            leftPage.setRotate(92);
            leftPage.setOpacity(0.72);
            leftPage.setTranslateX(-16);
            refreshOptions();
        });

        SequentialTransition pageTurn = new SequentialTransition(phaseOut, phaseIn);
        pageTurn.setOnFinished(e -> {
            turningPage = false;
            leftPage.setRotate(0);
            leftPage.setOpacity(1.0);
            leftPage.setTranslateX(0);
        });
        pageTurn.playFromStart();
    }

    private void renderLargeCard(Label title, Label tag, Label description, Label outcome, GameCard card) {
        title.setText(card.title());
        tag.setText("Categoria: " + card.category());
        description.setText(card.description());
        outcome.setText(card.outcome());
    }

    private GameCard randomCard() {
        return deck.get(random.nextInt(deck.size()));
    }

    private List<GameCard> randomDistinctCards(int count, GameCard exclude) {
        List<GameCard> pool = new ArrayList<>(deck);
        pool.remove(exclude);

        List<GameCard> picked = new ArrayList<>();
        int safeCount = Math.min(count, pool.size());

        for (int i = 0; i < safeCount; i++) {
            int index = random.nextInt(pool.size());
            picked.add(pool.remove(index));
        }

        return picked;
    }

    private record GameCard(String title, String category, String description, String outcome) {
        String shortDescription() {
            return description.length() > 56 ? description.substring(0, 56) + "..." : description;
        }
    }

    private record OptionCardView(GameCard card, StackPane node) {
    }
}

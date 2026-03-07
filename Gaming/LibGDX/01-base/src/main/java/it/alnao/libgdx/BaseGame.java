package it.alnao.libgdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Gioco base LibGDX con omino che si muove a destra o sinistra
 */
public class BaseGame extends ApplicationAdapter {
    
    private Stage stage;
    private Skin skin;
    private ShapeRenderer shapeRenderer;
    
    // Stati del gioco
    private enum GameState {
        START, PLAYING, END
    }
    
    private GameState gameState = GameState.START;
    
    // Posizione dell'omino
    private float playerX = 400;
    private float playerY = 300;
    private static final float MOVE_DISTANCE = 100f;
    
    @Override
    public void create() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        
        // Crea skin programmaticamente
        skin = createSkin();
        
        shapeRenderer = new ShapeRenderer();
        
        showStartScreen();
    }
    
    /**
     * Crea una skin personalizzata programmaticamente
     */
    private Skin createSkin() {
        Skin skin = new Skin();
        
        // Crea un font
        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.5f);
        skin.add("default-font", font);
        
        // Crea texture per i bottoni
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.valueOf("4A90E2"));
        pixmap.fill();
        skin.add("button-up", new Texture(pixmap));
        
        pixmap.setColor(Color.valueOf("357ABD"));
        pixmap.fill();
        skin.add("button-down", new Texture(pixmap));
        pixmap.dispose();
        
        // Stile per TextButton
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = skin.getFont("default-font");
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.downFontColor = Color.LIGHT_GRAY;
        buttonStyle.up = skin.newDrawable("button-up", Color.valueOf("4A90E2"));
        buttonStyle.down = skin.newDrawable("button-down", Color.valueOf("357ABD"));
        buttonStyle.over = skin.newDrawable("button-up", Color.valueOf("5BA3F5"));
        skin.add("default", buttonStyle);
        
        // Stile per Label
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font");
        labelStyle.fontColor = Color.BLACK;
        skin.add("default", labelStyle);
        
        return skin;
    }
    
    /**
     * Mostra la schermata iniziale con il bottone Start Game
     */
    private void showStartScreen() {
        stage.clear();
        
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        
        Label titleLabel = new Label("Gioco dell'Omino", skin);
        titleLabel.setFontScale(2);
        table.add(titleLabel).padBottom(50);
        table.row();
        
        TextButton startButton = new TextButton("Start Game", skin);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startGame();
            }
        });
        
        table.add(startButton).width(200).height(60);
    }
    
    /**
     * Avvia il gioco mostrando l'omino e i bottoni di movimento
     */
    private void startGame() {
        gameState = GameState.PLAYING;
        stage.clear();
        
        // Reset posizione omino al centro
        playerX = Gdx.graphics.getWidth() / 2f;
        playerY = Gdx.graphics.getHeight() / 2f;
        
        // Crea i bottoni per muoversi
        Table table = new Table();
        table.setFillParent(true);
        table.bottom().padBottom(20);
        stage.addActor(table);
        
        TextButton leftButton = new TextButton("Vai a sinistra", skin);
        leftButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                movePlayer(-MOVE_DISTANCE);
            }
        });
        
        TextButton rightButton = new TextButton("Vai a destra", skin);
        rightButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                movePlayer(MOVE_DISTANCE);
            }
        });
        
        table.add(leftButton).width(150).height(50).padRight(20);
        table.add(rightButton).width(150).height(50);
    }
    
    /**
     * Muove il giocatore e termina il gioco
     */
    private void movePlayer(float deltaX) {
        playerX += deltaX;
        
        // Dopo il movimento, termina il gioco
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500); // Pausa per vedere il movimento
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                endGame();
            }
        });
    }
    
    /**
     * Termina il gioco mostrando il messaggio finale
     */
    private void endGame() {
        gameState = GameState.END;
        stage.clear();
        
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        
        Label endLabel = new Label("Hai completato le tue scelte!", skin);
        endLabel.setFontScale(2);
        table.add(endLabel).padBottom(30);
        table.row();
        
        TextButton restartButton = new TextButton("Rigioca", skin);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showStartScreen();
                gameState = GameState.START;
            }
        });
        
        table.add(restartButton).width(150).height(50);
    }
    
    /**
     * Disegna l'omino (un semplice cerchio con corpo)
     */
    private void drawPlayer() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Testa (cerchio)
        shapeRenderer.setColor(Color.PINK);
        shapeRenderer.circle(playerX, playerY + 30, 15);
        
        // Corpo (rettangolo)
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(playerX - 10, playerY, 20, 30);
        
        // Braccia
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rectLine(playerX - 10, playerY + 25, playerX - 25, playerY + 15, 3);
        shapeRenderer.rectLine(playerX + 10, playerY + 25, playerX + 25, playerY + 15, 3);
        
        // Gambe
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rectLine(playerX - 5, playerY, playerX - 10, playerY - 20, 3);
        shapeRenderer.rectLine(playerX + 5, playerY, playerX + 10, playerY - 20, 3);
        
        shapeRenderer.end();
    }
    
    @Override
    public void render() {
        // Pulisci schermo
        Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Disegna l'omino se il gioco è in corso
        if (gameState == GameState.PLAYING) {
            drawPlayer();
        }
        
        // Aggiorna e disegna lo stage (UI)
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        shapeRenderer.dispose();
    }
}

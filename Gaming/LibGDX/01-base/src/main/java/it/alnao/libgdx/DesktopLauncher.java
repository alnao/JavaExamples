package it.alnao.libgdx;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

/**
 * Desktop Launcher per il gioco LibGDX
 */
public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        
        // Configurazione finestra
        config.setTitle("Gioco dell'Omino");
        config.setWindowedMode(800, 600);
        config.setResizable(false);
        config.useVsync(true);
        config.setForegroundFPS(60);
        
        // Avvia l'applicazione
        new Lwjgl3Application(new BaseGame(), config);
    }
}

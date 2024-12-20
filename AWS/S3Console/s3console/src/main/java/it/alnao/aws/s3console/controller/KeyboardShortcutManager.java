package it.alnao.aws.s3console.controller;

// KeyboardShortcutManager.java
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

public class KeyboardShortcutManager implements KeyEventDispatcher {
    private final UIController controller;

    public KeyboardShortcutManager(UIController controller) {
        this.controller = controller;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() != KeyEvent.KEY_PRESSED) {
            return false;
        }
//TODO sistemare
/* 
        if (e.isControlDown()) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_R:
                    controller.handleRefresh();
                    return true;
                case KeyEvent.VK_U:
                    controller.handleNavigateUp();
                    return true;
                case KeyEvent.VK_F:
                    controller.handleFocusFilter();
                    return true;
            }
        }
*/        
        return false;
    }
}
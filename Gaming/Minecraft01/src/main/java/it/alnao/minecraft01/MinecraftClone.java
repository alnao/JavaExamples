package it.alnao.minecraft01;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class MinecraftClone {

    // Costanti del gioco
    private static final int WORLD_WIDTH = 20;
    private static final int WORLD_DEPTH = 20;
    private static final int MAX_HEIGHT = 10;
    
    // Tipi di blocco
    private static final int EMPTY = 0;
    private static final int DIRT = 1;
    private static final int STONE = 2;
    private static final int FLOOR = 3;
    
    // Finestra e input
    private long window;
    private int windowWidth = 800;
    private int windowHeight = 600;
    
    // Camera
    private float cameraX = WORLD_WIDTH / 2.0f;
    private float cameraY = 5.0f;
    private float cameraZ = WORLD_DEPTH / 2.0f;
    private float pitch = 0.0f;
    private float yaw = -90.0f;
    
    // Movimenti
    private boolean forward = false;
    private boolean backward = false;
    private boolean left = false;
    private boolean right = false;
    private boolean up = false;
    private boolean down = false;
    
    // Selezione blocco
    private int selectedBlockType = DIRT;
    
    // Mondo
    private int[][][] world;
    
    // Ray casting
    private int targetX = -1;
    private int targetY = -1;
    private int targetZ = -1;
    private int targetFace = -1; // 0=x+, 1=x-, 2=y+, 3=y-, 4=z+, 5=z-
    
    public void run() {
        init();
        loop();
        
        // Liberare finestra e callbacks
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        
        // Terminare GLFW
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
    
    private void init() {
        // Inizializzare GLFW
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Impossibile inizializzare GLFW");
        }
        
        // Configurare GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        
        // Creare la finestra
        window = glfwCreateWindow(windowWidth, windowHeight, "Minecraft Clone", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Impossibile creare la finestra GLFW");
        }
        
        // Impostare i callback per input
        setupInputCallbacks();
        
        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            
            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);
            
            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            
            // Center the window
            glfwSetWindowPos(
                window,
                (vidmode.width() - pWidth.get(0)) / 2,
                (vidmode.height() - pHeight.get(0)) / 2
            );
        }
        
        // Rendere il contesto OpenGL corrente
        glfwMakeContextCurrent(window);
        
        // Abilitare v-sync
        glfwSwapInterval(1);
        
        // Rendere la finestra visibile
        glfwShowWindow(window);
        
        // Inizializzare il mondo
        initWorld();
    }
    
    private void setupInputCallbacks() {
        // Callback per il ridimensionamento della finestra
        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            windowWidth = width;
            windowHeight = height;
            glViewport(0, 0, width, height);
        });
        
        // Callback per input da tastiera
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
            
            // Movimento
            if (key == GLFW_KEY_W) forward = action != GLFW_RELEASE;
            if (key == GLFW_KEY_S) backward = action != GLFW_RELEASE;
            if (key == GLFW_KEY_A) left = action != GLFW_RELEASE;
            if (key == GLFW_KEY_D) right = action != GLFW_RELEASE;
            if (key == GLFW_KEY_SPACE) up = action != GLFW_RELEASE;
            if (key == GLFW_KEY_LEFT_SHIFT) down = action != GLFW_RELEASE;
            
            // Cambiare tipo di blocco
            if (key == GLFW_KEY_1 && action == GLFW_PRESS) selectedBlockType = DIRT;
            if (key == GLFW_KEY_2 && action == GLFW_PRESS) selectedBlockType = STONE;
            
            // Posizionare o rimuovere blocchi
            if (key == GLFW_KEY_E && action == GLFW_PRESS && targetX >= 0) {
                placeBlock();
            }
            if (key == GLFW_KEY_Q && action == GLFW_PRESS && targetX >= 0) {
                removeBlock();
            }
        });
        
        // Callback per il movimento del mouse
        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            // Calcolare spostamento mouse rispetto al centro
            float xOffset = (float) (xpos - windowWidth / 2);
            float yOffset = (float) (windowHeight / 2 - ypos);
            
            // Sensibilità
            float sensitivity = 0.1f;
            xOffset *= sensitivity;
            yOffset *= sensitivity;
            
            // Aggiorna angoli
            yaw += xOffset;
            pitch += yOffset;
            
            // Limita angolo pitch per evitare capovolgimenti
            if (pitch > 89.0f) pitch = 89.0f;
            if (pitch < -89.0f) pitch = -89.0f;
            
            // Riposiziona il cursore al centro
            glfwSetCursorPos(window, windowWidth / 2, windowHeight / 2);
        });
        
        // Nasconde il cursore e lo vincola alla finestra
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }
    
    private void initWorld() {
        world = new int[WORLD_WIDTH][MAX_HEIGHT + 1][WORLD_DEPTH];
        
        // Inizializza tutto come vuoto
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y <= MAX_HEIGHT; y++) {
                for (int z = 0; z < WORLD_DEPTH; z++) {
                    world[x][y][z] = EMPTY;
                }
            }
        }
        
        // Crea il pavimento indistruttibile
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int z = 0; z < WORLD_DEPTH; z++) {
                world[x][0][z] = FLOOR;
            }
        }
    }
    
    private void loop() {
        // Questo crea le capacità GL per la finestra corrente
        GL.createCapabilities();
        
        // Imposta colore di sfondo
        glClearColor(0.529f, 0.808f, 0.922f, 0.0f); // Azzurro cielo
        
        // Abilita test di profondità
        glEnable(GL_DEPTH_TEST);
        
        // Abilita backface culling
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        
        // Loop principale
        double lastTime = glfwGetTime();
        while (!glfwWindowShouldClose(window)) {
            double currentTime = glfwGetTime();
            double deltaTime = currentTime - lastTime;
            lastTime = currentTime;
            
            // Input e aggiornamento
            processInput(deltaTime);
            updateRayCast();
            
            // Rendering
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            // Impostazione vista 3D
            setup3DView();
            
            // Renderizza mondo
            renderWorld();
            
            // Renderizza mirino
            setupOrthoView();
            renderCrosshair();
            
            // Scambia i buffer
            glfwSwapBuffers(window);
            
            // Gestisci eventi
            glfwPollEvents();
        }
    }
    
    private void processInput(double deltaTime) {
        float speed = 5.0f * (float)deltaTime;
        
        // Calcola la direzione frontale basata su yaw e pitch
        float dirX = (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        float dirY = (float) Math.sin(Math.toRadians(pitch));
        float dirZ = (float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        
        // Normalizza il vettore direzione
        float length = (float) Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        dirX /= length;
        dirY /= length;
        dirZ /= length;
        
        // Calcola il vettore destro (perpendicolare alla direzione frontale)
        float rightX = (float) Math.cos(Math.toRadians(yaw - 90));
        float rightZ = (float) Math.sin(Math.toRadians(yaw - 90));
        
        // Movimento avanti/indietro
        if (forward) {
            cameraX += dirX * speed;
            cameraZ += dirZ * speed;
        }
        if (backward) {
            cameraX -= dirX * speed;
            cameraZ -= dirZ * speed;
        }
        
        // Movimento laterale
        if (right) {
            cameraX += rightX * speed;
            cameraZ += rightZ * speed;
        }
        if (left) {
            cameraX -= rightX * speed;
            cameraZ -= rightZ * speed;
        }
        
        // Movimento verticale
        if (up) cameraY += speed;
        if (down) cameraY -= speed;
        
        // Limiti del mondo
        if (cameraX < 0) cameraX = 0;
        if (cameraX > WORLD_WIDTH) cameraX = WORLD_WIDTH;
        if (cameraY < 1) cameraY = 1;
        if (cameraY > MAX_HEIGHT + 1) cameraY = MAX_HEIGHT + 1;
        if (cameraZ < 0) cameraZ = 0;
        if (cameraZ > WORLD_DEPTH) cameraZ = WORLD_DEPTH;
    }
    
    private void updateRayCast() {
        float dirX = (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        float dirY = (float) Math.sin(Math.toRadians(pitch));
        float dirZ = (float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        
        // Normalizza la direzione
        float length = (float) Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        dirX /= length;
        dirY /= length;
        dirZ /= length;
        
        // Distanza massima del ray cast
        float maxDistance = 5.0f;
        
        // Resetta target
        targetX = -1;
        targetY = -1;
        targetZ = -1;
        targetFace = -1;
        
        // Implementazione DDA (Digital Differential Analyzer)
        for (float t = 0; t < maxDistance; t += 0.1f) {
            int x = (int)(cameraX + dirX * t);
            int y = (int)(cameraY + dirY * t);
            int z = (int)(cameraZ + dirZ * t);
            
            // Controlla se le coordinate sono all'interno del mondo
            if (x >= 0 && x < WORLD_WIDTH && y >= 0 && y <= MAX_HEIGHT && z >= 0 && z < WORLD_DEPTH) {
                // Se c'è un blocco
                if (world[x][y][z] != EMPTY) {
                    targetX = x;
                    targetY = y;
                    targetZ = z;
                    
                    // Determina la faccia del blocco
                    float fracX = (cameraX + dirX * t) - x;
                    float fracY = (cameraY + dirY * t) - y;
                    float fracZ = (cameraZ + dirZ * t) - z;
                    
                    if (fracX < 0.01f) targetFace = 1; // x-
                    else if (fracX > 0.99f) targetFace = 0; // x+
                    else if (fracY < 0.01f) targetFace = 3; // y-
                    else if (fracY > 0.99f) targetFace = 2; // y+
                    else if (fracZ < 0.01f) targetFace = 5; // z-
                    else if (fracZ > 0.99f) targetFace = 4; // z+
                    
                    break;
                }
            }
        }
    }
    
    private void placeBlock() {
        int newX = targetX;
        int newY = targetY;
        int newZ = targetZ;
        
        // Calcola posizione del nuovo blocco in base alla faccia colpita
        switch (targetFace) {
            case 0: newX += 1; break; // x+
            case 1: newX -= 1; break; // x-
            case 2: newY += 1; break; // y+
            case 3: newY -= 1; break; // y-
            case 4: newZ += 1; break; // z+
            case 5: newZ -= 1; break; // z-
        }
        
        // Verifica se la nuova posizione è valida
        if (newX >= 0 && newX < WORLD_WIDTH && newY >= 1 && newY <= MAX_HEIGHT && newZ >= 0 && newZ < WORLD_DEPTH) {
            // Verifica se lo spazio è vuoto
            if (world[newX][newY][newZ] == EMPTY) {
                world[newX][newY][newZ] = selectedBlockType;
            }
        }
    }
    
    private void removeBlock() {
        // Non permettere la rimozione del pavimento
        if (world[targetX][targetY][targetZ] != FLOOR) {
            world[targetX][targetY][targetZ] = EMPTY;
        }
    }
    
    private void setup3DView() {
        // Imposta modalità proiezione
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        
        // Prospettiva con FOV di 45 gradi
        float aspectRatio = (float)windowWidth / (float)windowHeight;
        float fov = 45.0f;
        float nearPlane = 0.1f;
        float farPlane = 100.0f;
        float y_scale = (float) (1.0 / Math.tan(Math.toRadians(fov / 2)));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = farPlane - nearPlane;
        
        FloatBuffer matrix = BufferUtils.createFloatBuffer(16);
        matrix.put(0, x_scale);
        matrix.put(5, y_scale);
        matrix.put(10, -((farPlane + nearPlane) / frustum_length));
        matrix.put(11, -1);
        matrix.put(14, -((2 * nearPlane * farPlane) / frustum_length));
        matrix.put(15, 0);
        
        glLoadMatrixf(matrix);
        
        // Imposta modalità modello-vista
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        
        // Calcola vettori look-at
        float dirX = (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        float dirY = (float) Math.sin(Math.toRadians(pitch));
        float dirZ = (float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        
        // Imposta vista camera (gluLookAt equivalente)
        lookAt(cameraX, cameraY, cameraZ,
               cameraX + dirX, cameraY + dirY, cameraZ + dirZ,
               0, 1, 0);
    }
    
    private void lookAt(float eyeX, float eyeY, float eyeZ,
                         float centerX, float centerY, float centerZ,
                         float upX, float upY, float upZ) {
        // Calcola vettore forward (normalizzato)
        float forwardX = centerX - eyeX;
        float forwardY = centerY - eyeY;
        float forwardZ = centerZ - eyeZ;
        
        float forwardLength = (float) Math.sqrt(forwardX * forwardX + forwardY * forwardY + forwardZ * forwardZ);
        forwardX /= forwardLength;
        forwardY /= forwardLength;
        forwardZ /= forwardLength;
        
        // Calcola vettore side (normalizzato, prodotto vettoriale tra forward e up)
        float sideX = forwardY * upZ - forwardZ * upY;
        float sideY = forwardZ * upX - forwardX * upZ;
        float sideZ = forwardX * upY - forwardY * upX;
        
        float sideLength = (float) Math.sqrt(sideX * sideX + sideY * sideY + sideZ * sideZ);
        sideX /= sideLength;
        sideY /= sideLength;
        sideZ /= sideLength;
        
        // Calcola nuovo vettore up (prodotto vettoriale tra side e forward)
        float upNewX = sideY * forwardZ - sideZ * forwardY;
        float upNewY = sideZ * forwardX - sideX * forwardZ;
        float upNewZ = sideX * forwardY - sideY * forwardX;
        
        // Crea matrice look-at
        FloatBuffer matrix = BufferUtils.createFloatBuffer(16);
        matrix.put(0, sideX);
        matrix.put(4, sideY);
        matrix.put(8, sideZ);
        matrix.put(12, 0);
        
        matrix.put(1, upNewX);
        matrix.put(5, upNewY);
        matrix.put(9, upNewZ);
        matrix.put(13, 0);
        
        matrix.put(2, -forwardX);
        matrix.put(6, -forwardY);
        matrix.put(10, -forwardZ);
        matrix.put(14, 0);
        
        matrix.put(3, 0);
        matrix.put(7, 0);
        matrix.put(11, 0);
        matrix.put(15, 1);
        
        glMultMatrixf(matrix);
        glTranslatef(-eyeX, -eyeY, -eyeZ);
    }
    
    private void setupOrthoView() {
        // Switch to projection matrix
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        
        // Set up orthographic projection
        glOrtho(0, windowWidth, windowHeight, 0, -1, 1);
        
        // Switch to modelview matrix
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        
        // Disable depth testing for 2D elements
        glDisable(GL_DEPTH_TEST);
    }
    
    private void renderWorld() {
        // Per ogni blocco nel mondo
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y <= MAX_HEIGHT; y++) {
                for (int z = 0; z < WORLD_DEPTH; z++) {
                    int blockType = world[x][y][z];
                    
                    // Salta blocchi vuoti
                    if (blockType == EMPTY) continue;
                    
                    // Seleziona colore in base al tipo di blocco
                    switch (blockType) {
                        case DIRT:
                            glColor3f(0.6f, 0.3f, 0.0f); // Marrone
                            break;
                        case STONE:
                            glColor3f(0.5f, 0.5f, 0.5f); // Grigio
                            break;
                        case FLOOR:
                            glColor3f(0.0f, 0.5f, 0.0f); // Verde scuro
                            break;
                    }
                    
                    // Evidenzia il blocco selezionato
                    if (x == targetX && y == targetY && z == targetZ) {
                        glColor3f(1.0f, 1.0f, 1.0f); // Bianco
                    }
                    
                    // Disegna il cubo
                    drawCube(x, y, z);
                }
            }
        }
    }
    
    private void drawCube(float x, float y, float z) {
        glPushMatrix();
        glTranslatef(x, y, z);
        
        // Vertici del cubo
        glBegin(GL_QUADS);
        
        // Faccia superiore
        glVertex3f(0, 1, 0);
        glVertex3f(1, 1, 0);
        glVertex3f(1, 1, 1);
        glVertex3f(0, 1, 1);
        
        // Faccia inferiore
        glVertex3f(0, 0, 0);
        glVertex3f(0, 0, 1);
        glVertex3f(1, 0, 1);
        glVertex3f(1, 0, 0);
        
        // Faccia frontale
        glVertex3f(0, 0, 1);
        glVertex3f(0, 1, 1);
        glVertex3f(1, 1, 1);
        glVertex3f(1, 0, 1);
        
        // Faccia posteriore
        glVertex3f(0, 0, 0);
        glVertex3f(1, 0, 0);
        glVertex3f(1, 1, 0);
        glVertex3f(0, 1, 0);
        
        // Faccia sinistra
        glVertex3f(0, 0, 0);
        glVertex3f(0, 1, 0);
        glVertex3f(0, 1, 1);
        glVertex3f(0, 0, 1);
        
        // Faccia destra
        glVertex3f(1, 0, 0);
        glVertex3f(1, 0, 1);
        glVertex3f(1, 1, 1);
        glVertex3f(1, 1, 0);
        
        glEnd();
        
        glPopMatrix();
    }
    
    private void renderCrosshair() {
        glColor3f(1.0f, 1.0f, 1.0f); // Bianco
        
        int centerX = windowWidth / 2;
        int centerY = windowHeight / 2;
        int size = 10;
        
        glBegin(GL_LINES);
        // Linea orizzontale
        glVertex2f(centerX - size, centerY);
        glVertex2f(centerX + size, centerY);
        // Linea verticale
        glVertex2f(centerX, centerY - size);
        glVertex2f(centerX, centerY + size);
        glEnd();
        
        // Riattiva il test di profondità per il rendering 3D
        glEnable(GL_DEPTH_TEST);
    }
    
    public static void main(String[] args) {
        new MinecraftClone().run();
    }
}
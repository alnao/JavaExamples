package it.alnao.javafx.photocarousel.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Service for retrieving image files from selected directories with optional subfolder scanning.
 */
public class ImageService {

    private static final String[] SUPPORTED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
    private final Random random = new Random();
    private List<File> currentFolderImages = new ArrayList<>();

    public List<File> loadImagesFromFolder(String folderPath) {
        return loadImagesFromFolder(folderPath, true);
    }

    public List<File> loadImagesFromFolder(String folderPath, boolean includeSubfolders) {
        currentFolderImages.clear();
        if (folderPath == null || folderPath.trim().isEmpty()) {
            return currentFolderImages;
        }

        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("[ImageService] Cartella non valida: " + folderPath);
            return currentFolderImages;
        }

        if (includeSubfolders) {
            scanDirectoryRecursive(folder);
        } else {
            scanDirectorySingle(folder);
        }

        System.out.println("[ImageService] Trovate " + currentFolderImages.size() + " immagini in " + folderPath + (includeSubfolders ? " (incluse sottocartelle)" : " (solo cartella principale)"));
        return currentFolderImages;
    }

    private void scanDirectoryRecursive(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectoryRecursive(file);
            } else if (file.isFile() && isImageFile(file.getName())) {
                currentFolderImages.add(file);
            }
        }
    }

    private void scanDirectorySingle(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isFile() && isImageFile(file.getName())) {
                currentFolderImages.add(file);
            }
        }
    }

    private boolean isImageFile(String name) {
        String lower = name.toLowerCase();
        for (String ext : SUPPORTED_EXTENSIONS) {
            if (lower.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    public List<File> getInitialImages(int count) {
        List<File> result = new ArrayList<>();
        if (currentFolderImages.isEmpty() || count <= 0) {
            return result;
        }

        // Shuffled copy to pick initial random images
        List<File> shuffled = new ArrayList<>(currentFolderImages);
        Collections.shuffle(shuffled, random);

        for (int i = 0; i < Math.min(count, shuffled.size()); i++) {
            result.add(shuffled.get(i));
        }

        return result;
    }

    public File getRandomNextImage(List<File> currentlyDisplayed) {
        if (currentFolderImages.isEmpty()) {
            return null;
        }

        // Try to pick an image that is not currently visible
        List<File> candidates = new ArrayList<>();
        for (File f : currentFolderImages) {
            if (!currentlyDisplayed.contains(f)) {
                candidates.add(f);
            }
        }

        if (!candidates.isEmpty()) {
            return candidates.get(random.nextInt(candidates.size()));
        }

        // If all images in folder are already visible, pick any random one from folder
        return currentFolderImages.get(random.nextInt(currentFolderImages.size()));
    }

    public int getFolderImageCount() {
        return currentFolderImages.size();
    }
}

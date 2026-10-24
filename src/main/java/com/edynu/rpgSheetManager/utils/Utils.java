package com.edynu.rpgSheetManager.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

public class Utils {

    private static final Path PHOTO_DIR = Paths.get("data", "characterImages");

    static {
        try {
            Files.createDirectories(PHOTO_DIR);
        } catch (IOException e) {
            throw new RuntimeException("Could not create photo directory: " + PHOTO_DIR.toAbsolutePath(), e);
        }
    }

    public static byte[] saveCharacterPhoto(int characterId, File sourceFile, int maxSize) throws IOException {
        BufferedImage image = ImageIO.read(sourceFile);
        if (image == null) {
            throw new IllegalArgumentException("This file is not an image.");
        }
        double scale = Math.min(1.0, (double) maxSize / Math.max(image.getWidth(), image.getHeight()));
        int width = Math.max(1, (int) (image.getWidth() * scale));
        int height = Math.max(1, (int) (image.getHeight() * scale));

        BufferedImage small = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = small.createGraphics();
        g.drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        g.dispose();

        Path target = PHOTO_DIR.resolve("char_" + characterId + ".jpg");
        ImageIO.write(small, "jpg", target.toFile());
        return Files.readAllBytes(target);
    }

    public static byte[] loadCharacterPhoto(int characterId) {
        Path target = PHOTO_DIR.resolve("char_" + characterId + ".jpg");
        if (!Files.exists(target)) {
            return null;
        }
        try {
            return Files.readAllBytes(target);
        } catch (IOException e) {
            return null;
        }
    }

    public static void deleteCharacterPhoto(int characterId) {
        try {
            Files.deleteIfExists(PHOTO_DIR.resolve("char_" + characterId + ".jpg"));
        } catch (IOException ignored) {
        }
    }
}

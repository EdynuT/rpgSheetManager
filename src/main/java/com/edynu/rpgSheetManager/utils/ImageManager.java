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

public class ImageManager {

    private static final Path PHOTO_DIR = Paths.get("data", "characterImages");

    static {
        try {
            Files.createDirectories(PHOTO_DIR);
        } catch (IOException e) {
            throw new RuntimeException("Could not create photo directory at: " + PHOTO_DIR.toAbsolutePath(), e);
        }
    }

    /**
     * Saves the character's photo as JPEG, reducing it to fit the maximum size
     * without changing the aspect ratio. The image is not enlarged if it is already smaller than the limit.
     *
     * @param characterId identifier used to name the photo file
     * @param sourceFile source image file
     * @param maxSize maximum dimension, in pixels, for width or height
     * @return bytes of the saved JPEG
     * @throws IOException if there is an error reading or writing the file
     * @throws IllegalArgumentException if the file cannot be recognized as an image
     */
    public static byte[] saveCharacterPhoto(int characterId, File sourceFile, int maxSize) throws IOException {
        BufferedImage image = ImageIO.read(sourceFile);
        if (image == null) {
            throw new IllegalArgumentException("Este arquivo não é uma imagem válida.");
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

    /**
     * Loads the saved JPEG photo bytes for the character.
     *
     * @param characterId identifier used to locate the photo file
     * @return bytes of the photo, or {@code null} if the file does not exist or cannot be read
     */
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

    /**
     * Deletes the character's photo file, if it exists.
     *
     * @param characterId identifier used to locate the photo file
     */
    public static void deleteCharacterPhoto(int characterId) {
        try {
            Files.deleteIfExists(PHOTO_DIR.resolve("char_" + characterId + ".jpg"));
        } catch (IOException ignored) {
        }
    }
}

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
            throw new RuntimeException("Could not create photo directory: " + PHOTO_DIR.toAbsolutePath(), e);
        }
    }

    /**
     * Salva a foto do personagem como JPEG, reduzindo-a para caber no tamanho máximo
     * sem alterar a proporção. A imagem não é ampliada se já for menor que o limite.
     *
     * @param characterId identificador usado para nomear o arquivo da foto
     * @param sourceFile arquivo de imagem de origem
     * @param maxSize dimensão máxima, em pixels, para largura ou altura
     * @return bytes do JPEG salvo
     * @throws IOException se houver erro ao ler ou gravar o arquivo
     * @throws IllegalArgumentException se o arquivo não puder ser reconhecido como imagem
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
     * Carrega os bytes da foto JPEG salva para o personagem.
     *
     * @param characterId identificador usado para localizar o arquivo da foto
     * @return bytes da foto, ou {@code null} se o arquivo não existir ou não puder ser lido
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
     * Exclui o arquivo de foto do personagem, se existir.
     *
     * @param characterId identificador usado para localizar o arquivo da foto
     */
    public static void deleteCharacterPhoto(int characterId) {
        try {
            Files.deleteIfExists(PHOTO_DIR.resolve("char_" + characterId + ".jpg"));
        } catch (IOException ignored) {
        }
    }
}

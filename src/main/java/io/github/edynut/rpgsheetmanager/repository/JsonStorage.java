package io.github.edynut.rpgsheetmanager.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.edynut.rpgsheetmanager.model.Character;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class JsonStorage {

    private static final Path DATA_DIR = Paths.get(System.getProperty("user.dir"), "data");
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public JsonStorage() {
        try {
            Files.createDirectories(DATA_DIR);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar o diretório de dados", e);
        }
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /** Converte nome em nome de arquivo seguro. */
    private static String safeFilename(String nome) {
        String safe = nome.chars()
                .filter(c -> java.lang.Character.isLetterOrDigit(c) || c == ' ' || c == '_' || c == '-')
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString()
                .strip();
        return safe.isEmpty() ? "personagem" : safe;
    }

    private Path resolvePath(String nome) {
        return DATA_DIR.resolve(safeFilename(nome) + ".json");
    }

    // ------------------------------------------------------------------
    // CRUD
    // ------------------------------------------------------------------

    /** Retorna nomes de todos os personagens salvos, em ordem alfabética. */
    public List<String> listCharacters() {
        List<String> names = new ArrayList<>();
        try (Stream<Path> files = Files.list(DATA_DIR)) {
            files.filter(f -> f.getFileName().toString().endsWith(".json"))
                 .sorted()
                 .forEach(f -> {
                     try {
                         JsonNode data = MAPPER.readTree(f.toFile());
                         JsonNode nomeNode = data.get("nome");
                         if (nomeNode != null && !nomeNode.isNull()) {
                             names.add(nomeNode.asText());
                         }
                     } catch (IOException e) {
                         // ignora arquivos inválidos
                     }
                 });
        } catch (IOException e) {
            // ignora se o diretório não existe ainda
        }
        return names;
    }

    public Character load(String nome) {
        Path p = resolvePath(nome);
        if (!Files.exists(p)) {
            return new Character(nome);
        }
        try {
            return MAPPER.readValue(p.toFile(), Character.class);
        } catch (IOException e) {
            return new Character(nome);
        }
    }

    public void save(Character character) {
        Path p = resolvePath(character.getNome());
        try {
            MAPPER.writeValue(p.toFile(), character);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar personagem: " + character.getNome(), e);
        }
    }

    public void delete(String nome) {
        Path p = resolvePath(nome);
        try {
            Files.deleteIfExists(p);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar personagem: " + nome, e);
        }
    }

    public Character rename(String oldNome, String newNome) {
        Character character = load(oldNome);
        character.setNome(newNome);
        delete(oldNome);
        save(character);
        return character;
    }
}
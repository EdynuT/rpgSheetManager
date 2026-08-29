package src.dao;

import src.config.Database;
import src.model.PlayerCharacter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerCharacterDAO implements DAO<PlayerCharacter> {

    @Override
    public PlayerCharacter insert(PlayerCharacter p) throws SQLException {
        String sql = "INSERT INTO character (level, name, age, race, character_class, subclass, origin, language, " +
                     "base_health, base_mana, base_stamina, base_sanity, " +
                     "current_health, current_mana, current_stamina, current_sanity) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
                     
        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, p.getLevel());
            stmt.setString(2, p.getName());
            // setNull is required if age is null in the database
            if (p.getAge() != null) {
                stmt.setInt(3, p.getAge());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            
            stmt.setString(4, p.getRace());
            stmt.setString(5, p.getCharacterClass());
            stmt.setString(6, p.getSubclass());
            stmt.setString(7, p.getOrigin());
            stmt.setString(8, p.getLanguage());
            stmt.setInt(9, p.getBaseHealth());
            stmt.setInt(10, p.getBaseMana());
            stmt.setInt(11, p.getBaseStamina());
            stmt.setInt(12, p.getBaseSanity());
            stmt.setInt(13, p.getCurrentHealth());
            stmt.setInt(14, p.getCurrentMana());
            stmt.setInt(15, p.getCurrentStamina());
            stmt.setInt(16, p.getCurrentSanity());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    p.setId(rs.getInt("id"));
                }
            }
            return p;
        }
    }

    @Override
    public void update(PlayerCharacter p) throws SQLException {
        String sql = "UPDATE character SET level = ?, name = ?, age = ?, race = ?, character_class = ?, subclass = ?, " +
                     "origin = ?, language = ?, base_health = ?, base_mana = ?, base_stamina = ?, base_sanity = ?, " +
                     "current_health = ?, current_mana = ?, current_stamina = ?, current_sanity = ? WHERE id = ?";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, p.getLevel());
            stmt.setString(2, p.getName());
            if (p.getAge() != null) {
                stmt.setInt(3, p.getAge());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, p.getRace());
            stmt.setString(5, p.getCharacterClass());
            stmt.setString(6, p.getSubclass());
            stmt.setString(7, p.getOrigin());
            stmt.setString(8, p.getLanguage());
            stmt.setInt(9, p.getBaseHealth());
            stmt.setInt(10, p.getBaseMana());
            stmt.setInt(11, p.getBaseStamina());
            stmt.setInt(12, p.getBaseSanity());
            stmt.setInt(13, p.getCurrentHealth());
            stmt.setInt(14, p.getCurrentMana());
            stmt.setInt(15, p.getCurrentStamina());
            stmt.setInt(16, p.getCurrentSanity());
            stmt.setInt(17, p.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM character WHERE id = ?";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<PlayerCharacter> list() throws SQLException {
        String sql = "SELECT * FROM character";
        List<PlayerCharacter> characters = new ArrayList<>();

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                characters.add(mapRow(rs));
            }
        }
        return characters;
    }

    @Override
    public PlayerCharacter findById(int id) throws SQLException {
        String sql = "SELECT * FROM character WHERE id = ?";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    private PlayerCharacter mapRow(ResultSet rs) throws SQLException {
        int age = rs.getInt("age");
        Integer ageOrNull = rs.wasNull() ? null : age;
        return new PlayerCharacter(
                rs.getInt("id"),
                rs.getInt("level"),
                rs.getString("name"),
                ageOrNull,
                rs.getString("race"),
                rs.getString("character_class"),
                rs.getString("subclass"),
                rs.getString("origin"),
                rs.getString("language"),
                rs.getInt("base_health"),
                rs.getInt("base_mana"),
                rs.getInt("base_stamina"),
                rs.getInt("base_sanity"),
                rs.getInt("current_health"),
                rs.getInt("current_mana"),
                rs.getInt("current_stamina"),
                rs.getInt("current_sanity"));
    }
}

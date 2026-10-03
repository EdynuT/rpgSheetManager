package dao;

import config.Database;
import model.CharacterAbility;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Does not implement DAO<T>: character_ability has a composite key (character_id, ability_id),
// so rows are identified by two ids instead of the single "int id" the interface expects.
public class CharacterAbilityDAO {

    public CharacterAbility insert(CharacterAbility obj) throws SQLException {
        String sql = "INSERT INTO character_ability (character_id, ability_id, is_toggled) VALUES (?, ?, ?)";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, obj.getCharacterId());
            stmt.setInt(2, obj.getAbilityId());
            stmt.setBoolean(3, obj.isToggled());
            stmt.executeUpdate();
        }
        return obj;
    }

    public void update(CharacterAbility obj) throws SQLException {
        String sql = "UPDATE character_ability SET is_toggled = ? WHERE character_id = ? AND ability_id = ?";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setBoolean(1, obj.isToggled());
            stmt.setInt(2, obj.getCharacterId());
            stmt.setInt(3, obj.getAbilityId());
            stmt.executeUpdate();
        }
    }

    public void delete(int characterId, int abilityId) throws SQLException {
        String sql = "DELETE FROM character_ability WHERE character_id = ? AND ability_id = ?";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, characterId);
            stmt.setInt(2, abilityId);
            stmt.executeUpdate();
        }
    }

    public List<CharacterAbility> listByCharacter(int characterId) throws SQLException {
        String sql = "SELECT * FROM character_ability WHERE character_id = ?";
        List<CharacterAbility> links = new ArrayList<>();

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, characterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    links.add(new CharacterAbility(
                            rs.getInt("character_id"),
                            rs.getInt("ability_id"),
                            rs.getBoolean("is_toggled")));
                }
            }
        }
        return links;
    }
}

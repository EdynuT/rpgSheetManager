package com.edynu.rpgSheetManager.dao;

import com.edynu.rpgSheetManager.config.Database;
import com.edynu.rpgSheetManager.model.CharacterAbility;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Does not implement DAO<T>: character_ability has a composite key (character_id, ability_id),
// so rows are identified by two ids instead of the single "int id" the interface expects.
public class CharacterAbilityDAO {

    public CharacterAbility insert(CharacterAbility obj) throws SQLException {
        String sql = "INSERT INTO character_ability (character_id, ability_id) VALUES (?, ?)";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, obj.getCharacterId());
            stmt.setInt(2, obj.getAbilityId());
            stmt.executeUpdate();
        }
        return obj;
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
        String sql = "SELECT character_id, ability_id FROM character_ability WHERE character_id = ?";
        List<CharacterAbility> links = new ArrayList<>();

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, characterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    links.add(new CharacterAbility(
                            rs.getInt("character_id"),
                            rs.getInt("ability_id")
                    ));
                }
            }
        }
        return links;
    }
}
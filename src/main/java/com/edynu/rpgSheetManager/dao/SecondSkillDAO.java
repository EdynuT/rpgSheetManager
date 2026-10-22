package com.edynu.rpgSheetManager.dao;

import com.edynu.rpgSheetManager.config.Database;
import com.edynu.rpgSheetManager.model.SecondSkill;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SecondSkillDAO implements DAO<SecondSkill> {

    @Override
    public SecondSkill insert(SecondSkill obj) throws SQLException {
        String sql = "INSERT INTO second_skill (character_id, name, value) " +
                     "VALUES (?, ?, ?) RETURNING id";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, obj.getCharacterId());
            stmt.setString(2, obj.getSecondSkillName());
            stmt.setInt(3, obj.getValue());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    obj.setId(rs.getInt("id"));
                }
            }
        }
        return obj;
    }

    @Override
    public void update(SecondSkill obj) throws SQLException {
        String sql = "UPDATE second_skill SET character_id = ?, name = ?, value = ? WHERE id = ?";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, obj.getCharacterId());
            stmt.setString(2, obj.getSecondSkillName());
            stmt.setInt(3, obj.getValue());
            stmt.setInt(4, obj.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM second_skill WHERE id = ?";
        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<SecondSkill> list() throws SQLException {
        String sql = "SELECT * FROM second_skill";
        List<SecondSkill> secondSkills = new ArrayList<>();

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                secondSkills.add(mapRow(rs));
            }
        }
        return secondSkills;
    }

    public List<SecondSkill> listByCharacter(int characterId) throws SQLException {
        String sql = "SELECT * FROM second_skill WHERE character_id = ? ORDER BY name";
        List<SecondSkill> secondSkills = new ArrayList<>();

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, characterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    secondSkills.add(mapRow(rs));
                }
            }
        }
        return secondSkills;
    }

    @Override
    public SecondSkill findById(int id) throws SQLException {
        String sql = "SELECT * FROM second_skill WHERE id = ?";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    private SecondSkill mapRow(ResultSet rs) throws SQLException {
        return new SecondSkill(
                rs.getInt("id"),
                rs.getInt("character_id"),
                rs.getString("name"),
                rs.getInt("value")
        );
    }
}

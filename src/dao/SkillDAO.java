package dao;

import config.Database;
import model.Skill;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkillDAO implements DAO<Skill> {

    @Override
    public Skill insert(Skill obj) throws SQLException {
        String sql = "INSERT INTO skill (character_id, skill_name, value) " +
                     "VALUES (?, ?, ?) RETURNING id";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, obj.getCharacterId());
            stmt.setString(2, obj.getSkillName());
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
    public void update(Skill obj) throws SQLException {
        String sql = "UPDATE skill SET character_id = ?, skill_name = ?, value = ? WHERE id = ?";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, obj.getCharacterId());
            stmt.setString(2, obj.getSkillName());
            stmt.setInt(3, obj.getValue());
            stmt.setInt(4, obj.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM skill WHERE id = ?";
        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Skill> list() throws SQLException {
        String sql = "SELECT * FROM skill";
        List<Skill> skills = new ArrayList<>();

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                skills.add(mapRow(rs));
            }
        }
        return skills;
    }

    @Override
    public Skill findById(int id) throws SQLException {
        String sql = "SELECT * FROM skill WHERE id = ?";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    private Skill mapRow(ResultSet rs) throws SQLException {
        return new Skill(
                rs.getInt("id"),
                rs.getInt("character_id"),
                rs.getString("skill_name"),
                rs.getInt("value"));
    }
}

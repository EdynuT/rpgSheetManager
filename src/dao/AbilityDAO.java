package dao;

import config.Database;
import model.Ability;
import model.AbilityCategory;
import model.ActionType;
import model.CostType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AbilityDAO implements DAO<Ability> {

    @Override
    public Ability insert(Ability obj) throws SQLException {
        String sql = "INSERT INTO ability (name, category, action_type, cost_type, cost_value, description) " +
                     "VALUES (?, ?::ability_category, ?, ?, ?, ?) RETURNING id";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, obj.getName());
            stmt.setString(2, obj.getCategory().name());
            stmt.setString(3, obj.getActionType().name());
            stmt.setString(4, obj.getCostType().name());
            stmt.setInt(5, obj.getCostValue());
            stmt.setString(6, obj.getDescription());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    obj.setAbilityId(rs.getInt("id"));
                }
            }
        }
        return obj;
    }

    @Override
    public void update(Ability obj) throws SQLException {
        String sql = "UPDATE ability SET name = ?, category = ?::ability_category, action_type = ?, " +
                 "cost_type = ?, cost_value = ?, description = ? WHERE id = ?";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, obj.getName());
            stmt.setString(2, obj.getCategory().name());
            stmt.setString(3, obj.getActionType().name());
            stmt.setString(4, obj.getCostType().name());
            stmt.setInt(5, obj.getCostValue());
            stmt.setString(6, obj.getDescription());
            stmt.setInt(7, obj.getAbilityId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM ability WHERE id = ?";
        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Ability> list() throws SQLException {
        String sql = "SELECT * FROM ability";
        List<Ability> abilities = new ArrayList<>();

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                abilities.add(mapRow(rs));
            }
        }
        return abilities;
    }

    @Override
    public Ability findById(int id) throws SQLException {
        String sql = "SELECT * FROM ability WHERE id = ?";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    private Ability mapRow(ResultSet rs) throws SQLException {
        return new Ability(
                rs.getInt("id"),
                rs.getString("name"),
                AbilityCategory.valueOf(rs.getString("category")),
                ActionType.valueOf(rs.getString("action_type")),
                CostType.valueOf(rs.getString("cost_type")),
                rs.getInt("cost_value"),
                rs.getString("description"));
    }
}

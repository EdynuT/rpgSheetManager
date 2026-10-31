package com.edynu.rpgSheetManager.dao;

import com.edynu.rpgSheetManager.config.Database;
import com.edynu.rpgSheetManager.model.Ammunition;
import com.edynu.rpgSheetManager.model.ItemCategory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AmmunitionDAO implements DAO<Ammunition> {
    private final ItemDAO itemDAO = new ItemDAO();

    @Override
    public Ammunition insert(Ammunition obj) throws SQLException {
        // The item row and this row are saved in one transaction: both or neither.
        try (Connection con = Database.connect()) {
            con.setAutoCommit(false);
            try {
                itemDAO.insert(obj, con); // creates the shared "item" row and sets obj's id

                String sqlAmmunition = "INSERT INTO ammunition (item_id, damage) VALUES (?, ?)";
                try (PreparedStatement stmt = con.prepareStatement(sqlAmmunition)) {
                    stmt.setInt(1, obj.getId());
                    stmt.setString(2, obj.getDamageDice());
                    stmt.executeUpdate();
                }

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
        return obj;
    }

    @Override
    public void update(Ammunition obj) throws SQLException {
        // The item row and this row are saved in one transaction: both or neither.
        try (Connection con = Database.connect()) {
            con.setAutoCommit(false);
            try {
                itemDAO.update(obj, con);

                String sqlAmmunition = "UPDATE ammunition SET damage = ? WHERE item_id = ?";
                try (PreparedStatement stmt = con.prepareStatement(sqlAmmunition)) {
                    stmt.setString(1, obj.getDamageDice());
                    stmt.setInt(2, obj.getId());
                    stmt.executeUpdate();
                }

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        // ammunition.item_id has ON DELETE CASCADE, so removing the item row removes this one too.
        itemDAO.delete(id);
    }

    @Override
    public List<Ammunition> list() throws SQLException {
        String sql = "SELECT i.*, a.damage " +
                     "FROM item i JOIN ammunition a ON i.id = a.item_id ORDER BY i.name";
        List<Ammunition> ammunitions = new ArrayList<>();

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ammunitions.add(mapRow(rs));
            }
        }
        return ammunitions;
    }

    @Override
    public Ammunition findById(int id) throws SQLException {
        String sql = "SELECT i.*, a.damage " +
                     "FROM item i JOIN ammunition a ON i.id = a.item_id " +
                     "WHERE i.id = ?";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    private Ammunition mapRow(ResultSet rs) throws SQLException {
        Ammunition ammunition = new Ammunition();
        ammunition.setId(rs.getInt("id"));
        ammunition.setName(rs.getString("name"));
        ammunition.setDescription(rs.getString("description"));
        ammunition.setWeight(rs.getDouble("weight"));
        ammunition.setCategory(ItemCategory.valueOf(rs.getString("category")));
        ammunition.setDamageDice(rs.getString("damage"));
        return ammunition;
    }
}

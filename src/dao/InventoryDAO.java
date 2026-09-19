package dao;

import config.Database;
import model.Inventory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO implements DAO<Inventory> {

    @Override
    public Inventory insert(Inventory obj) throws SQLException {
        String sql = "INSERT INTO inventory (character_id, item_id, quantity, is_equipped) " + 
                     "VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, obj.getCharacterId());
            stmt.setInt(2, obj.getItemId());
            stmt.setInt(3, obj.getQuantity());
            stmt.setBoolean(4, obj.isEquipped());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    obj.setId(rs.getInt("id"));
                }
            }
        }
        return obj;
    }

    @Override
    public void update(Inventory obj) throws SQLException {
        String sql = "UPDATE inventory SET character_id, item_id = ?, quantity = ?, is_equipped = ? WHERE id = ?";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, obj.getCharacterId());
            stmt.setInt(2, obj.getItemId());
            stmt.setInt(3, obj.getQuantity());
            stmt.setBoolean(4, obj.isEquipped());
            stmt.setInt(5, obj.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM inventory WHERE id = ?";
        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Inventory> list() throws SQLException {
        String sql = "SELECT * FROM inventory";
        List<Inventory> inventories = new ArrayList<>();

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                inventories.add(mapRow(rs));
            }
        }
        return inventories;
    }

    @Override
    public Inventory findById(int id) throws SQLException {
        String sql = "SELECT * FROM inventory WHERE id = ?";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    private Inventory mapRow(ResultSet rs) throws SQLException {
        return new Inventory(
                rs.getInt("id"),
                rs.getInt("character_id"),
                rs.getInt("item_id"),
                rs.getInt("quantity"),
                rs.getBoolean("is_equipped"));
    }
}

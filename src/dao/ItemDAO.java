package src.dao;

import src.config.Database;
import src.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO implements DAO<Item> {
    @Override
    public Item insert(Item obj) throws SQLException {
        String sqlItem = "INSERT INTO item (name, description, weight, category) VALUES (?, ?, ?, ?::item_category) RETURNING id";
        
        Connection con = null;
        try {
            con = Database.connect();
            // Disable auto-commit to ensure that the insertion in the parent and child tables occurs together
            con.setAutoCommit(false); 
            
            try (PreparedStatement stmtItem = con.prepareStatement(sqlItem)) {
                stmtItem.setString(1, obj.getName());
                stmtItem.setString(2, obj.getDescription());
                stmtItem.setDouble(3, obj.getWeight());
                stmtItem.setString(4, obj.getCategory().name());
                
                try (ResultSet rs = stmtItem.executeQuery()) {
                    if (rs.next()) {
                        obj.setId(rs.getInt("id"));
                    }
                }
                
                if (obj instanceof Weapon) {
                    insertWeapon(con, (Weapon) obj);
                } 
                // If you create the classes Armor and Ammunition, add the else if blocks here
                // else if (obj instanceof Armor) { insertArmor(con, (Armor) obj); }
                
                con.commit(); // Confirm the transaction if everything went well
                return obj;
            }
        } catch (SQLException e) {
            if (con != null) con.rollback(); // Reverse the transaction if something goes wrong
            throw e;
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    private void insertWeapon(Connection con, Weapon w) throws SQLException {
        String sqlWeapon = "INSERT INTO weapon (item_id, scaling_attribute, damage_dice, flat_damage, critical_range, critical_multiplier) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sqlWeapon)) {
            stmt.setInt(1, w.getId());
            stmt.setString(2, w.getScalingAttribute());
            stmt.setString(3, w.getDamageDice());
            stmt.setInt(4, w.getFlatDamage());
            stmt.setInt(5, w.getCriticalRange());
            stmt.setInt(6, w.getCriticalMultiplier());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Item> list() throws SQLException {
        // A single wide JOIN to fetch all item types in one query.
        String sql = "SELECT i.*, " +
                     "w.scaling_attribute, w.damage_dice, w.flat_damage, w.critical_range, w.critical_multiplier " +
                     // Add the columns of armor and ammunition here after creating the models
                     "FROM item i " +
                     "LEFT JOIN weapon w ON i.id = w.item_id"; 
                     
        List<Item> items = new ArrayList<>();

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                items.add(mapRow(rs));
            }
        }
        return items;
    }

    @Override
    public Item findById(int id) throws SQLException {
        String sql = "SELECT i.*, " +
                     "w.scaling_attribute, w.damage_dice, w.flat_damage, w.critical_range, w.critical_multiplier " +
                     "FROM item i " +
                     "LEFT JOIN weapon w ON i.id = w.item_id " +
                     "WHERE i.id = ?";
                     
        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    // Instanciação dinâmica: Lê a categoria do banco e decide qual objeto Java criar
    private Item mapRow(ResultSet rs) throws SQLException {
        ItemCategory category = ItemCategory.valueOf(rs.getString("category"));
        
        Item item = null;

        if (category == ItemCategory.WEAPON) {
            Weapon w = new Weapon();
            w.setScalingAttribute(rs.getString("scaling_attribute"));
            w.setDamageDice(rs.getString("damage_dice"));
            w.setFlatDamage(rs.getInt("flat_damage"));
            w.setCriticalRange(rs.getInt("critical_range"));
            w.setCriticalMultiplier(rs.getInt("critical_multiplier"));
            item = w;
        } 
        else {
            // Fallback for general items (Rope, Torch, etc.)
            item = new Item(); 
        }

        // Common attributes to all items
        item.setId(rs.getInt("id"));
        item.setName(rs.getString("name"));
        item.setDescription(rs.getString("description"));
        item.setWeight(rs.getDouble("weight"));
        item.setCategory(category);

        return item;
    }

    @Override
    public void update(Item obj) throws SQLException {
        // The logic for updating an item would be similar to the insert method, but with an UPDATE SQL statement.
        // Stays the same for the parent table.
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM item WHERE id = ?";
        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            // The ON DELETE CASCADE constraint in PostgreSQL automatically deletes the weapon/armor.
        }
    }
}
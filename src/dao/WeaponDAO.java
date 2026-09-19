package dao;

import config.Database;
import model.ItemCategory;
import model.Weapon;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WeaponDAO implements DAO<Weapon> {
    private final ItemDAO itemDAO = new ItemDAO();

    @Override
    public Weapon insert(Weapon obj) throws SQLException {
        Connection con = null;
        try {
            con = Database.connect();
            con.setAutoCommit(false);

            itemDAO.insert(obj, con); // creates the shared "item" row and sets obj's id

            String sqlWeapon = "INSERT INTO weapon (item_id, scaling_attribute, damage_dice, critical_range, critical_multiplier) " +
                                "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = con.prepareStatement(sqlWeapon)) {
                stmt.setInt(1, obj.getId());
                stmt.setString(2, obj.getScalingAttribute());
                stmt.setString(3, obj.getDamageDice());
                stmt.setInt(4, obj.getCriticalRange());
                stmt.setInt(5, obj.getCriticalMultiplier());
                stmt.executeUpdate();
            }

            con.commit();
            return obj;
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    @Override
    public void update(Weapon obj) throws SQLException {
        Connection con = null;
        try {
            con = Database.connect();
            con.setAutoCommit(false);

            itemDAO.update(obj, con);

            String sqlWeapon = "UPDATE weapon SET scaling_attribute = ?, damage_dice = ?, critical_range = ?, critical_multiplier = ? WHERE item_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(sqlWeapon)) {
                stmt.setString(1, obj.getScalingAttribute());
                stmt.setString(2, obj.getDamageDice());
                stmt.setInt(3, obj.getCriticalRange());
                stmt.setInt(4, obj.getCriticalMultiplier());
                stmt.setInt(5, obj.getId());
                stmt.executeUpdate();
            }

            con.commit();
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        // weapon.item_id has ON DELETE CASCADE, so removing the item row removes this one too.
        itemDAO.delete(id);
    }

    @Override
    public List<Weapon> list() throws SQLException {
        String sql = "SELECT i.*, w.scaling_attribute, w.damage_dice, w.critical_range, w.critical_multiplier " +
                     "FROM item i JOIN weapon w ON i.id = w.item_id";
        List<Weapon> weapons = new ArrayList<>();

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                weapons.add(mapRow(rs));
            }
        }
        return weapons;
    }

    @Override
    public Weapon findById(int id) throws SQLException {
        String sql = "SELECT i.*, w.scaling_attribute, w.damage_dice, w.critical_range, w.critical_multiplier " +
                     "FROM item i JOIN weapon w ON i.id = w.item_id " +
                     "WHERE i.id = ?";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    private Weapon mapRow(ResultSet rs) throws SQLException {
        Weapon weapon = new Weapon();
        weapon.setId(rs.getInt("id"));
        weapon.setName(rs.getString("name"));
        weapon.setDescription(rs.getString("description"));
        weapon.setWeight(rs.getDouble("weight"));
        weapon.setCategory(ItemCategory.valueOf(rs.getString("category")));
        weapon.setScalingAttribute(rs.getString("scaling_attribute"));
        weapon.setDamageDice(rs.getString("damage_dice"));
        weapon.setCriticalRange(rs.getInt("critical_range"));
        weapon.setCriticalMultiplier(rs.getInt("critical_multiplier"));
        return weapon;
    }
}

package dao;

import config.Database;
import model.Armor;
import model.ItemCategory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArmorDAO implements DAO<Armor> {
    private final ItemDAO itemDAO = new ItemDAO();

    @Override
    public Armor insert(Armor obj) throws SQLException {
        Connection con = null;
        try {
            con = Database.connect();
            con.setAutoCommit(false);

            itemDAO.insert(obj, con); // creates the shared "item" row and sets obj's id

            String sqlArmor = "INSERT INTO armor (item_id, physical_ac, elemental_ac) " +
                               "VALUES (?, ?, ?)";
            try (PreparedStatement stmt = con.prepareStatement(sqlArmor)) {
                stmt.setInt(1, obj.getId());
                stmt.setInt(2, obj.getPhysicalAC());
                stmt.setInt(3, obj.getElementalAC());
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
    public void update(Armor obj) throws SQLException {
        Connection con = null;
        try {
            con = Database.connect();
            con.setAutoCommit(false);

            itemDAO.update(obj, con);

            String sqlArmor = "UPDATE armor SET physical_ac = ?, elemental_ac = ? WHERE item_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(sqlArmor)) {
                stmt.setInt(1, obj.getPhysicalAC());
                stmt.setInt(2, obj.getElementalAC());
                stmt.setInt(3, obj.getId());
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
        // armor.item_id has ON DELETE CASCADE, so removing the item row removes this one too.
        itemDAO.delete(id);
    }

    @Override
    public List<Armor> list() throws SQLException {
        String sql = "SELECT i.*, a.physical_ac, a.elemental_ac " +
                     "FROM item i JOIN armor a ON i.id = a.item_id";
        List<Armor> armors = new ArrayList<>();

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                armors.add(mapRow(rs));
            }
        }
        return armors;
    }

    @Override
    public Armor findById(int id) throws SQLException {
        String sql = "SELECT i.*, a.physical_ac, a.elemental_ac " +
                     "FROM item i JOIN armor a ON i.id = a.item_id " +
                     "WHERE i.id = ?";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    private Armor mapRow(ResultSet rs) throws SQLException {
        Armor armor = new Armor();
        armor.setId(rs.getInt("id"));
        armor.setName(rs.getString("name"));
        armor.setDescription(rs.getString("description"));
        armor.setWeight(rs.getDouble("weight"));
        armor.setCategory(ItemCategory.valueOf(rs.getString("category")));
        armor.setPhysicalAC(rs.getInt("physical_ac"));
        armor.setElementalAC(rs.getInt("elemental_ac"));
        return armor;
    }
}

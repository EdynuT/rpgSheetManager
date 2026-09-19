package dao;

import config.Database;
import model.Amunition;
import model.ItemCategory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AmunitionDAO implements DAO<Amunition> {
    private final ItemDAO itemDAO = new ItemDAO();

    @Override
    public Amunition insert(Amunition obj) throws SQLException {
        Connection con = null;
        try {
            con = Database.connect();
            con.setAutoCommit(false);

            itemDAO.insert(obj, con); // creates the shared "item" row and sets obj's id

            String sqlAmmunition = "INSERT INTO ammunition (item_id, damage) VALUES (?, ?)";
            try (PreparedStatement stmt = con.prepareStatement(sqlAmmunition)) {
                stmt.setInt(1, obj.getId());
                stmt.setString(2, obj.getDamageDice());
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
    public void update(Amunition obj) throws SQLException {
        Connection con = null;
        try {
            con = Database.connect();
            con.setAutoCommit(false);

            itemDAO.update(obj, con);

            String sqlAmmunition = "UPDATE ammunition SET damage = ? WHERE item_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(sqlAmmunition)) {
                stmt.setString(1, obj.getDamageDice());
                stmt.setInt(2, obj.getId());
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
        // ammunition.item_id has ON DELETE CASCADE, so removing the item row removes this one too.
        itemDAO.delete(id);
    }

    @Override
    public List<Amunition> list() throws SQLException {
        String sql = "SELECT i.*, a.damage " +
                     "FROM item i JOIN ammunition a ON i.id = a.item_id";
        List<Amunition> ammunitions = new ArrayList<>();

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
    public Amunition findById(int id) throws SQLException {
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

    private Amunition mapRow(ResultSet rs) throws SQLException {
        Amunition amunition = new Amunition();
        amunition.setId(rs.getInt("id"));
        amunition.setName(rs.getString("name"));
        amunition.setDescription(rs.getString("description"));
        amunition.setWeight(rs.getDouble("weight"));
        amunition.setCategory(ItemCategory.valueOf(rs.getString("category")));
        amunition.setDamageDice(rs.getString("damage"));
        return amunition;
    }
}

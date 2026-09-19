package dao;

import config.Database;
import model.Item;
import model.ItemCategory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO implements DAO<Item> {

    @Override
    public Item insert(Item obj) throws SQLException {
        try (Connection con = Database.connect()) {
            return insert(obj, con);
        }
    }

    // Package-visible: lets DAOs of Item subtypes (WeaponDAO, ArmorDAO, AmunitionDAO)
    // create the shared "item" row as part of their own transaction, instead of
    // duplicating this SQL or opening a second, uncoordinated connection.
    Item insert(Item obj, Connection con) throws SQLException {
        String sql = "INSERT INTO item (name, description, weight, category) " +
                    "VALUES (?, ?, ?, ?::item_category) RETURNING id";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, obj.getName());
            stmt.setString(2, obj.getDescription());
            stmt.setDouble(3, obj.getWeight());
            stmt.setString(4, obj.getCategory().name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    obj.setId(rs.getInt("id"));
                }
            }
        }
        return obj;
    }

    @Override
    public List<Item> list() throws SQLException {
        String sql = "SELECT * FROM item";
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
        String sql = "SELECT * FROM item WHERE id = ?";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    private Item mapRow(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setId(rs.getInt("id"));
        item.setName(rs.getString("name"));
        item.setDescription(rs.getString("description"));
        item.setWeight(rs.getDouble("weight"));
        item.setCategory(ItemCategory.valueOf(rs.getString("category")));
        return item;
    }

    @Override
    public void update(Item obj) throws SQLException {
        try (Connection con = Database.connect()) {
            update(obj, con);
        }
    }

    // Package-visible counterpart to insert(Item, Connection): lets subtype DAOs update
    // the shared "item" row within their own transaction.
    void update(Item obj, Connection con) throws SQLException {
        String sql = "UPDATE item SET name = ?, description = ?, weight = ?, category = ?::item_category WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, obj.getName());
            stmt.setString(2, obj.getDescription());
            stmt.setDouble(3, obj.getWeight());
            stmt.setString(4, obj.getCategory().name());
            stmt.setInt(5, obj.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM item WHERE id = ?";
        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            // The ON DELETE CASCADE constraint in PostgreSQL automatically deletes the weapon/armor/ammunition row too.
        }
    }
}

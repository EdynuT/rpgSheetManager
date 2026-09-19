package dao;

import config.Database;
import model.SecondBound;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SecondBoundDAO implements DAO<SecondBound> {

    @Override
    public SecondBound insert(SecondBound obj) throws SQLException {
        String sql = "INSERT INTO secondary_bond (character_id, name, value) " +
                     "VALUES (?, ?, ?) RETURNING id";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, obj.getCharacterId());
            stmt.setString(2, obj.getBoundName());
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
    public void update(SecondBound obj) throws SQLException {
        String sql = "UPDATE secondary_bond SET character_id = ?, name = ?, value = ? WHERE id = ?";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, obj.getCharacterId());
            stmt.setString(2, obj.getBoundName());
            stmt.setInt(3, obj.getValue());
            stmt.setInt(4, obj.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM secondary_bond WHERE id = ?";
        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<SecondBound> list() throws SQLException {
        String sql = "SELECT * FROM secondary_bond";
        List<SecondBound> bonds = new ArrayList<>();

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                bonds.add(mapRow(rs));
            }
        }
        return bonds;
    }

    @Override
    public SecondBound findById(int id) throws SQLException {
        String sql = "SELECT * FROM secondary_bond WHERE id = ?";

        try (Connection con = Database.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    private SecondBound mapRow(ResultSet rs) throws SQLException {
        return new SecondBound(
                rs.getInt("id"),
                rs.getInt("character_id"),
                rs.getString("name"),
                rs.getInt("value")
        );
    }
}

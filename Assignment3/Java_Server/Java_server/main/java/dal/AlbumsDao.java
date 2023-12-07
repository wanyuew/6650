package main.java.dal;

import main.java.util.Album;

import java.sql.*;

public class AlbumsDao {

    //post album to DB
    public static Album post(String artist, String year, String title, byte[] imgData, String size) throws SQLException {
        String insertAlbum = "INSERT INTO MyDB1.Albums(Artist,AlbumYear,Title,ImgData,ImageSize) VALUES(?,?,?,?,?);";
        Connection connection = null;
        PreparedStatement insertStmt = null;
        ResultSet generatedKeys;

        try {
            connection = DBConnection.getConnection();
            insertStmt = connection.prepareStatement(insertAlbum, Statement.RETURN_GENERATED_KEYS);
            insertStmt.setString(1, artist);
            insertStmt.setString(2, year);
            insertStmt.setString(3, title);
            insertStmt.setBytes(4, imgData);
            insertStmt.setString(5, size);
            insertStmt.executeUpdate();

            generatedKeys = insertStmt.getGeneratedKeys();

            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1);
                // Create and return the Album object with the generated ID
                return new Album(generatedId, artist, year, title, imgData, size);
            } else {
                throw new SQLException("Creating album failed, no generated key obtained.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            if(insertStmt != null) {
                insertStmt.close();
            }
            if(connection != null) {
                connection.close();
            }
        }
    }
}
package main.java.dal;

import main.java.Album;
import main.java.ImageMetaData;

import java.sql.*;

public class AlbumsDao {

    //Get Album Info by AlbumId from MySQL
    public static Album getAlbumByAlbumId(int albumId) throws SQLException {

        String selectAlbum = "SELECT AlbumId,Artist,AlbumYear,Title FROM Albums WHERE AlbumId=?;";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;

        try {
            //send Album to DB
            connection = DBConnection.getConnection();
            selectStmt = connection.prepareStatement(selectAlbum);
            selectStmt.setInt(1, albumId);

            // call executeQuery(). This is used for a SELECT statement
            results = selectStmt.executeQuery();

            //iterate result set
            if (results.next()) {
                int resultAlbumId = results.getInt("AlbumId");
                String artist = results.getString("Artist");
                String year = results.getString("AlbumYear");
                String title = results.getString("Title");
                return new Album(resultAlbumId, artist, year, title);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (results != null) {
                results.close();
            }

            if(selectStmt != null) {
                selectStmt.close();
            }

            if(connection != null) {
                connection.close();
            }
        }
        return null;
    }

    //post album to DB
    public static Album post(String artist, String year, String title, byte[] imgData, String size) throws SQLException {
        String insertAlbum = "INSERT INTO Albums(Artist,AlbumYear,Title,ImgData,ImageSize) VALUES(?,?,?,?,?);";
        Connection connection = null;
        PreparedStatement insertStmt = null;
        ResultSet generatedKeys = null;

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
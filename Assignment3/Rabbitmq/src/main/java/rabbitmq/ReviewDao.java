package rabbitmq;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;


public class ReviewDao {
    public static void updateReview(int albumId, String action) throws SQLException, InterruptedException {
        String selectReview = "SELECT * FROM MyDB1.Reviews WHERE AlbumId = ?";
        String updateReview = action.equals("like")
                ? "UPDATE MyDB1.Reviews SET Likes = Likes + 1 WHERE AlbumId = ?"
                : "UPDATE MyDB1.Reviews SET Dislikes = Dislikes + 1 WHERE AlbumId = ?";
        Connection connection = DBConnection.getConnection();
        String insertReview = action.equals("like")
                ? "INSERT INTO MyDB1.Reviews (AlbumId, Likes, Dislikes) VALUES (?, 1, 0)"
                : "INSERT INTO MyDB1.Reviews (AlbumId, Likes, Dislikes) VALUES (?, 0, 1)";

        boolean isUpdate = false;
        PreparedStatement selectStmt = null;
        try {
            // Check if a record with the given albumId exists
            selectStmt = connection.prepareStatement(selectReview);
            selectStmt.setInt(1, albumId);
            System.out.println(selectStmt);
            ResultSet resultSet = selectStmt.executeQuery();
            if (resultSet.next()) {
                isUpdate = true;
            }
        } catch (SQLException ex) {
            System.out.println("Something wrong with select " + ex);
            throw new SQLException(ex);
        } finally {
            if(selectStmt != null) {
                selectStmt.close();
            }
        }

        if (isUpdate) {
            PreparedStatement updateStmt = null;
            try {
                updateStmt = connection.prepareStatement(updateReview);
                // Record exists, update the likes or dislikes
                updateStmt.setInt(1, albumId);
                updateStmt.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("Something wrong with update " + ex);
                throw new SQLException(ex);
            } finally {
                if(updateStmt != null) {
                    updateStmt.close();
                }
            }
        } else {
            PreparedStatement insertStmt = null;
            try {
                // Record does not exist, insert a new row
                insertStmt = connection.prepareStatement(updateReview);
                insertStmt.setInt(1, albumId);
                System.out.println(insertStmt);
                insertStmt.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("Something wrong with update " + ex);
                throw new SQLException(ex);
            } finally {
                if(insertStmt != null) {
                    insertStmt.close();
                }
            }
        }
        connection.close();
    }
}

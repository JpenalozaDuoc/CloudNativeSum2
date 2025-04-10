package api.azure.service;


import api.azure.connection.DatabaseConnection;
import api.azure.model.UserRole;
import java.sql.*;



public class UserRoleService {

    // Asignar un rol a un usuario
    public void assignRoleToUser(UserRole userRole) throws SQLException {
        String sql = "INSERT INTO USER_ROLES (user_id, role_id) VALUES (?, ?)";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setLong(1, userRole.getUserId());
            stmt.setLong(2, userRole.getRoleId());
            stmt.executeUpdate();
        }
    }

    // Eliminar un rol de un usuario
    public void removeRoleFromUser(UserRole userRole) throws SQLException {
        String sql = "DELETE FROM USER_ROLES WHERE user_id = ? AND role_id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setLong(1, userRole.getUserId());
            stmt.setLong(2, userRole.getRoleId());
            stmt.executeUpdate();
        }
    }
}

package api.azure.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import api.azure.exception.RoleNotFoundException;
import api.azure.connection.DatabaseConnection;
import api.azure.model.Role;


public class RoleService {

    // Modificación en RoleService para soportar paginación
    public List<Role> getAllRoles(int limit, int offset) throws SQLException {
        List<Role> roles = new ArrayList<>();
    
        // Corregir la consulta para Oracle, utilizando ROW_NUMBER()
        String sql = "SELECT * FROM ( " +
                     "    SELECT r.*, ROW_NUMBER() OVER (ORDER BY r.id) AS rn " +
                     "    FROM roles r " +
                     ") WHERE rn BETWEEN ? AND ?";

   
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
    
            // Calcular el rango de filas a obtener
            int startRow = offset + 1;  // Oracle usa 1 como índice de filas
            int endRow = offset + limit;
    
            stmt.setInt(1, startRow);  // Establecer el valor para la primera fila
            stmt.setInt(2, endRow);    // Establecer el valor para la última fila
    
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Long id = rs.getLong("id");
                    String name = rs.getString("role_name");
                    Role role = new Role(id, name);
                    roles.add(role);
                }
            }
        }
    
        return roles;
    }
    /*NUEVO NO ESTABA EN VERSION SUMATIVA 1 */
    /*// Modificar el método getRoleById (para obtener por ID)
    public Role getRoleById(Long id) throws SQLException {
        Role role = null;
        String sql = "SELECT * FROM roles WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setLong(1, id);  // Establecer el ID del rol a buscar
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    role = new Role(rs.getLong("id"), rs.getString("role_name"));
                }
            }
        }

        return role;
    }*/

    // Obtener un rol por su ID
    public Role getRoleById(Long id) throws SQLException, RoleNotFoundException {
        String sql = "SELECT * FROM roles WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Long roleId = rs.getLong("id");
                String name = rs.getString("role_name");
                return new Role(roleId, name);
            } else {
                throw new RoleNotFoundException("Role not found with ID " + id);
            }
        }
    }

    /*// Crear un nuevo rol
    public void createRole(Role role) throws SQLException {
        String sql = "INSERT INTO roles (role_name) VALUES (?)";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, role.getName());
            stmt.executeUpdate();
        }
    }*/

    // Crear un nuevo rol
    public void createRole(Role role) throws SQLException {
        if (role.getName() == null || role.getName().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be empty");
        }

        String sql = "INSERT INTO roles (role_name) VALUES (?)";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, role.getName());
            stmt.executeUpdate();
        }
    }

    /*// Actualizar un rol
    public void updateRole(Long id, Role role) throws SQLException {
        String sql = "UPDATE roles SET role_name = ? WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, role.getName());
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }*/

    // Actualizar un rol
    public void updateRole(Long id, Role role) throws SQLException, RoleNotFoundException {
        if (role.getName() == null || role.getName().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be empty");
        }

        String sql = "UPDATE roles SET role_name = ? WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, role.getName());
            stmt.setLong(2, id);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                throw new RoleNotFoundException("Role not found with ID " + id);
            }
        }
    }

    /*// Eliminar un rol
    public void deleteRole(Long id) throws SQLException {
        String sql = "DELETE FROM roles WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }*/
    // Eliminar un rol
    public void deleteRole(Long id) throws SQLException, RoleNotFoundException {
        String sql = "DELETE FROM roles WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int rowsDeleted = stmt.executeUpdate();
            
            if (rowsDeleted == 0) {
                throw new RoleNotFoundException("Role not found with ID " + id);
            }
        }
    }

    /* DEPRECADO (CAMBIADO A REST)
    // Obtener todos los roles
    public List<Role> getAllRoles() throws SQLException {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM roles";
        
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Long id = rs.getLong("id");
                String name = rs.getString("role_name");
                
                Role role = new Role(id, name);
                roles.add(role);
            }
        }
        return roles;
    }
    */
}

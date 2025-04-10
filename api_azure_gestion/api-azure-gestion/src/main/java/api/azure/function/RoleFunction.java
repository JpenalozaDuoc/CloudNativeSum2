package api.azure.function;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import api.azure.exception.RoleNotFoundException;
import api.azure.model.Role;
import api.azure.service.RoleService;



public class RoleFunction {

    private final RoleService roleService = new RoleService();

    /*@FunctionName("getAllRoles")
    public HttpResponseMessage getAllRoles(
        @HttpTrigger(name = "req", methods = {HttpMethod.GET}, route = "roles") HttpRequestMessage<Optional<String>> request,
        ExecutionContext context) {

        try {
            // Ejemplo de paginación: obtener "limit" y "offset" de los parámetros de la query.
            String limitParam = request.getQueryParameters().get("limit");
            String offsetParam = request.getQueryParameters().get("offset");
            
            int limit = limitParam != null ? Integer.parseInt(limitParam) : 10;  // Default 10
            int offset = offsetParam != null ? Integer.parseInt(offsetParam) : 0;  // Default 0
            
            List<Role> roles = roleService.getAllRoles(limit, offset);  // Asumiendo que tienes una versión modificada del servicio para soportar esto.

            if (roles.isEmpty()) {
                return request.createResponseBuilder(HttpStatus.NO_CONTENT).body("No roles found").build();
            }

            return request.createResponseBuilder(HttpStatus.OK).body(roles).build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching roles: " + e.getMessage()).build();
        }
    }*/
    // Obtener todos los roles con paginación
    @FunctionName("getAllRoles")
    public HttpResponseMessage getAllRoles(
        @HttpTrigger(name = "req", methods = {HttpMethod.GET}, route = "roles") HttpRequestMessage<Optional<String>> request,
        ExecutionContext context) {

        try {
            // Obtener parámetros de paginación
            String limitParam = request.getQueryParameters().get("limit");
            String offsetParam = request.getQueryParameters().get("offset");

            // Valores por defecto
            int limit = (limitParam != null) ? Integer.parseInt(limitParam) : 10;
            int offset = (offsetParam != null) ? Integer.parseInt(offsetParam) : 0;

            // Llamar al servicio
            List<Role> roles = roleService.getAllRoles(limit, offset);

            return request.createResponseBuilder(HttpStatus.OK)
                          .body(roles)
                          .header("Content-Type", "application/json")
                          .build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                          .body("{\"error\": \"Error fetching roles: " + e.getMessage() + "\"}")
                          .build();
        }
    }

    /*@FunctionName("getRoleById")
    public HttpResponseMessage getRoleById(
        @HttpTrigger(name = "req", methods = {HttpMethod.GET}, route = "roles/{id}") HttpRequestMessage<Optional<String>> request,
        @BindingName("id") String id, ExecutionContext context) {

        try {
            Role role = roleService.getRoleById(Long.valueOf(id));
            if (role != null) {
                return request.createResponseBuilder(HttpStatus.OK).body(role).build();
            } else {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Role not found").build();
            }
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error fetching role: " + e.getMessage()).build();
        }
    }*/
    @FunctionName("getRoleById")
    public HttpResponseMessage getRoleById(
        @HttpTrigger(name = "req", methods = {HttpMethod.GET}, route = "roles/{id}") HttpRequestMessage<Optional<String>> request,
        @BindingName("id") String id, ExecutionContext context) {

        try {
            Role role = roleService.getRoleById(Long.valueOf(id)); // Este método puede lanzar RoleNotFoundException
            return request.createResponseBuilder(HttpStatus.OK)
                            .body(role)
                            .header("Content-Type", "application/json")
                            .build();
        } catch (RoleNotFoundException e) {  // Capturamos la excepción personalizada
            return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                            .body("{\"error\": \"" + e.getMessage() + "\"}")
                            .build();
        } catch (SQLException e) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("{\"error\": \"Database error: " + e.getMessage() + "\"}")
                            .build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                            .body("{\"error\": \"Error fetching role: " + e.getMessage() + "\"}")
                            .build();
        }
    }

    /*@FunctionName("createRole")
    public HttpResponseMessage createRole(
        @HttpTrigger(name = "req", methods = {HttpMethod.POST}, route = "roles") HttpRequestMessage<Optional<Role>> request,
        ExecutionContext context) {

        try {
            Role role = request.getBody().orElseThrow(() -> new IllegalArgumentException("Role data is required"));
            roleService.createRole(role);
            return request.createResponseBuilder(HttpStatus.CREATED).body("Role created successfully").build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error creating role: " + e.getMessage()).build();
        }
    }*/
    // Crear un nuevo rol
    @FunctionName("createRole")
    public HttpResponseMessage createRole(
        @HttpTrigger(name = "req", methods = {HttpMethod.POST}, route = "roles") HttpRequestMessage<Optional<Role>> request,
        ExecutionContext context) {

        try {
            Role role = request.getBody().orElseThrow(() -> new IllegalArgumentException("Role data is required"));
            roleService.createRole(role);
            return request.createResponseBuilder(HttpStatus.CREATED)
                            .body("Role created successfully")
                            .build();
        } catch (IllegalArgumentException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                            .body("{\"error\": \"" + e.getMessage() + "\"}")
                            .build();
        } catch (SQLException e) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("{\"error\": \"Database error: " + e.getMessage() + "\"}")
                            .build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                            .body("{\"error\": \"Error creating role: " + e.getMessage() + "\"}")
                            .build();
        }
    }

    /*@FunctionName("updateRole")
    public HttpResponseMessage updateRole(
        @HttpTrigger(name = "req", methods = {HttpMethod.PUT}, route = "roles/{id}") HttpRequestMessage<Optional<Role>> request,
        @BindingName("id") String id, ExecutionContext context) {

        try {
            Role role = request.getBody().orElseThrow(() -> new IllegalArgumentException("Role data is required"));
            roleService.updateRole(Long.valueOf(id), role);
            return request.createResponseBuilder(HttpStatus.OK).body("Role updated successfully").build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error updating role: " + e.getMessage()).build();
        }
    }*/
    // Actualizar un rol
    @FunctionName("updateRole")
    public HttpResponseMessage updateRole(
        @HttpTrigger(name = "req", methods = {HttpMethod.PUT}, route = "roles/{id}") HttpRequestMessage<Optional<Role>> request,
        @BindingName("id") String id, ExecutionContext context) {

        try {
            Role role = request.getBody().orElseThrow(() -> new IllegalArgumentException("Role data is required"));
            roleService.updateRole(Long.valueOf(id), role);
            return request.createResponseBuilder(HttpStatus.OK)
                            .body("Role updated successfully")
                            .build();
        } catch (RoleNotFoundException e) {  // Capturamos la excepción personalizada si no se encuentra el rol
            return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                            .body("{\"error\": \"" + e.getMessage() + "\"}")
                            .build();
        } catch (SQLException e) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("{\"error\": \"Database error: " + e.getMessage() + "\"}")
                            .build();
        } catch (IllegalArgumentException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                            .body("{\"error\": \"" + e.getMessage() + "\"}")
                            .build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                            .body("{\"error\": \"Error updating role: " + e.getMessage() + "\"}")
                            .build();
        }
    }


    /*@FunctionName("deleteRole")
    public HttpResponseMessage deleteRole(
        @HttpTrigger(name = "req", methods = {HttpMethod.DELETE}, route = "roles/{id}") HttpRequestMessage<Optional<String>> request,
        @BindingName("id") String id, ExecutionContext context) {

        try {
            roleService.deleteRole(Long.valueOf(id));
            return request.createResponseBuilder(HttpStatus.OK).body("Role deleted successfully").build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error deleting role: " + e.getMessage()).build();
        }
    }*/
    // Eliminar un rol
    @FunctionName("deleteRole")
    public HttpResponseMessage deleteRole(
        @HttpTrigger(name = "req", methods = {HttpMethod.DELETE}, route = "roles/{id}") HttpRequestMessage<Optional<String>> request,
        @BindingName("id") String id, ExecutionContext context) {

        try {
        roleService.deleteRole(Long.valueOf(id));  // Este método puede lanzar RoleNotFoundException
        return request.createResponseBuilder(HttpStatus.OK)
                      .body("Role deleted successfully")
                      .build();
        } catch (RoleNotFoundException e) {  // Capturamos la excepción personalizada
            return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"" + e.getMessage() + "\"}")
                        .build();
        } catch (SQLException e) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("{\"error\": \"Database error: " + e.getMessage() + "\"}")
                        .build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"Error deleting role: " + e.getMessage() + "\"}")
                        .build();
        }
    }

    /* 
    DEPRECADO MODIFICADO A REST
    @FunctionName("getAllRoles")
    
    public HttpResponseMessage getAllRoles(
        @HttpTrigger(name = "req", methods = {HttpMethod.GET}, route = "roles") HttpRequestMessage<Optional<String>> request,
        ExecutionContext context) {

        try {
            List<Role> roles = roleService.getAllRoles();
            return request.createResponseBuilder(HttpStatus.OK).body(roles).build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error fetching roles: " + e.getMessage()).build();
        }
    }
    */
}

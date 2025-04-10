package api.azure.function;

import java.util.Optional;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import api.azure.model.UserRole;
import api.azure.service.UserRoleService;

public class UserRoleFunction {

    private final UserRoleService userRoleService = new UserRoleService();

    // Asignar un rol a un usuario
    @FunctionName("assignRoleToUser")
    public HttpResponseMessage assignRoleToUser(
        @HttpTrigger(name = "req", methods = {HttpMethod.POST}, route = "users/{userId}/roles/{roleId}") HttpRequestMessage<Optional<String>> request,
        //@BindingName("userId") String userId, @BindingName("roleId") String roleId, ExecutionContext context) {
        @BindingName("userId") String userId, @BindingName("roleId") String roleId) {
        try {
            // Crear un objeto UserRole con los valores userId y roleId
            UserRole userRole = new UserRole(Long.valueOf(userId), Long.valueOf(roleId));

            //userRoleService.assignRoleToUser(Long.valueOf(userId), Long.valueOf(roleId));
            // Llamar al servicio con el objeto UserRole
            userRoleService.assignRoleToUser(userRole);
            return request.createResponseBuilder(HttpStatus.OK).body("Role assigned to user successfully").build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error assigning role: " + e.getMessage()).build();
        }
    }

    // Eliminar un rol de un usuario
    @FunctionName("removeRoleFromUser")
    public HttpResponseMessage removeRoleFromUser(
        @HttpTrigger(name = "req", methods = {HttpMethod.DELETE}, route = "users/{userId}/roles/{roleId}") HttpRequestMessage<Optional<String>> request,
        @BindingName("userId") String userId, @BindingName("roleId") String roleId, ExecutionContext context) {

        try {
            //userRoleService.removeRoleFromUser(Long.valueOf(userId), Long.valueOf(roleId));
            // Crear un objeto UserRole con los valores userId y roleId
            UserRole userRole = new UserRole(Long.valueOf(userId), Long.valueOf(roleId));
            // Llamar al servicio con el objeto UserRole
            userRoleService.removeRoleFromUser(userRole);
            return request.createResponseBuilder(HttpStatus.OK).body("Role removed from user successfully").build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error removing role: " + e.getMessage()).build();
        }
    }
}

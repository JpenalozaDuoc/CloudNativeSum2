package api.azure.function;

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

import api.azure.model.User;
import api.azure.service.UserService;

public class UserFunction {

    private final UserService userService = new UserService();

    @FunctionName("createUser")
    public HttpResponseMessage createUser(
        @HttpTrigger(name = "req", methods = {HttpMethod.POST}, route = "users") HttpRequestMessage<Optional<User>> request,
        ExecutionContext context) {

        try {
            User user = request.getBody().orElseThrow(() -> new IllegalArgumentException("User data is required"));
            userService.createUser(user);
            return request.createResponseBuilder(HttpStatus.CREATED).body("User created successfully").build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error creating user: " + e.getMessage()).build();
        }
    }

    @FunctionName("getAllUsers")
    public HttpResponseMessage getAllUsers(
        @HttpTrigger(name = "req", methods = {HttpMethod.GET}, route = "users") HttpRequestMessage<Optional<String>> request,
        ExecutionContext context) {

        try {
            List<User> users = userService.getAllUsers();
            return request.createResponseBuilder(HttpStatus.OK).body(users).build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error fetching users: " + e.getMessage()).build();
        }
    }

    @FunctionName("updateUser")
    public HttpResponseMessage updateUser(
        @HttpTrigger(name = "req", methods = {HttpMethod.PUT}, route = "users/{id}") HttpRequestMessage<Optional<User>> request,
        @BindingName("id") String id, ExecutionContext context) {

        try {
            User user = request.getBody().orElseThrow(() -> new IllegalArgumentException("User data is required"));
            userService.updateUser(Long.valueOf(id), user);
            return request.createResponseBuilder(HttpStatus.OK).body("User updated successfully").build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error updating user: " + e.getMessage()).build();
        }
    }

    @FunctionName("deleteUser")
    public HttpResponseMessage deleteUser(
        @HttpTrigger(name = "req", methods = {HttpMethod.DELETE}, route = "users/{id}") HttpRequestMessage<Optional<String>> request,
        @BindingName("id") String id, ExecutionContext context) {

        try {
            userService.deleteUser(Long.valueOf(id));
            return request.createResponseBuilder(HttpStatus.OK).body("User deleted successfully").build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error deleting user: " + e.getMessage()).build();
        }
    }
}

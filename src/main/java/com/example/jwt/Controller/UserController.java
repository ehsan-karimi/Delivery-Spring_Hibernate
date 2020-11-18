package com.example.jwt.Controller;

import com.example.jwt.Model.User.UserDto;
import com.example.jwt.Model.User.UserUpdate;
import com.example.jwt.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin()
public class UserController {

    @Autowired
    private UserService userService;

    // get list of users (only admin have access)
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/user/list", method = RequestMethod.GET)
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(userService.getUserList());
    }

    // everybody can register with this endpoint without any limit
    @RequestMapping(value = "/user/register", method = RequestMethod.POST)
    public ResponseEntity<?> addUser(@RequestBody UserDto user) {
        return ResponseEntity.ok(userService.add(user));
    }

    // edit user profile using UserUpdate model (UserUpdate model = Information requested) (each user can only edit their user profile)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/user/update", method = RequestMethod.POST)
    public ResponseEntity<?> updateUser(@RequestBody UserUpdate user) {
        return userService.update(user);
    }

    // edit user profile using UserUpdate model (UserUpdate model = Information requested) (only admin have access)(access to all users)
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/user/updateByAdmin", method = RequestMethod.POST)
    public ResponseEntity<?> updateUserByAdmin(@RequestBody UserUpdate user) {
        return ResponseEntity.ok(userService.updateByAdmin(user));
    }

    // remove user using user_id (only admin have access)
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/user/remove", method = RequestMethod.POST)
    public ResponseEntity<?> removeUserByAdmin(@RequestBody UserUpdate user) {
        return ResponseEntity.ok(userService.remove(user));
    }

    // get list of user products using user_id (only admin have access)
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/user/{id}/products", method = RequestMethod.GET)
    public ResponseEntity<?> userProductsList(@RequestHeader(value = "Authorization") String token, @PathVariable(value = "id") int userId) {
        return ResponseEntity.ok(userService.userProductsList(userId));
    }

    // get list of user orders using user_id (only admin have access)
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/user/{id}/orders", method = RequestMethod.GET)
    public ResponseEntity<?> userOrdersList(@RequestHeader(value = "Authorization") String token, @PathVariable(value = "id") int userId) {
        return ResponseEntity.ok(userService.userOrdersList(userId));
    }

    // get list of user products using token (only his/her products)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/user/me/products", method = RequestMethod.GET)
    public ResponseEntity<?> myProductsList(@RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok(userService.myProductsList(token));
    }

    // get list of user orders using token (only his/her orders)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/user/me/orders", method = RequestMethod.GET)
    public ResponseEntity<?> myOrdersList(@RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok(userService.myOrdersList(token));
    }
}


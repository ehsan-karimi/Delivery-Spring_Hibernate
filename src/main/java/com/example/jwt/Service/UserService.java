package com.example.jwt.Service;

import com.example.jwt.Config.JwtTokenUtil;
import com.example.jwt.Model.*;
import com.example.jwt.Model.Jwt.JwtDao;
import com.example.jwt.Model.Order.OrdersDao;
import com.example.jwt.Model.Product.ProductsDao;
import com.example.jwt.Model.User.*;
import com.example.jwt.Repository.*;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder bcryptEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtRepository jwtRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private ProductsRepository productsRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TagRepository tagRepository;

    // add user to database and return UserDao model of saved user
    public UserDao add(UserDto user) {
        // finding user role
        RoleDao roleDao = roleRepository.findByName("USER");
        // finding status
        StatusDao statusDao = statusRepository.findByName("NOT_ACTIVE");

        UserDao newUser = new UserDao();
        newUser.setUsername(user.getUsername());
        // encode password using PasswordEncoder and set to UserDao model
        newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
        newUser.setRoleDao(roleDao);
        newUser.setStatusDao(statusDao);
        return userRepository.save(newUser);
    }

    // find all users and return list of them
    public List getUserList() {
        Iterable<UserDao> iterable = userRepository.findAll();
        List<UserList<Long, String, String, String, Timestamp, Timestamp>> listUsers = new ArrayList<>();
        iterable.forEach(s -> {
            listUsers.add(new UserList(s.getId(), s.getUsername(), s.getRole(), s.getStatus(), s.getCreatedAt(), s.getUpdatedAt()));
        });
        return listUsers;
    }

    // edit user and return Response
    public ResponseEntity<?> update(UserUpdate userUpdate) {
        // find user using username
        UserDao userDao = userRepository.findByUsername(userUpdate.getUsername());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // find userDetails using username
        UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(userUpdate.getUsername());
        // Check user information with received information so that the user can only edit his/her own information
        if (userUpdate.getUsername().equals(userDetails.getUsername()) && encoder.matches(userUpdate.getPassword(), userDetails.getPassword())) {
            // find user token and remove it from jwt table and set jwt_id null in user table
            JwtDao jwtDao = jwtRepository.findByUserDao(userDao);
            if (jwtDao != null) {
                userDao.setJwtDao(null);
                userRepository.save(userDao);
                jwtRepository.delete(jwtDao);
            }
            // check if we have new username then replace it to older username
            if (userUpdate.getNewUsername() != null) {
                userDao.setUsername(userUpdate.getNewUsername());
            }
            // check if we have new password then replace it to older password
            if (userUpdate.getNewPassword() != null) {
                // encode password and set it to userDao model
                userDao.setPassword(bcryptEncoder.encode(userUpdate.getNewPassword()));
            }
            // set updated date to userDao model
            userDao.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            // save edited user
            userRepository.save(userDao);
            // find userDetails again (using updated user)
            final UserDetails userDetails2 = userDetailsService.loadUserByUsername(userDao.getUsername());
            // generate token for updated user
            final String token = jwtTokenUtil.generateToken(userDetails2);
            // save token to jwt token table
            Boolean tokenSaved = userDetailsService.saveToken(token, userDao.getUsername());
            // create response
            UserUpdateResponse userUpdateResponse = new UserUpdateResponse();
            userUpdateResponse.setToken(token);
            userUpdateResponse.setSuccessfully(tokenSaved);

            return ResponseEntity.ok(userUpdateResponse);

        } else {
            // create and return new response if Information received is wrong
            Response response = new Response();
            response.setMessage("You can only edit yourself profile");
            return ResponseEntity.ok(response);
        }
    }

    // edit user by admin and return UserUpdateResponse model
    public UserUpdateResponse updateByAdmin(UserUpdate userUpdate) {
        // find user using user id
        UserDao userDao = userRepository.findById(userUpdate.getUserId());
        // find user token and remove it from jwt table and set jwt_id null in user table
        JwtDao jwtDao = jwtRepository.findByUserDao(userDao);
        if (jwtDao != null) {
            userDao.setJwtDao(null);
            userRepository.save(userDao);
            jwtRepository.delete(jwtDao);
        }
        // check if we have new username then replace it to older username
        if (userUpdate.getNewUsername() != null) {
            userDao.setUsername(userUpdate.getNewUsername());
        }
        // check if we have new password then replace it to older password
        if (userUpdate.getNewPassword() != null) {
            userDao.setPassword(bcryptEncoder.encode(userUpdate.getNewPassword()));
        }
        // set updated date to userDao model
        userDao.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        // save edited user
        userRepository.save(userDao);
        // find userDetails again (using updated user)
        final UserDetails userDetails2 = userDetailsService.loadUserByUsername(userDao.getUsername());
        // generate token for updated user
        final String token = jwtTokenUtil.generateToken(userDetails2);
        // save token to jwt token table
        Boolean tokenSaved = userDetailsService.saveToken(token, userDao.getUsername());
        // create response
        UserUpdateResponse userUpdateResponse = new UserUpdateResponse();
        userUpdateResponse.setToken(token);
        userUpdateResponse.setSuccessfully(tokenSaved);

        return userUpdateResponse;
    }

    // remove logically user and return UserDao model
    public UserDao remove(UserUpdate userUpdate) {
        // find user using user id
        UserDao userDao = userRepository.findById(userUpdate.getUserId());
        // find user token and remove it from jwt table and set jwt_id null in user table
        JwtDao jwtDao = jwtRepository.findByUserDao(userDao);
        if (jwtDao != null) {
            userDao.setJwtDao(null);
            userRepository.save(userDao);
            jwtRepository.delete(jwtDao);
        }
        // find status id
        StatusDao statusDao = statusRepository.findByName("DELETED");
        // set new status to userDao model
        userDao.setStatusDao(statusDao);
        // save removed logically user
        userRepository.save(userDao);
        return userDao;
    }

    // find user products and return list of user products
    public List userProductsList(long id) {
        // find user using user id
        UserDao userDao = userRepository.findById(id);
        // find all products that the selected user owns
        List<ProductsDao> productsDao = productsRepository.findAllByOwnerId(userDao);

        return productsDao;
    }

    // find user orders and return list of user orders
    public List userOrdersList(long id) {
        // find user using user id
        UserDao userDao = userRepository.findById(id);
        // find all order that the selected user owns
        List<OrdersDao> productsDao = orderRepository.findAllByShopper(userDao);

        return productsDao;
    }

    // find user products using token and return list of user products
    public List myProductsList(String token) {
        // find user using token
        UserDao userDao = userRepository.findById(getUserId(token));
        // find all products that the selected user owns
        List<ProductsDao> productsDao = productsRepository.findAllByOwnerId(userDao);

        return productsDao;
    }

    // find user orders using token and return list of user orders
    public List myOrdersList(String token) {
        // find user using token
        UserDao userDao = userRepository.findById(getUserId(token));
        // find all order that the selected user owns
        List<OrdersDao> productsDao = orderRepository.findAllByShopper(userDao);

        return productsDao;
    }

    // find user id using token
    private long getUserId(String token) {
        String username = null;
        String jwtToken = null;
        // JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
        if (token != null && token.startsWith("Bearer ")) {
            jwtToken = token.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
            }
        }
        UserDao userDao = new UserDao();
        //Once we get the token validate it.
        if (username != null) {

            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
            System.out.println(username);
            userDao = userRepository.findByUsername(userDetails.getUsername());
            System.out.println(userDetails.getUsername());
            System.out.println(userDao.getId());
        }
        return userDao.getId();
    }

    // insert requirements data to database when we start project
    @EventListener
    public void appReady(ApplicationReadyEvent event) {
        long roleCount = roleRepository.count();
        if (roleCount == 0){
            List<String> listRoles = new ArrayList<>();
            listRoles.add("ADMIN");
            listRoles.add("USER");
            List<String> listRolesDescription = new ArrayList<>();
            listRolesDescription.add("Full Access");
            listRolesDescription.add("Limited Access");

            for (int a = 0; a < listRoles.size(); a++) {
                RoleDao role = new RoleDao();
                role.setName(listRoles.get(a));
                role.setDescription(listRolesDescription.get(a));
                roleRepository.save(role);
            }
        }

        long statsCount = statusRepository.count();
        if (statsCount == 0){
            List<String> listStatus = new ArrayList<>();
            listStatus.add("ACTIVE");
            listStatus.add("NOT_ACTIVE");
            listStatus.add("DELETED");
            listStatus.add("ACTIVE_PRODUCT");
            listStatus.add("ALL_SOLD_OUT");
            listStatus.add("WAITING_ORDER");
            listStatus.add("PACKING_ORDER");
            listStatus.add("DELIVERING_ORDER");
            listStatus.add("DELIVERED_ORDER");
            listStatus.add("CANCELED_ORDER");
            List<String> listStatusDescription = new ArrayList<>();
            listStatusDescription.add("Activated");
            listStatusDescription.add("WithOut Token");
            listStatusDescription.add("Deleted Logically");
            listStatusDescription.add("Product Exist");
            listStatusDescription.add("All Products Were Sold");
            listStatusDescription.add("Waiting For Process");
            listStatusDescription.add("Packing Product");
            listStatusDescription.add("Deliver Product");
            listStatusDescription.add("Delivered Successfully");
            listStatusDescription.add("Order Canceled");

            for (int a = 0; a < listStatus.size(); a++) {
                StatusDao statusDao = new StatusDao();
                statusDao.setDescription(listStatusDescription.get(a));
                statusDao.setName(listStatus.get(a));
                statusRepository.save(statusDao);
            }
        }

        long userCount = userRepository.count();
        if (userCount == 0){
            RoleDao roleDao = roleRepository.findByName("ADMIN");
            StatusDao statusDao = statusRepository.findByName("NOT_ACTIVE");
            UserDao addAdmin = new UserDao();
            addAdmin.setUsername("Admin");
            addAdmin.setPassword(bcryptEncoder.encode("1234"));
            addAdmin.setRoleDao(roleDao);
            addAdmin.setStatusDao(statusDao);
            userRepository.save(addAdmin);
        }

        long tagCount = tagRepository.count();
        if (tagCount == 0){
            List<String> listTags = new ArrayList<>();
            listTags.add("CLOTHS");
            listTags.add("DIGITAL");
            List<String> listTagsDescription = new ArrayList<>();
            listTagsDescription.add("Pooshak");
            listTagsDescription.add("Lavazem Digital");

            for (int a = 0; a < listTags.size(); a++) {
                TagDao tagDao = new TagDao();
                tagDao.setName(listTags.get(a));
                tagDao.setDescription(listTagsDescription.get(a));
                tagRepository.save(tagDao);
            }
        }

    }

}


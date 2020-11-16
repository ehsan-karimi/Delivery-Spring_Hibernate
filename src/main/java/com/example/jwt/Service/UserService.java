package com.example.jwt.Service;

import com.example.jwt.Config.JwtTokenUtil;
import com.example.jwt.Model.*;
import com.example.jwt.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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
    private TagRepository tagRepository;

    public UserDao save(UserDto user) {
        RoleDao roleDao = roleRepository.findByName("USER");
        StatusDao statusDao = statusRepository.findByName("NOT_ACTIVE");

        UserDao newUser = new UserDao();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
        newUser.setRoleDao(roleDao);
        newUser.setStatusDao(statusDao);
        return userRepository.save(newUser);
    }

    public List getUserList() {
        Iterable<UserDao> iterable = userRepository.findAll();
        List<UserList<Long, String, String, String, Timestamp, Timestamp>> listUsers = new ArrayList<>();
        iterable.forEach(s -> {
            listUsers.add(new UserList(s.getId(), s.getUsername(), s.getRole(), s.getStatus(), s.getCreatedAt(), s.getUpdatedAt()));
        });
        return listUsers;
    }

    public UserUpdateResponse update(UserUpdate userUpdate) {
        UserDao userDao = userRepository.findByUsername(userUpdate.getUsername());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(userUpdate.getUsername());
        if (userUpdate.getUsername().equals(userDetails.getUsername()) && encoder.matches(userUpdate.getPassword(), userDetails.getPassword())) {
            JwtDao jwtDao = jwtRepository.findByUserDao(userDao);
            if (jwtDao != null) {
                userDao.setJwtDao(null);
                userRepository.save(userDao);
                jwtRepository.delete(jwtDao);
            }


            if (userUpdate.getNewUsername() != null) {
                userDao.setUsername(userUpdate.getNewUsername());
            }

            if (userUpdate.getNewPassword() != null) {
                userDao.setPassword(bcryptEncoder.encode(userUpdate.getNewPassword()));
            }

            userDao.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            userRepository.save(userDao);

            final UserDetails userDetails2 = userDetailsService.loadUserByUsername(userDao.getUsername());

            final String token = jwtTokenUtil.generateToken(userDetails2);

            Boolean tokenSaved = userDetailsService.saveToken(token, userDao.getUsername());

            UserUpdateResponse userUpdateResponse = new UserUpdateResponse();
            userUpdateResponse.setToken(token);
            userUpdateResponse.setSuccessfully(tokenSaved);

            return userUpdateResponse;

        } else {
            return null;
        }
    }

    public UserUpdateResponse updateByAdmin(UserUpdate userUpdate) {
        UserDao userDao = userRepository.findById(userUpdate.getUserId());
        System.out.println(userDao.getUsername());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(userUpdate.getUsername());
        if (userUpdate.getUsername().equals(userDetails.getUsername()) && encoder.matches(userUpdate.getPassword(), userDetails.getPassword())) {
            JwtDao jwtDao = jwtRepository.findByUserDao(userDao);
            if (jwtDao != null) {
                userDao.setJwtDao(null);
                userRepository.save(userDao);
                jwtRepository.delete(jwtDao);
            }


            if (userUpdate.getNewUsername() != null) {
                userDao.setUsername(userUpdate.getNewUsername());
            }

            if (userUpdate.getNewPassword() != null) {
                userDao.setPassword(bcryptEncoder.encode(userUpdate.getNewPassword()));
            }

            userDao.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            userRepository.save(userDao);

            final UserDetails userDetails2 = userDetailsService.loadUserByUsername(userDao.getUsername());

            final String token = jwtTokenUtil.generateToken(userDetails2);

            Boolean tokenSaved = userDetailsService.saveToken(token, userDao.getUsername());

            UserUpdateResponse userUpdateResponse = new UserUpdateResponse();
            userUpdateResponse.setToken(token);
            userUpdateResponse.setSuccessfully(tokenSaved);

            return userUpdateResponse;

        } else {
            return null;
        }
    }

    public UserDao delete(UserUpdate userUpdate) {
        UserDao userDao = userRepository.findById(userUpdate.getUserId());
        JwtDao jwtDao = jwtRepository.findByUserDao(userDao);
        if (jwtDao != null) {
            userDao.setJwtDao(null);
            userRepository.save(userDao);
            jwtRepository.delete(jwtDao);
        }
        StatusDao statusDao = statusRepository.findByName("DELETED");
        userDao.setStatusDao(statusDao);
        userRepository.save(userDao);
        return userDao;
    }


    @EventListener
    public void appReady(ApplicationReadyEvent event) {
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

        List<String> listStatus = new ArrayList<>();
        listStatus.add("ACTIVE");
        listStatus.add("NOT_ACTIVE");
        listStatus.add("DELETED");
        listStatus.add("ACTIVE_PRODUCT");
        List<String> listStatusDescription = new ArrayList<>();
        listStatusDescription.add("Have Token");
        listStatusDescription.add("WithOut Token");
        listStatusDescription.add("Deleted Logically");
        listStatusDescription.add("Product Exist");

        for (int a = 0; a < listStatus.size(); a++) {
            StatusDao statusDao = new StatusDao();
            statusDao.setDescription(listStatusDescription.get(a));
            statusDao.setName(listStatus.get(a));
            statusRepository.save(statusDao);
        }

        RoleDao roleDao = roleRepository.findByName("ADMIN");
        StatusDao statusDao = statusRepository.findByName("NOT_ACTIVE");
        UserDao addAdmin = new UserDao();
        addAdmin.setUsername("Admin");
        addAdmin.setPassword(bcryptEncoder.encode("1234"));
        addAdmin.setRoleDao(roleDao);
        addAdmin.setStatusDao(statusDao);
        userRepository.save(addAdmin);


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


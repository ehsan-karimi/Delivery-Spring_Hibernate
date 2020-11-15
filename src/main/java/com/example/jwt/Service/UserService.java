package com.example.jwt.Service;

import com.example.jwt.Model.*;
import com.example.jwt.Repository.RoleRepository;
import com.example.jwt.Repository.StatusRepository;
import com.example.jwt.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserService  {
    @Autowired
    private UserRepository userDao;
    @Autowired
    private PasswordEncoder bcryptEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private StatusRepository statusRepository;

    public UserDao save(UserDto user) {
//        int roleId = roleRepository.findByName("USER").getId();
//        int statusId = statusRepository.findByName("NOT_ACTIVE").getId();
        RoleDao roleDao = roleRepository.findByName("USER");
        StatusDao statusDao = statusRepository.findByName("NOT_ACTIVE");

        UserDao newUser = new UserDao();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
        newUser.setRoleDao(roleDao);
        newUser.setStatusDao(statusDao);
        return userDao.save(newUser);
    }

    @EventListener
    public void appReady(ApplicationReadyEvent event) {
        RoleDao role = new RoleDao();
        role.setName("USER");
        role.setDescription("Access To SomeThing");
        roleRepository.save(role);

        StatusDao statusDao = new StatusDao();
        statusDao.setDescription("didnt authenticate");
        statusDao.setName("NOT_ACTIVE");
        statusRepository.save(statusDao);
    }

}


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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Service
public class UserService {
    @Autowired
    private UserRepository userDao;
    @Autowired
    private PasswordEncoder bcryptEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private StatusRepository statusRepository;

    public UserDao save(UserDto user) {
        RoleDao roleDao = roleRepository.findByName("USER");
        StatusDao statusDao = statusRepository.findByName("NOT_ACTIVE");

        UserDao newUser = new UserDao();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
        newUser.setRoleDao(roleDao);
        newUser.setStatusDao(statusDao);
        return userDao.save(newUser);
    }

    public List getUserList() {
        Iterable<UserDao> iterable = userDao.findAll();
        List<UserList<Long, String, String, String, Timestamp, Timestamp>> listUsers = new ArrayList<>();
        iterable.forEach(s -> {
            listUsers.add(new UserList(s.getId(), s.getUsername(), s.getRole(), s.getStatus(), s.getCreatedAt(), s.getUpdatedAt()));
        });
        return listUsers;
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
        List<String> listStatusDescription = new ArrayList<>();
        listStatusDescription.add("Have Token");
        listStatusDescription.add("WithOut Token");
        listStatusDescription.add("Deleted Logically");

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
        userDao.save(addAdmin);
    }

}


package com.example.jwt.Service;

import com.example.jwt.Model.JwtDao;
import com.example.jwt.Model.StatusDao;
import com.example.jwt.Model.UserDao;
import com.example.jwt.Model.UserDto;
import com.example.jwt.Repository.JwtRepository;
import com.example.jwt.Repository.RoleRepository;
import com.example.jwt.Repository.StatusRepository;
import com.example.jwt.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtRepository jwtRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Autowired
    private StatusRepository statusRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDao user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                getAuthority(user));
    }

    private Set<SimpleGrantedAuthority> getAuthority(UserDao user) throws ExceptionInInitializerError {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
//        user.getRoles().forEach(role -> {
//            //authorities.add(new SimpleGrantedAuthority(role.getName()));
//            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
//        });
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRoleDao().getName()));
        return authorities;
        //return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    public Boolean saveToken(String token, String username) {
        UserDao userDao = userRepository.findByUsername(username);
        StatusDao statusDeleted = statusRepository.findByName("DELETED");

        if (statusDeleted != userDao.getStatusDao()) {
            StatusDao statusDao = statusRepository.findByName("ACTIVE");
            JwtDao jwtDao = new JwtDao();
            jwtDao.setStatusDao(statusDao);
            jwtDao.setToken(token);
            jwtDao.setUserDao(userDao);
            jwtRepository.save(jwtDao);

            jwtDao = jwtRepository.findByUserDao(userDao);

            userDao.setJwtDao(jwtDao);
            userDao.setStatusDao(statusDao);
        }


        if (userRepository.save(userDao).getJwtDao().getToken() != null) {
            return true;
        } else {
            return false;
        }
    }
}

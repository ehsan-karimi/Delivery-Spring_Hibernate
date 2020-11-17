package com.example.jwt.Service;

import com.example.jwt.Config.JwtTokenUtil;
import com.example.jwt.Model.*;
import com.example.jwt.Repository.*;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder bcryptEncoder;
    @Autowired
    private OrderStatusRepository orderStatusRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ProductsRepository productsRepository;

    public OrdersDao save(String token, OrderDto orderDto) {
        OrdersDao newOrder = new OrdersDao();
        ProductsDao productsDao = productsRepository.findById(orderDto.getProductId());
        UserDao userDao = userRepository.findById(getUserId(token));

        int amount = productsDao.getAmount() - orderDto.getAmount();
        if (amount < 0) {
            return null;
        }

        if (amount == 0) {
            StatusDao statusDao = statusRepository.findByName("ALL_SOLD_OUT");
            productsDao.setStatusDao(statusDao);
        }

        newOrder.setPrice(orderDto.getAmount() * productsDao.getPrice());
        newOrder.setShopper_id(userDao);
        newOrder.setProductsId(productsDao);
        newOrder.setOwnerId(productsDao);
        newOrder.setAmount(orderDto.getAmount());
        newOrder.setAddress(orderDto.getAddress());
        newOrder.setPhone(orderDto.getPhone());
        newOrder.setPostalCode(orderDto.getPostalCode());

        OrdersDao insertedOrder = orderRepository.save(newOrder);

        OrderStatusDao orderStatusDao = new OrderStatusDao();
        StatusDao statusDao = statusRepository.findByName("WAITING");
        orderStatusDao.setStatusDao(statusDao);
        orderStatusDao.setOrdersDao(insertedOrder);
        OrderStatusDao insertedOrderStatus = orderStatusRepository.save(orderStatusDao);


        productsDao.setAmount(amount);
        productsRepository.save(productsDao);

        insertedOrder.setOrderStatusDao(insertedOrderStatus);

        return orderRepository.save(insertedOrder);
    }


    public OrdersDao update(String token, OrderDto orderDto) {
        OrdersDao ordersDao = orderRepository.findById(orderDto.getIdOrder());
        ProductsDao productsDao = productsRepository.findById(orderDto.getProductId());
        String name = userRepository.findById(getUserId(token)).getUsername();
        if (ordersDao.getShopper_id().getId() == getUserId(token) || name.equals("Admin")) {
            if (orderDto.getAmount() > 0) {
                int originalAmount = ordersDao.getAmount() + productsDao.getAmount();
                int amount = originalAmount - orderDto.getAmount();
                if (amount < 0) {
                    return null;
                }
                if (productsDao.getStatusDao().getName().equals("ALL_SOLD_OUT") && amount > 0) {
                    StatusDao statusDao = statusRepository.findByName("ACTIVE_PRODUCT");
                    productsDao.setStatusDao(statusDao);
                }

                if (amount == 0) {
                    StatusDao statusDao = statusRepository.findByName("ALL_SOLD_OUT");
                    productsDao.setStatusDao(statusDao);
                }

                productsDao.setAmount(amount);
                productsRepository.save(productsDao);
                ordersDao.setAmount(orderDto.getAmount());
                ordersDao.setPrice(orderDto.getAmount() * productsDao.getPrice());
            }

            if (orderDto.getAddress() != null) {
                ordersDao.setAddress(orderDto.getAddress());
            }

            if (orderDto.getPhone() > 0) {
                ordersDao.setPhone(orderDto.getPhone());
            }

            if (orderDto.getPostalCode() > 0) {
                ordersDao.setPostalCode(orderDto.getPostalCode());
            }


            return orderRepository.save(ordersDao);
        } else {
            return null;
        }
    }

    public OrderStatusDao remove(String token, OrderDto orderDto) {
        String name = userRepository.findById(getUserId(token)).getUsername();
        OrdersDao ordersDao = orderRepository.findById(orderDto.getIdOrder());
        if (ordersDao.getShopper_id().getId() == getUserId(token) || name.equals("Admin")) {
            ProductsDao productsDao = productsRepository.findById(ordersDao.getProductsId().getId());
            StatusDao statusDao = statusRepository.findByName("CANCEL_PRODUCT");

            OrderStatusDao newOrderStatus = new OrderStatusDao();
            newOrderStatus.setOrdersDao(ordersDao);
            newOrderStatus.setStatusDao(statusDao);


            if (productsDao.getStatusDao().getName().equals("ALL_SOLD_OUT")) {
                StatusDao statusDao2 = statusRepository.findByName("ACTIVE_PRODUCT");
                productsDao.setStatusDao(statusDao2);
            }
            productsDao.setAmount(ordersDao.getAmount() + productsDao.getAmount());
            productsRepository.save(productsDao);


            return orderStatusRepository.save(newOrderStatus);
        } else {
            return null;
        }

    }

    public List getOrdersList(String token){

        String name = userRepository.findById(getUserId(token)).getUsername();
        List<OrdersList<Long, ProductsDao, Integer, UserDao, Integer, String, Integer, Integer, Timestamp, Timestamp, OrderStatusDao>> listProduct = new ArrayList<>();
        if (name.equals("Admin")) {
            Iterable<OrdersDao> iterable = orderRepository.findAll();

            iterable.forEach(s -> {
                listProduct.add(new OrdersList(s.getId(), s.getProductsId(), s.getAmount(), s.getShopper_id(), s.getPrice(), s.getAddress(), s.getPhone(), s.getPostalCode(), s.getCreatedAt(), s.getUpdatedAt(), s.getOrderStatusDao()));
            });
            return listProduct;
        }else {
            UserDao userDao = userRepository.findById(getUserId(token));
            Iterable<OrdersDao> iterable = orderRepository.findAllByShopper_id(userDao);

            iterable.forEach(s -> {
                listProduct.add(new OrdersList(s.getId(), s.getProductsId(), s.getAmount(), s.getShopper_id(), s.getPrice(), s.getAddress(), s.getPhone(), s.getPostalCode(), s.getCreatedAt(), s.getUpdatedAt(), s.getOrderStatusDao()));
            });
            return listProduct;
        }

    }

//    public ProductsDao save(String token, ProductDto productDto) {
//        ProductsDao newProduct = new ProductsDao();
//        StatusDao statusDao = statusRepository.findByName("ACTIVE_PRODUCT");
//        newProduct.setName(productDto.getName());
//        newProduct.setPrice(productDto.getPrice());
//        newProduct.setAmount(productDto.getAmount());
//        newProduct.setTagId(tagRepository.findById(productDto.getTagId()));
//        newProduct.setOwnerId(getUserId(token));
//        newProduct.setStatusDao(statusDao);
//
//        return productsRepository.save(newProduct);
//    }

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
}

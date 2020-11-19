package com.example.jwt.Service;

import com.example.jwt.Config.JwtTokenUtil;
import com.example.jwt.Model.*;
import com.example.jwt.Model.Order.*;
import com.example.jwt.Model.Product.ProductsDao;
import com.example.jwt.Model.User.UserDao;
import com.example.jwt.Repository.*;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private UserRepository userRepository;
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

    // add order to database and return OrdersDao model of saved order
    public ResponseEntity<?> add(String token, OrderDto orderDto) {
        OrdersDao newOrder = new OrdersDao();
        // find product using product id
        ProductsDao productsDao = productsRepository.findById(orderDto.getProductId());
        // find user using user id
        UserDao userDao = userRepository.findById(getUserId(token));
        // check if requested amount is bigger than product amount return response
        int amount = productsDao.getAmount() - orderDto.getAmount();
        if (amount < 0) {
            // create and return new response if Information received is wrong
            Response response = new Response();
            response.setMessage("Your requested amount is bigger than product amount");
            return ResponseEntity.ok(response);
        }
        // check if amount (amount = product amount - requested amount) equal to 0 find ALL_SOLD_OUT status and replace it to older status
        if (amount == 0) {
            StatusDao statusDao = statusRepository.findByName("ALL_SOLD_OUT");
            productsDao.setStatusDao(statusDao);
        }
        // set requested information to model and add to database
        newOrder.setPrice(orderDto.getAmount() * productsDao.getPrice());
        newOrder.setShopper(userDao);
        newOrder.setProductsId(productsDao);
        newOrder.setOwnerId(productsDao);
        newOrder.setAmount(orderDto.getAmount());
        newOrder.setAddress(orderDto.getAddress());
        newOrder.setPhone(orderDto.getPhone());
        newOrder.setPostalCode(orderDto.getPostalCode());
        OrdersDao insertedOrder = orderRepository.save(newOrder);

        OrderStatusDao orderStatusDao = new OrderStatusDao();
        // find status (WAITING)
        StatusDao statusDao = statusRepository.findByName("WAITING");
        // set status to orderStatusDao model and set order id to orderStatusDao model and add to database (it is order status)
        orderStatusDao.setStatusDao(statusDao);
        orderStatusDao.setOrdersDao(insertedOrder);
        OrderStatusDao insertedOrderStatus = orderStatusRepository.save(orderStatusDao);
        // replace new amount to older amount and save
        productsDao.setAmount(amount);
        productsRepository.save(productsDao);
        // replace new status to older status and save
        insertedOrder.setOrderStatusDao(insertedOrderStatus);
        return ResponseEntity.ok(orderRepository.save(insertedOrder));
    }

    // add order status and return OrdersDao model
    public ResponseEntity<?> addOrderStatus(OrderDto orderDto) {
        // find order using order id
        OrdersDao ordersDao = orderRepository.findById(orderDto.getIdOrder());
        // find order status using order id
        OrderStatusDao orderStatusDao = orderStatusRepository.findByOrdersDao(ordersDao);
        // check if status equal with DELIVERED_ORDER or CANCELED_ORDER return response (cant add order status when status is DELIVERED_ORDER or CANCELED_ORDER)
        if (orderStatusDao.getStatusDao().getName().equals("DELIVERED_ORDER") || orderStatusDao.getStatusDao().getName().equals("CANCELED_ORDER")) {
            // create and return new response if Information received is wrong
            Response response = new Response();
            response.setMessage("You cant add order status when last status is DELIVERED_ORDER or CANCELED_ORDER");
            return ResponseEntity.ok(response);
        }
        // find status and replace new to older
        OrderStatusDao newOrderStatus = new OrderStatusDao();
        StatusDao statusDao;
        switch (orderStatusDao.getStatusDao().getName()) {
            case "WAITING_ORDER":
                statusDao = statusRepository.findByName("PACKING_ORDER");
                newOrderStatus.setStatusDao(statusDao);
                newOrderStatus.setOrdersDao(ordersDao);
                break;

            case "PACKING_ORDER":
                statusDao = statusRepository.findByName("DELIVERING_ORDER");
                newOrderStatus.setStatusDao(statusDao);
                newOrderStatus.setOrdersDao(ordersDao);
                break;

            case "DELIVERING_ORDER":
                statusDao = statusRepository.findByName("DELIVERED_ORDER");
                newOrderStatus.setStatusDao(statusDao);
                newOrderStatus.setOrdersDao(ordersDao);
                break;

        }
        ordersDao.setOrderStatusDao(orderStatusRepository.save(newOrderStatus));

        return ResponseEntity.ok(orderRepository.save(ordersDao));
    }

    // edit order and return OrdersDao model
    public ResponseEntity<?> update(String token, OrderDto orderDto) {
        // find order using order id
        OrdersDao ordersDao = orderRepository.findById(orderDto.getIdOrder());
        // find product using product id
        ProductsDao productsDao = productsRepository.findById(orderDto.getProductId());
        // find username using token
        String name = userRepository.findById(getUserId(token)).getUsername();
        // Check user information with received information so that the user can only edit his/her own order (admin have access to all orders)
        if (ordersDao.getShopper().getId() == getUserId(token) || name.equals("Admin")) {
            // check if we have new amount
            if (orderDto.getAmount() > 0) {
                // check if requested amount is bigger than product amount return response and find new amount
                int originalAmount = ordersDao.getAmount() + productsDao.getAmount();
                int amount = originalAmount - orderDto.getAmount();
                if (amount < 0) {
                    // create and return new response if Information received is wrong
                    Response response = new Response();
                    response.setMessage("Your requested amount is bigger than product amount");
                    return ResponseEntity.ok(response);
                }
                // check if product status is ALL_SOLD_OUT and new amount is bigger than 0 then edit status
                if (productsDao.getStatusDao().getName().equals("ALL_SOLD_OUT") && amount > 0) {
                    StatusDao statusDao = statusRepository.findByName("ACTIVE_PRODUCT");
                    productsDao.setStatusDao(statusDao);
                }
                // check if amount is 0 then edit status
                if (amount == 0) {
                    StatusDao statusDao = statusRepository.findByName("ALL_SOLD_OUT");
                    productsDao.setStatusDao(statusDao);
                }
                // edit product amount and order amount and price
                productsDao.setAmount(amount);
                productsRepository.save(productsDao);
                ordersDao.setAmount(orderDto.getAmount());
                ordersDao.setPrice(orderDto.getAmount() * productsDao.getPrice());
            }
            // check if we have new address then replace to older
            if (orderDto.getAddress() != null) {
                ordersDao.setAddress(orderDto.getAddress());
            }
            // check if we have new phone then replace to older
            if (orderDto.getPhone() > 0) {
                ordersDao.setPhone(orderDto.getPhone());
            }
            // check if we have new postal code then replace to older
            if (orderDto.getPostalCode() > 0) {
                ordersDao.setPostalCode(orderDto.getPostalCode());
            }

            return ResponseEntity.ok(orderRepository.save(ordersDao));
        } else {
            // create and return new response if Information received is wrong
            Response response = new Response();
            response.setMessage("You can only edit yourself order");
            return ResponseEntity.ok(response);
        }
    }

    // remove logically order and return OrdersDao model
    public ResponseEntity<?> remove(String token, OrderDto orderDto) {
        // find username using token
        String name = userRepository.findById(getUserId(token)).getUsername();
        // find order using order id
        OrdersDao ordersDao = orderRepository.findById(orderDto.getIdOrder());
        // Check user information with received information so that the user can remove logically his/her own order (admin have access to all orders)
        if (ordersDao.getShopper().getId() == getUserId(token) || name.equals("Admin")) {
            // find product using product id
            ProductsDao productsDao = productsRepository.findById(ordersDao.getProductsId().getId());
            // find status (CANCELED_ORDER)
            StatusDao statusDao = statusRepository.findByName("CANCELED_ORDER");

            OrderStatusDao newOrderStatus = new OrderStatusDao();
            newOrderStatus.setOrdersDao(ordersDao);
            newOrderStatus.setStatusDao(statusDao);

            // check if product status is ALL_SOLD_OUT then replace ACTIVE_PRODUCT to it
            if (productsDao.getStatusDao().getName().equals("ALL_SOLD_OUT")) {
                StatusDao statusDao2 = statusRepository.findByName("ACTIVE_PRODUCT");
                productsDao.setStatusDao(statusDao2);
            }
            // and receive back the amount
            productsDao.setAmount(ordersDao.getAmount() + productsDao.getAmount());
            productsRepository.save(productsDao);

            ordersDao.setOrderStatusDao(orderStatusRepository.save(newOrderStatus));

            return ResponseEntity.ok(orderRepository.save(ordersDao));
        } else {
            // create and return new response if Information received is wrong
            Response response = new Response();
            response.setMessage("You can only remove logically yourself order");
            return ResponseEntity.ok(response);
        }
    }

    // find orders and return list (user can only get his/her orders)
    public List getOrdersList(String token) {

        String name = userRepository.findById(getUserId(token)).getUsername();
        List<OrdersList<Long, ProductsDao, Integer, UserDao, Integer, String, Integer, Integer, Timestamp, Timestamp, OrderStatusDao>> listProduct = new ArrayList<>();
        if (name.equals("Admin")) {
            Iterable<OrdersDao> iterable = orderRepository.findAll();

            iterable.forEach(s -> {
                listProduct.add(new OrdersList(s.getId(), s.getProductsId(), s.getAmount(), s.getShopper(), s.getPrice(), s.getAddress(), s.getPhone(), s.getPostalCode(), s.getCreatedAt(), s.getUpdatedAt(), s.getOrderStatusDao()));
            });
            return listProduct;
        } else {
            UserDao userDao = userRepository.findById(getUserId(token));
            Iterable<OrdersDao> iterable = orderRepository.findAllByShopper(userDao);

            iterable.forEach(s -> {
                listProduct.add(new OrdersList(s.getId(), s.getProductsId(), s.getAmount(), s.getShopper(), s.getPrice(), s.getAddress(), s.getPhone(), s.getPostalCode(), s.getCreatedAt(), s.getUpdatedAt(), s.getOrderStatusDao()));
            });
            return listProduct;
        }

    }

    // find order status and return list (user can only get his/her order status)
    public List orderStatusList(String token, long id) {
        OrdersDao ordersDao = orderRepository.findById(id);
        String name = userRepository.findById(getUserId(token)).getUsername();
        if (ordersDao.getShopper().getId() == getUserId(token) || name.equals("Admin")) {
            Iterable<OrderStatusDao> iterable = orderStatusRepository.findAllByOrdersDao(ordersDao);

            List<OrderStatusList<String, Timestamp>> listOrderStatus = new ArrayList<>();

            iterable.forEach(s -> {
                listOrderStatus.add(new OrderStatusList<>(s.getStatusDao().getName(), s.getCreatedAt()));
            });

            return listOrderStatus;
        } else {
            return null;
        }

    }

    // get user id using token
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

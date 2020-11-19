package com.example.jwt.Service;

import com.example.jwt.Config.JwtTokenUtil;
import com.example.jwt.Model.*;
import com.example.jwt.Model.Order.OrdersDao;
import com.example.jwt.Model.Product.ProductDto;
import com.example.jwt.Model.Product.ProductList;
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
import java.util.Date;
import java.util.List;


@Service
public class ProductsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductsRepository productsRepository;

    // add product to database and return ProductsDao model of saved product
    public ProductsDao add(String token, ProductDto productDto) {
        ProductsDao newProduct = new ProductsDao();
        // find status
        StatusDao statusDao = statusRepository.findByName("ACTIVE_PRODUCT");
        newProduct.setName(productDto.getName());
        newProduct.setPrice(productDto.getPrice());
        newProduct.setAmount(productDto.getAmount());
        newProduct.setTagId(tagRepository.findById(productDto.getTagId()));
        newProduct.setOwnerId(userRepository.findById(getUserId(token)));
        newProduct.setStatusDao(statusDao);

        return productsRepository.save(newProduct);
    }

    // find all products and return list of them
    public List getProductsList(String token) {
        // find username using token
        String name = userRepository.findById(getUserId(token)).getUsername();
        List<ProductList<Long, String, Integer, String, Integer, String, Integer, Timestamp, Timestamp>> listProduct = new ArrayList<>();
        // check if user is admin return all product
        if (name.equals("Admin")) {
            Iterable<ProductsDao> iterable = productsRepository.findAll();

            iterable.forEach(s -> {
                listProduct.add(new ProductList(s.getId(), s.getName(), s.getTagId().getId(), s.getStatusDao().getName(), s.getPrice(), s.getOwnerId().getUsername(), s.getAmount(), s.getCreatedAt(), s.getUpdatedAt()));
            });
            return listProduct;
        } else {
            // if user is not admin return product that they are active(EXIST)
            StatusDao statusDao = statusRepository.findByName("ACTIVE_PRODUCT");
            Iterable<ProductsDao> iterable = productsRepository.findAllByStatusDao(statusDao);

            iterable.forEach(s -> {
                listProduct.add(new ProductList(s.getId(), s.getName(), s.getTagId().getId(), s.getStatusDao().getName(), s.getPrice(), null, s.getAmount(), s.getCreatedAt(), s.getUpdatedAt()));
            });
            return listProduct;
        }
    }

    // edit product and return Response
    public ResponseEntity<?> update(String token, ProductDto productDto) {
        // find product using product id
        ProductsDao updateProduct = productsRepository.findById(productDto.getId());
        // find username using token
        String name = userRepository.findById(getUserId(token)).getUsername();
        // Check user information with received information so that the user can only edit his/her own product
        if (updateProduct.getOwnerId().getId() == getUserId(token) || name.equals("Admin")) {
            // check if we have new product name then replace it to older product name
            if (productDto.getName() != null) {
                updateProduct.setName(productDto.getName());
            }
            // check if we have new price then replace it to older price
            if (productDto.getPrice() > 0) {
                updateProduct.setPrice(productDto.getPrice());
            }
            // check if we have new amount then replace it to older amount
            if (productDto.getAmount() > 0) {
                updateProduct.setAmount(productDto.getAmount());
            }
            // check if we have new tag then replace it to older tag
            if (productDto.getTagId() > 0) {
                updateProduct.setTagId(tagRepository.findById(productDto.getTagId()));
            }

            return ResponseEntity.ok(productsRepository.save(updateProduct));
        } else {
            // create and return new response if Information received is wrong
            Response response = new Response();
            response.setMessage("You can only edit yourself products");
            return ResponseEntity.ok(response);
        }
    }

    // remove logically product and return ProductsDao model
    public ResponseEntity<?> remove(String token, ProductDto productDto) {
        // find product using product id
        ProductsDao deleteProduct = productsRepository.findById(productDto.getId());
        // find username using token
        String name = userRepository.findById(getUserId(token)).getUsername();
        // Check user information with received information so that the user can only remove his/her own product (only admin have access to all products)
        if (deleteProduct.getOwnerId().getId() == getUserId(token) || name.equals("Admin")) {
            // find status and replace to older status and save to database
            StatusDao statusDao = statusRepository.findByName("DELETED");
            deleteProduct.setStatusDao(statusDao);
            productsRepository.save(deleteProduct);
            return ResponseEntity.ok(deleteProduct);
        } else {
            // create and return new response if Information received is wrong
            Response response = new Response();
            response.setMessage("You can only remove yourself products");
            return ResponseEntity.ok(response);
        }
    }

    // edit amount product and return ProductsDao model
    public ResponseEntity<?> addAmount(String token, ProductDto productDto) {
        // find product using product id
        ProductsDao updateProduct = productsRepository.findById(productDto.getId());
        // find username using token
        String name = userRepository.findById(getUserId(token)).getUsername();
        // Check user information with received information so that the user can only add amount to his/her own product (only admin have access to all products)
        if (updateProduct.getOwnerId().getId() == getUserId(token) || name.equals("Admin")) {
            // check if we have new amount replace it to older amount
            if (productDto.getAmount() > 0) {
                updateProduct.setAmount(productDto.getAmount());
            }

            return ResponseEntity.ok(productsRepository.save(updateProduct));
        } else {
            // create and return new response if Information received is wrong
            Response response = new Response();
            response.setMessage("You can only add amount to yourself products");
            return ResponseEntity.ok(response);
        }
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

    // find orders of selected product and return list
    public ResponseEntity<?> ordersList(String token, long id) {
        // find product using product id
        ProductsDao productsDao = productsRepository.findById(id);
        if (productsDao == null){
            // create and return new response if Information received is wrong
            Response response = new Response();
            response.setMessage("Product not exist");
            return ResponseEntity.ok(response);
        }
        // find orders using product id (productsDao model)
        List<OrdersDao> ordersDao = orderRepository.findAllByProductsId(productsDao);
        // find username using token
        String name = userRepository.findById(getUserId(token)).getUsername();
        // Check user information with received information so that the user can get order list of his/her own product (only admin have access to all products)
        if (productsDao.getOwnerId().getId() == getUserId(token) || name.equals("Admin")) {
            return ResponseEntity.ok(ordersDao);
        } else {
            // create and return new response if Information received is wrong
            Response response = new Response();
            response.setMessage("You can only get order list of yourself products");
            return ResponseEntity.ok(response);
        }
    }

    // find product using tag and return list of product
    public List search(String tag) {
        StatusDao statusDao = statusRepository.findByName("ACTIVE_PRODUCT");
        List<ProductsDao> productsDao = productsRepository.findAllByTagIdAndStatusDao(tagRepository.findByName(tag), statusDao);
        return productsDao;
    }

    // return tags list
    public List tagsList() {
        List<TagDao> tagDao = tagRepository.findAll();
        return tagDao;
    }

}


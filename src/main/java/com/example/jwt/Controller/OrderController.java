package com.example.jwt.Controller;

import com.example.jwt.Model.Order.OrderDto;
import com.example.jwt.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin()
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/order/add", method = RequestMethod.POST)
    public ResponseEntity<?> saveProduct(@RequestHeader(value="Authorization") String token, @RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(orderService.save(token,orderDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/order/list", method = RequestMethod.GET)
    public ResponseEntity<?> getProducts(@RequestHeader(value="Authorization") String token) {
        return ResponseEntity.ok(orderService.getOrdersList(token));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/order/update", method = RequestMethod.POST)
    public ResponseEntity<?> updateProduct(@RequestHeader(value="Authorization") String token, @RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(orderService.update(token,orderDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/order/remove", method = RequestMethod.POST)
    public ResponseEntity<?> removeProduct(@RequestHeader(value="Authorization") String token,@RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(orderService.remove(token,orderDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/order/addOrderStatus", method = RequestMethod.POST)
    public ResponseEntity<?> addOrderStatus(@RequestHeader(value="Authorization") String token,@RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(orderService.addOrderStatus(token,orderDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/order/{id}/status", method = RequestMethod.GET)
    public ResponseEntity<?> orderStatusList(@RequestHeader(value="Authorization") String token,@PathVariable(value = "id") int orderId) {
        return ResponseEntity.ok(orderService.orderStatusList(token, orderId));
    }

}

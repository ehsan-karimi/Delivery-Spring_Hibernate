package com.example.jwt.Controller;

import com.example.jwt.Model.Order.OrderDto;
import com.example.jwt.Service.OrderService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@ApiResponses({
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not Found"),
        @ApiResponse(code = 415, message = "Unsupported Media Type"),
        @ApiResponse(code = 422, message = "Unprocessable Entity")
})
@RequestMapping(value = "/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // add new order using OrderDto model
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/order/add", method = RequestMethod.POST)
    public ResponseEntity<?> saveProduct(@RequestHeader(value="Authorization") String token, @RequestBody OrderDto orderDto) {
        return orderService.add(token,orderDto);
    }

    // get list of orders using token
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/order/list", method = RequestMethod.GET)
    public ResponseEntity<?> getProducts(@RequestHeader(value="Authorization") String token) {
        return ResponseEntity.ok(orderService.getOrdersList(token));
    }

    // edit order using OrderDto model
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/order/update", method = RequestMethod.POST)
    public ResponseEntity<?> updateProduct(@RequestHeader(value="Authorization") String token, @RequestBody OrderDto orderDto) {
        return orderService.update(token,orderDto);
    }

    // remove logically order using OrderDto model
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/order/remove", method = RequestMethod.POST)
    public ResponseEntity<?> removeProduct(@RequestHeader(value="Authorization") String token,@RequestBody OrderDto orderDto) {
        return orderService.remove(token,orderDto);
    }

    // add order status using OrderDto model
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/order/addOrderStatus", method = RequestMethod.POST)
    public ResponseEntity<?> addOrderStatus(@RequestBody OrderDto orderDto) {
        return orderService.addOrderStatus(orderDto);
    }

    // get list of order status using order id (admin access to all orders)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/order/{id}/status", method = RequestMethod.GET)
    public ResponseEntity<?> orderStatusList(@RequestHeader(value="Authorization") String token,@PathVariable(value = "id") int orderId) {
        return ResponseEntity.ok(orderService.orderStatusList(token, orderId));
    }

}

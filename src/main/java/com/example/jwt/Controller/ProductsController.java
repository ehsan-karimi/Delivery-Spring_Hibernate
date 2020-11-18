package com.example.jwt.Controller;

import com.example.jwt.Model.Product.ProductDto;
import com.example.jwt.Service.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin()
public class ProductsController {

    @Autowired
    private ProductsService productsService;

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/product/add", method = RequestMethod.POST)
    public ResponseEntity<?> saveProduct(@RequestHeader(value="Authorization") String token, @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productsService.save(token,productDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/product/list", method = RequestMethod.GET)
    public ResponseEntity<?> getProducts(@RequestHeader(value="Authorization") String token) {
        return ResponseEntity.ok(productsService.getProductsList(token));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/product/update", method = RequestMethod.POST)
    public ResponseEntity<?> updateProduct(@RequestHeader(value="Authorization") String token, @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productsService.update(token,productDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/product/remove", method = RequestMethod.POST)
    public ResponseEntity<?> removeProduct(@RequestHeader(value="Authorization") String token,@RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productsService.delete(token,productDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/product/addAmount", method = RequestMethod.POST)
    public ResponseEntity<?> addProductAmount(@RequestHeader(value="Authorization") String token,@RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productsService.addAmount(token,productDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/product/{id}/orders", method = RequestMethod.GET)
    public ResponseEntity<?> orderStatusList(@RequestHeader(value="Authorization") String token,@PathVariable(value = "id") int productId) {
        return ResponseEntity.ok(productsService.orderList(token, productId));
    }

    @RequestMapping(value = "/product/search", method = RequestMethod.GET)
    public ResponseEntity<?> search(@RequestParam String tag) {
        return ResponseEntity.ok(productsService.search(tag));
    }

}

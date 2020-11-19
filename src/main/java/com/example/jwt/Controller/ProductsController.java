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

    // add new products using ProductDto model (ProductDto model = Information requested)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/product/add", method = RequestMethod.POST)
    public ResponseEntity<?> addProduct(@RequestHeader(value="Authorization") String token, @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productsService.add(token,productDto));
    }

    // get list of products
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/product/list", method = RequestMethod.GET)
    public ResponseEntity<?> getProductsList(@RequestHeader(value="Authorization") String token) {
        return ResponseEntity.ok(productsService.getProductsList(token));
    }

    // edit product using ProductDto model (ProductDto model = Information requested)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/product/update", method = RequestMethod.POST)
    public ResponseEntity<?> updateProduct(@RequestHeader(value="Authorization") String token, @RequestBody ProductDto productDto) {
        return productsService.update(token,productDto);
    }

    // remove logically product using ProductDto model (ProductDto model = Information requested)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/product/remove", method = RequestMethod.POST)
    public ResponseEntity<?> removeProduct(@RequestHeader(value="Authorization") String token,@RequestBody ProductDto productDto) {
        return productsService.remove(token,productDto);
    }

    // add amount to product using ProductDto model (ProductDto model = Information requested)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/product/addAmount", method = RequestMethod.POST)
    public ResponseEntity<?> addProductAmount(@RequestHeader(value="Authorization") String token,@RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productsService.addAmount(token,productDto));
    }

    // get list of orders of product
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/product/{id}/orders", method = RequestMethod.GET)
    public ResponseEntity<?> ordersList(@RequestHeader(value="Authorization") String token,@PathVariable(value = "id") int productId) {
        return productsService.ordersList(token, productId);
    }

    // search using tag (return list of products)
    @RequestMapping(value = "/product/search", method = RequestMethod.GET)
    public ResponseEntity<?> search(@RequestParam String tag) {
        return ResponseEntity.ok(productsService.search(tag));
    }

}

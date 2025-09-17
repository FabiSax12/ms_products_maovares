package com.maovares.ms_products;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {

    @GetMapping(path = "/")
    public String index() {
        return "Product MS running";
    }

    @GetMapping(path = "/{id}")
    public String getById(@PathVariable String id) {
        return "Product MS running";
    }
}

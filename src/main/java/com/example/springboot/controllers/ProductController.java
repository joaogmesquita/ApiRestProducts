package com.example.springboot.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.dtos.ProductRecordDTO;
import com.example.springboot.model.ProductModel;
import com.example.springboot.repository.ProductRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import jakarta.validation.Valid;

@RestController
public class ProductController {

	@Autowired
	ProductRepository pdRepository;

	@PostMapping("/products")
	public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDTO productRecordDTO) {
		var productModel = new ProductModel();
		BeanUtils.copyProperties(productRecordDTO, productModel);
		return ResponseEntity.status(HttpStatus.CREATED).body(pdRepository.save(productModel));

	}

	@GetMapping("/products")
	public ResponseEntity<List<ProductModel>> getAllProducts() {

		List<ProductModel> productList = pdRepository.findAll();

		if (!productList.isEmpty()) {
			for (ProductModel product : productList) {
				UUID ID = product.getIdProduct();
				product.add(linkTo(methodOn(ProductController.class).getOne(ID)).withSelfRel());
			}
		}

		return ResponseEntity.status(HttpStatus.OK).body(productList);

	}

	@GetMapping("/products/{id}")
	public ResponseEntity<Object> getOne(@PathVariable(value = "id") UUID id) {

		Optional<ProductModel> product = pdRepository.findById(id);

		if (product.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
		}
		product.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Products List"));

		return ResponseEntity.status(HttpStatus.OK).body(product.get());

	}

	@PutMapping("/products/{id}")
	public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id,
			@RequestBody @Valid ProductRecordDTO productRecordDTO) {

		Optional<ProductModel> product = pdRepository.findById(id);

		if (product.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
		}
		var productModel = product.get();

		BeanUtils.copyProperties(productRecordDTO, productModel);

		return ResponseEntity.status(HttpStatus.OK).body(pdRepository.save(productModel));

	}

	@DeleteMapping("/products/{id}")
	public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id) {
		Optional<ProductModel> product = pdRepository.findById(id);

		if (product.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
		}

		pdRepository.deleteById(product.get().getIdProduct());
		return ResponseEntity.status(HttpStatus.OK).body("Product delete succssfully");

	}

}

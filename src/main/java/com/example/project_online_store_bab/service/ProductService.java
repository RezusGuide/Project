package com.example.project_online_store_bab.service;

import com.example.project_online_store_bab.model.Product;
import com.example.project_online_store_bab.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    public Page<Product> findPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        logger.info("Запрос продуктов для страницы {} с размером {}", page, size);
        return productRepository.findAll(pageable);
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        logger.info("Запрос всех продуктов с параметрами пагинации: {}", pageable);
        return productRepository.findAll(pageable);
    }

    public List<Product> findByCategory(String categoryName) {
        logger.info("Запрос продуктов в категории: {}", categoryName);
        List<Product> products = productRepository.findByCategoryName(categoryName);
        if (products.isEmpty()) {
            logger.warn("Продукты в категории {} не найдены", categoryName);
        }
        return products;
    }

    public Product findById(Long id) {
        logger.info("Запрос продукта с ID: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Продукт с ID {} не найден", id);
                    return new IllegalArgumentException("Product not found with ID: " + id);
                });
    }
}
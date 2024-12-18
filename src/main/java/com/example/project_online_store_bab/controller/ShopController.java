package com.example.project_online_store_bab.controller;

import com.example.project_online_store_bab.model.Category;
import com.example.project_online_store_bab.model.Product;
import com.example.project_online_store_bab.service.MailService;
import com.example.project_online_store_bab.service.ProductService;
import com.example.project_online_store_bab.service.UserService;
import com.example.project_online_store_bab.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    // Отображение страницы с категориями
    @GetMapping
    public String shopPage(Model model) {
        List<Category> categories = categoryService.findAll(); // Используем CategoryService для получения категорий
        if (categories == null || categories.isEmpty()) {
            model.addAttribute("message", "Категории отсутствуют.");
        } else {
            model.addAttribute("categories", categories);
        }
        return "shop";
    }

    // Отображение продуктов по категории
    @GetMapping("/products")
    public String productsByCategory(@RequestParam(value = "category", required = false) String categoryName, Model model) {
        if (categoryName == null || categoryName.isEmpty()) {
            model.addAttribute("message", "Категория не указана.");
            return "error";
        }

        List<Product> products = productService.findByCategory(categoryName);
        if (products == null || products.isEmpty()) {
            model.addAttribute("message", "Товары в категории \"" + categoryName + "\" отсутствуют.");
        } else {
            model.addAttribute("products", products);
        }
        model.addAttribute("categoryName", categoryName);
        return "products";
    }

    // Отображение страницы проверки личности
    @GetMapping("/verify")
    public String verifyPage(@RequestParam("productId") Long productId, Model model) {
        Product product = productService.findById(productId);
        if (product == null) {
            model.addAttribute("error", "Продукт не найден.");
            return "error";
        }
        model.addAttribute("product", product);
        return "verify";
    }

    // Проверка личности и отправка письма
    @PostMapping("/send-mail")
    public String sendMail(@RequestParam("productId") Long productId,
                           @RequestParam("username") String username,
                           @RequestParam("password") String password,
                           Model model) {
        try {
            // Проверка пользователя
            var user = userService.findByUsername(username);
            if (user == null || !userService.checkPassword(user, password)) {
                model.addAttribute("error", "Неверный логин или пароль.");
                model.addAttribute("product", productService.findById(productId));
                return "verify";
            }

            // Получение информации о продукте
            Product product = productService.findById(productId);
            if (product == null) {
                model.addAttribute("error", "Продукт не найден.");
                return "error";
            }

            // Формирование письма
            String subject = "Информация о продукте: " + product.getName();
            String body = "Здравствуйте, " + user.getUsername() + "!\n\n" +
                    "Вы выбрали следующий продукт:\n" +
                    "Название: " + product.getName() + "\n" +
                    "Описание: " + product.getDescription() + "\n" +
                    "Цена: $" + product.getPrice() + "\n\n" +
                    "Спасибо за ваш интерес к нашему магазину!\nКоманда BaB.";

            // Отправка письма
            mailService.sendPurchaseConfirmation(user.getEmail(), subject, body);

            model.addAttribute("message", "Информация о продукте успешно отправлена на вашу почту: " + user.getEmail());
            return "success";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при обработке запроса: " + e.getMessage());
            return "error";
        }
    }
}
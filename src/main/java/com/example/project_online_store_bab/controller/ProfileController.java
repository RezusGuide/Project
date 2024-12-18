package com.example.project_online_store_bab.controller;

import com.example.project_online_store_bab.model.User;
import com.example.project_online_store_bab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String profilePage(@AuthenticationPrincipal UserDetails currentUserDetails, Model model) {
        String username = currentUserDetails.getUsername();
        User user = userService.findByUsername(username);

        model.addAttribute("user", user);
        model.addAttribute("activity", getSampleActivity());
        return "profile";
    }

    @PostMapping("/upload-photo")
    public String uploadPhoto(@AuthenticationPrincipal UserDetails currentUserDetails,
                              @RequestParam("photo") MultipartFile photo) {
        if (photo.isEmpty()) {
            return "redirect:/profile?error=empty";
        }

        if (!photo.getContentType().startsWith("image/")) {
            return "redirect:/profile?error=invalid_format";
        }

        String username = currentUserDetails.getUsername();
        User user = userService.findByUsername(username);

        try {
            String uploadDir = "src/main/resources/static/images/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = username + "_" + photo.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            photo.transferTo(filePath.toFile());

            user.setPhoto("/images/" + filename);
            userService.saveUser(user);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/profile?error=upload";
        }

        return "redirect:/profile?success";
    }

    private String[] getSampleActivity() {
        return new String[]{
                "Вы добавили новый товар в корзину",
                "Вы изменили свой профиль",
                "Вы оставили отзыв на товар 'Смартфон Samsung Galaxy S21'",
                "Вы оформили заказ №12345"
        };
    }
}
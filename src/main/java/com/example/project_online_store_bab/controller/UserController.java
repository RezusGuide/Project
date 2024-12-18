package com.example.project_online_store_bab.controller;

import com.example.project_online_store_bab.model.User;
import com.example.project_online_store_bab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            model.addAttribute("error", "Пользователь не найден.");
            return "redirect:/profile";
        }
        model.addAttribute("user", user);
        return "edit-profile";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user) {
        User existingUser = userService.findById(id);
        if (existingUser == null) {
            return "redirect:/profile?error=user_not_found";
        }
        userService.updateUser(id, user);
        return "redirect:/profile?success=updated";
    }
}
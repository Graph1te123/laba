package com.example;

import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private List<User> users = new ArrayList<>();
    //http://localhost:8080/users
    public UserController() {
        // Добавлять пользователей.
        users.add(new User("Egor", 21));
        users.add(new User("NeEgor", 22));
    }

    @GetMapping
    public List<User> getAllUsers() {
        return users;
    }

    static class User {
        private String name;
        private int age;

        public User() {}

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }
}

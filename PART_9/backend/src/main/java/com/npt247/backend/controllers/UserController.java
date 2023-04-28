package com.npt247.backend.controllers;

import com.npt247.backend.models.Artist;
import com.npt247.backend.models.Country;
import com.npt247.backend.models.Museum;
import com.npt247.backend.models.User;
import com.npt247.backend.repositories.MuseumRepository;
import com.npt247.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    MuseumRepository museumRepository;
    @GetMapping
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody User requestUser) throws Exception{
        try {
            User user = userRepository.save(requestUser);
            return ResponseEntity.ok(user);
        }catch (Exception e){
            String error;
            if (e.getMessage().contains("user.login_UNIQUE"))
                error = "user already exists";
            else error = "Undefined error";
            Map<String, String> errorMap =  new HashMap<>();
            errorMap.put("error", error);
            return ResponseEntity.ok(errorMap);
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable(value = "id") Long userId,
                                           @RequestBody User userDetails) {

        User user;
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            user = userOptional.get();
            user.setLogin(userDetails.getLogin());
            user.setEmail(userDetails.getEmail());
            userRepository.save(user);
            return ResponseEntity.ok(user);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") Long userId){
        Optional<User> userOptional = userRepository.findById(userId);
        Map<String, Boolean> resp = new HashMap<>();
        if (userOptional.isPresent()) {
            userRepository.delete(userOptional.get());
            resp.put("deleted", Boolean.TRUE);
        }
        else resp.put("deleted", Boolean.FALSE);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{id}/addMuseums")
    public ResponseEntity<Object> addMuseums(@PathVariable(value = "id") Long userId,
                                             @Validated @RequestBody Set<Museum> museums) {
        Optional<User> userOptional = userRepository.findById(userId);
        int cnt = 0;
        if (userOptional.isPresent()) {
            User u = userOptional.get();
            for (Museum m : museums) {
                Optional<Museum> museumOptional = museumRepository.findById(m.getId());
                if (museumOptional.isPresent()) {
                    u.addMuseum(museumOptional.get());
                    cnt++;
                }
            }
            userRepository.save(u);
        }
        Map<String, String> response = new HashMap<>();
        response.put("count", String.valueOf(cnt));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{id}/removemuseums")
    public ResponseEntity<Object> removeMuseums(@PathVariable(value = "id") Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        int cnt = 0;
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            for (Museum museum : user.museums) {
                user.removeMuseum(museum);
                cnt++;
            }
            userRepository.save(user);
        }
        Map<String, String> response = new HashMap<>();
        response.put("count", String.valueOf(cnt));
        return ResponseEntity.ok(response);
    }
}

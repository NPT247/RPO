package com.npt247.backend.controllers;


import com.npt247.backend.models.Museum;
import com.npt247.backend.repositories.MuseumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/museums")
public class MuseumController {
    @Autowired
    MuseumRepository museumRepository;

    @GetMapping
    public List<Museum> getAllMuseums(){
        return museumRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Object> createMuseum(@RequestBody Museum requestMuseum) throws Exception{
        try {
            Museum museum = museumRepository.save(requestMuseum);
            return ResponseEntity.ok(museum);
        }catch (Exception e){
            String error;
            if (e.getMessage().contains("museum.name_UNIQUE"))
                error = "Museum already exists";
            else error = "Undefined error";
            Map<String, String> errorMap =  new HashMap<>();
            errorMap.put("error", error);
            return ResponseEntity.ok(errorMap);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateMuseum(@PathVariable("id") Long idMuseum, @RequestBody Museum museumDetails){
        Museum museum;
        Optional<Museum> museumOptional = museumRepository.findById(idMuseum);
        if (museumOptional.isPresent()){
            museum = museumOptional.get();
            museum.setName(museumDetails.getName());
            museum.setLocation(museumDetails.getLocation());
            museumRepository.save(museum);
            return ResponseEntity.ok(museum);
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Museum not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteMuseum(@PathVariable("id") Long idMuseum){
        Optional<Museum> museumOptional = museumRepository.findById(idMuseum);
        Map<String, Boolean> resp = new HashMap<>();
        if (museumOptional.isPresent()) {
            museumRepository.delete(museumOptional.get());
            resp.put("deleted", Boolean.TRUE);
        }
        else resp.put("deleted", Boolean.FALSE);
        return ResponseEntity.ok(resp);
    }
}

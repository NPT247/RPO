package com.npt247.backend.controllers;


import com.npt247.backend.models.Artist;
import com.npt247.backend.models.Country;
import com.npt247.backend.models.Museum;
import com.npt247.backend.models.Painting;
import com.npt247.backend.repositories.MuseumRepository;
import com.npt247.backend.repositories.PaintingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/v1/museums")
public class MuseumController {
    @Autowired
    MuseumRepository museumRepository;
    @Autowired
    PaintingRepository paintingRepository;
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

    @GetMapping("/{id}/paintings")
    public ResponseEntity<List<Painting>> getMuseumPaintings(@PathVariable(value = "id") Long paintingId) {
        Optional<Painting> optionalPainting = paintingRepository.findById(paintingId);
        if (optionalPainting.isPresent()) {
            return ResponseEntity.ok(optionalPainting.get().getMuseum().paintings);
        }
        return ResponseEntity.ok(new ArrayList<Painting>());
    }
}

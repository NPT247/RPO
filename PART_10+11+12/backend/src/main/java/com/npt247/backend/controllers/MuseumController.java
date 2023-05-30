package com.npt247.backend.controllers;


import com.npt247.backend.models.Artist;
import com.npt247.backend.models.Country;
import com.npt247.backend.models.Museum;
import com.npt247.backend.models.Painting;
import com.npt247.backend.repositories.MuseumRepository;
import com.npt247.backend.repositories.PaintingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.*;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
public class MuseumController {
    @Autowired
    MuseumRepository museumRepository;
    @Autowired
    PaintingRepository paintingRepository;
    @GetMapping("/museums")
    public Page<Museum> getAllMuseums(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return museumRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name")));
    }

    @PostMapping("/museums")
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


    @PutMapping("/museums/{id}")
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

    @PostMapping("/deletemuseums")
    public ResponseEntity<Object> deleteMuseums(@Valid @RequestBody List<Museum> museums) {
        museumRepository.deleteAll(museums);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/museums/{id}/paintings")
    public ResponseEntity<List<Painting>> getMuseumPaintings(@PathVariable(value = "id") Long paintingId) {
        Optional<Painting> optionalPainting = paintingRepository.findById(paintingId);
        if (optionalPainting.isPresent()) {
            return ResponseEntity.ok(optionalPainting.get().getMuseum().paintings);
        }
        return ResponseEntity.ok(new ArrayList<Painting>());
    }
}

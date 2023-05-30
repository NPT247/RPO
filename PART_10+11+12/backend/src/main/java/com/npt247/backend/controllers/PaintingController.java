package com.npt247.backend.controllers;

import com.npt247.backend.models.Artist;
import com.npt247.backend.models.Country;
import com.npt247.backend.models.Museum;
import com.npt247.backend.models.Painting;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
public class PaintingController {
    @Autowired
    PaintingRepository paintingRepository;

    @GetMapping("/paintings")
    public Page<Painting> getAllCountries(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return paintingRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name")));
    }
    @PostMapping("/paintings")
    public ResponseEntity<Object> createPainting(@RequestBody Painting requestPainting) throws Exception{
        try {
            Painting painting = paintingRepository.save(requestPainting);
            return ResponseEntity.ok(painting);
        }catch (Exception e){
            String error;
            if (e.getMessage().contains("painting.name_UNIQUE"))
                error = "Painting already exists";
            else error = "Undefined error";
            Map<String, String> errorMap =  new HashMap<>();
            errorMap.put("error", error);
            return ResponseEntity.ok(errorMap);
        }
    }

    @PutMapping("/paintings/{id}")
    public ResponseEntity<Object> updatePainting(@PathVariable("id") Long idPainting, @RequestBody Painting paintingDetails){
        Painting painting;
        Optional<Painting> paintingOptional = paintingRepository.findById(idPainting);
        if (paintingOptional.isPresent()){
            painting = paintingOptional.get();
            painting.setName(paintingDetails.getName());
            painting.setArtist(paintingDetails.getArtist());
            painting.setMuseum(paintingDetails.getMuseum());
            painting.setYear(paintingDetails.getYear());
            paintingRepository.save(painting);
            return ResponseEntity.ok(painting);
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Painting not found");
        }
    }

    @PostMapping("/deletepaintings")
    public ResponseEntity<Object> deletePaintings(@Valid @RequestBody List<Painting> paintings) {
        paintingRepository.deleteAll(paintings);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

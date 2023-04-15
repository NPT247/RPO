package com.npt247.backend.controllers;

import com.npt247.backend.models.Museum;
import com.npt247.backend.models.Painting;
import com.npt247.backend.repositories.PaintingRepository;
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
@RequestMapping("/api/v1/paintings")
public class PaintingController {
    @Autowired
    PaintingRepository paintingRepository;

    @GetMapping
    public List<Painting> getAllPaintings(){
        return paintingRepository.findAll();
    }

    @PostMapping
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

    @PutMapping("/{id}")
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePainting(@PathVariable("id") Long idPainting){
        Optional<Painting> paintingOptional = paintingRepository.findById(idPainting);
        Map<String, Boolean> resp = new HashMap<>();
        if (paintingOptional.isPresent()) {
            paintingRepository.delete(paintingOptional.get());
            resp.put("deleted", Boolean.TRUE);
        }
        else resp.put("deleted", Boolean.FALSE);
        return ResponseEntity.ok(resp);
    }
}

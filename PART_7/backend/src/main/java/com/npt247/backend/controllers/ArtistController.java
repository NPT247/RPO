package com.npt247.backend.controllers;

import com.npt247.backend.models.Artist;
import com.npt247.backend.models.Country;
import com.npt247.backend.repositories.ArtistRepository;
import com.npt247.backend.repositories.CountryRepository;
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
@RequestMapping("/api/v1/artists")
public class ArtistController {
    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    CountryRepository countryRepository;

    @GetMapping
    public List<Artist> getAllArtists(){
        return artistRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Object> createArtist(@RequestBody Artist requestArtist) throws Exception{
        try {
            Optional<Country> optionalCountry = countryRepository.findById(requestArtist.getCountry().getId());
            if (optionalCountry.isPresent()) {
                requestArtist.setCountry(optionalCountry.get());
            }
            Artist artist = artistRepository.save(requestArtist);
            return ResponseEntity.ok(artist);
        }catch (Exception e){
            String error;
            if (e.getMessage().contains("artist.name_UNIQUE"))
                error = "Artist already exists";
            else
                error = "Undefined error";
            Map<String, String> errorMap =  new HashMap<>();
            errorMap.put("error", error);
            return ResponseEntity.ok(errorMap);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateArtist(@PathVariable("id") Long idArtist, @RequestBody Artist artistDetails){
        Artist artist;
        Optional<Artist> artistOptional = artistRepository.findById(idArtist);
        if (artistOptional.isPresent()){
            artist = artistOptional.get();
            artist.setAge(artistDetails.getAge());
            artist.setCountry(artistDetails.getCountry());
            artist.setName(artistDetails.getName());
            artistRepository.save(artist);
            return ResponseEntity.ok(artist);
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Artist not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteArtist(@PathVariable("id") Long idArtist){
        Optional<Artist> artistOptional = artistRepository.findById(idArtist);
        Map<String, Boolean> resp = new HashMap<>();
        if (artistOptional.isPresent()) {
            artistRepository.delete(artistOptional.get());
            resp.put("deleted", Boolean.TRUE);
        }
        else resp.put("deleted", Boolean.FALSE);
        return ResponseEntity.ok(resp);
    }
}

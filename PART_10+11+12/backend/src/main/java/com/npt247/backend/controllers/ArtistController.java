package com.npt247.backend.controllers;

import com.npt247.backend.models.Artist;
import com.npt247.backend.models.Country;
import com.npt247.backend.models.Painting;
import com.npt247.backend.repositories.ArtistRepository;
import com.npt247.backend.repositories.CountryRepository;
import com.npt247.backend.tools.DataValidationException;
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
@RequestMapping("/api/v1/")
public class ArtistController {
    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    CountryRepository countryRepository;


    @PostMapping("/artists")
    public ResponseEntity<Object> createArtist(@RequestBody Artist requestArtist) throws Exception{
        try {
            Optional<Country> optionalCountry = countryRepository.findById(requestArtist.getCountry().getId());
            optionalCountry.ifPresent(requestArtist::setCountry);
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

    @PutMapping("/artists/{id}")
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

    @GetMapping("/artists")
    public Page getAllArtists(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return artistRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name")));
    }

    @GetMapping("/artists/{id}")
    public ResponseEntity getArtist(@PathVariable(value = "id") Long artistId)
            throws DataValidationException
    {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(()-> new DataValidationException("Художник с таким индексом не найден"));
        return ResponseEntity.ok(artist);
    }

    @GetMapping("/artists/{id}/paintings")
    public ResponseEntity<List<Painting>> getArtistPaintings(@PathVariable(value = "id") Long artistId) {
        Optional<Artist> optionalArtist = artistRepository.findById(artistId);
        return optionalArtist.map(artist -> ResponseEntity.ok(artist.paintings)).orElseGet(() -> ResponseEntity.ok(new ArrayList<>()));
    }

    @PostMapping("/deleteartists")
    public ResponseEntity<Object> deleteArtists(@Valid @RequestBody List<Artist> artists) {
        artistRepository.deleteAll(artists);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

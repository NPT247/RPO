package com.npt247.backend.controllers;

import com.npt247.backend.models.Country;
import com.npt247.backend.repositories.CountryRepository;
import org.apache.coyote.Response;
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
@RequestMapping("/api/v1/countries")
public class CountryController {

    @Autowired
    CountryRepository countryRepository;

    @GetMapping
    public List<Country> getAllCountries(){
        return countryRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Object> createCountry(@RequestBody Country requestCountry) throws Exception{
        try {
            Country country = countryRepository.save(requestCountry);
            return ResponseEntity.ok(country);
        }catch (Exception e){
            String error;
            if (e.getMessage().contains("countries.name_UNIQUE"))
                error = "Country already exists";
            else error = "Undefined error";
            Map<String, String> errorMap =  new HashMap<>();
            errorMap.put("error", error);
            return ResponseEntity.ok(errorMap);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateCountry(@PathVariable("id") Long idCountry, @RequestBody Country countryDetails){
        Country country;
        Optional<Country> countryOptional = countryRepository.findById(idCountry);
        if (countryOptional.isPresent()){
            country = countryOptional.get();
            country.setName(countryDetails.getName());
            countryRepository.save(country);
            return ResponseEntity.ok(country);
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Country not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCountry(@PathVariable("id") Long idCountry){
        Optional<Country> countryOptional = countryRepository.findById(idCountry);
        Map<String, Boolean> resp = new HashMap<>();
        if (countryOptional.isPresent()) {
            countryRepository.delete(countryOptional.get());
            resp.put("deleted", Boolean.TRUE);
        }
        else
            resp.put("deleted", Boolean.FALSE);
        return ResponseEntity.ok(resp);
    }
}

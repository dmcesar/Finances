package com.portfolio.financas.api.controller;

import com.portfolio.financas.api.dto.EntryDTO;
import com.portfolio.financas.api.dto.UpdateStatusDTO;
import com.portfolio.financas.exceptions.BusinessRuleException;
import com.portfolio.financas.model.entity.Entry;
import com.portfolio.financas.model.entity.User;
import com.portfolio.financas.model.enums.EntryStatus;
import com.portfolio.financas.model.enums.EntryType;
import com.portfolio.financas.service.EntryService;
import com.portfolio.financas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/entries")
public class EntryController {

    private EntryService entryService;

    private UserService userService;

    @Autowired
    public EntryController(EntryService entryService, UserService userService) {

        this.entryService = entryService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity create(@RequestBody EntryDTO dto) {

        try {

            Entry entry = toEntry(dto);

            entry = entryService.create(entry);

            return new ResponseEntity(entry, HttpStatus.CREATED);

        } catch (BusinessRuleException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody EntryDTO dto) {

       return entryService.getByID(id).map( entity -> {

           try {

               Entry entry = toEntry(dto);

               entry.setId(entity.getId());

               entryService.update(entry);

               return ResponseEntity.ok(entry);

           } catch (BusinessRuleException e) {

                return new ResponseEntity("Entry not found.", HttpStatus.BAD_REQUEST);
           }

       } ).orElseGet(() -> new ResponseEntity("Entry not found.", HttpStatus.BAD_REQUEST));
    }

    @GetMapping
    public ResponseEntity read(
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "user") Long userID
    ) {

        Entry filterEntry = Entry.builder()
                .description(description)
                .month(month)
                .year(year)
                .build();

        if(type != null) {

            filterEntry.setType(EntryType.valueOf(type));
        }

        if(status != null) {

            filterEntry.setStatus(EntryStatus.valueOf(status));
        }

        Optional<User> user = userService.getByID(userID);

        if(!user.isPresent()) {

            return ResponseEntity.badRequest().body("User does not exist.");
        }

        filterEntry.setUser(user.get());

        List<Entry> entries =  entryService.read(filterEntry);

        return ResponseEntity.ok(entries);
    }

    @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {

        return entryService.getByID(id).map( entity -> {

            entryService.delete(entity);

            return new ResponseEntity(HttpStatus.NO_CONTENT);

        } ).orElseGet(() -> new ResponseEntity("Entry not found.", HttpStatus.BAD_REQUEST));
    }

    @PutMapping("{id}/update-status")
    public ResponseEntity updateStatus(@PathVariable("id") Long id, @RequestBody UpdateStatusDTO dto) {

        return entryService.getByID(id).map( entity -> {

            if(dto.getStatus() == null) {

                return ResponseEntity.badRequest().body("Invalid status.");
            }

            EntryStatus status = EntryStatus.valueOf(dto.getStatus());

            entity.setStatus(status);

            try {

                entryService.update(entity);

            } catch (BusinessRuleException e) {

                return ResponseEntity.badRequest().body(e.getMessage());
            }

            return ResponseEntity.ok(entity);

        } ).orElseGet(() -> new ResponseEntity("Entry not found.", HttpStatus.BAD_REQUEST));
    }

    private Entry toEntry(EntryDTO dto) {

        return Entry.builder()
                .description(dto.getDescription())
                .month(dto.getMonth())
                .year(dto.getYear())
                .registryDate(dto.getRegistryDate())
                .value(dto.getValue())
                .type(dto.getType() == null ? EntryType.EXPENSE : EntryType.valueOf(dto.getType()))
                .status(dto.getStatus() == null ? EntryStatus.PENDING : EntryStatus.valueOf(dto.getStatus()))
                .user(userService
                        .getByID(dto.getUser())
                        .orElseThrow(() -> new BusinessRuleException("User does not exist.")))
                .build();
    }
}
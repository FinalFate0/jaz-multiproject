package pl.edu.pjwstk.jazapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjwstk.jazapi.service.CrudService;
import pl.edu.pjwstk.jazapi.service.DbEntity;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class CrudController<T extends DbEntity> {
    private final CrudService<T> service;

    public CrudController(CrudService<T> service) {
        this.service = service;
    }

    @GetMapping()
    public ResponseEntity<List<Map<String, Object>>> getAll(@RequestParam(defaultValue = "4", name = "size") int size,
                                                            @RequestParam(defaultValue = "0", name = "page") int page,
                                                            @RequestParam(defaultValue = "desc", name = "direction") String direction,
                                                            @RequestParam(defaultValue = "id", name = "properties") String properties) {
        try {
            String[] propertiesList = properties.split(",");
            for(String string : propertiesList) {
                System.out.println(string);
            }
            List<T> all = service.getAll(page, size, direction, propertiesList);
            List<Map<String, Object>> payload = all.stream()
                    .map(obj -> transformToDTO().apply(obj))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(payload, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable("id") Long id) {
        try {
            T obj = service.getById(id);
            Map<String, Object> payload = transformToDTO().apply(obj);

            return new ResponseEntity<>(payload, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody T t) {
        try {
            service.createOrUpdate(t);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<Void> add(@RequestBody T t) {
        try {
            service.createOrUpdate(t);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        try {
            service.delete(id);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public abstract Function<T, Map<String, Object>> transformToDTO();
}

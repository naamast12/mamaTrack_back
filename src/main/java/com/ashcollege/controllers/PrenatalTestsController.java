package com.ashcollege.controllers;

import com.ashcollege.model.PrenatalTest;
import com.ashcollege.service.PrenatalTestsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
public class PrenatalTestsController {

    private final PrenatalTestsService service;

    public PrenatalTestsController(PrenatalTestsService service) {
        this.service = service;
    }

    @GetMapping("/ping")
    public String ping() { return "ok-tests"; }


    @GetMapping("/all")
    public List<PrenatalTest> all() {
        return service.getAll();
    }
    @GetMapping("/upcoming")
    public List<PrenatalTest> upcoming(@RequestParam int week) {
        return service.byWeek(week); // ← במקום getByWeek
    }
    @GetMapping("/trimester/{tri}")
    public List<PrenatalTest> byTrimesterPath(@PathVariable short tri) {
        return service.byTrimester(tri);
    }

//    @GetMapping("/trimester")
//    public List<PrenatalTest> byTrimesterQuery(@RequestParam("tri") short tri) {
//        return service.byTrimester(tri);
//    }
    @GetMapping("/by-trimester")
    public List<PrenatalTest> byTrimester(@RequestParam short trimester) {
        return service.byTrimester(trimester);
    }




}

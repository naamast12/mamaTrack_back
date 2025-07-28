package com.ashcollege.controllers;

import com.ashcollege.model.WeeklyUpdate;
import com.ashcollege.service.WeeklyUpdatesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weekly")
public class WeeklyUpdatesController {

    private final WeeklyUpdatesService service;
    public WeeklyUpdatesController(WeeklyUpdatesService service) { this.service = service; }

    @GetMapping("/all")
    public List<WeeklyUpdate> all() { return service.getAll(); }

    @GetMapping("/{week}")
    public WeeklyUpdate one(@PathVariable int week) { return service.byWeek(week); }

    @GetMapping("/range")
    public List<WeeklyUpdate> range(@RequestParam int from, @RequestParam int to) {
        return service.byRange(from, to);
    }
}

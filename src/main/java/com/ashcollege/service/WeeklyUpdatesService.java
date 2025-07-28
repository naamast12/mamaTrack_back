package com.ashcollege.service;

import com.ashcollege.model.WeeklyUpdate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeeklyUpdatesService {
    private static final Logger log = LoggerFactory.getLogger(WeeklyUpdatesService.class);

    @Value("classpath:data/weekly-updates.json")
    private Resource dataFile;

    private List<WeeklyUpdate> cache = Collections.emptyList();

    @PostConstruct
    public void load() {
        try (InputStream is = dataFile.getInputStream()) {
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            cache = mapper.readValue(is, new TypeReference<List<WeeklyUpdate>>() {});
            log.info("Loaded {} weekly updates", cache.size());
        } catch (Exception e) {
            log.error("Failed loading weekly-updates.json", e);
            cache = Collections.emptyList();
        }
    }

    public List<WeeklyUpdate> getAll() { return cache; }

    public WeeklyUpdate byWeek(int week) {
        return cache.stream()
                .filter(w -> w.getWeek() != null && w.getWeek() == week)
                .findFirst().orElse(null);
    }

    public List<WeeklyUpdate> byRange(int from, int to) {
        return cache.stream()
                .filter(w -> w.getWeek() != null && w.getWeek() >= from && w.getWeek() <= to)
                .collect(Collectors.toList());
    }
}

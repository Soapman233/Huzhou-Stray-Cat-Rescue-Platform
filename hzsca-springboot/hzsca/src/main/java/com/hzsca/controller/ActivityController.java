package com.hzsca.controller;

import com.hzsca.entity.Activity;
import com.hzsca.mapper.ActivityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class ActivityController {

    @Autowired
    private ActivityMapper activityMapper;

    @GetMapping("/latest")
    public List<Activity> getLatestActivities(@RequestParam(defaultValue = "3") int limit) {
        return activityMapper.findLatestActivities(limit);
    }

    @GetMapping
    public List<Activity> getAllActivities() {
        return activityMapper.findAllActivities();
    }

    @GetMapping("/ended")
    public List<Activity> getEndedActivities() {
        return activityMapper.findEndedActivities();
    }
}
package com.hzsca.controller;

import com.hzsca.common.Result;
import com.hzsca.entity.CatTraits;
import com.hzsca.mapper.CatTraitsMapper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cat-traits")
public class AdminCatTraitsController {
    private final CatTraitsMapper catTraitsMapper;

    public AdminCatTraitsController(CatTraitsMapper catTraitsMapper) {
        this.catTraitsMapper = catTraitsMapper;
    }

    @GetMapping
    public Result list() {
        List<CatTraits> list = catTraitsMapper.selectAll();
        return Result.success(list);
    }

    @GetMapping("/{catId}")
    public Result get(@PathVariable Integer catId) {
        if (catId == null) {
            return Result.error("400", "缺少 catId");
        }
        CatTraits traits = catTraitsMapper.selectByCatId(catId);
        return Result.success(traits);
    }

    @PutMapping("/{catId}")
    public Result upsert(@PathVariable Integer catId, @RequestBody CatTraits traits) {
        if (catId == null) {
            return Result.error("400", "缺少 catId");
        }
        if (traits == null) {
            return Result.error("400", "缺少参数");
        }
        traits.setCatId(catId);
        normalize(traits);
        catTraitsMapper.upsert(traits);
        return get(catId);
    }

    @DeleteMapping("/{catId}")
    public Result delete(@PathVariable Integer catId) {
        if (catId == null) {
            return Result.error("400", "缺少 catId");
        }
        catTraitsMapper.deleteByCatId(catId);
        return Result.success();
    }

    private static void normalize(CatTraits t) {
        if (t.getPersonality() == null || t.getPersonality().isBlank()) {
            t.setPersonality("ANY");
        }
        t.setActivityLevel(clamp(t.getActivityLevel(), 1, 5, 3));
        t.setAffectionLevel(clamp(t.getAffectionLevel(), 1, 5, 3));
        if (t.getChildFriendly() == null) {
            t.setChildFriendly(true);
        }
        if (t.getPetFriendly() == null) {
            t.setPetFriendly(true);
        }
        t.setSheddingLevel(clamp(t.getSheddingLevel(), 1, 5, 3));
        if (t.getNocturnal() == null) {
            t.setNocturnal(false);
        }
        t.setSpaceNeedLevel(clamp(t.getSpaceNeedLevel(), 1, 3, 2));
        if (t.getBeginnerFriendly() == null) {
            t.setBeginnerFriendly(true);
        }
        if (t.getSpecialCareNeeded() == null) {
            t.setSpecialCareNeeded(false);
        }
        if (t.getEnvironmentPreference() == null || t.getEnvironmentPreference().isBlank()) {
            t.setEnvironmentPreference("ANY");
        }
    }

    private static Integer clamp(Integer value, int min, int max, int def) {
        if (value == null) {
            return def;
        }
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}

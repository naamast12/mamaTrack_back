package com.ashcollege.controllers;

import com.ashcollege.entities.ChecklistStatusEntity;
import com.ashcollege.entities.ItemEntity;
import com.ashcollege.repository.ChecklistStatusRepository;
import com.ashcollege.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/checklist")
@CrossOrigin(origins = "*")
public class ChecklistController {

    @Autowired
    private ItemRepository itemRepo;

    @Autowired
    private ChecklistStatusRepository checklistRepo;

    @GetMapping("/{userId}")
    public List<Map<String, Object>> getChecklist(@PathVariable Long userId) {
        System.out.println("📢 Checklist API called for userId: " + userId);
        try {
            List<ItemEntity> items = itemRepo.findAll();
            System.out.println("🟡 Total items found: " + items.size());

            List<ChecklistStatusEntity> statuses = checklistRepo.findByUserId(userId);
            System.out.println("🟢 Total statuses found: " + statuses.size());

            Map<Long, Boolean> statusMap = new HashMap<>();
            for (ChecklistStatusEntity status : statuses) {
                System.out.println("🔍 Status: itemId=" + status.getItemId() + ", checked=" + status.isChecked());
                statusMap.put(status.getItemId(), status.isChecked());
            }

            List<Map<String, Object>> result = new ArrayList<>();
            for (ItemEntity item : items) {
                System.out.println("📝 Item: " + item.getName());
                Map<String, Object> entry = new HashMap<>();
                entry.put("id", item.getId());
                entry.put("name", item.getName());
                entry.put("category", item.getCategory());
                entry.put("type", item.getType());
                entry.put("checked", statusMap.getOrDefault(item.getId(), false));
                result.add(entry);
            }

            return result;

        } catch (Exception e) {
            System.out.println("❌ שגיאה בבקשת checklist:");
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @PostMapping("/update")
    public void updateChecklist(@RequestBody ChecklistStatusEntity update) {
        Optional<ChecklistStatusEntity> existing =
                checklistRepo.findByUserIdAndItemId(update.getUserId(), update.getItemId());

        if (existing.isPresent()) {
            ChecklistStatusEntity status = existing.get();
            status.setChecked(update.isChecked());
            checklistRepo.save(status);
        } else {
            checklistRepo.save(update);
        }
    }
}

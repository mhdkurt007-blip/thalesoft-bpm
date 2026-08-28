package com.thalesoft.engine.controller;

import com.thalesoft.engine.model.ProcessInstance;
import com.thalesoft.engine.model.ProcessTemplate;
import com.thalesoft.engine.repository.ProcessInstanceRepository;
import com.thalesoft.engine.repository.ProcessTemplateRepository;
import com.thalesoft.engine.service.BpmEngineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bpm")
@CrossOrigin(origins = "*") // Arayüzün API'ye erişebilmesi için
public class ProcessController {

    private final BpmEngineService engineService;
    private final ProcessTemplateRepository templateRepo;
    private final ProcessInstanceRepository instanceRepo;

    public ProcessController(BpmEngineService engineService, 
                             ProcessTemplateRepository templateRepo,
                             ProcessInstanceRepository instanceRepo) {
        this.engineService = engineService;
        this.templateRepo = templateRepo;
        this.instanceRepo = instanceRepo;
    }

    // --- ARAYÜZDE LİSTELEME YAPMAK İÇİN (GET) ---
    @GetMapping("/templates")
    public ResponseEntity<List<ProcessTemplate>> getAllTemplates() {
        return ResponseEntity.ok(templateRepo.findAll());
    }

    @GetMapping("/instances")
    public ResponseEntity<List<ProcessInstance>> getAllInstances() {
        return ResponseEntity.ok(instanceRepo.findAll());
    }

    // --- SÜREÇ YÖNETİMİ İÇİN (POST) ---
    @PostMapping("/templates")
    public ResponseEntity<ProcessTemplate> createTemplate(@RequestBody ProcessTemplate template) {
        return ResponseEntity.ok(templateRepo.save(template));
    }

    @PostMapping("/instances/start")
    public ResponseEntity<ProcessInstance> startProcess(
            @RequestParam String templateId, 
            @RequestParam String initiator, 
            @RequestBody(required = false) Map<String, Object> variables) {
        
        if (variables == null) variables = Map.of();
        return ResponseEntity.ok(engineService.startProcess(templateId, initiator, variables));
    }

    @PostMapping("/instances/{instanceId}/complete")
    public ResponseEntity<ProcessInstance> completeTask(
            @PathVariable String instanceId, 
            @RequestParam String transition,
            @RequestParam String completedBy) { 
        
        return ResponseEntity.ok(engineService.completeUserTask(instanceId, transition, completedBy));
    } 
}
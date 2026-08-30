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
        // HATA BURADA DÜZELTİLDİ: Sınıf adı yerine "templateRepo" objesini kullandık
        // Artık sadece ACTIVE (onaylanmış) süreçler arayüzde görünecek.
        return ResponseEntity.ok(templateRepo.findByStatus("ACTIVE"));
    }

    @GetMapping("/instances")
    public ResponseEntity<List<ProcessInstance>> getAllInstances() {
        return ResponseEntity.ok(instanceRepo.findAll());
    }

    // --- SÜREÇ YÖNETİMİ İÇİN (POST) - META PROCESS EKLENDİ ---
    @PostMapping("/templates")
    public ResponseEntity<ProcessTemplate> createTemplate(@RequestBody ProcessTemplate template) {
        // 1. Yeni gelen süreci güvenlik amacıyla zorla DRAFT (Taslak) statüsüne çekiyoruz
        template.setStatus("DRAFT");
        ProcessTemplate savedTemplate = templateRepo.save(template);

        // 2. Hocanın vizyonu: "Sürecin Süreci" (Meta-Process) burada tetikleniyor!
        try {
            // Sisteme yeni bir süreç eklendiğinde, motor kendi onay akışını (TEMPLATE_APPROVAL_V1) başlatır.
            engineService.startProcess("TEMPLATE_APPROVAL_V1", "SYSTEM", 
                Map.of(
                    "targetTemplateId", savedTemplate.getTemplateId(),
                    "targetTemplateName", savedTemplate.getName()
                )
            );
        } catch (Exception e) {
            // Eğer sistemde henüz TEMPLATE_APPROVAL_V1 adında bir süreç yoksa uygulama çökmesin,
            // sadece konsola uyarı yazsın. 
            System.out.println("Meta-Process (Şablon Onay Süreci) tetiklenemedi: " + e.getMessage());
        }

        return ResponseEntity.ok(savedTemplate);
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
package com.thalesoft.engine.controller;

import com.thalesoft.engine.model.ProcessInstance;
import com.thalesoft.engine.model.ProcessTemplate;
import com.thalesoft.engine.repository.ProcessInstanceRepository;
import com.thalesoft.engine.repository.ProcessTemplateRepository;
import com.thalesoft.engine.service.BpmEngineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
        // Artık sadece ACTIVE (onaylanmış) süreçler arayüzde görünecek.
        return ResponseEntity.ok(templateRepo.findByStatus("ACTIVE"));
    }

    @GetMapping("/instances")
    public ResponseEntity<List<ProcessInstance>> getAllInstances() {
        return ResponseEntity.ok(instanceRepo.findAll());
    }

    // --- SÜREÇ YÖNETİMİ İÇİN (POST) - META PROCESS ---
    @PostMapping("/templates")
    public ResponseEntity<ProcessTemplate> createTemplate(@RequestBody ProcessTemplate template) {
        // 1. Yeni gelen süreci güvenlik amacıyla zorla DRAFT (Taslak) statüsüne çekiyoruz
        template.setStatus("DRAFT");
        ProcessTemplate savedTemplate = templateRepo.save(template);

        // 2. Hocanın vizyonu: "Sürecin Süreci" (Meta-Process) burada tetikleniyor!
        try {
            // Null hatalarına karşı kurşun geçirmez HashMap yapısı
            Map<String, Object> variables = new HashMap<>();
            variables.put("targetTemplateId", savedTemplate.getTemplateId());
            variables.put("targetTemplateName", savedTemplate.getName());

            // Sisteme yeni bir süreç eklendiğinde, motor kendi onay akışını başlatır.
            engineService.startProcess("TEMPLATE_APPROVAL_V1", "SYSTEM", variables);
            
            System.out.println("Meta-Process Başarıyla Tetiklendi! Bekleyen Süreç: " + savedTemplate.getTemplateId());
        } catch (Exception e) {
            // Hata artık sessizce yutulmayacak, terminalde kırmızıyla tüm detaylarıyla bağıracak!
            System.err.println("DİKKAT! Meta-Process (Şablon Onay Süreci) tetiklenirken hata oluştu!");
            e.printStackTrace(); 
        }

        return ResponseEntity.ok(savedTemplate);
    }

    @PostMapping("/instances/start")
    public ResponseEntity<ProcessInstance> startProcess(
            @RequestParam String templateId, 
            @RequestParam String initiator, 
            @RequestBody(required = false) Map<String, Object> variables) {
        
        if (variables == null) variables = new HashMap<>();
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
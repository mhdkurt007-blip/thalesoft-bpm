package com.thalesoft.engine.service;

import com.thalesoft.engine.action.ServiceTaskAction;
import com.thalesoft.engine.model.ProcessInstance;
import com.thalesoft.engine.model.ProcessTemplate;
import com.thalesoft.engine.repository.ProcessInstanceRepository;
import com.thalesoft.engine.repository.ProcessTemplateRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BpmEngineService {

    private final ProcessTemplateRepository templateRepo;
    private final ProcessInstanceRepository instanceRepo;
    private final Map<String, ServiceTaskAction> actionStrategies;

    public BpmEngineService(ProcessTemplateRepository templateRepo,
                            ProcessInstanceRepository instanceRepo,
                            List<ServiceTaskAction> actionList) {
        this.templateRepo = templateRepo;
        this.instanceRepo = instanceRepo;
        // Spring Boot, projedeki tüm ServiceTaskAction sınıflarını actionList'e otomatik toplar.
        // Biz de bunları actionName'e göre (Örn: SAVE_TO_HR_SYSTEM) bir Map'e dönüştürüyoruz. (Strategy Pattern)
        this.actionStrategies = actionList.stream()
                .collect(Collectors.toMap(ServiceTaskAction::getActionName, Function.identity()));
    }

    // 1. Dışarıdan yeni bir süreç başlatıldığında burası çalışır
    public ProcessInstance startProcess(String templateId, String initiator, Map<String, Object> variables) {
        ProcessTemplate template = templateRepo.findByTemplateId(templateId)
                .orElseThrow(() -> new RuntimeException("Şablon bulunamadı: " + templateId));

        ProcessInstance instance = new ProcessInstance();
        instance.setTemplateId(templateId);
        instance.setInitiator(initiator);
        instance.setCurrentState(template.getInitialState()); // Şablonun ilk adımından başla
        instance.setStatus("ACTIVE");
        String time = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        instance.getAuditLogs().add(time + " - Süreç Başlatıldı (" + initiator + ")");
        instance.setVariables(variables);
        instance.setCreatedAt(LocalDateTime.now());
        instance.setUpdatedAt(LocalDateTime.now());

        instance = instanceRepo.save(instance);
        
        // Başlatılan süreci motorun dişlilerine gönder
        return executeProcess(instance, template);
    }

    // 2. Bekleyen bir kullanıcı onayı geldiğinde burası çalışır (Örn: Yönetici ONAYLA dedi)
    // DİKKAT: Buradaki parantez içine 'String completedBy' eklendi!
    public ProcessInstance completeUserTask(String instanceId, String transition, String completedBy) {
        ProcessInstance instance = instanceRepo.findById(instanceId)
                .orElseThrow(() -> new RuntimeException("Aktif süreç bulunamadı: " + instanceId));
        
        ProcessTemplate template = templateRepo.findByTemplateId(instance.getTemplateId())
                .orElseThrow(() -> new RuntimeException("Şablon bulunamadı"));

        ProcessTemplate.StateDefinition currentStateDef = template.getStates().get(instance.getCurrentState());
        
        if (!"USER_TASK".equals(currentStateDef.getType())) {
            throw new RuntimeException("HATA: Bu adım bir kullanıcı onayı beklemiyor!");
        }

        String nextState = currentStateDef.getTransitions().get(transition);
        if (nextState == null) {
            throw new RuntimeException("HATA: Geçersiz eylem (transition): " + transition);
        }
        String time = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        instance.getAuditLogs().add(time + " - " + completedBy + " tarafından ONAYLANDI.");

        instance.setCurrentState(nextState); // Durumu güncelle (Örn: MANAGER_APPROVAL'dan SYSTEM_RECORD'a geçti)
        instance.setUpdatedAt(LocalDateTime.now());
        instanceRepo.save(instance);

        // Yeni duruma geçildiği için motoru tekrar tetikle
        return executeProcess(instance, template);
    }

    // 3. Motorun Kalbi: Adımları otonom olarak sırayla işleten döngü
    private ProcessInstance executeProcess(ProcessInstance instance, ProcessTemplate template) {
        boolean isRunning = true;

        while (isRunning) {
            ProcessTemplate.StateDefinition stateDef = template.getStates().get(instance.getCurrentState());

            if ("END_EVENT".equals(stateDef.getType())) {
                // Süreç bitti, döngüyü kır
                instance.setStatus("COMPLETED");
                instanceRepo.save(instance);
                isRunning = false; 
            } 
            else if ("USER_TASK".equals(stateDef.getType())) {
                // Kullanıcı onayı (insan müdahalesi) gerekiyor. Motor burada uykuya yatar (döngü kırılır).
                isRunning = false;
            } 
            else if ("SERVICE_TASK".equals(stateDef.getType())) {
                // Otomatik adım! İlgili kod parçasını Map içerisinden bul ve çalıştır.
                ServiceTaskAction action = actionStrategies.get(stateDef.getActionName());
                if (action != null) {
                    action.execute(instance, stateDef);
                } else {
                    System.out.println("UYARI: " + stateDef.getActionName() + " için yazılmış bir kod bulunamadı!");
                }
                
                // YENİ EKLENDİ: Otomatik işlemlerin (Sistem) logu
                String time = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                instance.getAuditLogs().add(time + " - SİSTEM OTOMASYONU TAMAMLANDI (" + stateDef.getActionName() + ")");

                // Servis işi hatasız bittiyse "SUCCESS" rotasından bir sonraki adıma geç
                String nextState = stateDef.getTransitions().get("SUCCESS");
                instance.setCurrentState(nextState);
                instanceRepo.save(instance);
            }
        }
        return instance;
    }
}
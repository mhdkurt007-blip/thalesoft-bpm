package com.thalesoft.engine;

import com.thalesoft.engine.model.ProcessTemplate;
import com.thalesoft.engine.repository.ProcessTemplateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class EngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(EngineApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedDatabase(ProcessTemplateRepository templateRepo) {
        return args -> {
            // Eğer veritabanında şablon yoksa otomatik yükle (Data Seeding)
            if (templateRepo.count() == 0) {
                System.out.println(">>> Veritabanı boş. 4 Ana Süreç Şablonu otomatik yükleniyor...");

                // 1. Sistem Yetki Talebi
                ProcessTemplate t1 = new ProcessTemplate();
                t1.setTemplateId("SYSTEM_ACCESS_V1");
                t1.setName("Sistem Yetki Talebi");
                t1.setInitialState("MANAGER_APPROVAL");
                t1.setStates(Map.of(
                        "MANAGER_APPROVAL", createState("USER_TASK", "MANAGER", null, Map.of("APPROVE", "SECURITY_APPROVAL")),
                        "SECURITY_APPROVAL", createState("USER_TASK", "SECURITY_ADMIN", null, Map.of("APPROVE", "AUTO_PROVISION")),
                        "AUTO_PROVISION", createState("SERVICE_TASK", null, "GRANT_SYSTEM_ACCESS", Map.of("SUCCESS", "COMPLETED")),
                        "COMPLETED", createState("END_EVENT", null, null, null)
                ));

                // 2. Masraf Beyan Süreci
                ProcessTemplate t2 = new ProcessTemplate();
                t2.setTemplateId("EXPENSE_CLAIM_V1");
                t2.setName("Masraf Beyan Süreci");
                t2.setInitialState("MANAGER_APPROVAL");
                t2.setStates(Map.of(
                        "MANAGER_APPROVAL", createState("USER_TASK", "MANAGER", null, Map.of("APPROVE", "ACCOUNTING_APPROVAL")),
                        "ACCOUNTING_APPROVAL", createState("USER_TASK", "ACCOUNTANT", null, Map.of("APPROVE", "AUTO_PAYMENT")),
                        "AUTO_PAYMENT", createState("SERVICE_TASK", null, "PROCESS_EXPENSE", Map.of("SUCCESS", "COMPLETED")),
                        "COMPLETED", createState("END_EVENT", null, null, null)
                ));

                // 3. Donanım (Laptop) Talebi
                ProcessTemplate t3 = new ProcessTemplate();
                t3.setTemplateId("HARDWARE_REQUEST_V1");
                t3.setName("Donanım (Laptop) Talebi");
                t3.setInitialState("MANAGER_APPROVAL");
                t3.setStates(Map.of(
                        "MANAGER_APPROVAL", createState("USER_TASK", "MANAGER", null, Map.of("APPROVE", "IT_APPROVAL")),
                        "IT_APPROVAL", createState("USER_TASK", "IT_SPECIALIST", null, Map.of("APPROVE", "AUTO_ALLOCATION")),
                        "AUTO_ALLOCATION", createState("SERVICE_TASK", null, "ALLOCATE_HARDWARE", Map.of("SUCCESS", "COMPLETED")),
                        "COMPLETED", createState("END_EVENT", null, null, null)
                ));

                // 4. İzin Talebi
                ProcessTemplate t4 = new ProcessTemplate();
                t4.setTemplateId("LEAVE_REQUEST_V1");
                t4.setName("İzin Talebi Süreci");
                t4.setInitialState("MANAGER_APPROVAL");
                t4.setStates(Map.of(
                        "MANAGER_APPROVAL", createState("USER_TASK", "MANAGER", null, Map.of("APPROVE", "HR_APPROVAL")),
                        "HR_APPROVAL", createState("USER_TASK", "HR_SPECIALIST", null, Map.of("APPROVE", "AUTO_SAVE")),
                        "AUTO_SAVE", createState("SERVICE_TASK", null, "SAVE_TO_HR_SYSTEM", Map.of("SUCCESS", "COMPLETED")),
                        "COMPLETED", createState("END_EVENT", null, null, null)
                ));

                templateRepo.saveAll(List.of(t1, t2, t3, t4));
                System.out.println(">>> Şablonlar başarıyla veritabanına eklendi!");
            } else {
                System.out.println(">>> Şablonlar zaten mevcut. Data Seeding atlanıyor.");
            }
        };
    }

    // Şablon oluşturmayı kısaltan yardımcı metot
    private ProcessTemplate.StateDefinition createState(String type, String role, String actionName, Map<String, String> transitions) {
        ProcessTemplate.StateDefinition state = new ProcessTemplate.StateDefinition();
        state.setType(type);
        state.setAssigneeRole(role);
        state.setActionName(actionName);
        state.setTransitions(transitions);
        return state;
    }
}
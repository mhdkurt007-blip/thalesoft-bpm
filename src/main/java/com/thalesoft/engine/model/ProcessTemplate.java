package com.thalesoft.engine.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.util.Map;

@Data
@Document(collection = "process_templates")
public class ProcessTemplate {
    
    @Id
    private String id;
    private String templateId; // Örn: LEAVE_REQUEST_V1
    private String name;
    private String initialState;
    
    // Key: State adı (Örn: MANAGER_APPROVAL), Value: O state'in detayları
    private Map<String, StateDefinition> states; 

  @Data
    public static class StateDefinition {
        private String type; 
        private String assigneeRole; 
        private String actionName; 
        private Map<String, String> transitions; 
        
        // YENİ EKLENEN SATIR: Dış API'lere atılacak URL vb. bilgileri tutacak
        private Map<String, Object> actionConfig; 
    }
}
package com.thalesoft.engine.action;

import com.thalesoft.engine.model.ProcessInstance;
import com.thalesoft.engine.model.ProcessTemplate;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class GenericRestApiAction implements ServiceTaskAction {

    @Override
    public void execute(ProcessInstance instance, ProcessTemplate.StateDefinition stateDef) {
        // JSON şablonundan URL ve Method bilgilerini dinamik okuyoruz
        Map<String, Object> config = stateDef.getActionConfig();
        
        if (config != null) {
            String url = (String) config.get("url");
            String method = (String) config.get("method");
            
            System.out.println(">>> [JENERİK API MOTORU] " + method + " isteği hazırlanıyor...");
            System.out.println(">>> Hedef Sistem: " + url);
            System.out.println(">>> Süreç ID: " + instance.getId());
            // İleride buraya Spring RestTemplate veya WebClient ile gerçek HTTP isteği atan 2 satırlık kod eklenebilir.
        } else {
            System.out.println(">>> [JENERİK API MOTORU] HATA: API konfigürasyonu (actionConfig) JSON'da bulunamadı!");
        }
    }

    @Override
    public String getActionName() {
        return "GENERIC_REST_CALL"; // JSON'da bu ismi gördüğünde bu sınıf çalışacak
    }
}
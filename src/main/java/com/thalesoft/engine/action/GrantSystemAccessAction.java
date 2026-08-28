package com.thalesoft.engine.action;

import com.thalesoft.engine.model.ProcessInstance;
import com.thalesoft.engine.model.ProcessTemplate;

import org.springframework.stereotype.Component;

@Component
public class GrantSystemAccessAction implements ServiceTaskAction {

    @Override
    public void execute(ProcessInstance instance, ProcessTemplate.StateDefinition stateDef) {
        System.out.println(">>> SİSTEM OTOMASYONU (GÜVENLİK): Gerekli onaylar alındı. IAM API'si tetiklenerek çalışana sunucu/repo erişim yetkisi atandı.");
        System.out.println(">>> Süreç ID: " + instance.getId());
    }

    @Override
    public String getActionName() {
        return "GRANT_SYSTEM_ACCESS";
    }
}
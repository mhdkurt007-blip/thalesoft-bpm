package com.thalesoft.engine.action;

import com.thalesoft.engine.model.ProcessInstance;
import com.thalesoft.engine.model.ProcessTemplate;
import com.thalesoft.engine.repository.ProcessTemplateRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("ACTIVATE_TEMPLATE_ACTION")
public class ActivateTemplateAction implements ServiceTaskAction {

    private final ProcessTemplateRepository templateRepo;

    public ActivateTemplateAction(ProcessTemplateRepository templateRepo) {
        this.templateRepo = templateRepo;
    }

    // 1. EKSİK OLAN METOT EKLENDİ
    @Override
    public String getActionName() {
        return "ACTIVATE_TEMPLATE_ACTION";
    }

    // 2. İKİNCİ PARAMETRE (StateDefinition) EKLENDİ
    @Override
    public void execute(ProcessInstance instance, ProcessTemplate.StateDefinition stateDef) {
        // 1. Controller'da başlarken içine koyduğumuz "targetTemplateId" değişkenini okuyoruz
        String targetTemplateId = (String) instance.getVariables().get("targetTemplateId");

        if (targetTemplateId != null) {
            // 2. Veritabanından o gizli (DRAFT) taslak şablonu buluyoruz
            Optional<ProcessTemplate> templateOpt = templateRepo.findByTemplateId(targetTemplateId);
            
            if (templateOpt.isPresent()) {
                ProcessTemplate template = templateOpt.get();
                
                // 3. Statüsünü Aktif'e çevirip veritabanına geri kaydediyoruz!
                template.setStatus("ACTIVE");
                templateRepo.save(template);
                
                System.out.println("Meta-Process Başarılı: " + targetTemplateId + " isimli süreç artık kullanıma açık!");
            }
        }
    }
}
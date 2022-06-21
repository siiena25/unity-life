package src.impl.java.ru.mail.browser.assistant;

import ru.mail.browser.experiments.Features;
import ru.mail.browser.experiments.AssistantFeature;
import ru.mail.search.assistant.data.DialogModeProvider;

public class AssistantDialogModeProvider implements DialogModeProvider {

    private final Features mFeatures;

    public AssistantDialogModeProvider(Features features) {
        this.mFeatures = features;
    }
    
    @Override
    public String getDialogMode() {
        AssistantFeature feature = mFeatures.get(Features.ASSISTANT, AssistantFeature.class);
        return feature.get(AssistantFeature.PARAM_DIALOG_MODE);
    }
}

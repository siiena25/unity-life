package src.impl.java.ru.mail.browser.assistant;

import androidx.activity.ComponentActivity;

import ru.mail.search.assistant.AssistantSession;
import ru.mail.search.assistant.common.util.Logger;
import ru.mail.search.assistant.ui.popup.container.PopupFeatureProvider;
import ru.mail.search.assistant.ui.popup.plain.AssistantProvider;
import ru.mail.search.assistant.ui.popup.plain.AssistantSessionReference;
import ru.mail.search.assistant.ui.popup.plain.DirectSessionReference;

public class AssistantProviderImpl implements AssistantProvider {

    private final AssistantHelper mAssistantHelper;
    private final ComponentActivity mComponentActivity;

    public AssistantProviderImpl(
            AssistantHelper assistantHelper,
            ComponentActivity componentActivity
    ) {
        mAssistantHelper = assistantHelper;
        mComponentActivity = componentActivity;
    }

    @Override
    public AssistantSessionReference createSessionReference() {
        AssistantSession assistantSession = mAssistantHelper.createSession(mComponentActivity);
        return new DirectSessionReference(assistantSession);
    }

    @Override
    public Logger getLogger() {
        return mAssistantHelper.getLogger();
    }

    @Override
    public PopupFeatureProvider getFeatureProvider() {
        return null;
    }
}

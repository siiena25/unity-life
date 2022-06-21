package src.stub.java.ru.mail.browser.assistant;

import android.app.Activity;

import ru.mail.browser.analytics.DeviceIdRepository;
import ru.mail.browser.preferences.PreferenceRepository;

public class AssistantHelper {
    private final AssistantLogger mAssistantLogger;

    public AssistantHelper(
            DeviceIdRepository deviceIdRepository,
            Object features,
            PreferenceRepository preferenceRepository,
            Object authRepository
    ) {
        mAssistantLogger = new AssistantLogger();
    }

    public void init() {
    }

    public AssistantLogger getLogger() {
        return mAssistantLogger;
    }

    public void login(Activity activity, Runnable onLoggedInAction) {
        // No-op
    }
}

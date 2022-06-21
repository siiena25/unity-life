package src.main.java.ru.mail.browser.assistant;

public class AssistantHelperProvider {
    private static final Object LOCK = new Object();
    private static AssistantHelper INSTANCE;

    public static void set(AssistantHelper assistantHelper) {
        synchronized (LOCK) {
            INSTANCE = assistantHelper;
        }
    }

    public static AssistantHelper get() {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                throw new IllegalStateException("AuthModule was not created");
            }
        }
        return INSTANCE;
    }
}

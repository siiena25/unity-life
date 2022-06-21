package src.impl.java.ru.mail.browser.assistant;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public final class AssistantFragmentFactory {
    @Nullable
    public static final Fragment createAssistantFragment() {
        return new AssistantFragment();
    }

    private AssistantFragmentFactory() {}
}
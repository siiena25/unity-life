package src.main.java.ru.mail.browser.assistant;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import ru.mail.browser.analytics.AnalyticsLogger;
import ru.mail.browser.analytics.event.AssistantEventFactory;

public final class AssistantUtils {
    public static final String ASSISTANT_FRAGMENT_TAG = "AssistantFragment";

    @Nullable
    public static Fragment findAssistantFragment(@NonNull FragmentManager fragmentManager) {
        return fragmentManager.findFragmentByTag(ASSISTANT_FRAGMENT_TAG);
    }

    public static boolean isAssistantOpened(@NonNull FragmentManager fragmentManager) {
        return findAssistantFragment(fragmentManager) != null;
    }

    public static void addAssistantFragment(@NonNull FragmentManager fragmentManager,
                                            @IdRes int containerId,
                                            boolean isLoggedIn) {
        Fragment assistantFragment = AssistantFragmentFactory.createAssistantFragment();
        if (assistantFragment != null) {
            fragmentManager.beginTransaction().
                add(containerId, assistantFragment, ASSISTANT_FRAGMENT_TAG).
                addToBackStack(null).
                commitAllowingStateLoss();
            logAssistantOpenedEvent(isLoggedIn);
        }
    }

    public static void reattachAssistantFragment(@NonNull FragmentManager fragmentManager) {
        Fragment assistantFragment = findAssistantFragment(fragmentManager);
        if (assistantFragment != null) {
            fragmentManager.beginTransaction().
                detach(assistantFragment).
                attach(assistantFragment).
                commit();
        }
    }

    public static void logIfAssistantFragmentClosed(@NonNull FragmentManager fragmentManager, boolean isLoggedIn) {
        if (!isAssistantOpened(fragmentManager)) {
            logAssistantClosedEvent(isLoggedIn);
        }
    }

    public static void closeAssistantFragment(@NonNull FragmentManager fragmentManager, boolean isLoggedIn) {
        if (!isAssistantOpened(fragmentManager)) {
            fragmentManager.popBackStack();
            logAssistantClosedEvent(isLoggedIn);
        }
    }

    private static void logAssistantOpenedEvent(boolean isLoggedIn) {
        AnalyticsLogger.getInstance().logEvent(AssistantEventFactory.assistantOpened(isLoggedIn));
    }

    private static void logAssistantClosedEvent(boolean isLoggedIn) {
        AnalyticsLogger.getInstance().logEvent(AssistantEventFactory.assistantClosed(isLoggedIn));
    }

    private AssistantUtils() {}
}

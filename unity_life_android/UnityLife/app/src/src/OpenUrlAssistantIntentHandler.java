import android.os.Handler;
import android.os.Looper;

import androidx.activity.ComponentActivity;

import java.util.Collections;
import java.util.List;
import ru.mail.browser.base.util.UrlOpener;
import ru.mail.search.assistant.AssistantIntentHandlerProvider;
import ru.mail.search.assistant.commands.CommandsAdapter;
import ru.mail.search.assistant.commands.intent.AssistantIntent;
import ru.mail.search.assistant.commands.intent.AssistantIntentsHandler;


public class OpenUrlAssistantIntentHandler implements AssistantIntentHandlerProvider {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ComponentActivity mComponentActivity;

    public OpenUrlAssistantIntentHandler(ComponentActivity componentActivity) {
        mComponentActivity = componentActivity;
    }

    @Override
    public List<AssistantIntentsHandler> provide(CommandsAdapter adapter) {
        return Collections.singletonList(getIntentsHandler());
    }

    public AssistantIntentsHandler getIntentsHandler() {
        return (executionContext, assistantIntent, continuation) -> {
            if (assistantIntent instanceof AssistantIntent.OpenUrl) {
                handler.post(() -> handleIntent(assistantIntent));
                return true;
            } else {
                return false;
            }
        };
    }

    private void handleIntent(AssistantIntent assistantIntent) {
        mComponentActivity.getOnBackPressedDispatcher().onBackPressed();
        openUrl(assistantIntent);
    }

    private void openUrl(AssistantIntent assistantIntent) {
        String url = ((AssistantIntent.OpenUrl) assistantIntent).getUrl();
        UrlOpener.getInstance().openUrlInNewTab(url);
    }
}

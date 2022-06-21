import androidx.activity.ComponentActivity;

import java.util.Collections;
import java.util.List;

import ru.mail.browser.base.util.UrlOpener;
import ru.mail.search.assistant.Modification;
import ru.mail.search.assistant.commands.CommandsAdapter;
import ru.mail.search.assistant.commands.external.ExternalCommandDataProvider;
import ru.mail.search.assistant.commands.factory.PublicCommandsFactory;
import ru.mail.search.assistant.commands.intent.AssistantIntent;
import ru.mail.search.assistant.commands.intent.AssistantIntentsHandler;
import ru.mail.search.assistant.data.remote.parser.ExternalDataParser;
import ru.mail.search.assistant.interactor.PhraseInteractor;

public class AssistantOpenUrlModification implements Modification {

    private final ComponentActivity mComponentActivity;

    public AssistantOpenUrlModification(ComponentActivity componentActivity) {
        mComponentActivity = componentActivity;
    }

    @Override
    public ExternalCommandDataProvider getCommandDataProvider(
            PublicCommandsFactory publicCommandsFactory,
            CommandsAdapter commandsAdapter,
            PhraseInteractor phraseInteractor
    ) {
        return null;
    }

    @Override
    public List<ExternalDataParser> getDataParsers() {
        return Collections.emptyList();
    }
}
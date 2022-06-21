package src.impl.java.ru.mail.browser.assistant;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import ru.mail.browser.preferences.PreferenceRepository;
import ru.mail.search.assistant.auth.AuthCredentialsStorage;
import ru.mail.search.assistant.common.http.assistant.Credentials;
import ru.mail.search.assistant.common.http.assistant.SessionType;
import ru.mail.search.assistant.common.util.SecureString;

public class AssistantAuthCredentialsStorage implements AuthCredentialsStorage {
    private static final int SESSION_TYPE_ANONYMOUS_INT = 0;
    private static final int SESSION_TYPE_BASIC_INT = 1;
    private static final int NO_SESSION_TYPE_INT = 2;

    private final PreferenceRepository preferenceRepository;

    public AssistantAuthCredentialsStorage(PreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    @Override
    public Object getCredentials(Continuation<? super Credentials> continuation) {
        return new Credentials(
                new SecureString(preferenceRepository.getString(PreferenceRepository.ASSISTANT_SESSION, "")),
                new SecureString(preferenceRepository.getString(PreferenceRepository.ASSISTANT_SECRET, "")),
                getSessionType()
        );
    }

    @Override
    public Object putCredentials(Credentials credentials, Continuation<? super Unit> continuation) {
        String session = credentials.getSession().getRaw();
        int sessionTypeInt = sessionTypeToInt(credentials.getSessionType());
        String sessionSecret = credentials.getSecret().getRaw();

        preferenceRepository.putString(PreferenceRepository.ASSISTANT_SESSION, session);
        preferenceRepository.putInt(PreferenceRepository.ASSISTANT_SESSION_TYPE, sessionTypeInt);
        preferenceRepository.putString(PreferenceRepository.ASSISTANT_SECRET, sessionSecret);
        return Unit.INSTANCE;
    }
    
    @Override
    public Object onSessionExpired(Credentials credentials, Continuation<? super Unit> continuation) {
        return null; // TODO
    }
    

    @Override
    public boolean hasCredentials() {
        return preferenceRepository.getString(PreferenceRepository.ASSISTANT_SESSION, null) != null;
    }

    @Override
    public SessionType getSessionType() {
        int sessionTypeInt = preferenceRepository.getInt(PreferenceRepository.ASSISTANT_SESSION_TYPE, NO_SESSION_TYPE_INT);
        return intToSessionType(sessionTypeInt);
    }

    public void clear() {
        preferenceRepository.remove(PreferenceRepository.ASSISTANT_SESSION);
        preferenceRepository.remove(PreferenceRepository.ASSISTANT_SESSION_TYPE);
        preferenceRepository.remove(PreferenceRepository.ASSISTANT_SECRET);
    }

    private int sessionTypeToInt(SessionType sessionType) {
        switch (sessionType) {
            case ANONYMOUS: return SESSION_TYPE_ANONYMOUS_INT;
            case BASIC: return SESSION_TYPE_BASIC_INT;
            default: return NO_SESSION_TYPE_INT;
        }
    }

    private SessionType intToSessionType(int sessionTypeInt) {
        switch (sessionTypeInt) {
            case SESSION_TYPE_ANONYMOUS_INT: return SessionType.ANONYMOUS;
            case SESSION_TYPE_BASIC_INT: return SessionType.BASIC;
            default: return null;
        }
    }
}

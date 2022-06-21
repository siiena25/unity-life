package src.impl.java.ru.mail.browser.assistant;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import androidx.activity.ComponentActivity;

import com.vk.api.sdk.VKApiConfig;
import com.vk.auth.marusia.VkAuthMarusia;
import com.vk.silentauth.SilentAuthInfo;
import com.vk.superapp.SuperappKitCommon;
import com.vk.superapp.core.SuperappConfig;

import org.chromium.base.Consumer;
import org.chromium.base.Log;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import kotlin.Result;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.GlobalScope;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineStart;
import okhttp3.OkHttpClient;
import ru.mail.browser.analytics.DeviceIdRepository;
import ru.mail.browser.assistant.AssistantDialogModeProvider;
import ru.mail.browser.assistant.OpenUrlAssistantIntentHandler;
import ru.mail.browser.auth.AuthData;
import ru.mail.browser.auth.AuthRepository;
import ru.mail.browser.auth.AuthRepository.AuthListener;
import ru.mail.browser.base.util.HashUtil;
import ru.mail.browser.experiments.Features;
import ru.mail.browser.preferences.PreferenceRepository;
import ru.mail.search.assistant.Assistant;
import ru.mail.search.assistant.AssistantCore;
import ru.mail.search.assistant.AssistantSession;
import ru.mail.search.assistant.BuildConfig;
import ru.mail.search.assistant.ModificationsProvider;
import ru.mail.search.assistant.StartIntentController;
import ru.mail.search.assistant.auth.AuthFactory;
import ru.mail.search.assistant.auth.LoginRepository;
import ru.mail.search.assistant.commands.main.IntentHandlerProvider;
import ru.mail.search.assistant.common.http.assistant.SessionCredentialsProvider;
import ru.mail.search.assistant.common.http.assistant.SessionType;
import ru.mail.search.assistant.auth.domain.model.AuthResult;
import ru.mail.search.assistant.common.data.remote.NetworkConfig;
import ru.mail.search.assistant.common.http.AssistantOkHttpClient;
import ru.mail.search.assistant.common.schedulers.PoolDispatcher;
import ru.mail.search.assistant.common.schedulers.PoolDispatcherFactory;
import ru.mail.search.assistant.common.util.SecureString;
import ru.mail.search.assistant.data.DefaultConfig;
import ru.mail.search.assistant.modification.AlarmCommandModification;
import ru.mail.search.assistant.modification.OpenAppCommandModification;
import ru.mail.search.assistant.modification.TimerCommandModification;
import ru.mail.search.assistant.services.notification.DefaultPlayerNotificationResourcesProvider;
import ru.mail.search.assistant.ui.popup.welcome.ShortcutColumnsModification;
import ru.mail.search.assistant.vk.auth.VkAuthFactory;
import ru.mail.search.assistant.vk.auth.VkAuthorization;
import ru.mail.search.assistant.vk.auth.data.VkLoginDataSource;
import ru.mail.search.electroscope.kws.ElectroscopeKeywordSpotter;

public class AssistantHelper {

    private static final String TAG = "AssistantHelper";
    private static final String PLATFORM_ID = "android_atom";
    private static final String APP_ID = "6463690";
    private static final Map<String, Boolean> CAPABILITIES = new HashMap<String,Boolean>() {{
        put(Assistant.Capability.SUGGESTS, true);
        put(Assistant.Capability.CARD, true);
        put(Assistant.Capability.NEWS_CARD, true);
        put(Assistant.Capability.VK_MUSIC_LINKS, true);
        put(Assistant.Capability.CINEMA_CARD, true);
        put(Assistant.Capability.SET_ALARM, true);
        put(Assistant.Capability.SET_TIMER, true);
        put(Assistant.Capability.IMAGES, true);
        put(Assistant.Capability.SLIDER, true);
        put(Assistant.Capability.SKILL_LIST, true);
        put(Assistant.Capability.MAIL_CARD, true);
        put(Assistant.Capability.AUTH_CARD, true);
        put(Assistant.Capability.OPEN_APP, true);
        put(Assistant.Capability.ORGANIZATION_CARD, true);
        put(Assistant.Capability.SEARCH_RESULT, true);
        put(Assistant.Capability.RECIPES, true);
        put(Assistant.Capability.PODCASTS_NEW, true);
        put(Assistant.Capability.PLAYER_AUTOPLAY_FLAG, true);
    }};

    private final Handler mHandler = new Handler();
    private final AssistantLogger mAssistantLogger;
    private final DeviceIdRepository mDeviceIdRepository;
    private final Features mFeatures;
    private final AssistantAuthCredentialsStorage mAuthCredentialsStorage;
    private final AuthRepository mAuthRepository;

    private LoginRepository mLoginRepository;
    private AssistantCore mAssistantCore;
    private VkLoginDataSource mVkLoginDataSource;

    public AssistantHelper(
            DeviceIdRepository deviceIdRepository,
            Features features,
            PreferenceRepository preferenceRepository,
            AuthRepository authRepository
    ) {
        mAssistantLogger = new AssistantLogger();
        mDeviceIdRepository = deviceIdRepository;
        mFeatures = features;
        mAuthCredentialsStorage = new AssistantAuthCredentialsStorage(preferenceRepository);
        mAuthRepository = authRepository;
        mAuthRepository.addAuthListener(new AuthListener() {
            @Override
            public void onAuthSuccess(AuthData authData) {
                //no op
            }

            @Override
            public void onSignUpSuccess(String userId) {
                //no op
            }

            @Override
            public void onSignOutCompleted() {
                launchIoCoroutine((continuation) -> {
                    if (mAuthCredentialsStorage.getSessionType() == SessionType.BASIC) {
                        logout((Continuation<? super Unit>) continuation);
                    }
                });
            }

            @Override
            public void onCancel() {
                //no op
            }
        });
    }

    public void init() {
        mDeviceIdRepository.getDeviceId(this::initWithDeviceId);
    }

    public AssistantLogger getLogger() {
        return mAssistantLogger;
    }

    public AssistantSession createSession(ComponentActivity componentActivity) {
        ModificationsProvider modificationsProvider = createModificationsProvider(componentActivity);
        OpenUrlAssistantIntentHandler intentHandlerProvider = new OpenUrlAssistantIntentHandler(componentActivity);
        return mAssistantCore.createSession(modificationsProvider, intentHandlerProvider);
    }

    public void login(Activity activity, Runnable onLoggedInAction) {
        SessionType sessionType = mAuthCredentialsStorage.getSessionType();
        boolean shouldLoginAuthed = mAuthRepository.isLoggedIn() && sessionType != SessionType.BASIC;
        boolean shouldLoginAnonymously = !mAuthRepository.isLoggedIn() && sessionType != SessionType.ANONYMOUS;

        if (shouldLoginAuthed) {
            loginAuthed(activity, onLoggedInAction);
        } else if (shouldLoginAnonymously) {
            loginAnonymously(onLoggedInAction);
        } else {
            onLoggedInAction.run();
        }
    }

    private void initWithDeviceId(String deviceId) {
        SuperappConfig superappConfig = SuperappKitCommon.config;
        Context appContext = superappConfig.getAppContext();
        String appVersion = superappConfig.getAppInfo().getAppVersion();
        VKApiConfig vkApiConfig = superappConfig.getApiProvider().provide().getConfig();
        OkHttpClient okHttpClient = vkApiConfig.getOkHttpProvider().getClient();
        AssistantOkHttpClient assistantOkHttpClient = new AssistantOkHttpClient(okHttpClient);

        NetworkConfig networkConfig = NetworkConfig.Companion.createDefault(
                appContext,
                appVersion,
                PLATFORM_ID,
                HashUtil.getMd5Hash(new HashUtil.Params(deviceId)),
                null, // clientOutdatedCallback
                BuildConfig.DEBUG
        );
        
        AuthFactory authFactory = new AuthFactory(
                networkConfig,
                mAuthCredentialsStorage,
                assistantOkHttpClient,
                () -> false,  // AuthFeatureConfig.isDetailedAccountsMergeEnabled
                null  // analytics
        );

        mLoginRepository = authFactory.createLoginRepository();

        VkAuthFactory vkAuthFactory = new VkAuthFactory(networkConfig, assistantOkHttpClient, /* analytics */ null);
        mVkLoginDataSource = vkAuthFactory.createAuthDataSource();

        mAssistantCore = createAssistantCore(
                appContext,
                networkConfig,
                authFactory.createSessionCredentialsProvider(),
                assistantOkHttpClient
        );
    }

    private AssistantCore createAssistantCore(
            Context context,
            NetworkConfig networkConfig,
            SessionCredentialsProvider sessionCredentialsProvider,
            AssistantOkHttpClient assistantOkHttpClient
    ) {
        Assistant.AppProperties appProperties = new Assistant.AppProperties(
                context,
                () -> CAPABILITIES,
                null, //featureProvider
                new AssistantDialogModeProvider(mFeatures)
        );
        return Assistant.INSTANCE.createCore(
                appProperties,
                networkConfig,
                sessionCredentialsProvider,
                null, // locationProvider
                null, // analytics
                null, // cipher
                null, // preferences
                null, // playerNotificationManager
                new DefaultPlayerNotificationResourcesProvider(),
                null, // splitExperimentParamProvider
                new ElectroscopeKeywordSpotter(context),
                assistantOkHttpClient,
                new DefaultConfig(),
                null, // audioConfig
                mAssistantLogger,
                null, // advertisingIdProvider
                null, // messagesStorage,
                null // mailPhraseParamsProvider
        );
    }

    private ModificationsProvider createModificationsProvider(ComponentActivity componentActivity) {
        StartIntentController startIntentController = new StartIntentController(
                SuperappKitCommon.config.getAppContext(),
                new AssistantExternalApplicationNavigation(),
                mAssistantLogger
        );
        PoolDispatcher poolDispatcher = new PoolDispatcherFactory().createPoolDispatcher();
        return () -> Arrays.asList(
                new TimerCommandModification(startIntentController, null),
                new AlarmCommandModification(startIntentController, null),
                new OpenAppCommandModification(startIntentController, poolDispatcher, null),
                new ShortcutColumnsModification(SuperappKitCommon.config.getAppContext()),
                new AssistantOpenUrlModification(componentActivity)
        );
    }

    private void loginAuthed(Activity activity, Runnable onLoggedInAction) {
        authMarusia(activity, (Result<SilentAuthInfo> silentAuthInfoResult) ->
                launchIoCoroutine(continuation -> {
                    SilentAuthInfo silentAuthInfo = unboxSilentAuthInfo(silentAuthInfoResult);
                    VkAuthorization vkAuthorization = exchangeSilentToken(silentAuthInfo);
                    loginVk(vkAuthorization, continuation);
                    mHandler.post(onLoggedInAction);
                })
        );
    }

    private void authMarusia(Activity activity, Consumer<Result<SilentAuthInfo>> consumer) {
        new VkAuthMarusia(activity).authMarusia((Result<SilentAuthInfo> silentAuthInfoResult) -> {
            consumer.accept(silentAuthInfoResult);
            return Unit.INSTANCE;
        });
    }

    // We have not access to value of kotlin.Result from Java
    private SilentAuthInfo unboxSilentAuthInfo(Result<SilentAuthInfo> silentAuthInfoResult) {
        try {
            Method method = Result.class.getDeclaredMethod("unbox-impl");
            method.setAccessible(true);
            return (SilentAuthInfo) method.invoke(silentAuthInfoResult);
        } catch (Exception e) {
            Log.e(TAG, "Error while auth: " + e);
            return null;
        }
    }

    private VkAuthorization exchangeSilentToken(SilentAuthInfo silentAuthInfo, Continuation<? super VkAuthorization> continuation) {
        return mVkLoginDataSource.exchangeSilentToken(
                silentAuthInfo.getUuid(),
                silentAuthInfo.getToken(),
                APP_ID,
                continuation
        );
    }

    private void loginVk(VkAuthorization vkAuthorization, Continuation<? super AuthResult> continuation) {
        SecureString secureToken = new SecureString(vkAuthorization.getAccessToken());
        SecureString secureUserId = new SecureString(String.valueOf(vkAuthorization.getUserId()));
        mLoginRepository.loginVk(
                secureToken,
                secureUserId,
                null, // legalAccepts
                continuation
        );
    }

    private void loginAnonymously(Runnable onLoggedInAction) {
        launchIoCoroutine(continuation -> {
            mLoginRepository.loginAnonymously(continuation);
            mHandler.post(onLoggedInAction);
        });
    }

    private void logout(Continuation<? super Unit> continuation) {
        mLoginRepository.logout(continuation);
        mAuthCredentialsStorage.clear();
        mAssistantCore.getMessages().clearDialogData();
    }

    private void launchIoCoroutine(Consumer<Continuation<? super AuthResult>> action) {
        BuildersKt.launch(
                GlobalScope.INSTANCE,
                Dispatchers.getIO(),
                CoroutineStart.DEFAULT,
                (coroutineScope, continuation) -> {
                    action.accept((Continuation<? super AuthResult>)  continuation);
                    return Unit.INSTANCE;
                }
        );
    }
}

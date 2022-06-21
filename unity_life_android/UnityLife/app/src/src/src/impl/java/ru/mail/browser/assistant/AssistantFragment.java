package src.impl.java.ru.mail.browser.assistant;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import ru.mail.search.assistant.ui.popup.plain.AssistantProvider;
import ru.mail.search.assistant.ui.popup.plain.PlainPopupAssistantFragment;

public class AssistantFragment extends PlainPopupAssistantFragment {

    public static final String TAG = AssistantUtils.ASSISTANT_FRAGMENT_TAG;

    private static final int RECORD_AUDIO_REQUEST_CODE = 182;

    private boolean mIsFirstGlobalLayoutPerformed;

    @Override
    public AssistantProvider getAssistantProvider() {
        return new AssistantProviderImpl(AssistantHelperProvider.get(), (ComponentActivity) getActivity());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setFirstOnGlobalLayoutAction(view, this::checkAndRequestRecordAudioPermission);
        view.setClickable(true); // The background was not clickable, so clicks fell under the fragment
    }

    private void checkAndRequestRecordAudioPermission() {
        int permissionResult = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionResult != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{ Manifest.permission.RECORD_AUDIO }, RECORD_AUDIO_REQUEST_CODE);
        }
    }

    private void setFirstOnGlobalLayoutAction(View view, Runnable runnable) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (!mIsFirstGlobalLayoutPerformed) {
                    mIsFirstGlobalLayoutPerformed = true;
                    runnable.run();
                }
            }
        });
    }
}

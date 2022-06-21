import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.chromium.chrome.R;
import org.chromium.chrome.browser.ui.signin.PersonalizedSigninPromoView;

public class MrgPersonalizedSigninPromoView extends PersonalizedSigninPromoView {

    private View content;
    private ViewGroup placeholder;
    private View generalSignInButton;
    private View withAccountButton;
    private ImageView accountAvatar;
    private TextView withAccountText;
    private View withOtherAccountButton;
    private View close;

    public MrgPersonalizedSigninPromoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        content = findViewById(R.id.mrg_sign_in_promo_content);
        placeholder = findViewById(R.id.mrg_sign_in_promo_placeholder);
        generalSignInButton = findViewById(R.id.mrg_sign_in_general_button);
        withAccountButton = findViewById(R.id.mrg_sign_in_promo_sign_with_account_button);
        accountAvatar = findViewById(R.id.mrg_sign_in_promo_avatar);
        withAccountText = findViewById(R.id.mrg_signin_promo_sign_with_account_text);
        withOtherAccountButton = findViewById(R.id.mrg_sign_in_with_other_account_button);
        close = findViewById(R.id.mrg_sign_in_promo_close_button);
    }

    public View getContent() {
        return content;
    }

    public ViewGroup getPlaceholder() {
        return placeholder;
    }

    public View getGeneralSignInButton() {
        return generalSignInButton;
    }

    public View getWithAccountButton() {
        return withAccountButton;
    }

    public ImageView getAccountAvatar() {
        return accountAvatar;
    }

    public TextView getWithAccountText() {
        return withAccountText;
    }

    public View getWithOtherAccountButton() {
        return withOtherAccountButton;
    }

    public View getClose() {
        return close;
    }
}
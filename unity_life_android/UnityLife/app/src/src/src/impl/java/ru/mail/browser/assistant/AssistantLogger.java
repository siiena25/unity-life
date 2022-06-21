package src.impl.java.ru.mail.browser.assistant;

import org.chromium.base.Log;

import ru.mail.search.assistant.common.util.Logger;

public class AssistantLogger implements Logger {

    @Override
    public void d(String s, String s1, Throwable throwable) {
        if (throwable == null) {
            Log.d(s, s1);
        } else {
            Log.d(s, s1, throwable);
        }
    }

    @Override
    public void e(String s, Throwable throwable) {
        Log.e(s, "", throwable);
    }

    @Override
    public void e(String s, Throwable throwable, String s1) {
        Log.e(s, s1, throwable);
    }

    @Override
    public void i(String s, String s1, Throwable throwable) {
        if (throwable == null) {
            Log.i(s, s1);
        } else {
            Log.i(s, s1, throwable);
        }
    }

    @Override
    public void log(Level level, String s, String s1, Throwable throwable) {
        //No-op
    }

    @Override
    public void v(String s, String s1, Throwable throwable) {
        if (throwable == null) {
            Log.v(s, s1);
        } else {
            Log.v(s, s1, throwable);
        }
    }

    @Override
    public void w(String s, String s1) {
        Log.w(s, s1);
    }

    @Override
    public void w(String s, Throwable throwable, String s1) {
        if (throwable == null) {
            Log.w(s, s1);
        } else {
            Log.w(s, s1, throwable);
        }
    }
}

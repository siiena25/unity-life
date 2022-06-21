package com.example.unitylife.ext;

import org.apache.commons.lang3.StringEscapeUtils;

public class UnicodeExt {
    public static String urlToUnicode(String str) {
        return StringEscapeUtils.unescapeJava(str);
    }
}
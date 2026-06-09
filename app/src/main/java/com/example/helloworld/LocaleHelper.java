package com.example.helloworld;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

final class LocaleHelper {
    static final String PREFS = "bierchiller";
    static final String KEY_LANGUAGE = "language";
    static final String SYSTEM_LANGUAGE = "";

    private static final Set<String> SUPPORTED_LANGUAGES = new HashSet<>(Arrays.asList(
            "de", "en", "it", "fr", "es", "pt", "nl", "pl", "cs", "hr"
    ));

    private LocaleHelper() {
    }

    static Context wrap(Context context) {
        String language = getStoredLanguage(context);
        Locale locale = language.isEmpty()
                ? resolveSystemLocale()
                : new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocales(new LocaleList(locale));
        } else {
            configuration.setLocale(locale);
        }
        return new ContextWrapper(context.createConfigurationContext(configuration));
    }

    static String getStoredLanguage(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return preferences.getString(KEY_LANGUAGE, SYSTEM_LANGUAGE);
    }

    static void setStoredLanguage(Context context, String language) {
        SharedPreferences.Editor editor = context
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit();
        if (language == null || language.isEmpty()) {
            editor.remove(KEY_LANGUAGE);
        } else {
            editor.putString(KEY_LANGUAGE, language);
        }
        editor.apply();
    }

    static int currentSelectionIndex(Context context, String[] languageCodes) {
        String language = getStoredLanguage(context);
        if (language.isEmpty()) {
            return 0;
        }
        for (int i = 1; i < languageCodes.length; i++) {
            if (language.equals(languageCodes[i])) {
                return i;
            }
        }
        return 0;
    }

    private static Locale resolveSystemLocale() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        String language = locale.getLanguage();
        if (SUPPORTED_LANGUAGES.contains(language)) {
            return new Locale(language);
        }
        return Locale.ENGLISH;
    }
}

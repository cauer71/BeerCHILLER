package com.bierchiller.app;

import android.content.Context;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HelpActivity extends ComponentActivity {
    private static final String HELP_BASE_URL = "file:///android_asset/help/";
    private static final String HELP_TEMPLATE = "help/help_template.html";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_help);
        installSystemBarInsets();

        ImageButton backButton = findViewById(R.id.helpBackButton);
        TextView titleView = findViewById(R.id.helpTitle);
        WebView webView = findViewById(R.id.helpWebView);
        titleView.setText(R.string.calculation_model_title);
        setTitle(R.string.calculation_model_title);
        backButton.setOnClickListener(v -> finish());

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(false);
        settings.setBlockNetworkLoads(true);
        settings.setDefaultTextEncodingName("utf-8");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            settings.setForceDark(WebSettings.FORCE_DARK_AUTO);
        }

        webView.loadDataWithBaseURL(HELP_BASE_URL, buildHelpHtml(), "text/html", "UTF-8", null);
    }

    private void installSystemBarInsets() {
        View root = findViewById(R.id.helpRoot);
        final int basePaddingLeft = root.getPaddingLeft();
        final int basePaddingTop = root.getPaddingTop();
        final int basePaddingRight = root.getPaddingRight();
        final int basePaddingBottom = root.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            androidx.core.graphics.Insets safeDrawing = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout());
            root.setPadding(
                    basePaddingLeft + safeDrawing.left,
                    basePaddingTop + safeDrawing.top,
                    basePaddingRight + safeDrawing.right,
                    basePaddingBottom + safeDrawing.bottom);
            return insets;
        });
        ViewCompat.requestApplyInsets(root);
    }

    private String buildHelpHtml() {
        try {
            String markdown = readAsset(resolveHelpMarkdownAsset());
            String template = readAsset(HELP_TEMPLATE);
            return template.replace("{{content}}", markdownToHtml(markdown));
        } catch (IOException e) {
            return fallbackHtml();
        }
    }

    private String resolveHelpMarkdownAsset() {
        String language = getCurrentResourceLanguage();
        switch (language) {
            case "en":
            case "it":
            case "fr":
            case "es":
            case "pt":
            case "nl":
            case "pl":
            case "cs":
            case "hr":
                return "help/cooling_model_" + language + ".md";
            case "de":
                return "help/cooling_model_de.md";
            default:
                return "help/cooling_model_en.md";
        }
    }

    private String getCurrentResourceLanguage() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return getResources().getConfiguration().getLocales().get(0).getLanguage();
        }
        return getResources().getConfiguration().locale.getLanguage();
    }

    private String markdownToHtml(String markdown) {
        StringBuilder html = new StringBuilder();
        StringBuilder paragraph = new StringBuilder();
        boolean inList = false;
        boolean inFormula = false;
        boolean inTable = false;
        boolean cardOpen = false;
        boolean introOpen = false;

        String[] lines = markdown.replace("\r\n", "\n").replace('\r', '\n').split("\n");
        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (inFormula) {
                paragraph.setLength(0);
                html.append(escapeHtml(rawLine)).append('\n');
                if ("\\]".equals(line)) {
                    html.append("</div>");
                    inFormula = false;
                }
                continue;
            }
            if ("\\[".equals(line)) {
                flushParagraph(html, paragraph);
                if (inList) {
                    html.append("</ul>");
                    inList = false;
                }
                html.append("<div class=\"formula\">").append(escapeHtml(rawLine)).append('\n');
                inFormula = true;
                continue;
            }
            if (isTableRow(line)) {
                flushParagraph(html, paragraph);
                if (inList) {
                    html.append("</ul>");
                    inList = false;
                }
                if (!isTableSeparator(line)) {
                    if (!inTable) {
                        html.append("<div class=\"table-wrap\"><table>");
                        inTable = true;
                    }
                    appendTableRow(html, line);
                }
                continue;
            }
            if (inTable) {
                html.append("</table></div>");
                inTable = false;
            }
            if (line.isEmpty()) {
                flushParagraph(html, paragraph);
                continue;
            }
            if (line.startsWith("# ")) {
                flushParagraph(html, paragraph);
                if (inList) {
                    html.append("</ul>");
                    inList = false;
                }
                html.append("<h1>").append(formatInline(line.substring(2))).append("</h1>");
                html.append("<div class=\"intro\">");
                introOpen = true;
                continue;
            }
            if (line.startsWith("## ")) {
                flushParagraph(html, paragraph);
                if (introOpen) {
                    html.append("</div>");
                    introOpen = false;
                }
                if (inList) {
                    html.append("</ul>");
                    inList = false;
                }
                if (cardOpen) {
                    html.append("</section>");
                }
                String cardClass = line.contains("Grenzen") ? "card note" : "card";
                html.append("<section class=\"").append(cardClass).append("\">");
                html.append("<h2>").append(formatInline(line.substring(3))).append("</h2>");
                cardOpen = true;
                continue;
            }
            if (line.startsWith("- ")) {
                flushParagraph(html, paragraph);
                if (!inList) {
                    html.append("<ul>");
                    inList = true;
                }
                html.append("<li>").append(formatInline(line.substring(2))).append("</li>");
                continue;
            }
            if (inList) {
                html.append("</ul>");
                inList = false;
            }
            if (paragraph.length() > 0) {
                paragraph.append(' ');
            }
            paragraph.append(line);
        }

        flushParagraph(html, paragraph);
        if (inFormula) {
            html.append("</div>");
        }
        if (inTable) {
            html.append("</table></div>");
        }
        if (inList) {
            html.append("</ul>");
        }
        if (introOpen) {
            html.append("</div>");
        }
        if (cardOpen) {
            html.append("</section>");
        }
        return html.toString();
    }

    private void flushParagraph(StringBuilder html, StringBuilder paragraph) {
        if (paragraph.length() == 0) {
            return;
        }
        html.append("<p>").append(formatInline(paragraph.toString())).append("</p>");
        paragraph.setLength(0);
    }

    private boolean isTableRow(String line) {
        return line.startsWith("|") && line.endsWith("|") && line.indexOf('|', 1) > 0;
    }

    private boolean isTableSeparator(String line) {
        return line.matches("^\\|[\\s:\\-\\|]+\\|$");
    }

    private void appendTableRow(StringBuilder html, String line) {
        String[] cells = line.substring(1, line.length() - 1).split("\\|", -1);
        html.append("<tr>");
        for (String cell : cells) {
            html.append("<td>").append(formatInline(cell.trim())).append("</td>");
        }
        html.append("</tr>");
    }

    private String formatInline(String text) {
        String escaped = escapeHtml(text);
        return escaped
                .replaceAll("\\\\\\((.+?)\\\\\\)", "<span class=\"inline-math\">\\\\($1\\\\)</span>")
                .replace("`", "");
    }

    private String fallbackHtml() {
        String title = escapeHtml(getString(R.string.calculation_model_title));
        String message = escapeHtml(getString(R.string.help_load_error));
        return "<!doctype html><html><head><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">"
                + "<style>body{font-family:sans-serif;background:#f7fbfa;color:#123b4a;padding:24px;line-height:1.5}"
                + ".card{background:white;border-radius:20px;padding:18px;box-shadow:0 8px 24px rgba(18,59,74,.12)}</style>"
                + "</head><body><div class=\"card\"><h1>" + title + "</h1><p>" + message + "</p></div></body></html>";
    }

    private String readAsset(String path) throws IOException {
        try (InputStream inputStream = getAssets().open(path);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
            return builder.toString();
        }
    }

    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}

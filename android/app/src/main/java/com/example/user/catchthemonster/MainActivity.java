package com.example.user.catchthemonster;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Insets;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Offline host for the same game shipped to browsers. No JavaScript/native bridge. */
public final class MainActivity extends Activity {
    private static final String HOST = "appassets.androidplatform.net";
    private static final String GAME_URL = "https://" + HOST + "/game/index.html";
    private static final int BACKGROUND = Color.rgb(35, 61, 50);

    private FrameLayout container;
    private WebView webView;
    private boolean paused;
    private OnBackInvokedCallback backCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        container = new FrameLayout(this);
        container.setBackgroundColor(BACKGROUND);
        setContentView(container);
        configureInsets();
        createGameView();
        if (Build.VERSION.SDK_INT >= 33) {
            backCallback = this::handleBack;
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT, backCallback);
        }
    }

    private void configureInsets() {
        if (Build.VERSION.SDK_INT >= 30) {
            getWindow().setDecorFitsSystemWindows(false);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        container.setOnApplyWindowInsetsListener((view, insets) -> {
            if (Build.VERSION.SDK_INT >= 30) {
                Insets safe = insets.getInsets(WindowInsets.Type.systemBars()
                        | WindowInsets.Type.displayCutout());
                view.setPadding(safe.left, safe.top, safe.right, safe.bottom);
            } else {
                view.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            }
            return insets;
        });
        container.requestApplyInsets();
    }

    @SuppressLint("SetJavaScriptEnabled") // All scripts are immutable assets inside the APK.
    private void createGameView() {
        try {
            webView = new WebView(this);
        } catch (RuntimeException error) {
            Log.e("CatchTheMonster", "System WebView could not be created", error);
            showWebViewError();
            return;
        }
        webView.setBackgroundColor(BACKGROUND);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setBlockNetworkLoads(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setSupportMultipleWindows(false);
        settings.setMediaPlaybackRequiresUserGesture(true);
        settings.setTextZoom(100);
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        webView.setWebViewClient(new LocalGameClient());
        container.addView(webView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        webView.loadUrl(GAME_URL);
    }

    private void showWebViewError() {
        TextView error = new TextView(this);
        error.setText(R.string.webview_unavailable);
        error.setTextColor(Color.WHITE);
        error.setTextSize(18);
        error.setPadding(32, 32, 32, 32);
        error.setGravity(Gravity.CENTER);
        container.addView(error, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
    }

    private void notifyVisibility() {
        if (webView != null) {
            webView.evaluateJavascript("window.dispatchEvent(new CustomEvent('appvisibility',"
                    + "{detail:{hidden:" + paused + "}}));", null);
        }
    }

    @Override
    protected void onPause() {
        paused = true;
        notifyVisibility();
        if (webView != null) {
            webView.onPause();
            webView.pauseTimers();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        paused = false;
        if (webView != null) {
            webView.resumeTimers();
            webView.onResume();
        }
        notifyVisibility();
    }

    /** gameBack returns true after closing a dialog or returning to the game menu. */
    private void handleBack() {
        if (webView == null) {
            finish();
            return;
        }
        webView.evaluateJavascript("(function(){try{return typeof window.gameBack==='function'"
                + "&&window.gameBack()===true;}catch(e){return false;}})()", result -> {
            if (!"true".equals(result) && !isFinishing()) {
                finish();
            }
        });
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        handleBack();
    }

    @Override
    protected void onDestroy() {
        if (Build.VERSION.SDK_INT >= 33 && backCallback != null) {
            getOnBackInvokedDispatcher().unregisterOnBackInvokedCallback(backCallback);
        }
        if (webView != null) {
            container.removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    private static boolean isLocalGameUri(Uri uri) {
        return "https".equals(uri.getScheme()) && HOST.equals(uri.getHost())
                && uri.getPort() == -1 && uri.getUserInfo() == null
                && uri.getPath() != null && uri.getPath().startsWith("/game/");
    }

    private final class LocalGameClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            // No links may open the network, files, content providers, or another application.
            return !isLocalGameUri(request.getUrl());
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            Uri uri = request.getUrl();
            if (!isLocalGameUri(uri) || !"GET".equals(request.getMethod())) {
                return errorResponse(403, "Forbidden");
            }
            String path = uri.getPath().substring(1);
            for (String segment : path.split("/")) {
                if (segment.equals(".") || segment.equals("..") || segment.contains("\\")) {
                    return errorResponse(403, "Forbidden");
                }
            }
            try {
                InputStream data = getAssets().open(path);
                String mime = mimeType(path);
                String encoding = mime.startsWith("text/") || mime.contains("javascript")
                        || mime.contains("json") || mime.contains("svg") ? "UTF-8" : null;
                Map<String, String> headers = new HashMap<>();
                headers.put("Cache-Control", "no-store");
                headers.put("X-Content-Type-Options", "nosniff");
                headers.put("Content-Security-Policy", "default-src 'self'; script-src 'self'; "
                        + "style-src 'self' 'unsafe-inline'; img-src 'self' data: blob:; "
                        + "media-src 'self' blob:; connect-src 'self'; font-src 'self'; "
                        + "object-src 'none'; frame-src 'none'; worker-src 'none'; base-uri 'self'");
                return new WebResourceResponse(mime, encoding, 200, "OK", headers, data);
            } catch (IOException error) {
                return errorResponse(404, "Not Found");
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            notifyVisibility();
        }

        @Override
        public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
            container.removeView(view);
            view.destroy();
            webView = null;
            showWebViewError();
            return true;
        }
    }

    private static WebResourceResponse errorResponse(int code, String message) {
        return new WebResourceResponse("text/plain", "UTF-8", code, message,
                new HashMap<>(), new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8)));
    }

    private static String mimeType(String path) {
        String lower = path.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".html")) return "text/html";
        if (lower.endsWith(".css")) return "text/css";
        if (lower.endsWith(".js") || lower.endsWith(".mjs")) return "text/javascript";
        if (lower.endsWith(".json") || lower.endsWith(".webmanifest")) return "application/json";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".ico")) return "image/x-icon";
        if (lower.endsWith(".woff2")) return "font/woff2";
        if (lower.endsWith(".woff")) return "font/woff";
        if (lower.endsWith(".mp3")) return "audio/mpeg";
        if (lower.endsWith(".ogg")) return "audio/ogg";
        if (lower.endsWith(".wav")) return "audio/wav";
        return "application/octet-stream";
    }
}

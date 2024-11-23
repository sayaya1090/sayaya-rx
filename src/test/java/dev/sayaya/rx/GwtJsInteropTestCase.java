package dev.sayaya.rx;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Command;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

abstract class GwtJsInteropTestCase extends GWTTestCase {
    private final Logger logger = Logger.getLogger(getClass().getName());

    protected void waitForScriptToLoad(String script, Command callback) {
        waitForScriptToLoad(new String[] { script }, callback);
    }
    protected void waitForScriptToLoad(String[] scripts, Command callback) {
        AtomicInteger loaded = new AtomicInteger(0);
        Callback<Void, Exception> loadedCallback = new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                logger.severe("JS script injection failed: " + reason.getMessage());
            }

            @Override
            public void onSuccess(Void result) {
                logger.info("JS script successfully injected.");
                loaded.incrementAndGet();
            }
        };
        for(String script: scripts) ScriptInjector.fromUrl(script)
                .setWindow(ScriptInjector.TOP_WINDOW)
                .setCallback(loadedCallback)
                .inject();

        Scheduler.get().scheduleFixedDelay(() -> {
            if (loaded.get() == scripts.length) {
                callback.execute();
                return false;
            }
            return true;
        }, 100);
    }
}

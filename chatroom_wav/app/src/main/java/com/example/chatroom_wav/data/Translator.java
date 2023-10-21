package com.example.chatroom_wav.data;

import android.util.Log;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Translator {
    public String translate(final String text, final String language) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Callable<String> translationTask = new Callable<String>() {
            @Override
            public String call() {
                String targetLang = language; // 目标语言
                String sourceLang = "en"; // 源语言
                Translate translate = TranslateOptions.newBuilder().setApiKey("AIzaSyCTBuWmQq5v6zS7ZHcw7J5-_fd4pClDLvs").build().getService();
                Translation translation = translate.translate(text, Translate.TranslateOption.targetLanguage(targetLang), Translate.TranslateOption.sourceLanguage(sourceLang));
                String translatedText = translation.getTranslatedText();
                Log.d("translatedText", translatedText);
                return translatedText;
            }
        };

        Future<String> future = executorService.submit(translationTask);
        try {
            return future.get(); // This will block until the translation is complete
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null; // Handle the error appropriately
        } finally {
            executorService.shutdown();
        }
    }
}

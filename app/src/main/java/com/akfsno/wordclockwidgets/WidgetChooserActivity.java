package com.akfsno.wordclockwidgets;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class WidgetChooserActivity extends Activity {

    private static final int REQUEST_CODE_STYLE = 100;
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        showStyleSelectionDialog();
    }

    private void showStyleSelectionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Выберите стиль настройки")
                .setMessage("Как вы хотите настроить виджет?")
                .setPositiveButton("Базовый стиль", (dialog, which) -> {
                    Intent intent = new Intent(WidgetChooserActivity.this, BasicStyleActivity.class);
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    startActivityForResult(intent, REQUEST_CODE_STYLE);
                })
                .setNegativeButton("Конструктор", (dialog, which) -> {
                    Intent intent = new Intent(WidgetChooserActivity.this, WidgetConfigureActivity.class);
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    startActivityForResult(intent, REQUEST_CODE_STYLE);
                })
                .setNeutralButton("Отмена", (dialog, which) -> {
                    setResult(RESULT_CANCELED);
                    finish();
                })
                .setOnCancelListener(dialog -> {
                    setResult(RESULT_CANCELED);
                    finish();
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_STYLE) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, data);
            } else {
                setResult(RESULT_CANCELED);
            }
            finish();
        }
    }
}

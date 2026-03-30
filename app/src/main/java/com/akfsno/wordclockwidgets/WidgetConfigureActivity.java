package com.akfsno.wordclockwidgets;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;

public class WidgetConfigureActivity extends Activity {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Spinner styleSpinner, colorSpinner, borderColorSpinner, backgroundColorSpinner;
    private SeekBar fontSizeSeekBar, borderWidthSeekBar, backgroundAlphaSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        setResult(RESULT_CANCELED);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        styleSpinner = findViewById(R.id.style_spinner);
        ArrayAdapter<String> styleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Базовый", "Горизонтальный", "Расширенный", "Кислотный", "Неоновый"});
        styleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        styleSpinner.setAdapter(styleAdapter);

        colorSpinner = findViewById(R.id.color_spinner);
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Чёрный", "Белый", "Красный", "Зелёный", "Синий", "Жёлтый", "Оранжевый", "Фиолетовый", "Розовый", "Серый"});
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(colorAdapter);

        borderColorSpinner = findViewById(R.id.border_color_spinner);
        borderColorSpinner.setAdapter(colorAdapter);

        backgroundColorSpinner = findViewById(R.id.background_color_spinner);
        backgroundColorSpinner.setAdapter(colorAdapter);

        fontSizeSeekBar = findViewById(R.id.font_size_seekbar);
        fontSizeSeekBar.setProgress((int) WidgetPreferences.getFontSize(this, appWidgetId, 24f));

        borderWidthSeekBar = findViewById(R.id.border_width_seekbar);
        borderWidthSeekBar.setProgress(WidgetPreferences.getBorderWidth(this, appWidgetId, 2));

        backgroundAlphaSeekBar = findViewById(R.id.background_alpha_seekbar);
        backgroundAlphaSeekBar.setProgress(WidgetPreferences.getBackgroundAlpha(this, appWidgetId, 0));

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveConfiguration();
            }
        });
    }

    private void saveConfiguration() {
        String style = (String) styleSpinner.getSelectedItem();
        WidgetPreferences.saveStyle(this, appWidgetId, style);

        int color = getColorFromSpinner(colorSpinner);
        WidgetPreferences.saveColor(this, appWidgetId, color);

        float fontSize = fontSizeSeekBar.getProgress();
        WidgetPreferences.saveFontSize(this, appWidgetId, fontSize);

        int borderColor = getColorFromSpinner(borderColorSpinner);
        WidgetPreferences.saveBorderColor(this, appWidgetId, borderColor);

        int borderWidth = borderWidthSeekBar.getProgress();
        WidgetPreferences.saveBorderWidth(this, appWidgetId, borderWidth);

        int backgroundColor = getColorFromSpinner(backgroundColorSpinner);
        WidgetPreferences.saveBackgroundColor(this, appWidgetId, backgroundColor);

        int backgroundAlpha = backgroundAlphaSeekBar.getProgress();
        WidgetPreferences.saveBackgroundAlpha(this, appWidgetId, backgroundAlpha);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    private int getColorFromSpinner(Spinner spinner) {
        switch (spinner.getSelectedItemPosition()) {
            case 0: return Color.BLACK;
            case 1: return Color.WHITE;
            case 2: return Color.RED;
            case 3: return Color.GREEN;
            case 4: return Color.BLUE;
            case 5: return Color.YELLOW;
            case 6: return Color.rgb(255, 165, 0); // Orange
            case 7: return Color.rgb(128, 0, 128); // Purple
            case 8: return Color.rgb(255, 192, 203); // Pink
            case 9: return Color.GRAY;
            default: return Color.BLACK;
        }
    }
}
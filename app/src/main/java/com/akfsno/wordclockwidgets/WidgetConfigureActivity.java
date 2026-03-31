package com.akfsno.wordclockwidgets;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.util.TimeZone;

public class WidgetConfigureActivity extends Activity {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Spinner styleSpinner, colorSpinner, borderColorSpinner, backgroundColorSpinner;
    private SeekBar fontSizeSeekBar, borderWidthSeekBar, backgroundAlphaSeekBar;
    private SeekBar hourOffsetXSeekBar, hourOffsetYSeekBar;
    private SeekBar minuteOffsetXSeekBar, minuteOffsetYSeekBar;
    private CheckBox showSecondsCheckbox, showDateCheckbox, showDayOfWeekCheckbox, use12HourCheckbox, secondsAsWordsCheckbox;
    private Spinner secondsDisplayModeSpinner;
    private Spinner blockModeSpinner;
    private Spinner blockBackgroundColorSpinner;
    private Spinner blockBorderColorSpinner;
    private SeekBar minuteFontSizeSeekBar, secondFontSizeSeekBar;
    private SeekBar secondOffsetXSeekBar, secondOffsetYSeekBar, dateOffsetXSeekBar, dateOffsetYSeekBar, dayOfWeekOffsetXSeekBar, dayOfWeekOffsetYSeekBar, dayNightOffsetXSeekBar, dayNightOffsetYSeekBar;
    
    // Preview widgets
    private TextView previewHourText, previewMinuteText, previewDayNightText, previewDayOfWeekText, previewDateText, previewSecondText;
    private Handler previewHandler = new Handler(Looper.getMainLooper());
    private Runnable previewUpdateRunnable;

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
            appWidgetId = findExistingWidgetId();
            if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                Toast.makeText(this, "Сначала добавьте виджет на главный экран", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }

        // Initialize preview widgets
        previewHourText = findViewById(R.id.preview_hour_text);
        previewMinuteText = findViewById(R.id.preview_minute_text);
        previewDayNightText = findViewById(R.id.preview_day_night_text);
        previewDayOfWeekText = findViewById(R.id.preview_day_of_week_text);
        previewDateText = findViewById(R.id.preview_date_text);
        previewSecondText = findViewById(R.id.preview_second_text);

        styleSpinner = findViewById(R.id.style_spinner);
        ArrayAdapter<String> styleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Базовый", "Горизонтальный", "Расширенный", "Кислотный", "Неоновый", "Маленький"});
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

        secondsDisplayModeSpinner = findViewById(R.id.seconds_display_mode_spinner);
        ArrayAdapter<String> secondsModeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Горизонтально", "Вертикально"});
        secondsModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        secondsDisplayModeSpinner.setAdapter(secondsModeAdapter);

        blockModeSpinner = findViewById(R.id.block_mode_spinner);
        ArrayAdapter<String> blockModeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Обычный", "Блочная система"});
        blockModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        blockModeSpinner.setAdapter(blockModeAdapter);

        blockBackgroundColorSpinner = findViewById(R.id.block_background_color_spinner);
        blockBackgroundColorSpinner.setAdapter(colorAdapter);

        blockBorderColorSpinner = findViewById(R.id.block_border_color_spinner);
        blockBorderColorSpinner.setAdapter(colorAdapter);

        fontSizeSeekBar = findViewById(R.id.font_size_seekbar);
        fontSizeSeekBar.setProgress((int) WidgetPreferences.getFontSize(this, appWidgetId, 24f));

        borderWidthSeekBar = findViewById(R.id.border_width_seekbar);
        borderWidthSeekBar.setProgress(WidgetPreferences.getBorderWidth(this, appWidgetId, 2));

        backgroundAlphaSeekBar = findViewById(R.id.background_alpha_seekbar);
        backgroundAlphaSeekBar.setProgress(WidgetPreferences.getBackgroundAlpha(this, appWidgetId, 0));

        hourOffsetXSeekBar = findViewById(R.id.hour_offset_x_seekbar);
        hourOffsetYSeekBar = findViewById(R.id.hour_offset_y_seekbar);
        minuteOffsetXSeekBar = findViewById(R.id.minute_offset_x_seekbar);
        minuteOffsetYSeekBar = findViewById(R.id.minute_offset_y_seekbar);

        // Set seekbar ranges and initial positions
        int maxOffset = WidgetPreferences.getMaxOffset();
        int minOffset = WidgetPreferences.getMinOffset();
        int offsetRange = maxOffset - minOffset; // 400

        hourOffsetXSeekBar.setMax(offsetRange);
        hourOffsetYSeekBar.setMax(offsetRange);
        minuteOffsetXSeekBar.setMax(offsetRange);
        minuteOffsetYSeekBar.setMax(offsetRange);

        // Set initial progress with center position (progress 200 = 0 offset)
        int centerProgress = (0 - minOffset); // Center position = 200

        int hourOffsetX = WidgetPreferences.getOffsetX(this, appWidgetId, "hour", 0);
        int hourOffsetY = WidgetPreferences.getOffsetY(this, appWidgetId, "hour", 0);
        int minuteOffsetX = WidgetPreferences.getOffsetX(this, appWidgetId, "minute", 0);
        int minuteOffsetY = WidgetPreferences.getOffsetY(this, appWidgetId, "minute", 0);

        hourOffsetXSeekBar.setProgress(hourOffsetX - minOffset);
        hourOffsetYSeekBar.setProgress(hourOffsetY - minOffset);
        minuteOffsetXSeekBar.setProgress(minuteOffsetX - minOffset);
        minuteOffsetYSeekBar.setProgress(minuteOffsetY - minOffset);

        showSecondsCheckbox = findViewById(R.id.show_seconds_checkbox);
        showDateCheckbox = findViewById(R.id.show_date_checkbox);
        showDayOfWeekCheckbox = findViewById(R.id.show_day_of_week_checkbox);
        use12HourCheckbox = findViewById(R.id.use_12hour_checkbox);
        secondsAsWordsCheckbox = findViewById(R.id.seconds_as_words_checkbox);

        minuteFontSizeSeekBar = findViewById(R.id.minute_font_size_seekbar);
        secondFontSizeSeekBar = findViewById(R.id.second_font_size_seekbar);

        secondOffsetXSeekBar = findViewById(R.id.second_offset_x_seekbar);
        secondOffsetYSeekBar = findViewById(R.id.second_offset_y_seekbar);
        dateOffsetXSeekBar = findViewById(R.id.date_offset_x_seekbar);
        dateOffsetYSeekBar = findViewById(R.id.date_offset_y_seekbar);
        dayOfWeekOffsetXSeekBar = findViewById(R.id.day_of_week_offset_x_seekbar);
        dayOfWeekOffsetYSeekBar = findViewById(R.id.day_of_week_offset_y_seekbar);
        dayNightOffsetXSeekBar = findViewById(R.id.day_night_offset_x_seekbar);
        dayNightOffsetYSeekBar = findViewById(R.id.day_night_offset_y_seekbar);

        // Set ranges for additional offset seekbars
        secondOffsetXSeekBar.setMax(offsetRange);
        secondOffsetYSeekBar.setMax(offsetRange);
        dateOffsetXSeekBar.setMax(offsetRange);
        dateOffsetYSeekBar.setMax(offsetRange);
        dayOfWeekOffsetXSeekBar.setMax(offsetRange);
        dayOfWeekOffsetYSeekBar.setMax(offsetRange);
        dayNightOffsetXSeekBar.setMax(offsetRange);
        dayNightOffsetYSeekBar.setMax(offsetRange);

        // Set initial progress with bounded offsets
        int secondOffsetX = WidgetPreferences.getSecondOffsetX(this, appWidgetId, 0);
        int secondOffsetY = WidgetPreferences.getSecondOffsetY(this, appWidgetId, 0);
        int dateOffsetX = WidgetPreferences.getDateOffsetX(this, appWidgetId, 0);
        int dateOffsetY = WidgetPreferences.getDateOffsetY(this, appWidgetId, 0);
        int dayOfWeekOffsetX = WidgetPreferences.getDayOfWeekOffsetX(this, appWidgetId, 0);
        int dayOfWeekOffsetY = WidgetPreferences.getDayOfWeekOffsetY(this, appWidgetId, 0);
        int dayNightOffsetX = WidgetPreferences.getDayNightOffsetX(this, appWidgetId, 0);
        int dayNightOffsetY = WidgetPreferences.getDayNightOffsetY(this, appWidgetId, 0);

        secondOffsetXSeekBar.setProgress(secondOffsetX - minOffset);
        secondOffsetYSeekBar.setProgress(secondOffsetY - minOffset);
        dateOffsetXSeekBar.setProgress(dateOffsetX - minOffset);
        dateOffsetYSeekBar.setProgress(dateOffsetY - minOffset);
        dayOfWeekOffsetXSeekBar.setProgress(dayOfWeekOffsetX - minOffset);
        dayOfWeekOffsetYSeekBar.setProgress(dayOfWeekOffsetY - minOffset);
        dayNightOffsetXSeekBar.setProgress(dayNightOffsetX - minOffset);
        dayNightOffsetYSeekBar.setProgress(dayNightOffsetY - minOffset);

        showSecondsCheckbox.setChecked(WidgetPreferences.getShowSeconds(this, appWidgetId, false));
        showDateCheckbox.setChecked(WidgetPreferences.getShowDate(this, appWidgetId, false));
        showDayOfWeekCheckbox.setChecked(WidgetPreferences.getShowDayOfWeek(this, appWidgetId, false));
        use12HourCheckbox.setChecked(WidgetPreferences.getUse12HourFormat(this, appWidgetId, false));
        secondsAsWordsCheckbox.setChecked(WidgetPreferences.getSecondsAsWords(this, appWidgetId, true));

        String secondsDisplayMode = WidgetPreferences.getSecondsDisplayMode(this, appWidgetId, "Горизонтально");
        secondsDisplayModeSpinner.setSelection(secondsDisplayMode.equals("Вертикально") ? 1 : 0);

        String blockMode = WidgetPreferences.getBlockMode(this, appWidgetId, "Обычный");
        blockModeSpinner.setSelection(blockMode.equals("Блочная система") ? 1 : 0);

        blockBackgroundColorSpinner.setSelection(getPositionForColor(WidgetPreferences.getBlockBackgroundColor(this, appWidgetId, Color.TRANSPARENT)));
        blockBorderColorSpinner.setSelection(getPositionForColor(WidgetPreferences.getBlockBorderColor(this, appWidgetId, Color.GRAY)));

        minuteFontSizeSeekBar.setProgress((int) WidgetPreferences.getMinuteFontSize(this, appWidgetId, 24f));
        secondFontSizeSeekBar.setProgress((int) WidgetPreferences.getSecondFontSize(this, appWidgetId, 18f));

        String savedStyle = WidgetPreferences.getStyle(this, appWidgetId, "Базовый");
        int stylePosition = getStylePosition(savedStyle);
        if (stylePosition >= 0) styleSpinner.setSelection(stylePosition);

        int savedColor = WidgetPreferences.getColor(this, appWidgetId, Color.BLACK);
        colorSpinner.setSelection(getPositionForColor(savedColor));
        borderColorSpinner.setSelection(getPositionForColor(WidgetPreferences.getBorderColor(this, appWidgetId, Color.RED)));
        backgroundColorSpinner.setSelection(getPositionForColor(WidgetPreferences.getBackgroundColor(this, appWidgetId, Color.TRANSPARENT)));

        // Add listeners for live preview
        SeekBar.OnSeekBarChangeListener previewListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updatePreview();
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        CheckBox.OnCheckedChangeListener checkboxListener = (buttonView, isChecked) -> updatePreview();
        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updatePreview();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        fontSizeSeekBar.setOnSeekBarChangeListener(previewListener);
        colorSpinner.setOnItemSelectedListener(spinnerListener);
        borderColorSpinner.setOnItemSelectedListener(spinnerListener);
        backgroundColorSpinner.setOnItemSelectedListener(spinnerListener);
        secondsDisplayModeSpinner.setOnItemSelectedListener(spinnerListener);
        blockModeSpinner.setOnItemSelectedListener(spinnerListener);
        blockBackgroundColorSpinner.setOnItemSelectedListener(spinnerListener);
        blockBorderColorSpinner.setOnItemSelectedListener(spinnerListener);
        backgroundAlphaSeekBar.setOnSeekBarChangeListener(previewListener);
        
        showSecondsCheckbox.setOnCheckedChangeListener(checkboxListener);
        showDateCheckbox.setOnCheckedChangeListener(checkboxListener);
        showDayOfWeekCheckbox.setOnCheckedChangeListener(checkboxListener);
        use12HourCheckbox.setOnCheckedChangeListener(checkboxListener);

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveConfiguration();
            }
        });

        // Start preview updates
        startPreviewUpdates();
        updatePreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPreviewUpdates();
    }

    private void startPreviewUpdates() {
        previewUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                updatePreview();
                previewHandler.postDelayed(this, 1000); // Update every second
            }
        };
        previewHandler.post(previewUpdateRunnable);
    }

    private void stopPreviewUpdates() {
        if (previewUpdateRunnable != null) {
            previewHandler.removeCallbacks(previewUpdateRunnable);
        }
    }

    private void updatePreview() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"));
        boolean use12Hour = use12HourCheckbox.isChecked();
        int rawHour = calendar.get(Calendar.HOUR_OF_DAY);
        int hour = use12Hour ? calendar.get(Calendar.HOUR) : rawHour;
        if (!use12Hour && hour < 0) hour = 0;
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        String hourText = use12Hour ? NumberToWords.convertHour(hour) : NumberToWords.convertHour24(rawHour);
        String minuteText = NumberToWords.convertMinute(minute);
        String dayNightText = NumberToWords.getDayNight(rawHour);
        String dayOfWeekText = NumberToWords.getDayOfWeek(dayOfWeek);
        String dateText = NumberToWords.convertDate(day, month, year);

        boolean secondsAsWords = secondsAsWordsCheckbox.isChecked();
        String secondsDisplayMode = (String) secondsDisplayModeSpinner.getSelectedItem();

        String secondText = NumberToWords.convertSecond(second, secondsAsWords);
        if ("Вертикально".equals(secondsDisplayMode)) {
            if (secondsAsWords) {
                String[] secondLines = NumberToWords.convertSecondVertical(second, true);
                secondText = android.text.TextUtils.join("\n", secondLines);
            } else {
                String formatted = String.format("%02d", second);
                secondText = formatted.charAt(0) + "\n" + formatted.charAt(1);
            }
        }

        previewHourText.setText(hourText);
        previewDayNightText.setText(dayNightText);
        previewMinuteText.setText(minuteText);

        if (showDayOfWeekCheckbox.isChecked()) {
            previewDayOfWeekText.setVisibility(View.VISIBLE);
            previewDayOfWeekText.setText(dayOfWeekText);
        } else {
            previewDayOfWeekText.setVisibility(View.GONE);
        }

        if (showDateCheckbox.isChecked()) {
            previewDateText.setVisibility(View.VISIBLE);
            previewDateText.setText(dateText);
        } else {
            previewDateText.setVisibility(View.GONE);
        }

        if (showSecondsCheckbox.isChecked()) {
            previewSecondText.setVisibility(View.VISIBLE);
            previewSecondText.setText(secondText);
            previewSecondText.setTextSize(secondFontSizeSeekBar.getProgress());
        } else {
            previewSecondText.setVisibility(View.GONE);
        }

        int textColor = getColorFromSpinner(colorSpinner);
        int blockBackgroundColor = getColorFromSpinner(blockBackgroundColorSpinner);
        int blockBorderColor = getColorFromSpinner(blockBorderColorSpinner);

        boolean blockModeEnabled = "Блочная система".equals(blockModeSpinner.getSelectedItem());

        applyBlockStyle(previewHourText, blockModeEnabled, blockBackgroundColor, blockBorderColor);
        applyBlockStyle(previewMinuteText, blockModeEnabled, blockBackgroundColor, blockBorderColor);
        applyBlockStyle(previewSecondText, blockModeEnabled, blockBackgroundColor, blockBorderColor);
        applyBlockStyle(previewDayNightText, blockModeEnabled, blockBackgroundColor, blockBorderColor);
        applyBlockStyle(previewDayOfWeekText, blockModeEnabled, blockBackgroundColor, blockBorderColor);
        applyBlockStyle(previewDateText, blockModeEnabled, blockBackgroundColor, blockBorderColor);
        float fontSize = fontSizeSeekBar.getProgress();
        int backgroundColor = getColorFromSpinner(backgroundColorSpinner);
        int backgroundAlpha = backgroundAlphaSeekBar.getProgress();
        int bgColor = Color.argb(backgroundAlpha, Color.red(backgroundColor), Color.green(backgroundColor), Color.blue(backgroundColor));

        previewHourText.setTextColor(textColor);
        previewHourText.setTextSize(fontSize);
        previewMinuteText.setTextColor(textColor);
        previewMinuteText.setTextSize(fontSize);
        previewDayNightText.setTextColor(getColorFromSpinner(borderColorSpinner));
        previewDayOfWeekText.setTextColor(textColor);
        previewDateText.setTextColor(textColor);
        previewSecondText.setTextColor(textColor);

        findViewById(R.id.widget_preview_container).setBackgroundColor(bgColor);
    }

    private void applyBlockStyle(TextView view, boolean enabled, int bgColor, int borderColor) {
        if (view == null) return;
        if (enabled) {
            view.setBackgroundColor(bgColor);
            view.setTextColor(borderColor);
            view.setPadding(12, 8, 12, 8);
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setTextColor(getColorFromSpinner(colorSpinner));
            view.setPadding(0, 0, 0, 0);
        }
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

        String secondsDisplayMode = (String) secondsDisplayModeSpinner.getSelectedItem();
        WidgetPreferences.saveSecondsDisplayMode(this, appWidgetId, secondsDisplayMode);

        String blockMode = (String) blockModeSpinner.getSelectedItem();
        WidgetPreferences.saveBlockMode(this, appWidgetId, blockMode);

        int blockBackgroundColor = getColorFromSpinner(blockBackgroundColorSpinner);
        WidgetPreferences.saveBlockBackgroundColor(this, appWidgetId, blockBackgroundColor);

        int blockBorderColor = getColorFromSpinner(blockBorderColorSpinner);
        WidgetPreferences.saveBlockBorderColor(this, appWidgetId, blockBorderColor);

        // Convert bounded progress values back to real offsets
        int minOffset = WidgetPreferences.getMinOffset();
        
        int hourOffsetX = WidgetPreferences.constrainOffset(hourOffsetXSeekBar.getProgress() + minOffset);
        int hourOffsetY = WidgetPreferences.constrainOffset(hourOffsetYSeekBar.getProgress() + minOffset);
        int minuteOffsetX = WidgetPreferences.constrainOffset(minuteOffsetXSeekBar.getProgress() + minOffset);
        int minuteOffsetY = WidgetPreferences.constrainOffset(minuteOffsetYSeekBar.getProgress() + minOffset);
        int secondOffsetX = WidgetPreferences.constrainOffset(secondOffsetXSeekBar.getProgress() + minOffset);
        int secondOffsetY = WidgetPreferences.constrainOffset(secondOffsetYSeekBar.getProgress() + minOffset);
        int dateOffsetX = WidgetPreferences.constrainOffset(dateOffsetXSeekBar.getProgress() + minOffset);
        int dateOffsetY = WidgetPreferences.constrainOffset(dateOffsetYSeekBar.getProgress() + minOffset);
        int dayOfWeekOffsetX = WidgetPreferences.constrainOffset(dayOfWeekOffsetXSeekBar.getProgress() + minOffset);
        int dayOfWeekOffsetY = WidgetPreferences.constrainOffset(dayOfWeekOffsetYSeekBar.getProgress() + minOffset);
        int dayNightOffsetX = WidgetPreferences.constrainOffset(dayNightOffsetXSeekBar.getProgress() + minOffset);
        int dayNightOffsetY = WidgetPreferences.constrainOffset(dayNightOffsetYSeekBar.getProgress() + minOffset);

        WidgetPreferences.saveOffsetX(this, appWidgetId, "hour", hourOffsetX);
        WidgetPreferences.saveOffsetY(this, appWidgetId, "hour", hourOffsetY);
        WidgetPreferences.saveOffsetX(this, appWidgetId, "minute", minuteOffsetX);
        WidgetPreferences.saveOffsetY(this, appWidgetId, "minute", minuteOffsetY);

        WidgetPreferences.saveShowSeconds(this, appWidgetId, showSecondsCheckbox.isChecked());
        WidgetPreferences.saveShowDate(this, appWidgetId, showDateCheckbox.isChecked());
        WidgetPreferences.saveShowDayOfWeek(this, appWidgetId, showDayOfWeekCheckbox.isChecked());
        WidgetPreferences.saveUse12HourFormat(this, appWidgetId, use12HourCheckbox.isChecked());
        WidgetPreferences.saveSecondsAsWords(this, appWidgetId, secondsAsWordsCheckbox.isChecked());

        WidgetPreferences.saveMinuteFontSize(this, appWidgetId, minuteFontSizeSeekBar.getProgress());
        WidgetPreferences.saveSecondFontSize(this, appWidgetId, secondFontSizeSeekBar.getProgress());

        WidgetPreferences.saveSecondOffsetX(this, appWidgetId, secondOffsetX);
        WidgetPreferences.saveSecondOffsetY(this, appWidgetId, secondOffsetY);
        WidgetPreferences.saveDateOffsetX(this, appWidgetId, dateOffsetX);
        WidgetPreferences.saveDateOffsetY(this, appWidgetId, dateOffsetY);
        WidgetPreferences.saveDayOfWeekOffsetX(this, appWidgetId, dayOfWeekOffsetX);
        WidgetPreferences.saveDayOfWeekOffsetY(this, appWidgetId, dayOfWeekOffsetY);
        WidgetPreferences.saveDayNightOffsetX(this, appWidgetId, dayNightOffsetX);
        WidgetPreferences.saveDayNightOffsetY(this, appWidgetId, dayNightOffsetY);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);

        // Update the widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        Class<?> providerClass = getProviderClass(style);
        Intent intent = new Intent(this, providerClass);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{appWidgetId});
        sendBroadcast(intent);

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

    private int getStylePosition(String style) {
        switch (style) {
            case "Базовый": return 0;
            case "Горизонтальный": return 1;
            case "Расширенный": return 2;
            case "Кислотный": return 3;
            case "Неоновый": return 4;
            case "Маленький": return 5;
            default: return 0;
        }
    }

    private int getPositionForColor(int color) {
        switch (color) {
            case Color.BLACK: return 0;
            case Color.WHITE: return 1;
            case Color.RED: return 2;
            case Color.GREEN: return 3;
            case Color.BLUE: return 4;
            case Color.YELLOW: return 5;
            case 0xFFFFA500: return 6; // orange
            case 0xFF800080: return 7; // purple
            case 0xFFFFC0CB: return 8; // pink
            case Color.GRAY: return 9;
            default: return 0;
        }
    }

    private Class<?> getProviderClass(String style) {
        switch (style) {
            case "Базовый": return WordClockWidgetProvider.class;
            case "Горизонтальный": return HorizontalWordClockWidgetProvider.class;
            case "Расширенный": return ExtendedWordClockWidgetProvider.class;
            case "Кислотный": return AcidWordClockWidgetProvider.class;
            case "Неоновый": return NeonWordClockWidgetProvider.class;
            case "Маленький": return SmallWordClockWidgetProvider.class;
            default: return WordClockWidgetProvider.class;
        }
    }

    private int findExistingWidgetId() {
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        int[] widgetIds;

        widgetIds = manager.getAppWidgetIds(new ComponentName(this, WordClockWidgetProvider.class));
        if (widgetIds.length > 0) return widgetIds[0];

        widgetIds = manager.getAppWidgetIds(new ComponentName(this, HorizontalWordClockWidgetProvider.class));
        if (widgetIds.length > 0) return widgetIds[0];

        widgetIds = manager.getAppWidgetIds(new ComponentName(this, ExtendedWordClockWidgetProvider.class));
        if (widgetIds.length > 0) return widgetIds[0];

        widgetIds = manager.getAppWidgetIds(new ComponentName(this, AcidWordClockWidgetProvider.class));
        if (widgetIds.length > 0) return widgetIds[0];

        widgetIds = manager.getAppWidgetIds(new ComponentName(this, NeonWordClockWidgetProvider.class));
        if (widgetIds.length > 0) return widgetIds[0];

        widgetIds = manager.getAppWidgetIds(new ComponentName(this, SmallWordClockWidgetProvider.class));
        if (widgetIds.length > 0) return widgetIds[0];

        return AppWidgetManager.INVALID_APPWIDGET_ID;
    }
}
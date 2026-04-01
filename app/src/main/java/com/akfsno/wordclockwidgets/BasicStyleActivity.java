package com.akfsno.wordclockwidgets;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class BasicStyleActivity extends Activity {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private TextView previewHour, previewMinute, previewDayNight, previewDate, previewDayOfWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_style);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        // Initialize preview views
        previewHour = findViewById(R.id.preview_hour);
        previewMinute = findViewById(R.id.preview_minute);
        previewDayNight = findViewById(R.id.preview_day_night);
        previewDate = findViewById(R.id.preview_date);
        previewDayOfWeek = findViewById(R.id.preview_day_of_week);

        // Setup UI elements
        setupBackgroundColor();
        setupBackgroundAlpha();
        setupTextColor();
        setupHourSize();
        setupMinuteSize();

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> saveAndFinish());

        updatePreview();
        updatePreviewText();
    }

    private void setupBackgroundColor() {
        SeekBar seekBar = findViewById(R.id.background_color_seekbar);
        TextView valueText = findViewById(R.id.background_color_value);
        int[] colors = {0xFFFFFFFF, 0xFF000000, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFFFFFF00};
        int current = WidgetPreferences.getBackgroundColor(this, appWidgetId, 0xFFFFFFFF);
        int currentIndex = 0;
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] == current) {
                currentIndex = i;
                break;
            }
        }
        seekBar.setMax(colors.length - 1);
        seekBar.setProgress(currentIndex);
        valueText.setText(getColorName(colors[currentIndex]));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                WidgetPreferences.saveBackgroundColor(BasicStyleActivity.this, appWidgetId, colors[progress]);
                valueText.setText(getColorName(colors[progress]));
                updatePreview();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupBackgroundAlpha() {
        SeekBar seekBar = findViewById(R.id.background_alpha_seekbar);
        TextView valueText = findViewById(R.id.background_alpha_value);
        int current = WidgetPreferences.getBackgroundAlpha(this, appWidgetId, 255);
        seekBar.setMax(255);
        seekBar.setProgress(current);
        valueText.setText(String.valueOf(current));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                WidgetPreferences.saveBackgroundAlpha(BasicStyleActivity.this, appWidgetId, progress);
                valueText.setText(String.valueOf(progress));
                updatePreview();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupTextColor() {
        SeekBar seekBar = findViewById(R.id.text_color_seekbar);
        TextView valueText = findViewById(R.id.text_color_value);
        int[] colors = {0xFF000000, 0xFFFFFFFF, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF};
        int current = WidgetPreferences.getHourTextColor(this, appWidgetId, 0xFF000000);
        int currentIndex = 0;
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] == current) {
                currentIndex = i;
                break;
            }
        }
        seekBar.setMax(colors.length - 1);
        seekBar.setProgress(currentIndex);
        valueText.setText(getColorName(colors[currentIndex]));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int color = colors[progress];
                WidgetPreferences.saveHourTextColor(BasicStyleActivity.this, appWidgetId, color);
                WidgetPreferences.saveMinuteTextColor(BasicStyleActivity.this, appWidgetId, color);
                WidgetPreferences.saveDayNightTextColor(BasicStyleActivity.this, appWidgetId, color);
                WidgetPreferences.saveDateTextColor(BasicStyleActivity.this, appWidgetId, color);
                WidgetPreferences.saveDayOfWeekTextColor(BasicStyleActivity.this, appWidgetId, color);
                valueText.setText(getColorName(color));
                updatePreview();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupHourSize() {
        SeekBar seekBar = findViewById(R.id.hour_size_seekbar);
        TextView valueText = findViewById(R.id.hour_size_value);
        float current = WidgetPreferences.getFontSize(this, appWidgetId, 24f);
        seekBar.setMax(50);
        seekBar.setProgress((int) (current - 10));
        valueText.setText(String.valueOf((int) current));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float size = 10 + progress;
                WidgetPreferences.saveFontSize(BasicStyleActivity.this, appWidgetId, size);
                valueText.setText(String.valueOf((int) size));
                updatePreview();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupMinuteSize() {
        SeekBar seekBar = findViewById(R.id.minute_size_seekbar);
        TextView valueText = findViewById(R.id.minute_size_value);
        float current = WidgetPreferences.getMinuteFontSize(this, appWidgetId, 24f);
        seekBar.setMax(50);
        seekBar.setProgress((int) (current - 10));
        valueText.setText(String.valueOf((int) current));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float size = 10 + progress;
                WidgetPreferences.saveMinuteFontSize(BasicStyleActivity.this, appWidgetId, size);
                valueText.setText(String.valueOf((int) size));
                updatePreview();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updatePreview() {
        int bgColor = WidgetPreferences.getBackgroundColor(this, appWidgetId, 0xFFFFFFFF);
        int alpha = WidgetPreferences.getBackgroundAlpha(this, appWidgetId, 255);
        bgColor = (bgColor & 0x00FFFFFF) | ((alpha & 0xFF) << 24);

        View container = findViewById(R.id.preview_container);
        android.graphics.drawable.Drawable bg = container.getBackground();
        if (bg instanceof android.graphics.drawable.GradientDrawable) {
            android.graphics.drawable.GradientDrawable drawable = (android.graphics.drawable.GradientDrawable) bg.mutate();
            drawable.setColor(bgColor);
            int borderColor = WidgetPreferences.getBorderColor(this, appWidgetId, getResources().getColor(android.R.color.holo_red_dark));
            drawable.setStroke(WidgetPreferences.getBorderWidth(this, appWidgetId, 2), borderColor);
        } else {
            container.setBackgroundColor(bgColor);
        }

        updatePreviewText();
    }

    private void updatePreviewText() {
        Calendar calendar = Calendar.getInstance();
        int hour24 = calendar.get(Calendar.HOUR_OF_DAY);
        boolean use12 = WidgetPreferences.getUse12HourFormat(this, appWidgetId, true);

        String hourText = use12 ? NumberToWords.convertHour(hour24) : NumberToWords.convertHour24(hour24);
        String minuteText = NumberToWords.convertMinute(calendar.get(Calendar.MINUTE), WidgetPreferences.getAddZeroMinute(this, appWidgetId, false));
        String dayNightText = NumberToWords.getDayNight(hour24);
        String dateText = NumberToWords.convertDate(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        String dayOfWeekText = NumberToWords.getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK) - 1);

        previewHour.setText(hourText);
        previewMinute.setText(minuteText);
        previewDayNight.setText(dayNightText);
        previewDate.setText(dateText);
        previewDayOfWeek.setText(dayOfWeekText);

        previewHour.setTextColor(WidgetPreferences.getHourTextColor(this, appWidgetId, getResources().getColor(android.R.color.black)));
        previewMinute.setTextColor(WidgetPreferences.getMinuteTextColor(this, appWidgetId, getResources().getColor(android.R.color.black)));
        previewDayNight.setTextColor(WidgetPreferences.getDayNightTextColor(this, appWidgetId, getResources().getColor(android.R.color.holo_red_dark)));
        previewDate.setTextColor(WidgetPreferences.getDateTextColor(this, appWidgetId, getResources().getColor(android.R.color.black)));
        previewDayOfWeek.setTextColor(WidgetPreferences.getDayOfWeekTextColor(this, appWidgetId, getResources().getColor(android.R.color.black)));

        previewHour.setTextSize(WidgetPreferences.getFontSize(this, appWidgetId, 24f));
        previewMinute.setTextSize(WidgetPreferences.getMinuteFontSize(this, appWidgetId, 24f));

        previewHour.setVisibility(WidgetPreferences.getShowHour(this, appWidgetId, true) ? View.VISIBLE : View.GONE);
        previewMinute.setVisibility(WidgetPreferences.getShowMinute(this, appWidgetId, true) ? View.VISIBLE : View.GONE);
        previewDayNight.setVisibility(WidgetPreferences.getShowDayNight(this, appWidgetId, true) ? View.VISIBLE : View.GONE);
        previewDate.setVisibility(WidgetPreferences.getShowDate(this, appWidgetId, true) ? View.VISIBLE : View.GONE);
        previewDayOfWeek.setVisibility(WidgetPreferences.getShowDayOfWeek(this, appWidgetId, true) ? View.VISIBLE : View.GONE);
    }

    private void saveAndFinish() {
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    private String getColorName(int color) {
        if (color == 0xFFFFFFFF) return "Белый";
        if (color == 0xFF000000) return "Чёрный";
        if (color == 0xFFFF0000) return "Красный";
        if (color == 0xFF00FF00) return "Зелёный";
        if (color == 0xFF0000FF) return "Синий";
        if (color == 0xFFFFFF00) return "Жёлтый";
        return "Польз.";
    }
}
package com.akfsno.wordclockwidgets;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.TypedValue;

import java.util.Calendar;

public class ConstructorActivity extends Activity {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private String widgetProviderClass = WordClockWidgetProvider.class.getName();
    private TextView previewHour, previewMinute, previewDayNight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constructor);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        if (appWidgetManager != null) {
            android.appwidget.AppWidgetProviderInfo info = appWidgetManager.getAppWidgetInfo(appWidgetId);
            if (info != null && info.provider != null) {
                widgetProviderClass = info.provider.getClassName();
            }
        }

        // Initialize preview views
        previewHour = findViewById(R.id.preview_hour);
        previewMinute = findViewById(R.id.preview_minute);
        previewDayNight = findViewById(R.id.preview_day_night);

        // Remove elements that are not used in basic mode from preview
        View previewDate = findViewById(R.id.preview_date);
        View previewDayOfWeek = findViewById(R.id.preview_day_of_week);
        if (previewDate != null) previewDate.setVisibility(View.GONE);
        if (previewDayOfWeek != null) previewDayOfWeek.setVisibility(View.GONE);

        // Remove constructor-specific UI elements (block list and joystick)
        View blockList = findViewById(R.id.block_list);
        if (blockList != null) blockList.setVisibility(View.GONE);

        View joystickContainer = findViewById(R.id.joystick_container);
        if (joystickContainer != null) joystickContainer.setVisibility(View.GONE);

        Button saveButton = findViewById(R.id.save_button);
        if (saveButton != null) {
            saveButton.setOnClickListener(v -> saveAndFinish());
        }

        updatePreview();
        updatePreviewText();
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
        String minuteText = NumberToWords.convertMinute(calendar.get(Calendar.MINUTE), WidgetPreferences.getAddZeroMinute(this, appWidgetId, false), use12);

        if (!use12 && hour24 == 0 && calendar.get(Calendar.MINUTE) == 0) {
            hourText = "двенадцать";
            minuteText = "ноль-ноль";
        }
        String dayNightText = NumberToWords.getDayNight(hour24);

        previewHour.setText(hourText);
        previewMinute.setText(minuteText);
        previewDayNight.setText(dayNightText);

        int textColor = WidgetPreferences.getHourTextColor(this, appWidgetId, getResources().getColor(android.R.color.black));
        previewHour.setTextColor(textColor);
        previewMinute.setTextColor(textColor);
        previewDayNight.setTextColor(WidgetPreferences.getDayNightTextColor(this, appWidgetId, getResources().getColor(android.R.color.holo_red_dark)));

        previewHour.setTextSize(WidgetPreferences.getFontSize(this, appWidgetId, 24f));
        previewMinute.setTextSize(WidgetPreferences.getMinuteFontSize(this, appWidgetId, 24f));
        previewDayNight.setTextSize(WidgetPreferences.getDayNightFontSize(this, appWidgetId, 18f));

        previewHour.setVisibility(View.VISIBLE);
        previewMinute.setVisibility(View.VISIBLE);
        previewDayNight.setVisibility(View.VISIBLE);

        adjustPreviewTextSizes();
    }

    private void adjustPreviewTextSizes() {
        float hourSize = WidgetPreferences.getFontSize(this, appWidgetId, 24f);
        float minuteSize = WidgetPreferences.getMinuteFontSize(this, appWidgetId, 24f);
        float dayNightSize = WidgetPreferences.getDayNightFontSize(this, appWidgetId, 18f);

        boolean showHour = WidgetPreferences.getShowHour(this, appWidgetId, true);
        boolean showMinute = WidgetPreferences.getShowMinute(this, appWidgetId, true);
        boolean showDayNight = WidgetPreferences.getShowDayNight(this, appWidgetId, true);

        float totalPx = 0f;
        int visibleCount = 0;

        if (showHour) {
            totalPx += TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, hourSize, getResources().getDisplayMetrics());
            visibleCount++;
        }
        if (showDayNight) {
            totalPx += TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dayNightSize, getResources().getDisplayMetrics());
            visibleCount++;
        }
        if (showMinute) {
            totalPx += TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, minuteSize, getResources().getDisplayMetrics());
            visibleCount++;
        }

        if (visibleCount > 1) {
            totalPx += (visibleCount - 1) * dpToPx(4);
        }

        float availablePx = getPreviewContainerAvailableHeightPx();

        float scale = 1f;
        if (totalPx > 0 && totalPx > availablePx) {
            scale = availablePx / totalPx;
        }

        if (showHour) {
            previewHour.setTextSize(TypedValue.COMPLEX_UNIT_SP, hourSize * scale);
        }
        if (showDayNight) {
            previewDayNight.setTextSize(TypedValue.COMPLEX_UNIT_SP, dayNightSize * scale);
        }
        if (showMinute) {
            previewMinute.setTextSize(TypedValue.COMPLEX_UNIT_SP, minuteSize * scale);
        }
    }

    private float getPreviewContainerAvailableHeightPx() {
        View container = findViewById(R.id.preview_container);
        int heightPx = (container != null && container.getHeight() > 0) ? container.getHeight() : dpToPx(150);
        int padding = container != null ? container.getPaddingTop() + container.getPaddingBottom() : dpToPx(16);
        return Math.max(0, heightPx - padding - dpToPx(16));
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void updateWidget() {
        Intent intent = new Intent();
        try {
            intent.setComponent(new android.content.ComponentName(this, widgetProviderClass));
        } catch (Exception e) {
            intent.setComponent(new android.content.ComponentName(this, WordClockWidgetProvider.class));
        }
        intent.setAction(BaseWordClockWidgetProvider.UPDATE_ACTION);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        sendBroadcast(intent);
    }

    private void saveAndFinish() {
        WidgetPreferences.saveUseConstructorLayout(this, appWidgetId, false);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}

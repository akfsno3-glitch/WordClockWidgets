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
    private View hourWrapper, minuteWrapper, dayNightWrapper, dateWrapper, dayOfWeekWrapper;
    private TextView previewHour, previewMinute, previewDayNight, previewDate, previewDayOfWeek;

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

        // Initialize preview views (real widget layout)
        hourWrapper = findViewById(R.id.hour_wrapper);
        minuteWrapper = findViewById(R.id.minute_wrapper);
        dayNightWrapper = findViewById(R.id.day_night_wrapper);
        dateWrapper = findViewById(R.id.date_wrapper);
        dayOfWeekWrapper = findViewById(R.id.day_of_week_wrapper);

        previewHour = findViewById(R.id.hour_text);
        previewMinute = findViewById(R.id.minute_text);
        previewDayNight = findViewById(R.id.day_night_text);
        previewDate = findViewById(R.id.date_text);
        previewDayOfWeek = findViewById(R.id.day_of_week_text);

        // Keep behavior for basic mode: show/hide date and day-of-week according to preferences
        boolean showDate = WidgetPreferences.getShowDate(this, appWidgetId, false);
        boolean showDayOfWeek = WidgetPreferences.getShowDayOfWeek(this, appWidgetId, false);
        if (previewDate != null) previewDate.setVisibility(showDate ? View.VISIBLE : View.GONE);
        if (previewDayOfWeek != null) previewDayOfWeek.setVisibility(showDayOfWeek ? View.VISIBLE : View.GONE);

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
        BaseWordClockWidgetProvider.updateLocalWidgetView(this, findViewById(R.id.preview_container), appWidgetId);
    }

    private void updatePreviewText() {
        updatePreview();
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

        TextView hourView = findViewById(R.id.hour_text);
        TextView minuteView = findViewById(R.id.minute_text);
        TextView dayNightView = findViewById(R.id.day_night_text);

        if (showHour && hourView != null) {
            hourView.setTextSize(TypedValue.COMPLEX_UNIT_SP, hourSize * scale);
        }
        if (showDayNight && dayNightView != null) {
            dayNightView.setTextSize(TypedValue.COMPLEX_UNIT_SP, dayNightSize * scale);
        }
        if (showMinute && minuteView != null) {
            minuteView.setTextSize(TypedValue.COMPLEX_UNIT_SP, minuteSize * scale);
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

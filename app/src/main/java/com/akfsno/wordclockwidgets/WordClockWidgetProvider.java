package com.akfsno.wordclockwidgets;

import android.content.Context;
import android.widget.RemoteViews;

public class WordClockWidgetProvider extends BaseWordClockWidgetProvider {

    @Override
    protected int getLayoutResource(Context context, int appWidgetId) {
        String style = WidgetPreferences.getStyle(context, appWidgetId, "Базовый");
        switch (style) {
            case "Базовый": return R.layout.widget_layout;
            case "Горизонтальный": return R.layout.horizontal_widget_layout;
            case "Расширенный": return R.layout.extended_widget_layout;
            case "Кислотный": return R.layout.widget_layout_acid;
            case "Неоновый": return R.layout.widget_layout_neon;
            default: return R.layout.widget_layout;
        }
    }

    @Override
    protected void setTexts(RemoteViews views, String hourText, String minuteText, String dayNightText, String dayOfWeekText, String dateText) {
        // Implementation depends on layout, but for simplicity, assume standard
        views.setTextViewText(R.id.hour_text, hourText);
        views.setTextViewText(R.id.day_night_text, dayNightText);
        views.setTextViewText(R.id.minute_text, minuteText);
    }

    @Override
    protected int getDefaultTextColor() {
        return android.graphics.Color.BLACK;
    }

    @Override
    protected int getDefaultBorderColor() {
        return android.graphics.Color.RED;
    }
}
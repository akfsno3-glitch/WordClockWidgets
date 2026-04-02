package com.akfsno.wordclockwidgets;

import android.content.Context;
import android.widget.RemoteViews;

public class WordClockWidgetProvider extends BaseWordClockWidgetProvider {

    @Override
    protected int getLayoutResource(Context context, int appWidgetId) {
        boolean useConstructorLayout = WidgetPreferences.getUseConstructorLayout(context, appWidgetId, false);
        return useConstructorLayout ? R.layout.widget_layout : R.layout.widget_layout_basic;
    }

    @Override
    protected void setTexts(RemoteViews views, String hourText, String minuteText, String dayNightText, String dayOfWeekText, String dateText) {
        views.setTextViewText(R.id.hour_text, hourText);
        views.setTextViewText(R.id.minute_text, minuteText);
        views.setTextViewText(R.id.day_night_text, dayNightText);
        views.setTextViewText(R.id.day_of_week_text, dayOfWeekText);
        views.setTextViewText(R.id.date_text, dateText);
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
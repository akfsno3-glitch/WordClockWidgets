// License Statement
// ...
// other import statements...

// The rest of the class...

@Override
public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    // Other update code...
    // Removed scheduleNextTick(context) call from here
}

@Override
public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);
    if (/* condition for tick */) {
        scheduleNextTick(context); // Keep only here
    }
}

@Override
public void onEnabled(Context context) {
    super.onEnabled(context);
    scheduleNextTick(context); // Keep only here as well
}
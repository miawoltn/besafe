package com.miawoltn.emergencydispatch.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.miawoltn.emergencydispatch.R;
import com.miawoltn.emergencydispatch.service.WidgetService;
import com.miawoltn.emergencydispatch.util.Operations;

/**
 * Implementation of App Widget functionality.
 */
public class BeSafeAppWidget extends AppWidgetProvider {

    public static final String DISASTER_TAG = "disaster";
    public static final String ROBBERY_TAG = "robbery";
    public static final String TERROR_TAG = "terror";
    public static final String MURDER_TAG = "murder";
    public static final String ACCIDENT_TAG = "accident";
    public static final String SUICIDE_TAG = "suicide";
    public static final String FIRE_TAG = "fire";
   // ComponentName componentName;
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

       /* CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.be_safe_app_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);*/

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.be_safe_app_widget);
        ComponentName thisWidget = new ComponentName(context, BeSafeAppWidget.class);
        remoteViews.setOnClickPendingIntent(R.id.w_natural_disaster, getPendingSelfIntent(context, DISASTER_TAG));
        remoteViews.setOnClickPendingIntent(R.id.w_robbery, getPendingSelfIntent(context, ROBBERY_TAG));
        remoteViews.setOnClickPendingIntent(R.id.w_terrorist_attack, getPendingSelfIntent(context, TERROR_TAG));
        remoteViews.setOnClickPendingIntent(R.id.w_murder, getPendingSelfIntent(context, MURDER_TAG));
        remoteViews.setOnClickPendingIntent(R.id.w_accident, getPendingSelfIntent(context, ACCIDENT_TAG));
        remoteViews.setOnClickPendingIntent(R.id.w_suicide, getPendingSelfIntent(context, SUICIDE_TAG));
        remoteViews.setOnClickPendingIntent(R.id.w_fire, getPendingSelfIntent(context, FIRE_TAG));
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }


       /* Intent intent =  Operations.createIntent(context, WidgetService.class, new Bundle());
        Operations.startService(context, intent);*/
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, WidgetService.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}


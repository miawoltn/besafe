package com.miawoltn.emergencydispatch.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.miawoltn.emergencydispatch.core.SOSDispatcher;
import com.miawoltn.emergencydispatch.widget.BeSafeAppWidget;

public class WidgetService extends Service {

    SOSDispatcher sosDispatcher;
    public WidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sosDispatcher = new SOSDispatcher(getApplicationContext());
        switch (intent.getAction())
        {
            case BeSafeAppWidget.ACCIDENT_TAG:
                sosDispatcher.Dispatch(SOSDispatcher.DistressType.Accident);
                break;
            case BeSafeAppWidget.DISASTER_TAG:
                sosDispatcher.Dispatch(SOSDispatcher.DistressType.Natural_Disaster);
                break;
            case BeSafeAppWidget.FIRE_TAG:
                sosDispatcher.Dispatch(SOSDispatcher.DistressType.Fire);
                break;
            case BeSafeAppWidget.MURDER_TAG:
                sosDispatcher.Dispatch(SOSDispatcher.DistressType.Murder);
                break;
            case BeSafeAppWidget.ROBBERY_TAG:
                sosDispatcher.Dispatch(SOSDispatcher.DistressType.Robbery);
                break;
            case BeSafeAppWidget.SUICIDE_TAG:
                sosDispatcher.Dispatch(SOSDispatcher.DistressType.Suicide);
                break;
            case BeSafeAppWidget.TERROR_TAG:
                sosDispatcher.Dispatch(SOSDispatcher.DistressType.Terror_Attack);
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }
}

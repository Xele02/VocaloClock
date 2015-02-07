package org.xele.vocaloclock.vocaloclock;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;


public final class ClockUpdateService extends Service
{
    private BroadcastReceiver m_BroadcastReceiver = null;
    private static IntentFilter m_sIntentFilter = null;

    static {
        (m_sIntentFilter = new IntentFilter()).addAction("android.intent.action.TIME_TICK");
        m_sIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        m_sIntentFilter.addAction("android.intent.action.TIME_SET");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        m_BroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                update();
            }
        };

        registerReceiver(m_BroadcastReceiver, m_sIntentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        update();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(m_BroadcastReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void update()
    {
        Date now = new Date();
        String hour = new SimpleDateFormat("H:m").format(now);
        String date = new SimpleDateFormat("EEEE").format(now);
        String date2 = new SimpleDateFormat("d MMMM").format(now);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
                .getApplicationContext());
        ComponentName thisWidget = new ComponentName(getApplicationContext(),
                ClockWidget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(this
                    .getApplicationContext().getPackageName(),
                    R.layout.clock_widget);
            remoteViews.setTextViewText(R.id.hour, hour);
            remoteViews.setTextViewText(R.id.date, date);
            remoteViews.setTextViewText(R.id.date2, date2);
            remoteViews.setImageViewResource(R.id.icon, R.drawable.a01+(int)(1000.0 * Math.random() % 15.0));

            appWidgetManager.updateAppWidget(new ComponentName(this
                    .getApplicationContext(), (Class) ClockWidget.class), remoteViews);
        }
    }

}

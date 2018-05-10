package com.edotassi.amazmodcompanionservice;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.edotassi.amazmodcompanionservice.events.NightscoutDataEvent;
import com.edotassi.amazmodcompanionservice.events.NightscoutRequestSyncEvent;
import com.edotassi.amazmodcompanionservice.notifications.NotificationService;
import com.edotassi.amazmodcompanionservice.notifications.NotificationSpec;
import com.edotassi.amazmodcompanionservice.notifications.NotificationSpecFactory;
import com.edotassi.amazmodcompanionservice.notifications.NotificationsReceiver;
import com.huami.watch.transport.DataBundle;
import com.huami.watch.transport.TransportDataItem;
import com.huami.watch.transport.Transporter;
import com.huami.watch.transport.TransporterClassic;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import xiaofei.library.hermeseventbus.HermesEventBus;

/**
 * Created by edoardotassinari on 04/04/18.
 */

public class MainService extends Service implements Transporter.ChannelListener, Transporter.ServiceConnectionListener {

    private Transporter companionTransporter;
    private Transporter notificationsTransporter;

    private NotificationsReceiver notificationsReceiver;

    private NotificationService notificationManager;

    private Map<String, Class> messages = new HashMap<String, Class>() {{
        put(Constants.ACTION_NIGHTSCOUT_SYNC, NightscoutDataEvent.class);
    }};

    @Override
    public void onCreate() {
        Log.d(Constants.TAG, "EventBus init");

        HermesEventBus.getDefault().init(this);
        HermesEventBus.getDefault().register(this);

        notificationManager = new NotificationService(this);

        notificationsReceiver = new NotificationsReceiver();

        /*
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("reply");
        LocalBroadcastManager.getInstance(this).registerReceiver(notificationsReceiver, intentFilter);
        */
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Constants.TAG, "MainService started");

        if (companionTransporter == null) {
            initTransporter();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onChannelChanged(boolean b) {
    }

    private void initTransporter() {
        companionTransporter = TransporterClassic.get(this, Constants.TRANSPORTER_MODULE);
        companionTransporter.addChannelListener(this);
        companionTransporter.addServiceConnectionListener(this);
        companionTransporter.addDataListener(new Transporter.DataListener() {
            @Override
            public void onDataReceived(TransportDataItem transportDataItem) {
                String action = transportDataItem.getAction();

                Log.d(Constants.TAG, "action: " + action + ", module: " + transportDataItem.getModuleName());

                if (action == null) {
                    return;
                }

                Class messageClass = messages.get(action);

                if (messageClass != null) {
                    Class[] args = new Class[1];
                    args[0] = DataBundle.class;

                    try {
                        Constructor eventContructor = messageClass.getDeclaredConstructor(args);
                        Object event = eventContructor.newInstance(transportDataItem.getData());

                        Log.d(Constants.TAG, "posting event " + event.toString());
                        HermesEventBus.getDefault().post(event);
                    } catch (NoSuchMethodException e) {
                        Log.w(Constants.TAG, "event mapped with action \"" + action + "\" doesn't have constructor with DataBundle as parameter");
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        if (!companionTransporter.isTransportServiceConnected()) {
            Log.d(Constants.TAG, "connecting companionTransporter to transportService");
            companionTransporter.connectTransportService();
        }

        notificationsTransporter = TransporterClassic.get(this, Constants.TRANSPORTER_MODULE_NOTIFICATIONS);
        notificationsTransporter.addChannelListener(this);
        notificationsTransporter.addServiceConnectionListener(this);
        notificationsTransporter.addDataListener(new Transporter.DataListener() {
            @Override
            public void onDataReceived(TransportDataItem transportDataItem) {
                String action = transportDataItem.getAction();
                Log.d(Constants.TAG, "action: " + action + ", module: " + transportDataItem.getModuleName());

                if ((action != null) && (action.equals("add"))) {
                    NotificationSpec notificationSpec = NotificationSpecFactory.getNotificationSpec(MainService.this, transportDataItem);
                    if (notificationSpec != null) {
                        notificationManager.post(notificationSpec);
                    } else {
                        //TODO warn about notification null
                    }
                }
            }
        });

        if (!notificationsTransporter.isTransportServiceConnected()) {
            notificationsTransporter.connectTransportService();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestNightscoutSync(NightscoutRequestSyncEvent event) {
        Log.d(Constants.TAG, "requested nightscout sync");

        companionTransporter.send(Constants.ACTION_NIGHTSCOUT_SYNC, new DataBundle());
    }

    @Override
    public void onServiceConnected(Bundle bundle) {
    }

    @Override
    public void onServiceConnectionFailed(Transporter.ConnectionResult connectionResult) {
    }

    @Override
    public void onServiceDisconnected(Transporter.ConnectionResult connectionResult) {
    }
}

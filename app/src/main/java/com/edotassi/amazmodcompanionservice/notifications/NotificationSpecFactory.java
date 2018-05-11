package com.edotassi.amazmodcompanionservice.notifications;

import android.content.Context;
import android.os.Bundle;

import com.edotassi.amazmodcompanionservice.util.DeviceUtil;
import com.huami.watch.notification.data.NotificationData;
import com.huami.watch.notification.data.StatusBarNotificationData;
import com.huami.watch.transport.DataBundle;
import com.huami.watch.transport.TransportDataItem;

public class NotificationSpecFactory {

    public static NotificationSpec getNotificationSpec(Context context, TransportDataItem transportDataItem) {
        DataBundle dataBundle = transportDataItem.getData();
        if (dataBundle == null) {
            return null;
        }

        StatusBarNotificationData statusBarNotificationData = dataBundle.getParcelable("data");
        if (statusBarNotificationData == null) {
            return null;
        }

        NotificationSpec notificationSpec = extractDataStatusBarNotificationData(statusBarNotificationData);
        if (notificationSpec == null) {
            return null;
        }

        notificationSpec.setVibration(100);
        notificationSpec.setTimeoutRelock(15 * 1000);
        notificationSpec.setDeviceLocked(DeviceUtil.isDeviceLocked(context));
        notificationSpec.setEnableCutomUI(dataBundle.getBoolean("enableCustomUI", true));

        return notificationSpec;
    }

    private static NotificationSpec extractDataStatusBarNotificationData(StatusBarNotificationData statusBarNotificationData) {
        NotificationData notificationData = statusBarNotificationData.notification;
        if (notificationData == null) {
            return null;
        }

        NotificationSpec notificationSpec = new NotificationSpec();


        notificationSpec.setTitle(notificationData.title);
        notificationSpec.setText(notificationData.text);
        notificationSpec.setIcon(notificationData.smallIcon);
        notificationSpec.setId(statusBarNotificationData.id);

        return notificationSpec;
    }

    public static Bundle toBundle(NotificationSpec notificationSpec) {
        return toBundle(new Bundle(), notificationSpec);
    }

    public static Bundle toBundle(Bundle bundle, NotificationSpec notificationSpec) {
        bundle.putParcelable("notificationSpec", notificationSpec);
        return bundle;
    }
}

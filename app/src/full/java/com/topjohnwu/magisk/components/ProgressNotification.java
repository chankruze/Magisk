package com.topjohnwu.magisk.components;

import android.app.Notification;

import com.topjohnwu.core.App;
import com.topjohnwu.core.utils.Utils;
import com.topjohnwu.magisk.R;
import com.topjohnwu.net.DownloadProgressListener;

import androidx.core.app.NotificationCompat;

import com.sdsmdg.tastytoast.TastyToast;

public class ProgressNotification implements DownloadProgressListener {

    private NotificationCompat.Builder builder;
    private Notification notification;
    private long prevTime;

    public ProgressNotification(String title) {
        builder = Notifications.progress(title);
        prevTime = System.currentTimeMillis();
        update();
	TastyToast.makeText(App.self.getApplicationContext(), App.self.getString(R.string.downloading_toast, title), TastyToast.LENGTH_LONG, TastyToast.INFO);
    }

    @Override
    public void onProgress(long bytesDownloaded, long totalBytes) {
        long cur = System.currentTimeMillis();
        if (cur - prevTime >= 1000) {
            prevTime = cur;
            int progress = (int) (bytesDownloaded * 100 / totalBytes);
            builder.setProgress(100, progress, false);
            builder.setContentText(progress + "%");
            update();
        }
    }

    public NotificationCompat.Builder getNotificationBuilder() {
        return builder;
    }

    public Notification getNotification() {
        return notification;
    }

    public void update() {
        notification = builder.build();
        Notifications.mgr.notify(hashCode(), notification);
    }

    private void lastUpdate() {
        notification = builder.build();
        Notifications.mgr.cancel(hashCode());
        Notifications.mgr.notify(notification.hashCode(), notification);
    }

    public void dlDone() {
        builder.setProgress(0, 0, false)
                .setContentText(App.self.getString(R.string.download_complete))
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setOngoing(false);
        lastUpdate();
    }

    public void dlFail() {
        builder.setProgress(0, 0, false)
                .setContentText(App.self.getString(R.string.download_file_error))
                .setSmallIcon(android.R.drawable.stat_notify_error)
                .setOngoing(false);
        lastUpdate();
    }

    public void dismiss() {
        Notifications.mgr.cancel(hashCode());
    }
}

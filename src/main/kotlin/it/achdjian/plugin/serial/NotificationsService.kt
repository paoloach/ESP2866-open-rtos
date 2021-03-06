package it.achdjian.plugin.serial

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType

object NotificationsService {
    private var notificationGroup = NotificationGroup.toolWindowGroup("Serial Monitor Notification", "Serial Monitor")

    fun createNotification(content: String, type: NotificationType): Notification {
        return notificationGroup.createNotification(content, type)
    }

    fun createErrorNotification(content: String): Notification {
        return notificationGroup.createNotification(content, NotificationType.ERROR)
    }
}
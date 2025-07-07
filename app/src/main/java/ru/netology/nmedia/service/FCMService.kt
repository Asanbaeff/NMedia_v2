package ru.netology.nmedia.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import kotlin.random.Random


class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("ServiceCast")
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("fcm_token", token)
        Log.d("FCM", "Token: $token")
        println(token)

//        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//        val clip = ClipData.newPlainText("FCM Token", token)
//        clipboard.setPrimaryClip(clip)
    }


    override fun onMessageReceived(message: RemoteMessage) {

        Log.i("fcm_message", message.data.toString())
        println(Gson().toJson(message))

        message.data[action]?.let { actionString ->
            val action = try {
                Action.valueOf(actionString)
            } catch (e: IllegalArgumentException) {
                Action.UNKNOWN
            }

            when (action) {
                Action.LIKE -> handleLike(gson.fromJson(message.data[content], Like::class.java))
                Action.SHARE -> "Share_sss"
                Action.COMMENT -> "Comment"
                Action.UNKNOWN -> Log.w("fcm_message", "Unknown action received: $actionString")
            }
        }
    }

    private fun handleLike(content: Like) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                    content.userName,
                    content.postAuthor,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notify(notification)
    }

    private fun notify(notification: Notification) {
        if (
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this)
                .notify(Random.nextInt(10_000_000), notification)
        }
    }
}

enum class Action {
    LIKE, SHARE, COMMENT, UNKNOWN
    //LIKE, UNKNOWN
}

data class Like(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
)


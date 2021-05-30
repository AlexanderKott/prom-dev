package ru.netology.nmedia.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.auth.RecipientInfo
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {
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

    override fun onMessageReceived(message: RemoteMessage) {
        Log.e("tokenn content", message.data["content"] ?: "null >")

        val msg = gson.fromJson(message.data["content"] ,RecipientInfo::class.java)
        val myid =  AppAuth.getInstance().authStateFlow.value.id

        if (msg.recipientId == null){
            //show notify
            Notificator.notificate(this, channelId, msg.content)
        }

        if (msg.recipientId == myid.toString()){
            //show notify
            Notificator.notificate(this, channelId, msg.content)
        }

        if (msg.recipientId == "0" && msg.recipientId != myid.toString()){
            //send again
            AppAuth.getInstance().sendPushToken()
        }

        if (msg.recipientId != "0" && msg.recipientId != myid.toString()){
            //send again
            AppAuth.getInstance().sendPushToken()
        }

    }

    override fun onNewToken(token: String) {
        AppAuth.getInstance().sendPushToken(token)
        Log.e("tokenn onNewToken","onNewToken")
    }
}

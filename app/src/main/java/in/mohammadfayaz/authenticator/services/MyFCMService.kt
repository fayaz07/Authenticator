package `in`.mohammadfayaz.authenticator.services

import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFCMService : FirebaseMessagingService() {
  override fun getStartCommandIntent(p0: Intent): Intent {
    return super.getStartCommandIntent(p0)
  }

  override fun handleIntent(intent: Intent) {
    super.handleIntent(intent)
  }

  override fun onDeletedMessages() {
    super.onDeletedMessages()
  }

  override fun onMessageReceived(p0: RemoteMessage) {
    super.onMessageReceived(p0)
    // I don't want to show notification when the app is turned on
  }

  override fun onMessageSent(p0: String) {
    super.onMessageSent(p0)
  }

  override fun onNewToken(p0: String) {
    super.onNewToken(p0)
  }

  override fun onSendError(p0: String, p1: Exception) {
    super.onSendError(p0, p1)
  }
}
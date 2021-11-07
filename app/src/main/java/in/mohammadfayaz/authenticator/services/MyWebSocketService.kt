package `in`.mohammadfayaz.authenticator.services

import `in`.mohammadfayaz.authenticator.network.MyWebSocketClient
import `in`.mohammadfayaz.authenticator.utils.EventData
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LiveData
import java.net.URI

class MyWebSocketService : Service() {

  private val url = "ws://10.0.2.2:8080/"
  private val socketClient: MyWebSocketClient = MyWebSocketClient(URI(url))

  val TAG = "MyService"
  val mBinder = MyBinder()

  override fun onCreate() {
    println("oncreate service")
    super.onCreate()
  }

  fun connectToSocket() {
    //open websocket
    socketClient.connect()
  }

  override fun onDestroy() {
    super.onDestroy()
    socketClient.close()
  }

  fun getEventListener(): LiveData<EventData> {
    return socketClient.events
  }

  override fun onBind(intent: Intent): IBinder {
    return mBinder
  }

  class MyBinder : Binder() {
    private var instance: MyWebSocketService? = null
    public fun getService(): MyWebSocketService {
      if (instance == null)
        instance = MyWebSocketService()
      return instance!!
    }
  }

  fun sendFCMTokenAndClientId(fcmToken: String, clientId: String) {
    socketClient.sayHelloToServer(fcmToken, clientId)
  }

}
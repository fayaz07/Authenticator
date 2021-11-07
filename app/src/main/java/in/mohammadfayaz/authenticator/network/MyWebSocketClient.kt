package `in`.mohammadfayaz.authenticator.network

import `in`.mohammadfayaz.authenticator.utils.EventData
import `in`.mohammadfayaz.authenticator.utils.EventTypes
import `in`.mohammadfayaz.authenticator.utils.RequestTypes
import `in`.mohammadfayaz.authenticator.utils.ResponseTypes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI


class MyWebSocketClient(url: URI) : WebSocketClient(url) {

  private val _events = MutableLiveData<EventData>()
  val events: LiveData<EventData> = _events

  override fun onMessage(message: String?) {
    if (message != null){
      val obj = JSONObject(message)
      if (obj.getString("type").toString() != null) {
        when (obj.getString("type")) {
          ResponseTypes.RECEIVED_OTP -> {
            _events.postValue(EventData(obj.get("data").toString(), EventTypes.RECEIVED_OTP))
          }
        }
      }
    }
  }

  override fun onOpen(handshake: ServerHandshake?) {
    _events.postValue(EventData("", EventTypes.CONNECTED_TO_SOCKET))
    println("opened connection")
  }

  override fun onClose(code: Int, reason: String?, remote: Boolean) {
    _events.postValue(EventData("", EventTypes.DISCONNECTED_FROM_SOCKET))
    println("closed connection")
  }

  override fun onError(ex: Exception) {
    ex.printStackTrace()
  }

  fun sayHelloToServer(fcmToken: String, clientId: String) {
    val jsonObject = JSONObject()
    jsonObject.put("type", RequestTypes.SEND_FCM_TOKEN)
    jsonObject.put("client", "mobile")
    jsonObject.put("clientId", clientId)
    jsonObject.put("fcmToken", fcmToken)
    this.send(jsonObject.toString())
  }

}
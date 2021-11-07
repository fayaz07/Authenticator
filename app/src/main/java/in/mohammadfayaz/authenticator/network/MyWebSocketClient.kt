package `in`.mohammadfayaz.authenticator

import `in`.mohammadfayaz.authenticator.utils.EventData
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI

object RequestTypes {

}

object ResponseTypes {
  const val RECEIVED_OTP = "sent_otp"
}

object EventTypes {
  const val CONNECTED_TO_SOCKET = 1
  const val DISCONNECTED_FROM_SOCKET = 2
  const val RECEIVED_OTP = 4
}

class MyWebSocketClient(url: URI) : WebSocketClient(url) {

  private val _events = MutableLiveData<EventData>()
  val events: LiveData<EventData> = _events

  override fun onMessage(message: String?) {
    val obj = JSONObject(message)
    if(obj.getString("type").toString() != null){
      when(obj.getString("type")){
        ResponseTypes.RECEIVED_OTP ->{
          _events.postValue(EventData(obj.get("data").toString(),EventTypes.RECEIVED_OTP))
        }
      }
    }
  }

  override fun onOpen(handshake: ServerHandshake?) {
    val jsonObject = JSONObject()
    jsonObject.put("type", "hello")
    jsonObject.put("client", "mobile")
    jsonObject.put("id", "test")
    this.send(jsonObject.toString())
    _events.postValue(EventData("",EventTypes.CONNECTED_TO_SOCKET))
    println("opened connection")
  }

  override fun onClose(code: Int, reason: String?, remote: Boolean) {
    _events.postValue(EventData("",EventTypes.DISCONNECTED_FROM_SOCKET))
    println("closed connection")
  }

  override fun onError(ex: Exception) {
    ex.printStackTrace()
  }

}
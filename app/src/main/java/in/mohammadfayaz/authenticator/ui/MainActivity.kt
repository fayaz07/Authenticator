package `in`.mohammadfayaz.authenticator.ui

import `in`.mohammadfayaz.authenticator.databinding.ActivityMainBinding
import `in`.mohammadfayaz.authenticator.services.MyWebSocketService
import `in`.mohammadfayaz.authenticator.utils.EventData
import `in`.mohammadfayaz.authenticator.utils.EventTypes
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding

  // service code
  var serviceBinder: IBinder? = null

  private var serviceConnection: ServiceConnection = object : ServiceConnection {
    override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
      serviceBinder = iBinder as (MyWebSocketService.MyBinder)
      (serviceBinder as MyWebSocketService.MyBinder).getService().connectToSocket()
      listenToEvents()
    }

    override fun onServiceDisconnected(componentName: ComponentName) {
      serviceBinder = null
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    registerViewEvents()

    startSocketService()
  }

  private fun registerViewEvents(){

  }

  override fun onResume() {
    super.onResume()
    if (serviceBinder == null) {
      startSocketService()
    }
  }

  override fun onPause() {
    super.onPause()
//        if (serviceBinder != null) {
//            unbindService(serviceConnection)
//        }
  }

  // ---------------- server and notifications
  private fun startSocketService(){
      startService()
      bindService()
  }

  private fun startService() {
    val serviceIntent: Intent = Intent(this, MyWebSocketService::class.java)
    startService(serviceIntent)
  }

  private fun bindService() {
    val serviceIntent: Intent = Intent(this, MyWebSocketService::class.java)
    bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
//        listenToEvents()
  }

  private fun listenToEvents() {

    if (serviceBinder == null) {
      println("Service not running")
      return
    }
    println("listening to events")


    (serviceBinder as MyWebSocketService.MyBinder).getService().getEventListener().observe(
      this,
      {

        handleEvent(it)
      }

    )
  }

  private fun handleEvent(eventData: EventData) {
    when (eventData.event) {

      EventTypes.CONNECTED_TO_SOCKET -> {
//        text.text = "Connected to socket"
//        hideLoader()
        sendFCMTokenAndClientId()
      }

      EventTypes.RECEIVED_OTP -> {
        Log.d("socket", "got an otp here, " + eventData.data)

//        text.text = "Your OTP is " + eventData.data
//        textField.visibility = View.VISIBLE
//        sendOtp.text = "Validate OTP"
//        hideLoader()
      }
    }
  }

  private fun sendFCMTokenAndClientId(){
    FirebaseMessaging.getInstance().token.addOnCompleteListener {
      println(it.result)
    }
  }
}

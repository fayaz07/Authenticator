package `in`.mohammadfayaz.authenticator.ui

import `in`.mohammadfayaz.authenticator.databinding.ActivityMainBinding
import `in`.mohammadfayaz.authenticator.services.MyWebSocketService
import `in`.mohammadfayaz.authenticator.utils.EventData
import `in`.mohammadfayaz.authenticator.utils.EventTypes
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.opengl.Visibility
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding

  // service code
  var serviceBinder: IBinder? = null

  private var serviceConnection: ServiceConnection = object : ServiceConnection {
    override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
      try {
        serviceBinder = iBinder as (MyWebSocketService.MyBinder)
        (serviceBinder as MyWebSocketService.MyBinder).getService().connectToSocket()
        listenToEvents()
      } catch (e: Exception) {
        binding.statusTextView.text =
          "There's something wrong, here is the stacktrace\n" + e.message!!
        e.printStackTrace()
      }
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

  override fun onResume() {
    super.onResume()
    if (serviceBinder == null) {
      unbindService(serviceConnection)
      startSocketService()
    }
  }

  override fun onPause() {
    super.onPause()
    if (serviceBinder != null) {
      unbindService(serviceConnection)
    }
  }

  private fun registerViewEvents() {
    binding.apply {
//      loader will be already in shown state
//      showLoader()

//      initially we will be connecting to the socket
      statusTextView.text = "Hang on, while we connect to the server!"

    }
  }

  private fun showLoader() {
    binding.loader.root.visibility = View.VISIBLE
  }

  private fun hideLoader() {
    binding.loader.root.visibility = View.GONE
  }

  // ---------------- service and notifications
  private fun startSocketService() {
    showLoader()
    val serviceIntent = getServiceIntent()
    startService(serviceIntent)
    bindService(serviceIntent)
  }

  private fun getServiceIntent(): Intent {
    return Intent(this, MyWebSocketService::class.java)
  }

  private fun bindService(serviceIntent: Intent) {
    bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
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
        binding.statusTextView.text = "Connected to server!"
      }

      EventTypes.RECEIVED_OTP -> {
//        Log.d("socket", "got an otp here, " + eventData.data)
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        binding.statusTextView.text = "Here is your Auth code.\nReceived at: $currentDate"
        binding.otpTextView.text = eventData.data
      }

      EventTypes.FCM_ACKNOWLEDGEMENT -> {
        binding.statusTextView.text = "All setup. Go ahead and request for auth code"
        hideLoader()
      }
    }
  }

  private fun sendFCMTokenAndClientId() {
    showLoader()
    binding.statusTextView.text = "Hang on, setting up notification service"
    FirebaseMessaging.getInstance().token.addOnCompleteListener {
      if (it.isSuccessful)
        (serviceBinder as MyWebSocketService.MyBinder).getService()
          .sendFCMTokenAndClientId(it.result!!, "client1")
      else {
        println("Unable to get FCM token")
        (serviceBinder as MyWebSocketService.MyBinder).getService()
          .sendFCMTokenAndClientId("", "client1")
      }
    }
  }
}

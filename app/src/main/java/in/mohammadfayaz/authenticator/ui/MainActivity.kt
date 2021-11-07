package `in`.mohammadfayaz.authenticator.ui

import `in`.mohammadfayaz.authenticator.R
import `in`.mohammadfayaz.authenticator.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    FirebaseMessaging.getInstance().token.addOnCompleteListener {
      println(it.result)
    }
  }
}

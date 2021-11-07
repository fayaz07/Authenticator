package `in`.mohammadfayaz.authenticator.utils

object RequestTypes {
  const val SEND_FCM_TOKEN = "send_fcm_token"
}

object ResponseTypes {
  const val RECEIVED_OTP = "sent_otp"
  const val FCM_ACK = "fcm_ack"
}

object EventTypes {
  const val CONNECTED_TO_SOCKET = 1
  const val DISCONNECTED_FROM_SOCKET = 2
  const val RECEIVED_OTP = 3
  const val FCM_ACKNOWLEDGEMENT = 4
}

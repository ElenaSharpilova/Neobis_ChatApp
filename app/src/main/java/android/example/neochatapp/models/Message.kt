package android.example.neochatapp.models

import com.google.firebase.Timestamp
import java.io.Serializable

data class Message(
    var text: String = "",
    var time: Timestamp = Timestamp.now(),
    //var time:Long = 0,
    val isHave: Boolean = false,
    var senderId: String= "")
    : Serializable {
}

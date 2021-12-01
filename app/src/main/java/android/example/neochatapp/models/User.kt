package android.example.neochatapp.models

import java.io.Serializable


data class User
    (var id: String = "",
     var name: String = "" )
    : Serializable
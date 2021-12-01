package android.example.neochatapp.models

import java.io.Serializable

data class Chat(var id: String? = null,
                var userIds: List<String> = listOf())
    : Serializable

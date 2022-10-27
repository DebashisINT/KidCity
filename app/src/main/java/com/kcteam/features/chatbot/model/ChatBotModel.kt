package com.kcteam.features.chatbot.model

import java.io.Serializable

data class ChatBotDataModel(var msg: String = "",
                            var user: String = "",
                            var isBotActionShow: Boolean = false,
                            var isAudioPlaying: Boolean = false): Serializable
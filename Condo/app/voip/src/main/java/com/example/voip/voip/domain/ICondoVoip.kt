package com.example.voip.voip.domain

import android.view.TextureView
import kotlinx.coroutines.flow.StateFlow
import org.linphone.core.Call
import org.linphone.core.TransportType
import org.linphone.mediastream.video.capture.CaptureTextureView

interface ICondoVoip {

    val callState : StateFlow<Call.State>
    fun initVideo(textureView: TextureView,captureTextureView: CaptureTextureView)
    fun login(username: String,password: String,domain :String,transportType: TransportType)
    fun outgoingCall(remoteSipUri : String)

    fun answerCall()
    fun hangUp()
    fun startKeepAliveService()

    fun toggleVideo()
    fun toggleCamera()
    fun pauseOrResume()
}
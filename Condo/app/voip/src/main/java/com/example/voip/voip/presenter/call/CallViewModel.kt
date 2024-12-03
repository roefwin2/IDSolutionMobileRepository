package com.example.voip.voip.presenter.call

import android.view.TextureView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voip.voip.data.AccountState
import com.example.voip.voip.domain.ICondoVoip
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.linphone.core.Call
import org.linphone.core.TransportType
import org.linphone.mediastream.video.capture.CaptureTextureView

class CallViewModel(
    private val voip: ICondoVoip
) : ViewModel() {

    val callState = voip.callState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Call.State.Idle
    )

    fun initVideo(textureView: TextureView, captureTextureView: CaptureTextureView) {
        voip.initVideo(textureView, captureTextureView)
    }
    fun toggleVideo() {
        voip.toggleCamera()
    }

    fun hangUp() {
        voip.hangUp()
    }
}
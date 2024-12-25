package com.example.voip.voip.data

import android.content.Context
import android.view.TextureView
import androidx.lifecycle.MutableLiveData
import org.linphone.core.Account
import org.linphone.core.Call
import org.linphone.core.Core
import org.linphone.core.CoreListenerStub
import org.linphone.core.Factory
import org.linphone.core.LogCollectionState
import org.linphone.core.MediaEncryption
import org.linphone.core.RegistrationState
import org.linphone.core.TransportType
import org.linphone.mediastream.video.capture.CaptureTextureView
import com.example.voip.voip.domain.ICondoVoip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class ICondoLinphoneImpl(context: Context) : ICondoVoip {
    private lateinit var core: Core
    private val _accountState: MutableLiveData<AccountState> = MutableLiveData()
    val accountState = _accountState

    private val _callState: MutableStateFlow<Call.State> = MutableStateFlow(Call.State.Idle)
    override val callState = _callState

    private val coreListener = object : CoreListenerStub() {
        override fun onAccountRegistrationStateChanged(
            core: Core,
            account: Account,
            state: RegistrationState?,
            message: String
        ) {
            _accountState.postValue(AccountState(message = message, registrationState = state))
        }

        override fun onCallStateChanged(
            core: Core,
            call: Call,
            state: Call.State?,
            message: String
        ) {
            // This function will be called each time a call state changes,
            // which includes new incoming/outgoing calls
            println("LOGIN TEST  call state ${state?.name}/${call.params}")
            _callState.update {
                state ?: Call.State.Idle
            }
        }
    }

    init {
        val factory = Factory.instance()
        factory.setDebugMode(true, "Hello Linphone")
        core = factory.createCore(null, null, context)

        // If the following property is enabled, it will automatically configure created call params with video enabled
        //core.videoActivationPolicy.automaticallyInitiate = true

        core.enableLogCollection(LogCollectionState.Enabled)
        login(
            "regis_test",
            "e1d2o3U4",
            "sip.linphone.org",
            TransportType.Tls
        )
    }

    override fun initVideo(textureView: TextureView, captureTextureView: CaptureTextureView) {
        // For video to work, we need two TextureViews:
        // one for the remote video and one for the local preview
        core.nativeVideoWindowId = textureView
        // The local preview is a org.linphone.mediastream.video.capture.CaptureTextureView
        // which inherits from TextureView and contains code to keep the ratio of the capture video
        core.nativePreviewWindowId = captureTextureView

        // Here we enable the video capture & display at Core level
        // It doesn't mean calls will be made with video automatically,
        // But it allows to use it later
        core.isVideoCaptureEnabled = true
        core.isVideoDisplayEnabled = true

        // When enabling the video, the remote will either automatically answer the update request
        // or it will ask it's user depending on it's policy.
        // Here we have configured the policy to always automatically accept video requests
        core.videoActivationPolicy.automaticallyAccept = true
        // If you don't want to automatically accept,
        // you'll have to use a code similar to the one in toggleVideo to answer a received request
    }

    override fun login(
        username: String,
        password: String,
        domain: String,
        transportType: TransportType
    ) {
        val authInfo =
            Factory.instance().createAuthInfo(username, null, password, null, null, domain, null)

        val params = core.createAccountParams()
        val identity = Factory.instance().createAddress("sip:$username@$domain")
        params.identityAddress = identity

        val address = Factory.instance().createAddress("sip:$domain")
        address?.password = password
        address?.transport = transportType
        params.serverAddress = address
        params.isRegisterEnabled = true
        val account = core.createAccount(params)

        core.addAuthInfo(authInfo)
        core.addAccount(account)

        // Asks the CaptureTextureView to resize to match the captured video's size ratio
        core.config.setBool("video", "auto_resize_preview_to_keep_ratio", true)

        core.defaultAccount = account
        core.addListener(coreListener)
        core.start()
    }

    override fun outgoingCall(remoteSipUri: String) {
        val remoteAddress = Factory.instance().createAddress(remoteSipUri)
        remoteAddress
            ?: return // If address parsing fails, we can't continue with outgoing call process
        // We also need a CallParams object
        // Create call params expects a Call object for incoming calls, but for outgoing we must use null safely
        val params = core.createCallParams(null)
        params ?: return // Same for params

        // We can now configure it
        // Here we ask for no encryption but we could ask for ZRTP/SRTP/DTLS
        params.mediaEncryption = MediaEncryption.None
        // If we wanted to start the call with video directly
        //params.enableVideo(true)

        // Finally we start the call
        core.inviteAddress(remoteAddress)
        // Call process can be followed in onCallStateChanged callback from core listener
    }

    override fun hangUp() {
        if (core.callsNb == 0) return

        // If the call state isn't paused, we can get it using core.currentCall
        val call = if (core.currentCall != null) core.currentCall else core.calls[0]
        call ?: return

        // Terminating a call is quite simple
        call.terminate()
    }

    override fun toggleVideo() {
        if (core.callsNb == 0) return
        val call = if (core.currentCall != null) core.currentCall else core.calls[0]
        call ?: return
        // To update the call, we need to create a new call params, from the call object this time
        val params = core.createCallParams(call)
        // Here we toggle the video state (disable it if enabled, enable it if disabled)
        // Note that we are using currentParams and not params or remoteParams
        // params is the object you configured when the call was started
        // remote params is the same but for the remote
        // current params is the real params of the call, resulting of the mix of local & remote params
        params?.isVideoEnabled = !call.currentParams.isVideoEnabled
        // Finally we request the call update
        call.update(params)

        // Note that when toggling off the video, TextureViews will keep showing the latest frame displayed
    }

    override fun toggleCamera() {
        // Currently used camera
        val currentDevice = core.videoDevice

        // Let's iterate over all camera available and choose another one
        for (camera in core.videoDevicesList) {
            // All devices will have a "Static picture" fake camera, and we don't want to use it
            if (camera != currentDevice && camera != "StaticImage: Static picture") {
                core.videoDevice = camera
                break
            }
        }
    }

    override fun pauseOrResume() {
        if (core.callsNb == 0) return
        val call = if (core.currentCall != null) core.currentCall else core.calls[0]
        call ?: return

        if (call.state != Call.State.Paused && call.state != Call.State.Pausing) {
            // If our call isn't paused, let's pause it
            call.pause()
        } else if (call.state != Call.State.Resuming) {
            // Otherwise let's resume it
            call.resume()
        }
    }
}

data class AccountState(
    val message: String,
    val registrationState: RegistrationState?
)
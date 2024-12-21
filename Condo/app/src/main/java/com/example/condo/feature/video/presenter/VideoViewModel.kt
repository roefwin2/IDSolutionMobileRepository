package com.example.condo.feature.video.presenter

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import org.webrtc.DataChannel
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpReceiver
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack

class VideoViewModel : ViewModel(), WebSocketSignaling.SignalingListener {
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null
    private var surfaceViewRenderer: SurfaceViewRenderer? = null

    // URL du serveur de signalisation
    val signalingServerUrl = "ws://192.168.1.107:8080/rtc"

    // Initialiser le WebSocketSignaling
    private  val webSocketSignaling = WebSocketSignaling(signalingServerUrl, this)

    fun initWebRTC(
        eglBaseContext: EglBase.Context,
        context: Context,
        renderer: SurfaceViewRenderer
    ) {
        surfaceViewRenderer = renderer
        initializePeerConnectionFactory(eglBaseContext, context)
        webSocketSignaling.connect()
        createPeerConnection()
        // Ici, vous devrez implémenter la logique de connexion à votre serveur WebRTC
    }

    private fun initializePeerConnectionFactory(eglBaseContext: EglBase.Context, context: Context) {
        val options = PeerConnectionFactory.InitializationOptions.builder(context)
            .setEnableInternalTracer(true)
            .createInitializationOptions()
        PeerConnectionFactory.initialize(options)

        val factory = PeerConnectionFactory.builder()
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBaseContext))
            .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBaseContext, true, true))
            .createPeerConnectionFactory()

        peerConnectionFactory = factory
    }

    private fun setupRemoteVideoTrack(remoteVideoTrack: VideoTrack) {
        remoteVideoTrack.addSink(surfaceViewRenderer)
    }

    private fun createPeerConnection() {
        val iceServers = listOf(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
        )
        val rtcConfig = PeerConnection.RTCConfiguration(iceServers)
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        peerConnection = peerConnectionFactory?.createPeerConnection(
            rtcConfig,
            object : PeerConnection.Observer {
                // Implémentez les callbacks nécessaires
                override fun onSignalingChange(p0: PeerConnection.SignalingState?) {

                }

                override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {

                }

                override fun onIceConnectionReceivingChange(p0: Boolean) {

                }

                override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {

                }

                override fun onIceCandidate(p0: IceCandidate?) {
                    val message = JSONObject()
                    message.put("type", "candidate")
                    message.put("sdpMid", p0?.sdpMid)
                    message.put("sdpMLineIndex",p0?.sdpMLineIndex)
                    message.put("candidate", p0?.sdp)
                    webSocketSignaling.sendMessage(message.toString())
                    val candidate = IceCandidate(p0?.sdpMid, p0?.sdpMLineIndex ?: 0, p0?.toString()
                    )
                    peerConnection?.addIceCandidate(candidate)
                }

                override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {

                }

                override fun onAddStream(p0: MediaStream?) {
                    p0?.videoTracks?.get(0)?.let {
                        setupRemoteVideoTrack(it)
                    }
                }

                override fun onRemoveStream(p0: MediaStream?) {

                }

                override fun onDataChannel(p0: DataChannel?) {

                }

                override fun onRenegotiationNeeded() {

                }

                override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
                }
            }
        )

    }

    fun stopWebRTC() {
        peerConnection?.dispose()
        peerConnectionFactory?.dispose()
        surfaceViewRenderer?.release()
    }

    override fun onCleared() {
        super.onCleared()
        stopWebRTC()
    }

    override fun onConnectionEstablished() {
        Log.d("Signaling", "WebSocket connection established.")
    }

    override fun onMessageReceived(message: String) {
        // Traiter les messages de signalisation (SDP ou ICE)
        val json = JSONObject(message)
        when (json.getString("type")) {
            "offer" -> {
                val sdp = json.getString("sdp")
                peerConnection!!.createOffer(object : SdpObserver {
                    override fun onCreateSuccess(sdp: SessionDescription) {
                        peerConnection!!.setLocalDescription(this, sdp)
                        sendSignalingMessage("offer", sdp.description)
                    }

                    override fun onSetSuccess() {}

                    override fun onCreateFailure(error: String) {
                        Log.e("WebRTC", "SDP creation failed: $error")
                    }

                    override fun onSetFailure(error: String) {
                        Log.e("WebRTC", "SDP setting failed: $error")
                    }
                }, MediaConstraints())
            }
            "answer" -> {
                val sdp = json.getString("sdp")
                peerConnection!!.setRemoteDescription(
                    object : SdpObserver {
                        override fun onCreateSuccess(p0: SessionDescription?) {

                        }

                        override fun onSetSuccess() {

                        }

                        override fun onCreateFailure(p0: String?) {
                        }

                        override fun onSetFailure(p0: String?) {
                        }
                    },
                    SessionDescription(SessionDescription.Type.ANSWER, sdp)
                )
            }
            "candidate" -> {
                val candidate = IceCandidate(
                    json.getString("sdpMid"),
                    json.getInt("sdpMLineIndex"),
                    json.getString("candidate")
                )
                peerConnection!!.addIceCandidate(candidate)
            }
        }
    }

    override fun onError(error: String) {
        Log.e("Signaling", "WebSocket error: $error")
    }

    private fun sendSignalingMessage(type: String, content: String) {
        val message = JSONObject()
        message.put("type", type)
        message.put("sdp", content)
        webSocketSignaling.sendMessage(message.toString())
    }
}

class WebSocketSignaling(private val url: String, private val listener: SignalingListener) {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connect() {
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                listener.onConnectionEstablished()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                listener.onMessageReceived(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                listener.onError(t.message ?: "Unknown error")
            }
        })
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    fun disconnect() {
        webSocket?.close(1000, null)
    }

    interface SignalingListener {
        fun onConnectionEstablished()
        fun onMessageReceived(message: String)
        fun onError(error: String)
    }
}

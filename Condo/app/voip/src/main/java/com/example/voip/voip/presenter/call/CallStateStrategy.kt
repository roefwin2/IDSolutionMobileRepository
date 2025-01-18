package com.example.voip.voip.presenter.call

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.voip.voip.presenter.call.activities.ControlBar
import com.example.voip.voip.presenter.call.activities.MainCallScreen
import com.example.voip.voip.presenter.call.activities.VideoCallViewModel
import org.koin.androidx.compose.koinViewModel
import org.linphone.core.Call
import org.linphone.core.Call.State

@Composable
fun CallStateDisplay(
    callState: Call.State,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        AnimatedContent(
            targetState = callState,
            label = "call_state_transition"
        ) { state ->
            when (state) {
                Call.State.Idle -> IdleState()
                State.PushIncomingReceived, State.IncomingReceived, State.IncomingEarlyMedia -> IncomingState()
                State.StreamsRunning -> VideoCallScreen {  }
                Call.State.OutgoingInit -> OutgoingInitState()
                Call.State.OutgoingProgress -> OutgoingProgressState()
                Call.State.OutgoingRinging -> OutgoingRingingState()
                Call.State.OutgoingEarlyMedia -> EarlyMediaState()
                Call.State.Connected -> ConnectedState()
                Call.State.Error -> ErrorState()
                Call.State.End,State.Released -> EndState()
                Call.State.UpdatedByRemote -> UpdatedByRemoteState()
                else -> UnknownState()
            }
        }
    }
}

@Composable
private fun IdleState() {
    StateContainer(
        backgroundColor = MaterialTheme.colorScheme.surface,
        icon = Icons.Default.Phone,
        iconTint = MaterialTheme.colorScheme.onSurface,
        title = "En attente",
        description = "Aucun appel en cours"
    )
}

@Composable
fun VideoCallScreen(
    viewModel: VideoCallViewModel = koinViewModel(),
    onCallEnded: () -> Unit
) {
    val callState by viewModel.callState.collectAsState()

    if (!callState.isCallActive) {
        LaunchedEffect(Unit) {
            onCallEnded()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Zone de prévisualisation vidéo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 120.dp)
        ) {
            if (callState.isCameraEnabled) {
                // Placeholder pour la prévisualisation vidéo
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.DarkGray)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Caméra désactivée",
                        color = Color.White
                    )
                }
            }
        }

        // Barre de contrôles
        ControlBar(
            isMicEnabled = callState.isMicEnabled,
            isCameraEnabled = callState.isCameraEnabled,
            onToggleMic = { viewModel.toggleMicrophone() },
            onToggleCamera = { viewModel.toggleCamera() },
            onEndCall = { viewModel.endCall() },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun IncomingState() {
    StateContainer(
        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
        icon = Icons.Default.CallReceived,
        iconTint = MaterialTheme.colorScheme.primary,
        title = "Appel entrant",
        description = "Réception d'un nouvel appel"
    )
}

@Composable
private fun OutgoingInitState() {
    StateContainer(
        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
        icon = Icons.Default.CallMade,
        iconTint = MaterialTheme.colorScheme.tertiary,
        title = "Initialisation de l'appel",
        description = "Préparation de l'appel sortant"
    )
}

@Composable
private fun OutgoingProgressState() {
    val infiniteTransition = rememberInfiniteTransition(label = "rotate")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    StateContainer(
        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
        icon = Icons.Default.Refresh,
        iconTint = MaterialTheme.colorScheme.tertiary,
        title = "Appel en cours",
        description = "Établissement de la connexion...",
        iconModifier = Modifier.rotate(rotation)
    )
}

@Composable
private fun OutgoingRingingState() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    StateContainer(
        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
        icon = Icons.Default.Phone,
        iconTint = MaterialTheme.colorScheme.tertiary,
        title = "Sonnerie en cours",
        description = "En attente de réponse...",
        iconModifier = Modifier.scale(scale)
    )
}

@Composable
private fun EarlyMediaState() {
    StateContainer(
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        icon = Icons.Default.Phone,
        iconTint = MaterialTheme.colorScheme.secondary,
        title = "Early Media",
        description = "Établissement de la connexion média"
    )
}

@Composable
private fun ConnectedState() {
    StateContainer(
        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
        icon = Icons.Default.Check,
        iconTint = MaterialTheme.colorScheme.primary,
        title = "Appel connecté",
        description = "Communication établie"
    )
}

@Composable
private fun ErrorState() {
    StateContainer(
        backgroundColor = MaterialTheme.colorScheme.errorContainer,
        icon = Icons.Default.Warning,
        iconTint = MaterialTheme.colorScheme.error,
        title = "Erreur",
        description = "Une erreur est survenue pendant l'appel"
    )
}

@Composable
private fun EndState() {
    StateContainer(
        backgroundColor = MaterialTheme.colorScheme.surface,
        icon = Icons.Default.CallEnd,
        iconTint = MaterialTheme.colorScheme.onSurface,
        title = "Appel terminé",
        description = "La communication est terminée"
    )
}

@Composable
private fun UpdatedByRemoteState() {
    StateContainer(
        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
        icon = Icons.Default.Refresh,
        iconTint = MaterialTheme.colorScheme.primary,
        title = "Mise à jour distante",
        description = "L'appel est en cours de mise à jour"
    )
}

@Composable
private fun UnknownState() {
    StateContainer(
        backgroundColor = MaterialTheme.colorScheme.surface,
        icon = Icons.Default.Error,
        iconTint = MaterialTheme.colorScheme.onSurface,
        title = "État inconnu",
        description = "État non reconnu"
    )
}

@Composable
private fun StateContainer(
    backgroundColor: Color,
    icon: ImageVector,
    iconTint: Color,
    title: String,
    description: String,
    iconModifier: Modifier = Modifier
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = iconModifier.size(24.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
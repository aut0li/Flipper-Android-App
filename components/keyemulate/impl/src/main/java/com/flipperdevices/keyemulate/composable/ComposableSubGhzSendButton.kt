package com.flipperdevices.keyemulate.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.core.ui.ktx.image.Picture
import com.flipperdevices.core.ui.ktx.onHoldPress
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyemulate.composable.common.ComposableActionDisable
import com.flipperdevices.keyemulate.composable.common.ComposableActionLoading
import com.flipperdevices.keyemulate.composable.common.ComposableBubbleHoldToSend
import com.flipperdevices.keyemulate.composable.common.ComposableEmulateButtonWithText
import com.flipperdevices.keyemulate.composable.common.ComposableErrorDialogs
import com.flipperdevices.keyemulate.impl.R
import com.flipperdevices.keyemulate.model.DisableButtonReason
import com.flipperdevices.keyemulate.model.EmulateButtonState
import com.flipperdevices.keyemulate.viewmodel.SubGhzViewModel
import tangle.viewmodel.compose.tangleViewModel
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableSubGhzSendButton(
    flipperKey: FlipperKey,
    modifier: Modifier = Modifier
) {
    val emulateViewModel = tangleViewModel<SubGhzViewModel>()
    val emulateButtonState by emulateViewModel.getEmulateButtonStateFlow().collectAsState()

    if (!flipperKey.synchronized) {
        ComposableActionDisable(
            modifier = modifier,
            textId = R.string.keyscreen_send,
            iconId = DesignSystem.drawable.ic_send,
            reason = DisableButtonReason.NOT_SYNCHRONIZED
        )
        return
    }

    ComposableErrorDialogs(emulateButtonState, emulateViewModel::closeDialog)

    when (emulateButtonState) {
        is EmulateButtonState.Disabled -> ComposableActionDisable(
            modifier = modifier,
            textId = R.string.keyscreen_send,
            iconId = DesignSystem.drawable.ic_send,
            reason = (emulateButtonState as EmulateButtonState.Disabled).reason
        )
        is EmulateButtonState.Active,
        is EmulateButtonState.Inactive -> {
            ComposableActiveStateEmulateInternal(
                modifier = modifier,
                emulateButtonState = emulateButtonState,
                emulateViewModel = emulateViewModel,
                flipperKey = flipperKey
            )
        }
        is EmulateButtonState.Loading -> ComposableActionLoading(
            modifier = modifier,
            loadingState = (emulateButtonState as EmulateButtonState.Loading).state
        )
    }
}

@Composable
private fun ComposableActiveStateEmulateInternal(
    emulateViewModel: SubGhzViewModel,
    flipperKey: FlipperKey,
    emulateButtonState: EmulateButtonState,
    modifier: Modifier = Modifier
) {
    var isBubbleOpen by remember { mutableStateOf(false) }
    val buttonActiveModifier = Modifier.onHoldPress(
        onTap = {
            isBubbleOpen = true
            emulateViewModel.onSinglePress(flipperKey)
        },
        onLongPressStart = {
            emulateViewModel.onStartEmulate(flipperKey)
        },
        onLongPressEnd = {
            emulateViewModel.onStopEmulate()
        }
    )

    var positionYEmulateButton by remember { mutableStateOf(0) }
    ComposableActiveEmulateInternal(
        modifier = modifier.onGloballyPositioned {
            val coordinate = it.positionInRoot()
            positionYEmulateButton = coordinate.y.toInt()
        },
        buttonActiveModifier = buttonActiveModifier,
        emulateProgress = (emulateButtonState as? EmulateButtonState.Active)?.progress,
        isActive = emulateButtonState is EmulateButtonState.Active
    )

    // After single emulate close bubble
    if (isBubbleOpen && emulateButtonState is EmulateButtonState.Inactive) isBubbleOpen = false
    if (isBubbleOpen) {
        ComposableBubbleHoldToSend(positionYEmulateButton)
    }
}

/**
 * We need to use a single composable function here because
 * the gesture listener must be the same for both components and the components must be the same
 */
@Composable
private fun ComposableActiveEmulateInternal(
    emulateProgress: com.flipperdevices.keyemulate.model.EmulateProgress?,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    buttonActiveModifier: Modifier = Modifier
) {
    val textId = if (isActive) R.string.keyscreen_sending else R.string.keyscreen_send
    val color = if (isActive) {
        LocalPallet.current.actionOnFlipperSubGhzProgress
    } else {
        LocalPallet.current.actionOnFlipperSubGhzEnable
    }
    val progressColor = if (isActive) {
        LocalPallet.current.actionOnFlipperSubGhzEnable
    } else {
        Color.Transparent
    }
    val descriptionId = if (isActive) null else R.string.keyscreen_sending_desc
    val picture = if (isActive) {
        Picture.LottieRes(
            DesignSystem.raw.ic_sending,
            DesignSystem.drawable.ic_send
        )
    } else {
        Picture.StaticRes(DesignSystem.drawable.ic_send)
    }

    ComposableEmulateButtonWithText(
        modifier = modifier,
        buttonModifier = buttonActiveModifier,
        buttonTextId = textId,
        textId = descriptionId,
        color = color,
        progressColor = progressColor,
        progress = emulateProgress,
        picture = picture
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableHoldToSendWithButtonPreview() {
    FlipperThemeInternal {
        var positionYEmulateButton by remember { mutableStateOf(0) }
        var isBubbleOpen by remember { mutableStateOf(false) }

        val buttonActiveModifier = Modifier.onHoldPress(
            onTap = { isBubbleOpen = true },
            onLongPressStart = { },
            onLongPressEnd = { },
        )

        val modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)

        ComposableActiveEmulateInternal(
            modifier = modifier
                .onGloballyPositioned {
                    val coordinate = it.positionInRoot()
                    positionYEmulateButton = coordinate.y.toInt()
                },
            buttonActiveModifier = buttonActiveModifier,
            emulateProgress = null,
            isActive = false
        )

        if (isBubbleOpen) {
            ComposableBubbleHoldToSend(positionYEmulateButton)
        }
    }
}

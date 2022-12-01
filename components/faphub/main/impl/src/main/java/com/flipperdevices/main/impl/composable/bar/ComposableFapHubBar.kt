package com.flipperdevices.main.impl.composable.bar

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.main.impl.R

@Composable
fun ComposableFapHubBar(onBack: () -> Unit) {
    OrangeAppBar(
        titleId = R.string.faphub_main_title,
        onBack = onBack,
        endBlock = {
            Icon(
                modifier = Modifier
                    .padding(end = 14.dp)
                    .size(24.dp),
                painter = painterResource(DesignSystem.drawable.ic_search),
                contentDescription = stringResource(R.string.faphub_main_title),
                tint = LocalPallet.current.onAppBar
            )
        }
    )
}
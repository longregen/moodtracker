package com.moodtracker.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.moodtracker.ui.theme.Animations
import com.moodtracker.ui.theme.GradientColors
import com.moodtracker.ui.theme.Spacing

@Composable
fun CommonHeader(
    title: String,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBackClick != null) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold
        )
        
        actions()
    }
}

@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(Spacing.extraLarge)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(Spacing.emptyStateIconSize),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(Spacing.medium))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            subtitle?.let { text ->
                Spacer(modifier = Modifier.height(Spacing.small))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonCard(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    showGradientAccent: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) Animations.CARD_PRESS_SCALE else Animations.CARD_NORMAL_SCALE,
        animationSpec = spring(),
        label = "card_scale"
    )

    val cardModifier = modifier
        .fillMaxWidth()
        .scale(scale)

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = cardModifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        }
                    )
                },
            elevation = CardDefaults.cardElevation(defaultElevation = Spacing.cardElevation),
            shape = MaterialTheme.shapes.medium
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                if (showGradientAccent) {
                    Box(
                        modifier = Modifier
                            .width(Spacing.gradientAccentWidth)
                            .fillMaxHeight()
                            .background(
                                brush = GradientColors.cardAccentGradient(
                                    primary = MaterialTheme.colorScheme.primary,
                                    secondary = MaterialTheme.colorScheme.secondary
                                )
                            )
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(Spacing.cardPadding)
                ) {
                    content()
                }
            }
        }
    } else {
        Card(
            modifier = cardModifier,
            elevation = CardDefaults.cardElevation(defaultElevation = Spacing.cardElevation),
            shape = MaterialTheme.shapes.medium
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                if (showGradientAccent) {
                    Box(
                        modifier = Modifier
                            .width(Spacing.gradientAccentWidth)
                            .fillMaxHeight()
                            .background(
                                brush = GradientColors.cardAccentGradient(
                                    primary = MaterialTheme.colorScheme.primary,
                                    secondary = MaterialTheme.colorScheme.secondary
                                )
                            )
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(Spacing.cardPadding)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(vertical = Spacing.small)
    )
}

@Composable
fun CardTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier.padding(bottom = Spacing.small)
    )
}

@Composable
fun CardSubtitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

@Composable
fun CardCaption(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    val gradientBrush = GradientColors.buttonGradient(
        primary = MaterialTheme.colorScheme.primary,
        secondary = MaterialTheme.colorScheme.secondary
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(Spacing.iconSizeSmall)
                )
                Spacer(modifier = Modifier.width(Spacing.small))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled
    ) {
        Text(text)
    }
}

@Composable
fun InfoCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primaryContainer,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.cardPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Spacing.iconSizeLarge)
                )
                Spacer(modifier = Modifier.height(Spacing.small))
            }
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            
            subtitle?.let { text ->
                Spacer(modifier = Modifier.height(Spacing.extraSmall))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun StandardScreenLayout(
    title: String,
    onBackClick: (() -> Unit)? = null,
    headerActions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.screenPadding)
    ) {
        CommonHeader(
            title = title,
            onBackClick = onBackClick,
            actions = headerActions
        )
        
        content()
    }
}
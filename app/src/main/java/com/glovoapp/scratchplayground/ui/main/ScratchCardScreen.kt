package com.glovoapp.scratchplayground.ui.main

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.glovoapp.scratchplayground.R

private const val MAX_SCRATCH_TIME = 3200L
private const val CONFETTI_ANIMATION_URL =
    "https://assets9.lottiefiles.com/packages/lf20_i6sqnxav.json"

@ExperimentalComposeUiApi
@Composable
fun ScratchCardScreen() {
    val overlayImage = ImageBitmap.imageResource(id = R.drawable.ic_scratch_card_overlay)
    val baseImage = ImageBitmap.imageResource(id = R.drawable.base_image)

    val currentPathState by remember { mutableStateOf(DraggedPath(path = Path())) }
    var movedOffsetState by remember { mutableStateOf<Offset?>(null) }
    val totalScratchTime = remember { mutableStateOf<Long>(0) }
    val isRevealed = remember { mutableStateOf(false) }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFC244))
    ) {
//        IconButton(
//            onClick = {
//                movedOffsetState = null
//                currentPathState = DraggedPath(path = Path())
//                totalScratchTime.value = 0L
//                isRevealed.value = false
//            },
//            modifier = Modifier.align(Alignment.TopCenter)
//        ) {
//            Icon(
//                imageVector = Icons.Default.Clear, contentDescription = "Clear",
//                tint = MaterialTheme.colors.onPrimary
//            )
//        }

        val (scratchCanvasRef, titleRef, messageRef, backgroundCirclesRef, confettiRef) = createRefs()

        if (totalScratchTime.value >= MAX_SCRATCH_TIME) {
            val composition by rememberLottieComposition(
                LottieCompositionSpec.Url(CONFETTI_ANIMATION_URL)
            )
            val progress by animateLottieCompositionAsState(composition)

            LottieAnimation(
                composition,
                progress,
                modifier = Modifier.constrainAs(confettiRef) {
                    linkTo(parent.start, parent.top, parent.end, parent.bottom)
                })
            isRevealed.value = true
        }

        Text(
            text = "Scratch and win!",
            fontSize = 24.sp,
            fontWeight = Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(titleRef) {
                linkTo(parent.start, parent.top, parent.end, scratchCanvasRef.top)
            }
        )

        Image(
            painter = painterResource(id = R.drawable.ic_scratch_background),
            contentDescription = null,
            modifier = Modifier
                .constrainAs(backgroundCirclesRef) {
                    linkTo(parent.start, parent.top, parent.end, parent.bottom)
                }
                .size(335.dp),
        )

        // Scratch Card Implementation
        ScratchingCanvas(
            overlayImage = overlayImage,
            baseImage = baseImage,
            modifier = Modifier.constrainAs(scratchCanvasRef) {
                linkTo(parent.start, parent.top, parent.end, parent.bottom)
            },
            movedOffset = movedOffsetState,
            onMovedOffset = { x, y ->
                movedOffsetState = Offset(x, y)
            },
            currentPath = currentPathState.path,
            currentPathThickness = currentPathState.width,
            totalScratchTime,
            isRevealed
        )

        Text(
            text = "In the meantime, try your luck by scratching the card above.",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(messageRef) {
                width = Dimension.fillToConstraints
                linkTo(
                    parent.start,
                    scratchCanvasRef.bottom,
                    parent.end,
                    parent.bottom,
                    startMargin = 40.dp,
                    endMargin = 40.dp
                )
            }
        )
    }
}

@ExperimentalComposeUiApi
@Composable
fun ScratchingCanvas(
    overlayImage: ImageBitmap,
    baseImage: ImageBitmap,
    modifier: Modifier = Modifier,
    movedOffset: Offset?,
    onMovedOffset: (Float, Float) -> Unit,
    currentPath: Path,
    currentPathThickness: Float,
    totalScratchTime: MutableState<Long>,
    isRevealed: MutableState<Boolean>,
) {
    var startTime by remember { mutableStateOf(0L) }
    Canvas(
        modifier = modifier
            .size(220.dp)
            .clipToBounds()
            .clip(RoundedCornerShape(size = 16.dp))
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        currentPath.moveTo(it.x, it.y)
                        startTime = System.currentTimeMillis()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        onMovedOffset(it.x, it.y)
                        val currentTime: Long = System.currentTimeMillis()
                        val scratchTime: Long = currentTime - startTime
                        startTime = currentTime
                        totalScratchTime.value += scratchTime
                    }
                }
                true
            }
    ) {
        val canvasWidth = size.width.toInt()
        val canvasHeight = size.height.toInt()
        val imageSize = IntSize(width = canvasWidth, height = canvasHeight)

        // Overlay Image to be scratched
        drawImage(
            image = overlayImage,
            dstSize = imageSize
        )

        movedOffset?.let {
            currentPath.addOval(oval = Rect(it, currentPathThickness))
        }

        if (!isRevealed.value) {
            clipPath(path = currentPath, clipOp = ClipOp.Intersect) {
                // Base Image after scratching
                drawImage(
                    image = baseImage,
                    dstSize = imageSize
                )
            }
        } else {
            drawImage(
                image = baseImage,
                dstSize = imageSize
            )
        }
    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun preview() = ScratchCardScreen()



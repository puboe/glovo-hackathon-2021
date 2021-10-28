package com.glovoapp.scratchplayground.ui.main

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.glovoapp.scratchplayground.R

private const val MAX_SCRATCH_TIME = 4000L

@ExperimentalComposeUiApi
@Composable
fun ScratchCardScreen() {
    val overlayImage = ImageBitmap.imageResource(id = R.drawable.ic_scratch_card_overlay)
    val baseImage = ImageBitmap.imageResource(id = R.drawable.base_image)

    var currentPathState by remember { mutableStateOf(DraggedPath(path = Path())) }
    var movedOffsetState by remember { mutableStateOf<Offset?>(null) }
    val totalScratchTime = remember { mutableStateOf<Long>(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFC244))
    ) {
        IconButton(
            onClick = {
                movedOffsetState = null
                currentPathState = DraggedPath(path = Path())
                totalScratchTime.value = 0L
            },
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Icon(
                imageVector = Icons.Default.Clear, contentDescription = "Clear",
                tint = MaterialTheme.colors.onPrimary
            )
        }

        // Scratch Card Implementation
        ScratchingCanvas(
            overlayImage = overlayImage,
            baseImage = baseImage,
            modifier = Modifier.align(Alignment.Center),
            movedOffset = movedOffsetState,
            onMovedOffset = { x, y ->
                movedOffsetState = Offset(x, y)
            },
            currentPath = currentPathState.path,
            currentPathThickness = currentPathState.width,
            totalScratchTime
        )

        if(totalScratchTime.value >= MAX_SCRATCH_TIME) {
//            reveal()
            println("Reveal! total time = $totalScratchTime")
            val composition by rememberLottieComposition(LottieCompositionSpec.Url("https://assets9.lottiefiles.com/packages/lf20_i6sqnxav.json"))
            val progress by animateLottieCompositionAsState(composition)

            LottieAnimation(composition, progress)
        }
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
//                        println("CurrentPath/ACTION_DOWN: (${it.x}, ${it.y})")
                        currentPath.moveTo(it.x, it.y)
                        startTime = System.currentTimeMillis()
                    }
                    MotionEvent.ACTION_MOVE -> {
//                        println("MovedOffset/ACTION_MOVE: (${it.x}, ${it.y})")
                        onMovedOffset(it.x, it.y)
                        val currentTime: Long = System.currentTimeMillis()
                        val scratchTime: Long = currentTime - startTime
                        startTime = currentTime
                        totalScratchTime.value += scratchTime
                        println("Delta=$scratchTime, total=$totalScratchTime")
                    }
                }
                true
            }
    ) {

        this.drawContext
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

        clipPath(path = currentPath, clipOp = ClipOp.Intersect) {
            // Base Image after scratching
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



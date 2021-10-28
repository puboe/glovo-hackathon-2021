package com.glovoapp.scratchplayground.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            ScratchScreen()
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun ScratchScreen() {

        var isVisible by remember { mutableStateOf(false) }

//        Button(modifier = Modifier
//            .wrapContentHeight()
//            .wrapContentWidth(),
//            onClick = { isVisible = true }) {
//            Text(text = "Scratch & Win")
//        }
        ScratchCardScreen()

        if (isVisible) {
            val composition by rememberLottieComposition(LottieCompositionSpec.Url("https://assets9.lottiefiles.com/packages/lf20_i6sqnxav.json"))
            val progress by animateLottieCompositionAsState(composition)

            LottieAnimation(composition, progress)
            if (progress >= 1.0f) {
                isVisible = false
            }
        }
    }

    @Composable
    private fun ConfettiTime() {

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val confettiView: LottieAnimationView = view.findViewById(R.id.confettiView)
//
//        with(view.findViewById<Button>(R.id.scratch_box)) {
//            setOnClickListener {
//                confettiView.visibility = View.VISIBLE
//                confettiView.playAnimation()
//            }
//        }
//    }

}
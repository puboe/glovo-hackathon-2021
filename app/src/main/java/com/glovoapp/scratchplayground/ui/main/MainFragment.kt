package com.glovoapp.scratchplayground.ui.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.jinatonic.confetti.CommonConfetti
import com.glovoapp.scratchplayground.R

class MainFragment : Fragment() {

    companion object {
        private const val STREAM_DURATION = 5000L
        private val colors = intArrayOf(
            Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
            Color.CYAN, Color.MAGENTA, Color.LTGRAY,
        )

        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val containerView: ViewGroup = view.findViewById(R.id.main)

        with(view.findViewById<Button>(R.id.scratch_box)) {
            setOnClickListener {
                CommonConfetti.explosion(
                    containerView,
                    it.x.toInt() + it.width / 2,
                    it.y.toInt() + height / 2,
                    colors
                ).stream(STREAM_DURATION)
            }
        }
    }

}
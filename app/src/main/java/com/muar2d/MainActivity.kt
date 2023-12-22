package com.muar2d

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.muar2d.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding ?: throw Exception("NPE for binding")

    private lateinit var viewModel: MainViewModel

    private val handler = Handler(Looper.getMainLooper())

    var renderThread: Thread = Thread()

    private var isRendering = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        deployUi()
        setObserversToVM()
        setBehaviour()

    }

    private fun deployUi() {
        val sheetBehaviour = BottomSheetBehavior.from(binding.logSheet)
        sheetBehaviour.peekHeight = 0
        sheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
        sheetBehaviour.isHideable = false

        handler.postDelayed({
            sheetBehaviour.peekHeight =
                binding.appbarSheet.height + binding.layA.height + binding.layB.height
            sheetBehaviour.maxHeight = 1000
            sheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }, 300)

        val renderTarget = binding.mainSurfaceView.holder
        renderTarget.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                renderThread = Thread() {
                    while (true) {
                        if (isRendering) {
                            val canvas = renderTarget.lockCanvas()
                            viewModel.drawOnHolder(canvas)
                            viewModel.calculate()
                            renderTarget.unlockCanvasAndPost(canvas)
                        }
                    }
                }
                renderThread.start()
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                viewModel.setScreenDimensions(
                    newHeight = height.toFloat(),
                    newWidth = width.toFloat()
                )
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                isRendering=false
                var retry = true

                while (retry) {
                    try {
                        renderThread.join()
                        retry = false
                    } catch (e: InterruptedException) {
                        showSnackBar(e.message.toString())
                    }
                }
            }
        })
    }

    private fun setObserversToVM() {
        viewModel.errorMsg.observe(this) {
            showSnackBar(stringToShow = it)
        }
    }

    private fun setBehaviour() {
        binding.slider.addOnChangeListener { rangeSlider, value, fromUser ->
            viewModel.angleIncrement = value
        }
    }

    private fun showSnackBar(stringToShow: String) {
        Snackbar.make(binding.root, stringToShow, Snackbar.LENGTH_INDEFINITE)
            .setTextMaxLines(20)
            .setAction("OK") {}
            .show()
    }
}
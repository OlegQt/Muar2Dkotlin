package com.muar2d

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
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

    private val renderThread = RenderThread()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        deployUi()
        setObserversToVM()
        setBehaviour()

    }

    private fun setUpSurface() {
       binding.mainSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                renderThread.setHolder(holder)
                renderThread.setRunning(true)
                renderThread.start()
            }
            override fun surfaceChanged(holder: SurfaceHolder,format: Int,width: Int,height: Int) {

            }
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                var retry = true
                renderThread.setRunning(false)

                while (retry){
                    try {
                        renderThread.join()
                        retry = false
                    }
                    catch (e:Exception){

                    }
                }

            }
        })
    }

    private fun setUpBottomSheet(){
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
    }

    private fun deployUi() {
        setUpBottomSheet()
        setUpSurface()
    }

    private fun setObserversToVM() {
        viewModel.errorMsg.observe(this) {
            showSnackBar(stringToShow = it)
        }
    }

    private fun setBehaviour() {
        binding.slider.addOnChangeListener { rangeSlider, value, fromUser ->
            renderThread.setAnimationSpeed(value.toInt())
        }

        binding.mainSurfaceView.setOnTouchListener { view, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN ->{
                    renderThread.addFingerTouch(xPos = event.x, yPos = event.y)
                    true
                }
                MotionEvent.ACTION_UP ->{
                    view.performClick()
                    true
                }

                else -> {
                    false
                }
            }
        }

        binding.mainSurfaceView.setOnClickListener {

        }
    }

    private fun showSnackBar(stringToShow: String) {
        Snackbar.make(binding.root, stringToShow, Snackbar.LENGTH_INDEFINITE)
            .setTextMaxLines(20)
            .setAction("OK") {}
            .show()
    }
}
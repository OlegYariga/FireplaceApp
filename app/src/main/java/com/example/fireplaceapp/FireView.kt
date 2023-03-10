package com.example.fireplaceapp

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import java.util.*
import kotlin.math.roundToInt

@Suppress("unused")
class FireView : View {

    private lateinit var mPixels: IntArray
    private lateinit var mBitmap: Bitmap
    private lateinit var mSourceRect: Rect
    private lateinit var mDestRect: Rect
    private var mLastDrawTime: Long = 0
    private val mFireColorsRed = intArrayOf(
        0xFFFFFFFF.toInt(),
        0xFFEFEFC7.toInt(),
        0xFFDFDF9F.toInt(),
        0xFFCFCF6F.toInt(),
        0xFFB7B737.toInt(),
        0xFFB7B72F.toInt(),
        0xFFB7AF2F.toInt(),
        0xFFbfaf2f.toInt(),
        0xFFbfa727.toInt(),
        0xFFbf9f1f.toInt(),
        0xFFc7971f.toInt(),
        0xFFc78f17.toInt(),
        0xFFc78717.toInt(),
        0xFFcf7f0f.toInt(),
        0xFFcf770f.toInt(),
        0xFFcf6f0f.toInt(),
        0xFFd7670f.toInt(),
        0xFFd75f07.toInt(),
        0xFFdf5707.toInt(),
        0xFFdf5708.toInt(),
        0xFFdf4f07.toInt(),
        0xFFc74707.toInt(),
        0xFFbf4707.toInt(),
        0xFFaf3f07.toInt(),
        0xFF9f2f07.toInt(),
        0xFF8f2707.toInt(),
        0xFF771f07.toInt(),
        0xFF671f07.toInt(),
        0xFF571707.toInt(),
        0xFF470f07.toInt(),
        0xFF2f0f07.toInt(),
        0xFF1f0707.toInt(),
        0
    )

    private val mFireColorsGreen = intArrayOf(
        0xFFFFFFFF.toInt(),
        0xFFEFEFC7.toInt(),
        0xFFDFDF9F.toInt(),
        0xFFCFCF6F.toInt(),
        0xFFB7B737.toInt(),
        0xFFB7B72F.toInt(),
        0xFFB7AF2F.toInt(),
        0xFFbfaf2f.toInt(),
        0xFFbfa727.toInt(),
        0xFFbf9f1f.toInt(),
        0xFF93c47d.toInt(),
        0xFF84d75e.toInt(),
        0xFF6fd442.toInt(),
        0xFF5ec92e.toInt(),
        0xFF57e717.toInt(),
        0xFF37aa04.toInt(),
        0xFF38761d.toInt(),
        0xFF2f7411.toInt(),
        0xFF225c09.toInt(),
        0xFF174603.toInt(),
        0xFF1c3c0b.toInt(),
        0xFF20490a.toInt(),
        0xFF48bd0b.toInt(),
        0xFF4e962e.toInt(),
        0xFF4e962e.toInt(),
        0xFF3d811f.toInt(),
        0xFF2d6b13.toInt(),
        0xFF1b4b06.toInt(),
        0xFF0b2002.toInt(),
        0xFF2e9103.toInt(),
        0xFF51f808.toInt(),
        0xFF45818e.toInt(),
        0
    )

    private val mFireColorsBlue = intArrayOf(
        0xFFFFFFFF.toInt(),
        0xFFEFEFC7.toInt(),
        0xFFcfe2f3.toInt(),
        0xFFadc9e2.toInt(),
        0xFF87abcc.toInt(),
        0xFF6499c9.toInt(),
        0xFF4782b7.toInt(),
        0xFF3e87c9.toInt(),
        0xFF3076b4.toInt(),
        0xFF205e96.toInt(),
        0xFF124c81.toInt(),
        0xFF30608a.toInt(),
        0xFF26537c.toInt(),
        0xFF1b4871.toInt(),
        0xFF1b7bd2.toInt(),
        0xFF1271c7.toInt(),
        0xFF0c65b6.toInt(),
        0xFF075aa4.toInt(),
        0xFF064f91.toInt(),
        0xFF043a6b.toInt(),
        0xFF0d839c.toInt(),
        0xFF106c7f.toInt(),
        0xFF065c6e.toInt(),
        0xFF034553.toInt(),
        0xFF03abcf.toInt(),
        0xFF037187.toInt(),
        0xFF013873.toInt(),
        0xFF02294c.toInt(),
        0xFF021b31.toInt(),
        0xFF021a57.toInt(),
        0xFF02206b.toInt(),
        0xFF002343.toInt(),
        0
    )
    private var isActive = false
    private var mFireColors = mFireColorsRed

    private val mHandler = Handler(Looper.getMainLooper(), Handler.Callback {
        invalidate()
        spreadFire()
        true
    })

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defaultRes: Int) : super(context, attrs, defaultRes) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defaultRes: Int, defaultStyle: Int) :
            super(context, attrs, defaultRes, defaultStyle) {
        init()
    }

    private fun init() {
        mPixels = IntArray(RENDER_WIDTH * RENDER_HEIGHT)
        for (i in 0 until RENDER_WIDTH) {
            mPixels[i] = mFireColors[0]
        }
        mBitmap = Bitmap.createBitmap(mPixels, RENDER_WIDTH, RENDER_HEIGHT, Bitmap.Config.ARGB_8888)
        mSourceRect = Rect(0, 0, RENDER_WIDTH - 1, RENDER_HEIGHT - 1)
    }

    private fun spreadFire() {
        var x = 0
        while (x < RENDER_WIDTH) {
            var y = 1
            while (y < RENDER_HEIGHT) {
                val random = Random()
                var lowerBound = 0
                var upperBound = 3
                val verticalColorOffset = random.nextInt(upperBound - lowerBound) + lowerBound
                lowerBound = -3
                upperBound = 3
                var horizontalParticleOffset = random.nextInt(
                    upperBound - lowerBound
                ) + lowerBound
                horizontalParticleOffset =
                    (RENDER_WIDTH - 1).coerceAtMost(x + horizontalParticleOffset)
                horizontalParticleOffset = horizontalParticleOffset.coerceAtLeast(0)
                var colorIndex = mFireColors.indexOf(
                    mPixels[(y - 1) * RENDER_WIDTH + x]
                ) + verticalColorOffset
                if (colorIndex > (mFireColors.size - 1)) colorIndex = mFireColors.size - 1
                mPixels[y * RENDER_WIDTH + horizontalParticleOffset] = mFireColors[colorIndex]
                y++
            }
            x++
        }
        mBitmap = Bitmap.createBitmap(mPixels, RENDER_WIDTH, RENDER_HEIGHT, Bitmap.Config.ARGB_8888)
        val timeDiff = System.currentTimeMillis() - mLastDrawTime
        if (timeDiff > MS_BETWEEN_FRAMES)
            mHandler.sendEmptyMessage(0)
        else
            mHandler.sendEmptyMessageDelayed(0, MS_BETWEEN_FRAMES - timeDiff)
    }

    fun startFire() {
        for (i in 0 until RENDER_WIDTH) {
            isActive = true
            mPixels[i] = mFireColors[0]
        }
        mHandler.removeCallbacksAndMessages(null)
        mHandler.sendEmptyMessage(0)
    }

    fun changeColour(i: Int){
        this.init()

        if (isActive) startFire()
        else stopFire()

        if (i==0){
            this.mFireColors = mFireColorsRed
        }else if (i == 1) {
            this.mFireColors = mFireColorsBlue
        }else if (i == 2) {
            this.mFireColors = mFireColorsGreen
        }
    }

    fun stopFire() {
        for (i in 0 until RENDER_WIDTH) {
            isActive = false
            mPixels[i] = 0
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mDestRect = Rect(0, 0, w - 1, (h * (1f * RENDER_HEIGHT / RENDER_WIDTH) - 1).roundToInt())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return

        canvas.save()
        canvas.rotate(180f, width / 2f, height / 2f)
        canvas.drawBitmap(mBitmap, mSourceRect, mDestRect, null)
        canvas.restore()

        mLastDrawTime = System.currentTimeMillis()
    }

    companion object {
        private const val MS_BETWEEN_FRAMES = 100 // 15 FPS
        private const val RENDER_WIDTH = 100
        private const val RENDER_HEIGHT = 130
    }
}
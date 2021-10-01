package libertypassage.com.corporate.utilities

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import libertypassage.com.corporate.R


class CircleSeekBar @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mWheelPaint: Paint? = null
    private var mReachedPaint: Paint? = null
    private var mReachedEdgePaint: Paint? = null
    private var mPointerPaint: Paint? = null
    private var mMaxProcess = 0.0
    private var mMinProcess = 0.0
    private var mCurProcess = 0.0
    private var mUnreachedRadius = 0f
    private var mReachedColor = 0
    private var mUnreachedColor = 0
    private var mReachedWidth = 0f
    private var mUnreachedWidth = 0f
    private var isHasReachedCornerRound = false
    private var mPointerColor = 0
    private var mPointerRadius = 0f
    private var mCurAngle = 0.0
    private var mWheelCurX = 0f
    private var mWheelCurY = 0f
    var isHasWheelShadow = false
        private set
    var isHasPointerShadow = false
        private set
    var wheelShadowRadius = 0f
        private set
    private var mPointerShadowRadius = 0f
    private var isHasCache = false
    private var mCacheCanvas: Canvas? = null
    private var mCacheBitmap: Bitmap? = null
    private var isCanTouch = false
    private var isScrollOneCircle = false
    private var mDefShadowOffset = 0f
    private var mChangListener: OnSeekBarChangeListener? = null
    private fun initPaints() {
        mDefShadowOffset = getDimen(R.dimen.def_shadow_offset)
        mWheelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mWheelPaint!!.color = mUnreachedColor
        mWheelPaint!!.style = Paint.Style.STROKE
        mWheelPaint!!.strokeWidth = mUnreachedWidth
        if (isHasWheelShadow) {
            mWheelPaint!!.setShadowLayer(
                wheelShadowRadius,
                mDefShadowOffset,
                mDefShadowOffset,
                Color.DKGRAY
            )
        }
        mReachedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mReachedPaint!!.color = mReachedColor
        mReachedPaint!!.style = Paint.Style.STROKE
        mReachedPaint!!.strokeWidth = mReachedWidth
        if (isHasReachedCornerRound) {
            mReachedPaint!!.strokeCap = Paint.Cap.ROUND
        }
        mPointerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPointerPaint!!.color = mPointerColor
        mPointerPaint!!.style = Paint.Style.FILL
        if (isHasPointerShadow) {
            mPointerPaint!!.setShadowLayer(
                mPointerShadowRadius,
                mDefShadowOffset,
                mDefShadowOffset,
                Color.DKGRAY
            )
        }
        mReachedEdgePaint = Paint(mReachedPaint)
        mReachedEdgePaint!!.style = Paint.Style.FILL
    }

    private fun initAttrs(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CircleSeekBar, defStyle, 0)
        mMaxProcess = a.getInt(R.styleable.CircleSeekBar_wheel_max_process, 5).toDouble()
        mCurProcess = a.getInt(R.styleable.CircleSeekBar_wheel_cur_process, 0).toDouble()
        if (mCurProcess > mMaxProcess) mCurProcess = mMaxProcess
        mReachedColor = a.getColor(
            R.styleable.CircleSeekBar_wheel_reached_color,
            getColor(R.color.def_reached_color)
        )
        mUnreachedColor = a.getColor(
            R.styleable.CircleSeekBar_wheel_unreached_color,
            getColor(R.color.def_wheel_color)
        )
        mUnreachedWidth = a.getDimension(
            R.styleable.CircleSeekBar_wheel_unreached_width,
            getDimen(R.dimen.def_wheel_width)
        )
        isHasReachedCornerRound =
            a.getBoolean(R.styleable.CircleSeekBar_wheel_reached_has_corner_round, true)
        mReachedWidth =
            a.getDimension(R.styleable.CircleSeekBar_wheel_reached_width, mUnreachedWidth)
        mPointerColor = a.getColor(
            R.styleable.CircleSeekBar_wheel_pointer_color,
            getColor(R.color.def_pointer_color)
        )
        mPointerRadius =
            a.getDimension(R.styleable.CircleSeekBar_wheel_pointer_radius, mReachedWidth / 2)
        isHasWheelShadow = a.getBoolean(R.styleable.CircleSeekBar_wheel_has_wheel_shadow, false)
        if (isHasWheelShadow) {
            wheelShadowRadius = a.getDimension(
                R.styleable.CircleSeekBar_wheel_shadow_radius,
                getDimen(R.dimen.def_shadow_radius)
            )
        }
        isHasPointerShadow = a.getBoolean(R.styleable.CircleSeekBar_wheel_has_pointer_shadow, false)
        if (isHasPointerShadow) {
            mPointerShadowRadius = a.getDimension(
                R.styleable.CircleSeekBar_wheel_pointer_shadow_radius,
                getDimen(R.dimen.def_shadow_radius)
            )
        }
        isHasCache = a.getBoolean(R.styleable.CircleSeekBar_wheel_has_cache, isHasWheelShadow)
        isCanTouch = a.getBoolean(R.styleable.CircleSeekBar_wheel_can_touch, true)
        isScrollOneCircle =
            a.getBoolean(R.styleable.CircleSeekBar_wheel_scroll_only_one_circle, false)
        if (isHasPointerShadow or isHasWheelShadow) {
            setSoftwareLayer()
        }
        a.recycle()
    }

    private fun initPadding() {
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom
        var paddingStart = 0
        var paddingEnd = 0
        if (Build.VERSION.SDK_INT >= 17) {
            paddingStart = getPaddingStart()
            paddingEnd = getPaddingEnd()
        }
        val maxPadding = Math.max(
            paddingLeft, Math.max(
                paddingTop,
                Math.max(paddingRight, Math.max(paddingBottom, Math.max(paddingStart, paddingEnd)))
            )
        )
        setPadding(maxPadding, maxPadding, maxPadding, maxPadding)
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun getColor(colorId: Int): Int {
        val version = Build.VERSION.SDK_INT
        return if (version >= 23) {
            context.getColor(colorId)
        } else {
            ContextCompat.getColor(context, colorId)
        }
    }

    private fun getDimen(dimenId: Int): Float {
        return resources.getDimension(dimenId)
    }

    private fun setSoftwareLayer() {
        if (Build.VERSION.SDK_INT >= 11) {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val min = Math.min(width, height)
        setMeasuredDimension(min, min)
        refershPosition()
        refershUnreachedWidth()
    }

    override fun onDraw(canvas: Canvas) {
        val left = paddingLeft + mUnreachedWidth / 2
        val top = paddingTop + mUnreachedWidth / 2
        val right = canvas.width - paddingRight - mUnreachedWidth / 2
        val bottom = canvas.height - paddingBottom - mUnreachedWidth / 2
        val centerX = (left + right) / 2
        val centerY = (top + bottom) / 2
        val wheelRadius = (canvas.width - paddingLeft - paddingRight) / 2 - mUnreachedWidth / 2
        if (isHasCache) {
            if (mCacheCanvas == null) {
                buildCache(centerX, centerY, wheelRadius)
            }
            canvas.drawBitmap(mCacheBitmap!!, 0f, 0f, null)
        } else {
            canvas.drawCircle(centerX, centerY, wheelRadius, mWheelPaint!!)
        }
        canvas.drawArc(
            RectF(left, top, right, bottom),
            -90f,
            mCurAngle.toFloat(),
            false,
            mReachedPaint!!
        )
        canvas.drawCircle(mWheelCurX, mWheelCurY, mPointerRadius, mPointerPaint!!)
    }

    private fun buildCache(centerX: Float, centerY: Float, wheelRadius: Float) {
        mCacheBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
       // mCacheCanvas = Canvas(mCacheBitmap)
        mCacheCanvas!!.drawCircle(centerX, centerY, wheelRadius, mWheelPaint!!)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        return if (isCanTouch && (event.action == MotionEvent.ACTION_MOVE || isTouch(x, y))) {
            var cos = computeCos(x, y)
            val angle: Double
            angle = if (x < width / 2) {
                Math.PI * RADIAN + Math.acos(cos.toDouble()) * RADIAN
            } else {
                Math.PI * RADIAN - Math.acos(cos.toDouble()) * RADIAN
            }
            if (isScrollOneCircle) {
                if (mCurAngle > 270 && angle < 90) {
                    mCurAngle = 360.0
                    cos = -1f
                } else if (mCurAngle < 90 && angle > 270) {
                    mCurAngle = 0.0
                    cos = -1f
                } else {
                    mCurAngle = angle
                }
            } else {
                mCurAngle = angle
            }
            mCurProcess = selectedValue
            refershWheelCurPosition(cos.toDouble())
            if (mChangListener != null && event.action and (MotionEvent.ACTION_MOVE or MotionEvent.ACTION_UP) > 0) {
                mChangListener!!.onChanged(this, mCurProcess)
            }
            invalidate()
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    private fun isTouch(x: Float, y: Float): Boolean {
        val radius = (width - paddingLeft - paddingRight + circleWidth) / 2.toDouble()
        val centerX = width / 2.toDouble()
        val centerY = height / 2.toDouble()
        return Math.pow(centerX - x, 2.0) + Math.pow(centerY - y, 2.0) < radius * radius
    }

    private val circleWidth: Float
        private get() = Math.max(mUnreachedWidth, Math.max(mReachedWidth, mPointerRadius))

    private fun refershUnreachedWidth() {
        mUnreachedRadius = (measuredWidth - paddingLeft - paddingRight - mUnreachedWidth) / 2
    }

    private fun refershWheelCurPosition(cos: Double) {
        mWheelCurX = calcXLocationInWheel(mCurAngle, cos)
        mWheelCurY = calcYLocationInWheel(cos)
    }

    private fun refershPosition() {
        mCurAngle = mCurProcess / mMaxProcess * 360.0
        val cos = -Math.cos(Math.toRadians(mCurAngle))
        refershWheelCurPosition(cos)
    }

    private fun calcXLocationInWheel(angle: Double, cos: Double): Float {
        return if (angle < 180) {
            (measuredWidth / 2 + Math.sqrt(1 - cos * cos) * mUnreachedRadius).toFloat()
        } else {
            (measuredWidth / 2 - Math.sqrt(1 - cos * cos) * mUnreachedRadius).toFloat()
        }
    }

    private fun calcYLocationInWheel(cos: Double): Float {
        return measuredWidth / 2 + mUnreachedRadius * cos.toFloat()
    }

    private fun computeCos(x: Float, y: Float): Float {
        val width = x - width / 2
        val height = y - height / 2
        val slope = Math.sqrt(width * width + height * height.toDouble())
            .toFloat()
        return height / slope
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(INATANCE_STATE, super.onSaveInstanceState())
        bundle.putDouble(INSTANCE_MAX_PROCESS, mMaxProcess)
        bundle.putDouble(INSTANCE_CUR_PROCESS, mCurProcess)
        bundle.putInt(INSTANCE_REACHED_COLOR, mReachedColor)
        bundle.putFloat(INSTANCE_REACHED_WIDTH, mReachedWidth)
        bundle.putBoolean(INSTANCE_REACHED_CORNER_ROUND, isHasReachedCornerRound)
        bundle.putInt(INSTANCE_UNREACHED_COLOR, mUnreachedColor)
        bundle.putFloat(INSTANCE_UNREACHED_WIDTH, mUnreachedWidth)
        bundle.putInt(INSTANCE_POINTER_COLOR, mPointerColor)
        bundle.putFloat(INSTANCE_POINTER_RADIUS, mPointerRadius)
        bundle.putBoolean(INSTANCE_POINTER_SHADOW, isHasPointerShadow)
        bundle.putFloat(INSTANCE_POINTER_SHADOW_RADIUS, mPointerShadowRadius)
        bundle.putBoolean(INSTANCE_WHEEL_SHADOW, isHasWheelShadow)
        bundle.putFloat(INSTANCE_WHEEL_SHADOW_RADIUS, mPointerShadowRadius)
        bundle.putBoolean(INSTANCE_WHEEL_HAS_CACHE, isHasCache)
        bundle.putBoolean(INSTANCE_WHEEL_CAN_TOUCH, isCanTouch)
        bundle.putBoolean(INSTANCE_WHEEL_SCROLL_ONLY_ONE_CIRCLE, isScrollOneCircle)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            val bundle = state
            super.onRestoreInstanceState(bundle.getParcelable(INATANCE_STATE))
            mMaxProcess = bundle.getInt(INSTANCE_MAX_PROCESS).toDouble()
            mMinProcess = bundle.getInt(INSTANCE_MIN_PROCESS).toDouble()
            mCurProcess = bundle.getInt(INSTANCE_CUR_PROCESS).toDouble()
            mReachedColor = bundle.getInt(INSTANCE_REACHED_COLOR)
            mReachedWidth = bundle.getFloat(INSTANCE_REACHED_WIDTH)
            isHasReachedCornerRound = bundle.getBoolean(INSTANCE_REACHED_CORNER_ROUND)
            mUnreachedColor = bundle.getInt(INSTANCE_UNREACHED_COLOR)
            mUnreachedWidth = bundle.getFloat(INSTANCE_UNREACHED_WIDTH)
            mPointerColor = bundle.getInt(INSTANCE_POINTER_COLOR)
            mPointerRadius = bundle.getFloat(INSTANCE_POINTER_RADIUS)
            isHasPointerShadow = bundle.getBoolean(INSTANCE_POINTER_SHADOW)
            mPointerShadowRadius = bundle.getFloat(INSTANCE_POINTER_SHADOW_RADIUS)
            isHasWheelShadow = bundle.getBoolean(INSTANCE_WHEEL_SHADOW)
            mPointerShadowRadius = bundle.getFloat(INSTANCE_WHEEL_SHADOW_RADIUS)
            isHasCache = bundle.getBoolean(INSTANCE_WHEEL_HAS_CACHE)
            isCanTouch = bundle.getBoolean(INSTANCE_WHEEL_CAN_TOUCH)
            isScrollOneCircle = bundle.getBoolean(INSTANCE_WHEEL_SCROLL_ONLY_ONE_CIRCLE)
            initPaints()
        } else {
            super.onRestoreInstanceState(state)
        }
        if (mChangListener != null) {
            mChangListener!!.onChanged(this, mCurProcess)
        }
    }

    private val selectedValue: Double
        private get() = mMaxProcess * (mCurAngle.toFloat() / 360)
    var curProcess: Double
        get() = mCurProcess
        set(curProcess) {
            mCurProcess = if (curProcess > mMaxProcess) mMaxProcess else curProcess
            if (mChangListener != null) {
                mChangListener!!.onChanged(this, curProcess)
            }
            refershPosition()
            invalidate()
        }
    var minProcess: Double
        get() = mMinProcess
        set(minProcess) {
            mMinProcess = minProcess
            refershPosition()
            invalidate()
        }
    var maxProcess: Double
        get() = mMaxProcess
        set(maxProcess) {
            mMaxProcess = maxProcess
            refershPosition()
            invalidate()
        }
    var reachedColor: Int
        get() = mReachedColor
        set(reachedColor) {
            mReachedColor = reachedColor
            mReachedPaint!!.color = reachedColor
            mReachedEdgePaint!!.color = reachedColor
            invalidate()
        }
    var unreachedColor: Int
        get() = mUnreachedColor
        set(unreachedColor) {
            mUnreachedColor = unreachedColor
            mWheelPaint!!.color = unreachedColor
            invalidate()
        }
    var reachedWidth: Float
        get() = mReachedWidth
        set(reachedWidth) {
            mReachedWidth = reachedWidth
            mReachedPaint!!.strokeWidth = reachedWidth
            mReachedEdgePaint!!.strokeWidth = reachedWidth
            invalidate()
        }

    fun isHasReachedCornerRound(): Boolean {
        return isHasReachedCornerRound
    }

    fun setHasReachedCornerRound(hasReachedCornerRound: Boolean) {
        isHasReachedCornerRound = hasReachedCornerRound
        mReachedPaint!!.strokeCap =
            if (hasReachedCornerRound) Paint.Cap.ROUND else Paint.Cap.BUTT
        invalidate()
    }

    var unreachedWidth: Float
        get() = mUnreachedWidth
        set(unreachedWidth) {
            mUnreachedWidth = unreachedWidth
            mWheelPaint!!.strokeWidth = unreachedWidth
            refershUnreachedWidth()
            invalidate()
        }
    var pointerColor: Int
        get() = mPointerColor
        set(pointerColor) {
            mPointerColor = pointerColor
            mPointerPaint!!.color = pointerColor
        }
    var pointerRadius: Float
        get() = mPointerRadius
        set(pointerRadius) {
            mPointerRadius = pointerRadius
            mPointerPaint!!.strokeWidth = pointerRadius
            invalidate()
        }

    fun setWheelShadow(wheelShadow: Float) {
        wheelShadowRadius = wheelShadow
        if (wheelShadow == 0f) {
            isHasWheelShadow = false
            mWheelPaint!!.clearShadowLayer()
            mCacheCanvas = null
            mCacheBitmap!!.recycle()
            mCacheBitmap = null
        } else {
            mWheelPaint!!.setShadowLayer(
                wheelShadowRadius,
                mDefShadowOffset,
                mDefShadowOffset,
                Color.DKGRAY
            )
            setSoftwareLayer()
        }
        invalidate()
    }

    var pointerShadowRadius: Float
        get() = mPointerShadowRadius
        set(pointerShadowRadius) {
            mPointerShadowRadius = pointerShadowRadius
            if (mPointerShadowRadius == 0f) {
                isHasPointerShadow = false
                mPointerPaint!!.clearShadowLayer()
            } else {
                mPointerPaint!!.setShadowLayer(
                    pointerShadowRadius,
                    mDefShadowOffset,
                    mDefShadowOffset,
                    Color.DKGRAY
                )
                setSoftwareLayer()
            }
            invalidate()
        }

    fun setOnSeekBarChangeListener(listener: OnSeekBarChangeListener?) {
        mChangListener = listener
    }

    interface OnSeekBarChangeListener {
        fun onChanged(seekbar: CircleSeekBar?, curValue: Double)
    }

    companion object {
        private const val RADIAN = 180 / Math.PI
        private const val INATANCE_STATE = "state"
        private const val INSTANCE_MAX_PROCESS = "max_process"
        private const val INSTANCE_MIN_PROCESS = "min_process"
        private const val INSTANCE_CUR_PROCESS = "cur_process"
        private const val INSTANCE_REACHED_COLOR = "reached_color"
        private const val INSTANCE_REACHED_WIDTH = "reached_width"
        private const val INSTANCE_REACHED_CORNER_ROUND = "reached_corner_round"
        private const val INSTANCE_UNREACHED_COLOR = "unreached_color"
        private const val INSTANCE_UNREACHED_WIDTH = "unreached_width"
        private const val INSTANCE_POINTER_COLOR = "pointer_color"
        private const val INSTANCE_POINTER_RADIUS = "pointer_radius"
        private const val INSTANCE_POINTER_SHADOW = "pointer_shadow"
        private const val INSTANCE_POINTER_SHADOW_RADIUS = "pointer_shadow_radius"
        private const val INSTANCE_WHEEL_SHADOW = "wheel_shadow"
        private const val INSTANCE_WHEEL_SHADOW_RADIUS = "wheel_shadow_radius"
        private const val INSTANCE_WHEEL_HAS_CACHE = "wheel_has_cache"
        private const val INSTANCE_WHEEL_CAN_TOUCH = "wheel_can_touch"
        private const val INSTANCE_WHEEL_SCROLL_ONLY_ONE_CIRCLE = "wheel_scroll_only_one_circle"
    }

    init {
        initAttrs(attrs, defStyleAttr)
        initPadding()
        initPaints()
    }
}
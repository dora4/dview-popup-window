package dora.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes

class DoraPopupWindow private constructor(private val context: Context) :
    PopupWindow(context) {

    private var contentViewLayout: View? = null
    private var layoutId: Int? = null

    private var cornerRadiusDp = 12f
    private var backgroundColor = Color.WHITE

    private var onBindView: ((View) -> Unit)? = null

    private var onShow: (() -> Unit)? = null

    private var onDismiss: (() -> Unit)? = null

    fun contentView(@LayoutRes layoutId: Int): DoraPopupWindow {
        this.layoutId = layoutId
        return this
    }

    fun contentView(view: View): DoraPopupWindow {
        this.contentViewLayout = view
        return this
    }

    fun onBind(block: (View) -> Unit): DoraPopupWindow {
        this.onBindView = block
        return this
    }

    fun onShow(block: () -> Unit): DoraPopupWindow {
        this.onShow = block
        return this
    }

    fun onDismiss(block: () -> Unit): DoraPopupWindow {
        this.onDismiss = block
        return this
    }

    fun cornerRadius(dp: Float): DoraPopupWindow {
        cornerRadiusDp = dp
        return this
    }

    fun backgroundColor(@ColorInt color: Int): DoraPopupWindow {
        backgroundColor = color
        return this
    }

    fun build(): DoraPopupWindow {
        val density = context.resources.displayMetrics.density
        val radiusPx = cornerRadiusDp * density
        if (contentViewLayout == null && layoutId != null) {
            contentViewLayout =
                LayoutInflater.from(context).inflate(layoutId!!, null)
        }
        contentViewLayout?.let { onBindView?.invoke(it) }
        val bg = GradientDrawable().apply {
            cornerRadius = radiusPx
            setColor(backgroundColor)
        }
        val wrapper = FrameLayout(context).apply {
            setPadding(
                radiusPx.toInt(),
                radiusPx.toInt(),
                radiusPx.toInt(),
                radiusPx.toInt()
            )
            background = bg
        }
        contentViewLayout?.let { wrapper.addView(it) }
        contentView = wrapper
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        setBackgroundDrawable(BitmapDrawable())
        isFocusable = true
        isOutsideTouchable = true
        elevation = 8f
        setOnDismissListener {
            onDismiss?.invoke()
        }
        return this
    }

    fun show(anchor: View, xOff: Int = 0, yOff: Int = 0) {
        showAsDropDown(anchor, xOff, yOff)
        onShow?.invoke()
    }

    fun showAbove(anchor: View) {
        contentView.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )
        val height = contentView.measuredHeight

        showAsDropDown(anchor, 0, -anchor.height - height)
        onShow?.invoke()
    }

    fun showCenter(parent: View) {
        showAtLocation(parent, Gravity.CENTER, 0, 0)
        onShow?.invoke()
    }

    companion object {
        @JvmStatic
        fun create(context: Context): DoraPopupWindow {
            return DoraPopupWindow(context)
        }
    }
}
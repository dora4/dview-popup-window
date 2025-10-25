package dora.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes

class DoraPopupWindow private constructor(private val context: Context) :
    PopupWindow(context) {

    private var contentViewLayout: View? = null
    private var cornerRadius = 16f
    private var backgroundColor = Color.WHITE

    fun contentView(@LayoutRes layoutId: Int): DoraPopupWindow {
        contentViewLayout = LayoutInflater.from(context).inflate(layoutId, null)
        return this
    }

    fun contentView(view: View): DoraPopupWindow {
        contentViewLayout = view
        return this
    }

    /** 设置圆角半径，单位 dp */
    fun cornerRadius(dp: Float): DoraPopupWindow {
        cornerRadius = dp
        return this
    }

    /** 设置背景颜色 */
    fun backgroundColor(@ColorInt color: Int): DoraPopupWindow {
        backgroundColor = color
        return this
    }

    /** 创建 PopupWindow */
    fun build(): DoraPopupWindow {
        val radiusPx = context.resources.displayMetrics.density * cornerRadius
        val bg = GradientDrawable().apply {
            cornerRadius = radiusPx
            setColor(backgroundColor)
        }

        val wrapper = contentViewLayout ?: View(context)
        wrapper.background = bg

        contentView = wrapper
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isFocusable = true
        isOutsideTouchable = true
        elevation = 12f

        return this
    }

    /** 在某个锚点 View 下方显示 */
    fun show(anchor: View, xOff: Int = 0, yOff: Int = 0) {
        showAsDropDown(anchor, xOff, yOff)
    }

    companion object {
        @JvmStatic
        fun create(context: Context): DoraPopupWindow {
            return DoraPopupWindow(context)
        }
    }
}

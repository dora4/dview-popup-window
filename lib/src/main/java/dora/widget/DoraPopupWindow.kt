package dora.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
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
    private var cornerRadiusDp = 12f
    private var backgroundColor = Color.WHITE

    fun contentView(@LayoutRes layoutId: Int): DoraPopupWindow {
        contentViewLayout = LayoutInflater.from(context).inflate(layoutId, null)
        return this
    }

    fun contentView(view: View): DoraPopupWindow {
        contentViewLayout = view
        return this
    }

    /** 设置背景圆角半径（单位 dp） */
    fun cornerRadius(dp: Float): DoraPopupWindow {
        cornerRadiusDp = dp
        return this
    }

    /** 设置背景颜色 */
    fun backgroundColor(@ColorInt color: Int): DoraPopupWindow {
        backgroundColor = color
        return this
    }

    /** 创建 PopupWindow */
    fun build(): DoraPopupWindow {
        val density = context.resources.displayMetrics.density
        val radiusPx = cornerRadiusDp * density

        // 创建圆角背景
        val bg = GradientDrawable().apply {
            cornerRadius = radiusPx
            setColor(backgroundColor)
        }

        // 外层包裹 FrameLayout，用于设置内边距
        val wrapper = FrameLayout(context).apply {
            setPadding(radiusPx.toInt(), radiusPx.toInt(), radiusPx.toInt(), radiusPx.toInt())
            background = bg
        }

        // 将内容添加到 wrapper
        contentViewLayout?.let { wrapper.addView(it) }

        // 设置 PopupWindow 内容
        contentView = wrapper
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT

        // PopupWindow 本身背景透明
        setBackgroundDrawable(BitmapDrawable())
        isFocusable = true
        isOutsideTouchable = true
        elevation = 0f // 去掉阴影

        return this
    }

    /** 在锚点 View 下方显示 */
    fun show(anchor: View, xOff: Int = 0, yOff: Int = 0) {
        showAsDropDown(anchor, xOff, yOff)
    }

    fun show(anchor: View) {
        show(anchor, 0, 0)
    }

    companion object {
        fun create(context: Context): DoraPopupWindow {
            return DoraPopupWindow(context)
        }
    }
}

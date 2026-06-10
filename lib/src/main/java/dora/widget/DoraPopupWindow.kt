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

/**
 * DoraPopupWindow
 *
 * 轻量级通用 Popup 组件，支持：
 * - Builder 风格链式调用
 * - 自定义布局 / View
 * - 圆角背景
 * - 多种显示方式（上下左右 / 居中 / 全屏定位）
 *
 * 适用场景：
 * - IM 长按菜单（复制 / 删除 / 转发）
 * - 气泡菜单（类似微信 / Telegram）
 * - 底部弹窗（ActionSheet）
 * - 顶部提示 / 中间弹层
 */
class DoraPopupWindow private constructor(private val context: Context) :
    PopupWindow(context) {

    /** 内容 View（优先使用） */
    private var contentViewLayout: View? = null

    /** 布局资源 ID（备用） */
    private var layoutId: Int? = null

    /** 圆角（dp） */
    private var cornerRadiusDp = 12f

    /** 背景颜色 */
    private var backgroundColor = Color.WHITE

    /** View 绑定回调（类似 ViewBinding） */
    private var onBindView: (DoraPopupWindow.(View) -> Unit)? = null

    /** 显示回调 */
    private var onShow: (DoraPopupWindow.() -> Unit)? = null

    /** 消失回调 */
    private var onDismiss: (DoraPopupWindow.() -> Unit)? = null

    /**
     * 设置内容布局（layoutId）。
     */
    fun contentView(@LayoutRes layoutId: Int): DoraPopupWindow {
        this.layoutId = layoutId
        return this
    }

    /**
     * 设置内容 View（优先级高于 layoutId）。
     */
    fun contentView(view: View): DoraPopupWindow {
        this.contentViewLayout = view
        return this
    }

    /**
     * 绑定 View（用于初始化 UI / 点击事件）。
     */
    fun onBind(block: DoraPopupWindow.(View) -> Unit): DoraPopupWindow {
        this.onBindView = block
        return this
    }

    /**
     * 显示时回调。
     */
    fun onShow(block: DoraPopupWindow.() -> Unit): DoraPopupWindow {
        this.onShow = block
        return this
    }

    /**
     * 消失时回调。
     */
    fun onDismiss(block: DoraPopupWindow.() -> Unit): DoraPopupWindow {
        this.onDismiss = block
        return this
    }

    /**
     * 设置圆角（dp）。
     */
    fun cornerRadius(dp: Float): DoraPopupWindow {
        cornerRadiusDp = dp
        return this
    }

    /**
     * 设置背景颜色。
     */
    fun backgroundColor(@ColorInt color: Int): DoraPopupWindow {
        backgroundColor = color
        return this
    }

    /**
     * 构建 PopupWindow。
     */
    fun build(): DoraPopupWindow {
        val density = context.resources.displayMetrics.density
        val radiusPx = cornerRadiusDp * density

        // 初始化内容 View
        if (contentViewLayout == null && layoutId != null) {
            contentViewLayout =
                LayoutInflater.from(context).inflate(layoutId!!, null)
        }

        // 绑定 View
        contentViewLayout?.let { onBindView?.invoke(this, it) }

        // 创建圆角背景
        val bg = GradientDrawable().apply {
            cornerRadius = radiusPx
            setColor(backgroundColor)
        }

        // 包裹一层容器（用于 padding + 圆角）
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

        // 设置 PopupWindow 参数
        contentView = wrapper
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT

        // 必须设置，否则点击外部不消失
        setBackgroundDrawable(BitmapDrawable())

        isFocusable = true
        isOutsideTouchable = true
        elevation = 8f

        // 消失监听
        setOnDismissListener {
            onDismiss?.invoke(this)
        }

        return this
    }

    /**
     * 显示在 anchor 下方（默认方式）。
     */
    fun show(anchor: View, xOff: Int = 0, yOff: Int = 0) {
        showAsDropDown(anchor, xOff, yOff)
        onShow?.invoke(this)
    }

    /**
     * 显示在 anchor 上方（左对齐）。
     * ⚠️ 使用 showAtLocation，支持更自由定位
     */
    fun showAbove(anchor: View) {
        val location = IntArray(2)
        anchor.getLocationOnScreen(location)

        contentView.measure(
            View.MeasureSpec.makeMeasureSpec(
                anchor.resources.displayMetrics.widthPixels,
                View.MeasureSpec.AT_MOST
            ),
            View.MeasureSpec.UNSPECIFIED
        )

        val popupHeight = contentView.measuredHeight

        showAtLocation(
            anchor,
            Gravity.TOP or Gravity.START,
            0,
            location[1] - popupHeight
        )
        onShow?.invoke(this)
    }

    /**
     * 显示在屏幕顶部（适合通知条 / 顶部提示）。
     */
    fun showTop(parent: View) {
        showAtLocation(parent, Gravity.TOP, 0, 0)
        onShow?.invoke(this)
    }

    /**
     * 显示在屏幕底部（类似 iOS ActionSheet）。
     */
    fun showBottom(parent: View) {
        showAtLocation(parent, Gravity.BOTTOM, 0, 0)
        onShow?.invoke(this)
    }

    /**
     * 显示在指定坐标（绝对定位）。
     */
    fun showAt(x: Int, y: Int, parent: View) {
        showAtLocation(parent, Gravity.TOP or Gravity.START, x, y)
        onShow?.invoke(this)
    }

    /**
     * 显示在 anchor 左侧。
     */
    fun showLeft(anchor: View, margin: Int = 0) {
        val location = IntArray(2)
        anchor.getLocationOnScreen(location)

        contentView.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )

        val popupWidth = contentView.measuredWidth

        showAtLocation(
            anchor,
            Gravity.TOP or Gravity.START,
            location[0] - popupWidth - margin,
            location[1]
        )
        onShow?.invoke(this)
    }

    /**
     * 显示在 anchor 右侧。
     */
    fun showRight(anchor: View, margin: Int = 0) {
        val location = IntArray(2)
        anchor.getLocationOnScreen(location)

        showAtLocation(
            anchor,
            Gravity.TOP or Gravity.START,
            location[0] + anchor.width + margin,
            location[1]
        )
        onShow?.invoke(this)
    }

    /**
     * 显示在 anchor 上方（水平居中）。
     */
    fun showAboveCenter(anchor: View, margin: Int = 0) {
        val location = IntArray(2)
        anchor.getLocationOnScreen(location)

        contentView.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )

        val popupWidth = contentView.measuredWidth
        val popupHeight = contentView.measuredHeight

        val x = location[0] + anchor.width / 2 - popupWidth / 2
        val y = location[1] - popupHeight - margin

        showAtLocation(anchor, Gravity.TOP or Gravity.START, x, y)
        onShow?.invoke(this)
    }

    /**
     * 显示在 anchor 下方（水平居中）。
     */
    fun showBelowCenter(anchor: View, margin: Int = 0) {
        val location = IntArray(2)
        anchor.getLocationOnScreen(location)

        contentView.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )

        val popupWidth = contentView.measuredWidth

        val x = location[0] + anchor.width / 2 - popupWidth / 2
        val y = location[1] + anchor.height + margin

        showAtLocation(anchor, Gravity.TOP or Gravity.START, x, y)
        onShow?.invoke(this)
    }

    /**
     * 屏幕居中显示（类似 Dialog）。
     */
    fun showCenter(parent: View) {
        showAtLocation(parent, Gravity.CENTER, 0, 0)
        onShow?.invoke(this)
    }

    companion object {

        @JvmStatic
        fun create(context: Context): DoraPopupWindow {
            return DoraPopupWindow(context)
        }
    }
}

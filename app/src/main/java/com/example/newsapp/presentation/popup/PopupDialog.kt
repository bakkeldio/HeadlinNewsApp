package com.example.newsapp.presentation.popup

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ListAdapter
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import com.example.newsapp.R

class PopupDialog private constructor(
    private var context: Context,
    private var anchor: View?,
    @DrawableRes private var background: Int?,
    @LayoutRes private var listItemLayout: Int = android.R.layout.simple_list_item_1,
    private var items: List<String>?,
    private var adapter: ArrayAdapter<*>?,
    private var clickListener: AdapterView.OnItemClickListener?,
    private var horizontalOffsetValue: Int = 0,
    private var verticalOffsetValue: Int = 0,
    private var rightToRightOfAnchor: Boolean?
) : ListPopupWindow(context) {

    private constructor(builder: Builder) : this(
        context = builder.context,
        anchor = builder.anchorView,
        background = builder.background,
        listItemLayout = builder.listItemLayout,
        items = builder.items,
        adapter = builder.adapter,
        clickListener = builder.clickListener,
        horizontalOffsetValue = builder.horizontalOffset,
        verticalOffsetValue = builder.verticalOffset,
        rightToRightOfAnchor = builder.rightToRightOfAnchor
    ) {
        initPopupWindow()
    }

    private fun initPopupWindow() {
        this.apply {
            /**
             * Если хотим использовать adapter по умолчанию, задаем items
             */
            items?.let {
                adapter = ArrayAdapter(context, listItemLayout, it)
            }

            adapter?.let {
                setAdapter(it)
            }
            clickListener?.let {
                setOnItemClickListener(it)
            }
            anchorView = anchor
            width = measureContentWidth(context,
                adapter) + context.resources.getDimensionPixelSize(R.dimen.dp24)
            height = WRAP_CONTENT
            isModal = true

            horizontalOffset = horizontalOffsetValue
            verticalOffset = verticalOffsetValue

            rightToRightOfAnchor?.let {
                if (it) {
                    horizontalOffset = -width + (anchor?.width ?: 0)
                }
            }

            background?.let {
                val backgroundDrawable = ContextCompat.getDrawable(context, it)
                setBackgroundDrawable(backgroundDrawable)
            }
        }
    }

    /**
     * By default, the ListPopupWindow needs an anchorView and the width of the view will be determined by the anchorView.
     * So, we have to calculate it manually for that. Calculating the contentWidth by passing in the ListAdapter.
     * This method is basically just comparing and getting the longest width and then return that value.
     */
    private fun measureContentWidth(context: Context, adapter: ListAdapter?): Int {
        val measureParentViewGroup = FrameLayout(context)
        var itemView: View? = null

        var maxWidth = 0
        var itemType = 0

        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

        adapter?.let { arrayAdapter ->
            for (index in 0 until arrayAdapter.count) {
                val positionType = arrayAdapter.getItemViewType(index)

                if (positionType != itemType) {
                    itemType = positionType
                    itemView = null
                }

                itemView = arrayAdapter.getView(index, itemView, measureParentViewGroup)
                itemView?.let {
                    it.apply {
                        measure(widthMeasureSpec, heightMeasureSpec)
                        val itemWidth = measuredWidth
                        itemWidth.let { iw ->
                            if (iw > maxWidth) {
                                maxWidth = iw
                            }
                        }
                    }
                }
            }
        }
        return maxWidth
    }


    class Builder(internal val context: Context) {

        var background: Int? = null
            private set

        fun backgroundDrawable(@DrawableRes background: Int?) =
            apply { this.background = background }

        var listItemLayout: Int = android.R.layout.simple_list_item_1
            private set

        fun listItemLayout(@LayoutRes listItemLayout: Int) =
            apply { this.listItemLayout = listItemLayout }

        var items: List<String>? = null
            private set

        fun items(items: List<String>?) = apply { this.items = items }

        var adapter: ArrayAdapter<*>? = null
            private set

        fun adapter(adapter: ArrayAdapter<*>?) = apply { this.adapter = adapter }

        var clickListener: AdapterView.OnItemClickListener? = null
            private set

        fun onItemClickListener(clickListener: AdapterView.OnItemClickListener) = apply {
            this.clickListener = clickListener
        }

        var horizontalOffset: Int = 0
            private set

        fun horizontalOffset(horizontalOffset: Int) =
            apply { this.horizontalOffset = horizontalOffset }

        var rightToRightOfAnchor: Boolean? = null
            private set

        fun rightToRightOfAnchor(status: Boolean) = apply { rightToRightOfAnchor = status }

        var verticalOffset: Int = 0
            private set

        fun verticalOffset(verticalOffset: Int) =
            apply { this.verticalOffset = verticalOffset }

        var anchorView: View? = null
            private set

        fun anchorView(anchorView: View?) = apply { this.anchorView = anchorView }

        fun build() = PopupDialog(builder = this)
    }
}
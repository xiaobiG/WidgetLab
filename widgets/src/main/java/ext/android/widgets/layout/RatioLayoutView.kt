package ext.android.widgets.layout

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import ext.android.widgets.R

class RatioLayoutView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var ratio: Float = 0.75f

    init {
        context.obtainStyledAttributes(attrs, R.styleable.RatioLayoutView, defStyleAttr, 0).apply {
            try {
                ratio = getFloat(R.styleable.RatioLayoutView_layoutRatio, ratio)
            } finally {
                recycle()
            }
        }
    }

    fun setRatio(newRatio: Float) {
        if (newRatio > 0f && ratio != newRatio) {
            ratio = newRatio
            requestLayout()
        }
    }

    fun getRatio(): Float = ratio

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize > 0) {
            val height = (widthSize * ratio).toInt().coerceAtLeast(0)
            val heightMeasureSpecFixed = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            super.onMeasure(widthMeasureSpec, heightMeasureSpecFixed)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        return SavedState(superState).apply {
            savedRatio = ratio
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            ratio = state.savedRatio
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private class SavedState : BaseSavedState {
        var savedRatio: Float = 0.75f

        constructor(superState: Parcelable?) : super(superState)

        private constructor(source: Parcel) : super(source) {
            savedRatio = source.readFloat()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeFloat(savedRatio)
        }

        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel): SavedState = SavedState(source)
                override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
            }
        }
    }
}

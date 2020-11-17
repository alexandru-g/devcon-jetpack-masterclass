package com.softvision.jetpackmasterclass.cheats

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.SpannedString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.text.*
import androidx.fragment.app.Fragment


/**
 * Class that adds an extra Modifier capability to Text. It can also contain a list of other
 * SpannedTexts that it modifies. You can think of the SpannedText as a tree in which each node
 * is a modifier and leafs are standard Texts.
 *
 * Usage
 * You basically use one of 2 methods: buildSpannedString (for Views) or buildSpannedStrong (for
 * anything other than a View)
 *
 * In Fragments:
 *
 * If building the span in the Fragment:
 * textView.text = buildSpannedString {
 *   append(R.string.first_part)
 *   bold {
 *     state.secondPart
 *   }
 * }
 *
 * If the span is build somewhere else (just like a normal Text):
 * textView.text = getString(someSpannedStrong)
 *
 * In ViewModels:
 *
 * buildSpannedStrong {
 *   append(R.string.dynamic_string)
 *   color(R.color.primary) {
 *     append(Strong("Some other Text"))
 *   }
 * }
 */
class SpannedStrong(private val strong: Strong = NONE) : Strong(strong) {

    /**
     * Hide the Modifier class from users as we'll only expose functions
     */
    private constructor(strong: Strong, modifier: Modifier = Modifier.None): this(strong) {
        this.modifier = modifier
    }

    private var modifier: Modifier = Modifier.None

    private sealed class Modifier {
        object None : Modifier()
        object Bold : Modifier()
        object Italic : Modifier()
        object Underline : Modifier()
        data class Color(@ColorRes val colorRes: Int) : Modifier()
        data class BackgroundColor(@ColorRes val colorRes: Int) : Modifier()
        object StrikeThrough : Modifier()
        data class Scale(val proportion: Float) : Modifier()
        object Superscript : Modifier()
        object Subscript : Modifier()
        data class Clickable(val onClick: () -> Unit) : Modifier()

        override fun toString(): String {
            return when (this) {
                None -> "none"
                Bold -> "bold"
                Italic -> "italic"
                Underline -> "underline"
                is Color -> "color"
                is BackgroundColor -> "backgroundColor"
                StrikeThrough -> "strikethrough"
                is Scale -> "scale"
                Superscript -> "superscript"
                Subscript -> "subscript"
                is Clickable -> "clickable"
            }
        }
    }

    private val spans = mutableListOf<SpannedStrong>()

    /**
     * Use the standard SpannableStringBuilder API to build our String on demand.
     */
    internal fun build(context: Context): SpannedString {
        val builder = SpannableStringBuilder()
        if (strong == NONE) {
            spans.forEach { span ->
                when (val modifier = span.modifier) {
                    Modifier.None -> builder.append(span.build(context))
                    Modifier.Bold -> builder.bold { append(span.build(context)) }
                    Modifier.Italic -> builder.italic { append(span.build(context)) }
                    Modifier.Underline -> builder.underline { append(span.build(context)) }
                    is Modifier.Color -> {
                        builder.color(ContextCompat.getColor(context, modifier.colorRes)) {
                            append(span.build(context))
                        }
                    }
                    is Modifier.BackgroundColor -> {
                        builder.backgroundColor(
                            ContextCompat.getColor(context, modifier.colorRes)) {
                            append(span.build(context))
                        }
                    }
                    Modifier.StrikeThrough -> builder.strikeThrough { append(span.build(context)) }
                    is Modifier.Scale -> builder.scale(modifier.proportion) { append(span.build(context)) }
                    Modifier.Superscript -> builder.superscript { append(span.build(context)) }
                    Modifier.Subscript -> builder.subscript { append(span.build(context)) }
                    is Modifier.Clickable -> builder.clickable(modifier.onClick) { append(span.build(context)) }
                }
            }
        } else {
            builder.append(strong.get(context.resources))
        }
        return SpannedString(builder)
    }

    fun bold(builderAction: SpannedStrong.() -> Unit) {
        spans.add(SpannedStrong(NONE, Modifier.Bold).apply { builderAction() })
    }

    fun italic(builderAction: SpannedStrong.() -> Unit) {
        spans.add(SpannedStrong(NONE, Modifier.Italic).apply { builderAction() })
    }

    fun underline(builderAction: SpannedStrong.() -> Unit) {
        spans.add(SpannedStrong(NONE, Modifier.Underline).apply { builderAction() })
    }

    fun color(@ColorRes colorRes: Int, builderAction: SpannedStrong.() -> Unit) {
        spans.add(SpannedStrong(NONE, Modifier.Color(colorRes)).apply { builderAction() })
    }

    fun scale(proportion: Float, builderAction: SpannedStrong.() -> Unit) {
        spans.add(SpannedStrong(NONE, Modifier.Scale(proportion)).apply { builderAction() })
    }

    fun backgroundColor(@ColorRes colorRes: Int, builderAction: SpannedStrong.() -> Unit) {
        spans.add(SpannedStrong(NONE, Modifier.BackgroundColor(colorRes)).apply { builderAction() })
    }

    fun strikeThrough(builderAction: SpannedStrong.() -> Unit) {
        spans.add(SpannedStrong(NONE, Modifier.StrikeThrough).apply { builderAction() })
    }

    fun superscript(builderAction: SpannedStrong.() -> Unit) {
        spans.add(SpannedStrong(NONE, Modifier.Superscript).apply { builderAction() })
    }

    fun subscript(builderAction: SpannedStrong.() -> Unit) {
        spans.add(SpannedStrong(NONE, Modifier.Subscript).apply { builderAction() })
    }

    fun clickable(onClick: () -> Unit, builderAction: SpannedStrong.() -> Unit) {
        spans.add(SpannedStrong(NONE, Modifier.Clickable(onClick)).apply { builderAction() })
    }

    fun append(strong: Strong) {
        spans.add(SpannedStrong(strong))
    }

    fun append(@StringRes resourceId: Int) {
        append(resourceId.toStrong())
    }

    fun append(string: String) {
        append(string.toStrong())
    }

    override fun equals(other: Any?): Boolean = other is SpannedStrong
            && strong == other.strong
            && modifier == other.modifier
            && spans == other.spans
            && super.equals(other)

    override fun hashCode(): Int {
        return super.hashCode() + strong.hashCode() + modifier.hashCode() + spans.hashCode()
    }

    override fun toString(): String {
        return "$modifier($strong) with $spans"
    }
}

fun Fragment.buildSpannedString(builderAction: SpannedStrong.() -> Unit): SpannedString =
    SpannedStrong(Strong.NONE).apply { builderAction() }.build(requireContext())

fun buildSpannedStrong(builderAction: SpannedStrong.() -> Unit): SpannedStrong =
    SpannedStrong(Strong.NONE).apply { builderAction() }

/**
 * Wrap appended text in [builderAction] in a [ClickableSpan].
 *
 * Make sure the TextView you are setting this spannable also uses the link movement method and
 * that is disables any link highlights:
 * textView.movementMethod = LinkMovementMethod.getInstance()
 * textView.highlightColor = Color.TRANSPARENT
 *
 * @see SpannableStringBuilder.inSpans
 */
inline fun SpannableStringBuilder.clickable(crossinline onClick: () -> Unit, builderAction: SpannableStringBuilder.() -> Unit) =
    inSpans(object : ClickableSpan() {
        override fun onClick(widget: View) {
            widget.cancelPendingInputEvents()
            onClick()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
        }
    }, builderAction)
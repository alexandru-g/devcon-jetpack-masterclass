package com.softvision.jetpackmasterclass.cheats

import android.content.Context
import android.content.res.Resources
import android.text.SpannedString
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.softvision.jetpackmasterclass.R

/**
 * Class that represents a given String resource, without needing to extract value in ViewModels.
 *
 * Examples:
 *
 * state.error = Strong(R.string.generic_error)
 *
 * state.error = Strong(R.string.int_error, 10)
 *
 * state.error = Strong(R.string.string_arg_error, Strong(R.string.other_string))
 *
 * state.error = Strong(R.string.string_arg_error, Strong(R.string.inception_string, Strong(R.string.other_string)))
 */
open class Strong(
    @StringRes private val id: Int,
    vararg params: Any
) {
    private val parameters = params

    constructor(value: String) : this(NO_RES, value)

    constructor(other: Strong) : this(other.id, *other.parameters)

    /**
     * Create the String represented by this Strong instance. If a parameter is Strong, extract its
     * actual value before passing it to resources.
     */
    internal fun get(resources: Resources): String? =
        when (id) {
            NO_RES -> parameters.first() as String
            NO_TEXT -> null
            else -> resources.getString(id, *resolveParams(resources, parameters))
        }

    /**
     * Check if any parameter is Strong. If so, resolve it to a string.
     */
    private fun resolveParams(resources: Resources, params: Array<out Any>): Array<Any?> =
        params.map { param ->
            if (param is Strong) {
                param.get(resources)
            } else {
                param
            }
        }.toTypedArray()

    fun isEmpty() = this == EMPTY || this == NONE

    fun isNotEmpty() = !isEmpty()

    override fun equals(other: Any?): Boolean =
        when (other) {
            is Strong -> id == other.id && parameters.contentEquals(other.parameters)
            else -> false
        }

    override fun hashCode(): Int = id.hashCode() + parameters.hashCode()

    override fun toString(): String {
        return "$id(${parameters.joinToString(",")})"
    }

    companion object {
        private const val NO_RES: Int = -1
        private const val NO_TEXT: Int = -2

        val EMPTY: Strong = Strong(R.string.empty)
        val NONE: Strong = Strong(NO_TEXT)
    }
}

fun Context.getString(strong: Strong?) = when(strong) {
    is SpannedStrong -> strong.build(this)
    else -> strong?.get(resources)
}

fun Fragment.getString(strong: Strong?) = when(strong) {
    is SpannedStrong -> strong.build(requireContext())
    else -> strong?.get(resources)
}

fun View.getString(strong: Strong?) = when(strong) {
    is SpannedStrong -> strong.build(context)
    else -> strong?.get(resources)
}

fun String.toStrong() = Strong(this)

fun Int.toStrong() = Strong(this)

fun Strong?.orEmpty(): Strong = this ?: Strong.EMPTY

fun Strong?.isNullOrEmpty() = this == null || isEmpty()

fun SpannedString.toStrong() = Strong(this.toString())

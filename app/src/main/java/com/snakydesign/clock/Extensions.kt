package com.snakydesign.clock

import androidx.ui.unit.Dp
import java.util.*
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * @author Adib Faramarzi (adibfara@gmail.com)
 */

fun randomInt(start: Int, end: Int, random: Random): Int {
    return start + (random.nextFloat() * (end - start)).toInt()
}

fun randomFloat(start: Float, end: Float, random: Random): Float {
    return start + random.nextFloat() * (end.toInt() - start.toInt())
}

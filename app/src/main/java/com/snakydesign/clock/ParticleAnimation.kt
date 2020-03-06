package com.snakydesign.clock

import androidx.animation.FloatPropKey
import androidx.animation.Infinite
import androidx.animation.transitionDefinition

/**
 * @author Adib Faramarzi (adibfara@gmail.com)
 */


val Progress = FloatPropKey()

val ParticleAnimations = transitionDefinition {
    state(0) {
        this[Progress] = 0f
    }

    state(1) {
        this[Progress] = 1f
    }

    transition(fromState = 0, toState = 1) {
        Progress using repeatable {
            iterations = Infinite
            animation = tween {
                duration = 1000
            }
        }
    }
}

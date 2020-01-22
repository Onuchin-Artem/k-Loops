package kLoops.music

import java.util.concurrent.ThreadLocalRandom
import kotlin.math.PI
import kotlin.math.sin


fun Double.addJitter(jitter: Double) = this * (1.0 + ThreadLocalRandom.current().nextGaussian() * jitter / 2.0)

fun LoopContext.up(from: Double = 0.0, to: Double = 1.0, period: Int, phase: Double = 0.0, jitter: Double = 0.0) =
        this.LFO(from, to, period, phase, jitter) { step ->
            (step % period).toDouble() / period.toDouble()
        }

fun LoopContext.down(from: Double = 0.0, to: Double = 1.0, period: Int, phase: Double = 0.0, jitter: Double = 0.0) =
        this.LFO(from, to, period, phase, jitter) { step ->
            1.0 - (step % period).toDouble() / period.toDouble()
        }

fun LoopContext.trapezoid(
        from: Double = 0.0, to: Double = 1.0,
        period: Int, t1: Int, t2: Int = (period / 2), t3: Int = t2 + t1,
        phase: Double = 0.0, jitter: Double = 0.0): LoopContext.LFO {
    check(t1 <= t2 && t2 <= t3 && t3 <= period) {
        "wrong values: $t1, $t2, $t3, $period"
    }
    return this.LFO(from, to, period, phase, jitter) { step ->
        if (step < t1) step.toDouble() / t1.toDouble()
        else if (step in t1 until t2) 1.0
        else if (step in t2 until t3) (t3 - step).toDouble() / (t3 - t2).toDouble()
        else 0.0
    }
}

fun LoopContext.triag(
        from: Double = 0.0, to: Double = 1.0,
        period: Int, t1: Int = period / 2,
        phase: Double = 0.0, jitter: Double = 0.0) =
        trapezoid(from, to, period, t1 = t1, t2 = t1, t3 = period, phase = phase, jitter = jitter)

fun LoopContext.rect(
        from: Double = 0.0, to: Double = 1.0,
        period: Int, t1: Int = period / 2,
        phase: Double = 0.0, jitter: Double = 0.0) =
        trapezoid(from, to, period, t1 = 0, t2 = t1, t3 = t1, phase = phase, jitter = jitter)

fun LoopContext.sine(
        from: Double = 0.0, to: Double = 1.0, period: Int,
        phase: Double = 0.0, jitter: Double = 0.0) =
        this.LFO(from, to, period, phase, jitter) { step ->
            sin(step.toDouble() / period.toDouble() * 2 * PI)
        }

fun LoopContext.line(
        from: Double = 0.0, to: Double = 1.0,
        time: Int, jitter: Double = 0.0) =
        this.Generator() { step ->
            if (step < time) (from + (to - from) * step.toDouble() / time.toDouble()).addJitter(jitter)
            else to
        }

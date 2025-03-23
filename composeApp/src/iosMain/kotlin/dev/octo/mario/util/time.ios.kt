package dev.octo.mario.util

import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSince1970

actual fun currentTimeMillis(): Long = NSDate.dateWithTimeIntervalSince1970() .currentTimeMillis()
actual fun currentTimeNanos(): Long = System.nanoTime()

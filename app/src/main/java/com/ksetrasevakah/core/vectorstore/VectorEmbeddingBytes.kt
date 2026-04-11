package com.ksetrasevakah.core.vectorstore

import java.nio.ByteBuffer
import java.nio.ByteOrder

internal fun FloatArray.toLittleEndianByteArray(): ByteArray {
    val buffer = ByteBuffer.allocate(size * 4).order(ByteOrder.LITTLE_ENDIAN)
    for (f in this) buffer.putFloat(f)
    return buffer.array()
}

internal fun ByteArray.toFloatArrayLittleEndian(): FloatArray {
    val buffer = ByteBuffer.wrap(this).order(ByteOrder.LITTLE_ENDIAN)
    val out = FloatArray(size / 4)
    var i = 0
    while (buffer.hasRemaining() && i < out.size) {
        out[i++] = buffer.float
    }
    return out
}

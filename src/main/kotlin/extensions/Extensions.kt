package extensions

fun ByteArray.firstIndexOf(target: Byte, startPosition: Int): Int {
    for (i in startPosition..this.size) {
        if (target == this[i]) {
            return i
        }
    }

    return -1
}
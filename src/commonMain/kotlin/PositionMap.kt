import com.soywiz.kds.IntArray2
import kotlin.random.Random

enum class Direction {
    LEFT, RIGHT, TOP, BOTTOM
}

class Position(val x: Int, val y: Int)

class PositionMap(private val array: IntArray2 = IntArray2(4, 4, -1)) {

    operator fun get(x: Int, y: Int) = array[x, y]

    operator fun set(x: Int, y: Int, value: Int) {
        array[x, y] = value
    }

    override fun equals(other: Any?) = other is PositionMap && this.array.data.contentEquals(other.array.data)

    override fun hashCode(): Int = array.hashCode()

    fun forEach(action: (Int) -> Unit) {
        array.forEach(action)
    }

    fun getRandomFreePosition(): Position? {
        val quantity = array.count { it == -1 }
        if (quantity == 0) return null
        val chosen = Random.nextInt(quantity)
        var current = 0
        array.each { x, y, v ->
            if (v != -1) return@each
            if (current++ == chosen) {
                return Position(x, y)
            }
        }
        return null
    }

    fun hasAvailableMoves(): Boolean {
        array.each { x, y, _ ->
            if (hasAdjacentEqualPosition(x, y)) return true
        }
        return false
    }

    fun hasAdjacentEqualPosition(x: Int, y: Int): Boolean = getNumberOrdinal(x, y).let {
        it == getNumberOrdinal(x - 1, y) || it == getNumberOrdinal(x + 1, y)
                || it == getNumberOrdinal(x, y - 1) || it == getNumberOrdinal(x, y + 1)
    }

    private fun getOrNull(x: Int, y: Int) = array.tryGet(x, y)?.takeIf { it != -1 }?.let { Position(x, y) }

    private fun getNumberOrdinal(x: Int, y: Int) = array.tryGet(x, y)?.let { blocks[it]?.number?.ordinal } ?: -1
}
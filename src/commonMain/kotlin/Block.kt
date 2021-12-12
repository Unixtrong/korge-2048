import Number.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors

class Block(val number: Number) : Container() {

    private val Number.textSize
        get() = when (this) {
            LEVEL_00, LEVEL_01, LEVEL_02, LEVEL_03, LEVEL_04, LEVEL_05 -> cellSize / 2
            LEVEL_06, LEVEL_07, LEVEL_08 -> cellSize * 4 / 9
            LEVEL_09, LEVEL_10, LEVEL_11, LEVEL_12 -> cellSize * 2 / 5
            LEVEL_13, LEVEL_14, LEVEL_15 -> cellSize * 7 / 20
            LEVEL_16 -> cellSize * 3 / 10
        }

    init {
        roundRect(cellSize, cellSize, 5.0, fill = number.color)
        val textColor = when (number) {
            LEVEL_00, LEVEL_01 -> Colors.BLACK
            else -> Colors.WHITE
        }
        text(number.value.toString(), number.textSize, textColor, font) {
            centerBetween(.0, .0, cellSize, cellSize)
        }
    }
}

fun Container.block(number: Number) = Block(number).addTo(this)
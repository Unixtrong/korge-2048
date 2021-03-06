import com.soywiz.korev.Key
import com.soywiz.korge.Korge
import com.soywiz.korge.input.SwipeDirection
import com.soywiz.korge.input.keys
import com.soywiz.korge.input.onSwipe
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.font.BitmapFont
import com.soywiz.korim.font.readBitmapFont
import com.soywiz.korim.format.readBitmap
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.Rectangle
import com.soywiz.korma.geom.vector.roundRect
import kotlin.properties.Delegates
import kotlin.random.Random

var cellSize = .0
var fieldSize = .0
var leftIndent = .0
var topIndent = .0
var font: BitmapFont by Delegates.notNull()

val map = PositionMap()
val blocks = mutableMapOf<Int, Block>()
var freeId = 0

fun columnX(number: Int) = leftIndent + 10 + (cellSize + 10) * number
fun rowY(number: Int) = topIndent + 10 + (cellSize + 10) * number

fun Container.createNewBlockWithId(id: Int, number: Number, pos: Position) {
    blocks[id] = block(number).position(columnX(pos.x), rowY(pos.y))
}

fun Container.createNewBlock(number: Number, pos: Position): Int {
    val id = freeId++
    createNewBlockWithId(id, number, pos)
    return id
}

fun Container.generateBlock() {
    val position = map.getRandomFreePosition() ?: return
    val number = Number.LEVEL_00.takeIf { Random.nextDouble() < 0.9 } ?: Number.LEVEL_01
    val newId = createNewBlock(number, position)
    map[position.x, position.y] = newId
}

fun Stage.moveBlocksTo(direction: Direction) {
    println(direction)
}

suspend fun main() = Korge(
    width = 480, height = 640, bgcolor = RGBA(253, 247, 240), title = "2048"
) {
    cellSize = views.virtualWidth / 5.0
    fieldSize = 50 + 4 * cellSize
    leftIndent = (views.virtualWidth - fieldSize) / 2
    topIndent = 150.0
    font = resourcesVfs["clear_sans.fnt"].readBitmapFont()
    lg.debug { "cellSize: $cellSize, fieldSize: $fieldSize" }

    val bgField = roundRect(fieldSize, fieldSize, 5.0, fill = Colors["#B9AEA0"]) {
        position(leftIndent, topIndent)
    }

    val bgLogo = roundRect(cellSize, cellSize, 5.0, fill = Colors["#EDC403"]) {
        position(leftIndent, 30.0)
    }

    val bgBest = roundRect(cellSize * 1.5, cellSize * .8, 5.0, fill = Colors["#BBAE9E"]) {
        alignRightToRightOf(bgField)
        alignTopToTopOf(bgLogo)
    }

    val bgScore = roundRect(cellSize * 1.5, cellSize * .8, 5.0, fill = Colors["#BBAE9E"]) {
        alignRightToLeftOf(bgBest, 24)
        alignTopToTopOf(bgBest)
    }

    text("2048", cellSize * 0.5, Colors.WHITE, font).centerOn(bgLogo)
    text("BEST", cellSize * 0.25, Colors.WHITE, font) {
        centerXOn(bgBest)
        alignTopToTopOf(bgBest, 5)
    }
    text("0", cellSize * 0.5, Colors.WHITE, font) {
        setTextBounds(Rectangle(0.0, 0.0, bgBest.width, cellSize - 24.0))
        alignment = TextAlignment.MIDDLE_CENTER
        alignTopToTopOf(bgBest, 12)
        centerXOn(bgBest)
    }
    text("SCORE", cellSize * 0.25, Colors.WHITE, font) {
        centerXOn(bgScore)
        alignTopToTopOf(bgScore, 5)
    }
    text("0", cellSize * 0.5, Colors.WHITE, font) {
        setTextBounds(Rectangle(0.0, 0.0, bgBest.width, cellSize - 24.0))
        alignment = TextAlignment.MIDDLE_CENTER
        alignTopToTopOf(bgScore, 12)
        centerXOn(bgScore)
    }

    val restartImg = resourcesVfs["restart.png"].readBitmap()
    val undoImg = resourcesVfs["undo.png"].readBitmap()
    val btnSize = cellSize * 0.3
    val restartBlock = container {
        val background = roundRect(btnSize, btnSize, 5.0, fill = RGBA(185, 174, 160))
        image(restartImg) {
            size(btnSize * 0.8, btnSize * 0.8)
            centerOn(background)
        }
        alignTopToBottomOf(bgBest, 5)
        alignRightToRightOf(bgField)
    }
    val undoBlock = container {
        val background = roundRect(btnSize, btnSize, 5.0, fill = RGBA(185, 174, 160))
        image(undoImg) {
            size(btnSize * 0.8, btnSize * 0.8)
            centerOn(background)
        }
        alignTopToTopOf(restartBlock)
        alignRightToLeftOf(restartBlock, 5)
    }

    graphics {
        position(leftIndent, topIndent)
        fill(Colors["#CEC0B2"]) {
            for (i in 0..3) {
                for (j in 0..3) {
                    roundRect(10 + (10 + cellSize) * j, 10 + (10 + cellSize) * i, cellSize, cellSize, 5.0)
                }
            }
        }
    }

    generateBlock()

    root.keys.down {
        when (it.key) {
            Key.LEFT -> moveBlocksTo(Direction.LEFT)
            Key.RIGHT -> moveBlocksTo(Direction.RIGHT)
            Key.UP -> moveBlocksTo(Direction.TOP)
            Key.DOWN -> moveBlocksTo(Direction.BOTTOM)
            else -> Unit
        }
    }

    onSwipe(20.0) {
        when (it.direction) {
            SwipeDirection.LEFT -> moveBlocksTo(Direction.LEFT)
            SwipeDirection.RIGHT -> moveBlocksTo(Direction.RIGHT)
            SwipeDirection.TOP -> moveBlocksTo(Direction.TOP)
            SwipeDirection.BOTTOM -> moveBlocksTo(Direction.BOTTOM)
        }
    }
}
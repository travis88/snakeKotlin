package com.mygdx.snake.screens

import com.badlogic.gdx.Gdx.*
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import java.awt.Color

class GameScreen : ScreenAdapter() {
    private lateinit var batch: SpriteBatch
    private lateinit var snakeHead: Texture
    private lateinit var snakeBody: Texture
    private lateinit var apple: Texture

    private var timer = MOVE_TIME
    var snakeX = 0f
    var snakeY = 0f
    private var snakeXBeforeUpdate = 0f
    private var snakeYBeforeUpdate = 0f
    private var snakeDirection = RIGHT
    private var appleAvailable = false
    private var appleX = 0f
    private var appleY = 0f
    private lateinit var bodyParts: Array<BodyPart>

    override fun show() {
        batch = SpriteBatch()
        snakeHead = Texture(files.internal("snake-head.png"))
        snakeBody = Texture(files.internal("snake-body.png"))
        apple = Texture(files.internal("apple.png"))
        bodyParts = Array<BodyPart>()
    }

    override fun render(delta: Float) {
        queryInput()
        timer -= delta
        if (timer <= 0) {
            timer = MOVE_TIME
            moveSnake()
            checkForOutOfBounds()
            updateBodyPartsPosition()
        }
        checkAppleCollision()
        checkAndPlaceApple()
        clearScreen()
        draw()
    }

    private fun draw() {
        batch.begin()
        batch.draw(snakeHead, snakeX, snakeY)
        for (bodyPart in bodyParts) {
            bodyPart.draw(batch)
        }
        if (appleAvailable)
            batch.draw(apple, appleX, appleY)

        batch.end()
    }

    private fun clearScreen() {
        gl.glClearColor(Color.BLACK.red.toFloat(), Color.BLACK.green.toFloat(),
                Color.BLACK.blue.toFloat(), Color.BLACK.alpha.toFloat())
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }

    override fun dispose() {
        snakeHead.dispose()
        batch.dispose()
    }

    private fun checkForOutOfBounds() {
        if (snakeX >= graphics.width)
            snakeX = 0f
        if (snakeX < 0)
            snakeX = graphics.width.toFloat()
        if (snakeY >= graphics.height)
            snakeY = 0f
        if (snakeY < 0)
            snakeY = graphics.height.toFloat()
    }

    private fun moveSnake() {
        snakeXBeforeUpdate = snakeX
        snakeYBeforeUpdate = snakeY

        when (snakeDirection) {
            RIGHT -> snakeX += SNAKE_MOVEMENT
            LEFT -> snakeX -= SNAKE_MOVEMENT
            UP -> snakeY += SNAKE_MOVEMENT
            DOWN -> snakeY -= SNAKE_MOVEMENT
        }
    }

    private fun updateBodyPartsPosition() {
        if (bodyParts.size > 0) {
            val bodyPart = bodyParts.removeIndex(0)
            bodyPart.updateBodyPosition(snakeXBeforeUpdate, snakeYBeforeUpdate)
            bodyParts.add(bodyPart)
        }
    }

    private fun queryInput() {
        val lPressed = input.isKeyPressed(Input.Keys.LEFT)
        val rPressed = input.isKeyPressed(Input.Keys.RIGHT)
        val uPressed = input.isKeyPressed(Input.Keys.UP)
        val dPressed = input.isKeyPressed(Input.Keys.DOWN)

        if (lPressed) snakeDirection = LEFT
        if (rPressed) snakeDirection = RIGHT
        if (uPressed) snakeDirection = UP
        if (dPressed) snakeDirection = DOWN
    }

    private fun checkAndPlaceApple() {
        if (!appleAvailable) {
            do {
                appleX = MathUtils.random(graphics.width / SNAKE_MOVEMENT - 1).toFloat() * SNAKE_MOVEMENT
                appleY = MathUtils.random(graphics.height / SNAKE_MOVEMENT - 1).toFloat() * SNAKE_MOVEMENT
                appleAvailable = true
            } while (appleX == snakeX && appleY == snakeY)
        }
    }

    private fun checkAppleCollision() {
        if (appleAvailable && appleX == snakeX && appleY == snakeY) {
            val bodyPart = BodyPart(snakeBody)
            bodyPart.updateBodyPosition(snakeX, snakeY)
            bodyParts.insert(0, bodyPart)
            appleAvailable = false
        }
    }

    inner class BodyPart {
        private var x: Float = 0.0f
        private var y: Float = 0.0f
        private var texture: Texture

        constructor(texture: Texture) {
            this.texture = texture
        }

        fun updateBodyPosition(x: Float, y: Float) {
            this.x = x
            this.y = y
        }

        fun draw(batch: Batch) {
            if (!(x == snakeX && y == snakeY)) batch.draw(texture, x, y)
        }
    }

    companion object {
        const val MOVE_TIME = .2f
        const val SNAKE_MOVEMENT = 32
        const val RIGHT = 0
        const val LEFT = 1
        const val UP = 2
        const val DOWN = 3
    }
}
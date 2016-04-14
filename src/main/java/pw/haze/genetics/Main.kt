package pw.haze.genetics

import com.sun.javafx.geom.Vec4f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.stb.STBEasyFont
import org.lwjgl.system.MemoryUtil.NULL
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

/**
 * |> Author: haze
 * |> Since: 4/13/16
 */

private var errorCallback: GLFWErrorCallback? = null
private var keyCallback: GLFWKeyCallback? = null
private var entPair: Pair<Entity, Entity> = Pair(Entity(true), Entity(true))
private var parentPair: Pair<Entity, Entity>? = null
private var curEntity: Entity = entPair.first
private var window: Long = 0
private var curGeneration = 0
private var hasPrintedGeneration = false
private var shouldLoop = false
const val WIDTH = 500
const val HEIGHT = 500

private fun init() {
    // GL.createCapabilities()
    errorCallback = GLFWErrorCallback.createPrint(System.err)
    if (glfwInit() != GLFW_TRUE)
        throw IllegalStateException("Unable to initialize GLFW")
    glfwDefaultWindowHints() // optional, the current window hints are already the default
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
    glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)

    window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL)
    if (window == NULL)
        throw RuntimeException("Failed to create the GLFW window")

    keyCallback = object : GLFWKeyCallback() {
        override fun invoke(p0: kotlin.Long, p1: kotlin.Int, p2: kotlin.Int, p3: kotlin.Int, p4: kotlin.Int) {
            input(p1, p3, p2)
        }
    }

    val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())
    glfwSetWindowPos(
            window,
            (vidmode.width() - WIDTH) / 2,
            (vidmode.height() - HEIGHT) / 2)
    glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

    window = glfwCreateWindow(WIDTH, HEIGHT, "xxx", NULL, NULL);
    glfwSetKeyCallback(window, keyCallback)
    glfwSetErrorCallback(errorCallback)

    if (window == NULL) {
        System.err.println("Could not create our window");
    }

    // Sets the initial position of our game window.
    glfwSetWindowPos(window, 100, 100);
    // Sets the context of GLFW, this is vital for our program to work.
    glfwMakeContextCurrent(window);
    // finally shows our created window in all it's glory.
    glfwShowWindow(window);

    GL.createCapabilities(false)


    glEnable(GL_TEXTURE_2D)
    glShadeModel(GL_SMOOTH)
    glDisable(GL_DEPTH_TEST)
    glDisable(GL_LIGHTING)
    glClearColor(248 / 100F, 236 / 100F, 194 / 100F, 1.0f);
    glClearDepth(1.toDouble());
    glViewport(0, 0, WIDTH, HEIGHT)
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    glOrtho(0.toDouble(), WIDTH.toDouble(), HEIGHT.toDouble(), 0.toDouble(), -1.toDouble(), 1.toDouble())
    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    println("OpenGL: ${glGetString(GL_VERSION)}\n")
}

fun border(vec4f: Vec4f, other: Vec4f): Vec4f {
    return Vec4f(vec4f.x - other.x, vec4f.y - other.y, vec4f.z + other.z * 2, vec4f.w + other.w * 2)
}

fun drawBoxWithBorder(box: Vec4f, ln: Float, col: Vec4f, col2: Vec4f) {
    drawBox(box, col)
    drawBox(border(box, Vec4f(ln, ln, ln, ln)), col2)
}

fun drawBox(box: Vec4f, col: Vec4f) {
    glPushMatrix()
    glColor4f(col.x, col.y, col.w, col.z)
    glBegin(GL_POLYGON)
    glVertex2f(box.x, box.y)
    glVertex2f(box.x + box.w, box.y)
    glVertex2f(box.x + box.w, box.y + box.z)
    glVertex2f(box.x, box.y + box.z)
    glEnd()
    glPopMatrix()
}

fun input(key: Int, type: Int, scancode: Int) {
    val keyStr = glfwGetKeyName(key, scancode)
    when (type) {
        GLFW_RELEASE -> {
            when (key) {
                GLFW_KEY_SPACE -> {
                    shouldLoop = false
                }
                else -> {
                    // println("$keyStr was released!")
                }
            }
        }
        GLFW_PRESS -> {
            when (key) {
                GLFW_KEY_SPACE -> {
                    shouldLoop = true
                }
                else -> {
                    // println("$keyStr was pressed!")
                }
            }
        }
        GLFW_REPEAT -> {
            when (key) {
                else -> {
                    // println("$keyStr is being held down!")
                }
            }
        }
        else -> {
            // println("Unknown input type !!!")
        }
    }
    if (key == GLFW_KEY_ESCAPE && type == GLFW_RELEASE)
        glfwSetWindowShouldClose(window, GLFW_TRUE) // We will detect this in our rendering loop
}

private fun loop(box: Vec4f) {
    //Set the clear color
    var now: Float = glfwGetTime().toFloat()
    var last: Float = 0F
    var delta: Float = 0F
    glClearColor(148 / 100F, 136 / 100F, 94 / 100F, 1.0f);
    while (glfwWindowShouldClose(window) == GLFW_FALSE) {
        now = glfwGetTime().toFloat()
        delta = now - last
        last = now
        if (!shouldLoop)
            logic(box, delta)
        render(box)
        glfwPollEvents()
        glfwSwapBuffers(window) // swap the color buffers
    }
}

fun render(box: Vec4f) {


    glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    drawBoxWithBorder(curEntity.position, 3F, Vec4f(0.2F, 0.1F, 0.4F, 1F), Vec4f(0.1F, 0.05F, 0.2F, 255F))
    // println(curEntity.position)
    drawBoxWithBorder(box, 3F, Vec4f(0.5F, 0.1F, 0.4F, 1F), Vec4f(0.3F, 0.05F, 0.2F, 255F))
    // font.drawString(2F, 2F, "TESTING", Color.green)
    drawText("Generation: $curGeneration", 2F, 242F, Vec4f(1F, 0.5F, 0.2F, 1F))
    if (parentPair != null)
        drawText("Parent: ${fitnessFormat(parentPair!!)}", 2F, 234F, Vec4f(1F, 0.5F, 0.2F, 1F))
}

fun fitnessFormat(parents: Pair<Entity, Entity>): String {
    val format = NumberFormat.getNumberInstance(Locale("en", "UK")) as DecimalFormat
    format.applyPattern("##.#####")
    return "A<${format.format(parents.first.fitness)}>, B<${format.format(parents.second.fitness)}>"
}

fun drawText(text: String, x: Float, y: Float, col: Vec4f) {
    val buff = BufferUtils.createByteBuffer(text.length * 270)
    val quads = STBEasyFont.stb_easy_font_print(x, y, text, null, buff)
    glEnableClientState(GL_VERTEX_ARRAY);
    glVertexPointer(2, GL_FLOAT, 16, buff);
    glPushMatrix()
    glScalef(2F, 2F, 1f);
    glColor4f(col.x, col.y, col.z, col.w)
    glDrawArrays(GL_QUADS, 0, quads * 4);
    glPopMatrix()
}

fun logic(box: Vec4f, delta: Float) {
    if (!hasPrintedGeneration) {
        println("GENERATION: $curGeneration")
        hasPrintedGeneration = true
    }
    if (!curEntity.isGrown) {
        if (!curEntity.isMoving) {
            for (input in curEntity.chromosome) {
                // println("reading ${input.name}")
                move(input, delta)
                curEntity.readChromosomes++
                curEntity.isMoving = false
            }
        }
    }

    if (curEntity.readChromosomes >= curEntity.chromosome.size) {
        curEntity.fitness = calcFitness(curEntity, box)
        // println("Calculated ${if(curEntity == entPair.first) "first" else "second"}'s fitness, ${curEntity.fitness}}")
        if (entPair.first.isGrown && entPair.second.isGrown) {
            println("Generation Crossed! Parents = {${entPair.first.fitness} / ${entPair.second.fitness}}")
            // println("Grown first pair! {${entPair.first.fitness} / ${entPair.second.fitness}}")
            parentPair = entPair
            entPair = cross(entPair)
            curGeneration++
            hasPrintedGeneration = false
            // println("CROSSED PARENTS !!!!")
        } else {
            // println("Not both are grown !!")
            curEntity.isGrown = true
            curEntity = if (curEntity == entPair.first) entPair.second else entPair.first
        }
        // println("test {${entPair.first.isGrown} / ${entPair.second.isGrown}}")
    }
}


fun move(inp: MotionInputEnum, delta: Float) {
    if (!curEntity.isGrown) {
        when (inp) {
            MotionInputEnum.UP -> {
                curEntity.dy = 16F
            }
            MotionInputEnum.DOWN -> {
                curEntity.dy = -16F
            }
            MotionInputEnum.LEFT -> {
                curEntity.dx = -16F
            }
            MotionInputEnum.RIGHT -> {
                curEntity.dx = 16F
            }
            else -> {
                // println("Weird movement enum!!")
                curEntity.dx = 0F; curEntity.dy = 0F
            }
        }
        //  // println("b4 movement dx=${ent.dx} dy=${ent.dy}")
        curEntity.position.x += curEntity.dx * delta
        curEntity.position.y += curEntity.dy * delta
        //  // println("af movement dx=${ent.dx} dy=${ent.dy}")
    } else {
        curEntity.dx = 0F
        curEntity.dy = 0F
    }
}

fun main(args: Array<String>) {
    try {
        val rand: Random = Random()
        val box: Vec4f = Vec4f(rand.nextInt(500).toFloat(), rand.nextInt(500).toFloat(), 50F, 50F)
        // println(box)
        init()
        //val font = TrueTypeFont(Font("Arial", Font.PLAIN, 14), false)
        loop(Vec4f((500 / 2) - 50F, (500 / 2) - 50F, 50F, 50F))

        glfwDestroyWindow(window)
        keyCallback!!.release()
    } finally {
        glfwTerminate()
        errorCallback!!.release()
    }
}

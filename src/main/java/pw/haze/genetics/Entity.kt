package pw.haze.genetics

import com.sun.javafx.geom.Vec2f
import com.sun.javafx.geom.Vec4f
import java.util.*

/**
 * |> Author: haze
 * |> Since: 4/13/16
 */
class Entity {
    var readChromosomes: Int = 0
    var isGrown: Boolean = false
    var isMoving: Boolean = false
        get() = dx != 0F || dy != 0F
    var fitness: Float = -1F
    val chromosome: MutableList<MotionInputEnum> = mutableListOf()
    val position: Vec4f = Vec4f(20F, 20F, 50F, 50F)
    var dx: Float = 0F
    var dy: Float = 0F
    constructor(randChro: Boolean) {
        // generate random chromosomes input
        if(randChro) {
            val rand: Random = Random()
            for (i in 0..999) {
                chromosome.add(MotionInputEnum.values()[rand.nextInt(MotionInputEnum.values().size)])
            }
        }
    }

}
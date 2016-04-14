package pw.haze.genetics

import com.sun.javafx.geom.Vec4f
import java.util.*

/**
 * |> Author: haze
 * |> Since: 4/13/16
 */


fun collides(p1: Vec4f, p2: Vec4f): Boolean = p1.x + p1.w < p2.x || p2.x + p2.w < p1.x || p1.y + p1.z < p2.y || p2.y + p2.z < p1.y

fun cross(parents: Pair<Entity, Entity>): Pair<Entity, Entity> {
    val rand: Random = Random()
    val children: Pair<Entity, Entity> = Pair(Entity(false), Entity(false))
    var ccount = 0
    do {
        var childAParent = parents.second
        // mutation kek

        /*
        if (rand.nextInt(5000) == 25)
            if (rand.nextBoolean()) {
                println("mutated child a")
                mutate(children.first)
            } else {
                println("mutated child b")
                mutate(children.second)
            }
        */

      //  println("DBG: ${parents.first.chromosome.size}")
      //  println("DBG: ${parents.second.chromosome.size}\n")
        val eliteParent: Entity = if(parents.first.fitness > parents.second.fitness) parents.first else parents.second
        val shitParent: Entity = if(parents.first.fitness > parents.second.fitness) parents.second else parents.second
        // println("shit parent fit = ${shitParent.fitness} / elite parent fit = ${eliteParent.fitness}")
        if(rand.nextBoolean()) {
            if(rand.nextBoolean()){
                children.first.chromosome.add(eliteParent.chromosome[ccount])
            } else {
                children.first.chromosome.add(shitParent.chromosome[ccount])
            }

            if(rand.nextBoolean()){
                children.second.chromosome.add(eliteParent.chromosome[ccount])
            } else {
                children.second.chromosome.add(shitParent.chromosome[ccount])
            }

            //TODO: USE BETTER FITNESS ALGORITHm
        } else {
            if(rand.nextBoolean()){
                children.second.chromosome.add(eliteParent.chromosome[ccount])
            } else {
                children.second.chromosome.add(shitParent.chromosome[ccount])
            }

            if(rand.nextBoolean()){
                children.first.chromosome.add(eliteParent.chromosome[ccount])
            } else {
                children.first.chromosome.add(shitParent.chromosome[ccount])
            }
        }
        ccount++
    } while (ccount < parents.first.chromosome.size)

    // println("cDBG: ${children.first.chromosome.size}")
    // println("cDBG: ${children.second.chromosome.size}\n")


    return children
}

fun calcFitness(ent: Entity, box: Vec4f): Float {
    val distX = Math.abs(box.x - ent.position.x)
    val distY = Math.abs(box.y - ent.position.y)
    return 1 / (distX + distY)
}

fun mutate(entity: Entity) {
    entity.chromosome.reverse()
}


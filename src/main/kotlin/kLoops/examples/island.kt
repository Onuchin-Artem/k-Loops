package kLoops.examples

typealias Point = Pair<Int, Int>

data class World(val landPoints: Set<Point>, val height: Int, val width: Int) {
    fun print() {
        (0 until height).forEach { row ->
            (0 until width).forEach { column ->
                val value = if (landPoints.contains(Point(row, column))) 1 else 0
                print("$value ")
            }
            println()
        }
    }

    fun neighbours(point: Point): List<Point> {
        val neighbours = mutableListOf<Point>()
        if (point.first > 0) neighbours.add(point.copy(first = point.first - 1))
        if (point.first < height - 1) neighbours.add(point.copy(first = point.first + 1))
        if (point.second > 0) neighbours.add(point.copy(second = point.second - 1))
        if (point.second > width - 1) neighbours.add(point.copy(second = point.second + 1))
        return neighbours.toList()
    }
}

fun isPerfectIsland(world: World): Boolean {
    val visitedPoint = mutableListOf(world.landPoints.first())
    fun traverseIsland() {
        world.neighbours(visitedPoint.last()).forEach { neighbour ->
            if (world.landPoints.contains(neighbour) && !visitedPoint.contains(neighbour)) {
                visitedPoint.add(neighbour)
                traverseIsland()
            }
        }
    }
    traverseIsland()
    return visitedPoint.containsAll(world.landPoints)
}

val memmoization = mutableMapOf<World, Int>()
fun findNumberOfSwitches(world: World, path: List<World>): Int {
    memmoization[world] = -1
    if (memmoization[world]?: -1 > 0 ) {
        return memmoization[world]!!
    }
    val numberOfSwitches = if (isPerfectIsland(world)) {

        0
    } else {
        1 + world.landPoints.flatMap { point ->
            world.neighbours(point).map { neighbour ->
                world.copy(landPoints = world.landPoints - point + neighbour)
            }.filter {newWorld ->
                !path.contains(newWorld)
            }.map { newWorld ->
                val sw = findNumberOfSwitches(newWorld, path + world)
                sw
            }
        }.min()!!
    }
    memmoization.put(world, numberOfSwitches)
    return numberOfSwitches
}

fun main() {
    val worldArray = listOf(
            listOf(0, 1, 0),
            listOf(0, 0, 1),
            listOf(1, 0, 0)
    )

    val height = worldArray.size
    val width = worldArray[0].size
    val landPoints = (0 until height).flatMap { row ->
        (0 until width).flatMap { column ->
            if (worldArray[row][column] == 1) listOf(Point(row, column)) else listOf()
        }
    }.toSet()
    val world = World(landPoints, height, width)
    world.print()

    println("Fuck ${findNumberOfSwitches(world, listOf())}")
    println("Fuck ${findNumberOfSwitches(world, listOf())}")
}
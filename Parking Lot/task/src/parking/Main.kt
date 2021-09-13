package parking

data class Car(val regNo: String, val color: String)
sealed interface Slot
data class Occupied(val position: Int, val occupiedBy: Car) : Slot
object Vacant : Slot
class ParkingLot(size: Int) {
    private val spots = MutableList<Slot>(size) { Vacant }

    init {
        println("Created a parking lot with $size spots.")
    }

    fun hasVacancy(): Boolean = spots.any { it is Vacant }

    private fun findSpotIdx(): Int = spots.indexOfFirst { it is Vacant }

    fun park(car: Car) {
        val spotIdx = findSpotIdx()
        spots[spotIdx] = Occupied(spotIdx + 1, car)
        println("${car.color} car parked in spot ${spotIdx + 1}.")
    }

    fun leave(spot: Int) {
        val idx = spot - 1
        when (spots[idx]) {
            Vacant -> println("There is no car in spot $spot.")
            is Occupied -> {
                spots[idx] = Vacant
                println("Spot $spot is free.")
            }
        }
    }

    fun status() {
        val filled = spots.filterNot { it is Vacant }.map { it as Occupied }
        if (filled.isEmpty()) {
            println("Parking lot is empty.")
        } else {
            filled.forEach { println("${it.position} ${it.occupiedBy.regNo} ${it.occupiedBy.color}") }
        }
    }

    private fun filterOccupiedByPredicate(
        predicate: (Occupied) -> Boolean,
        extractor: (List<Occupied>) -> String,
        notFoundMsg: String,
    ) {
        val filtered = spots
            .filterNot { it is Vacant }
            .map { it as Occupied }
            .filter(predicate)
        if (filtered.isEmpty()) {
            println(notFoundMsg)
        } else {
            println(extractor(filtered))
        }
    }

    fun regByColor(color: String) = filterOccupiedByPredicate(
        { it.occupiedBy.color.lowercase() == color.lowercase() },
        { it.joinToString { occupied -> occupied.occupiedBy.regNo } },
        "No cars with color $color were found."
    )

    fun spotByColor(color: String) = filterOccupiedByPredicate(
        { it.occupiedBy.color.lowercase() == color.lowercase() },
        { it.joinToString { occupied -> occupied.position.toString() } },
        "No cars with color $color were found."
    )

    fun spotByReg(regNo: String) = filterOccupiedByPredicate(
        { it.occupiedBy.regNo == regNo },
        { it.joinToString { occupied -> occupied.position.toString() } },
        "No cars with registration number $regNo were found."
    )
}

fun main() {
    var plot: ParkingLot? = null

    while (true) {
        readLine()?.let { cmd ->
            val parts = cmd.split(" ")
            when (parts[0]) {
                "create" -> plot = ParkingLot(parts[1].toInt())
                "park" -> {
                    plot?.let {
                        if (it.hasVacancy()) {
                            it.park(Car(parts[1], parts[2]))
                        } else {
                            println("Sorry, the parking lot is full.")
                        }
                    } ?: println("Sorry, a parking lot has not been created.")
                }
                "leave" -> plot?.leave(parts[1].toInt()) ?: println("Sorry, a parking lot has not been created.")
                "status" -> plot?.status() ?: println("Sorry, a parking lot has not been created.")
                "reg_by_color" -> plot?.regByColor(parts[1]) ?: println("Sorry, a parking lot has not been created.")
                "spot_by_color" -> plot?.spotByColor(parts[1]) ?: println("Sorry, a parking lot has not been created.")
                "spot_by_reg" -> plot?.spotByReg(parts[1]) ?: println("Sorry, a parking lot has not been created.")
                "exit" -> return
            }
        }
    }
}
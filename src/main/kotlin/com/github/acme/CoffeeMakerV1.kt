package com.github.acme

/**
 * Make me some coffee!
 */
class CoffeeMakerV1 {
    var milkQuantityInMl: Int = 0
        private set
    var waterQuantityInMl: Int = 0
        private set
    var coffeeQuantityInGrams: Int = 0
        private set

    /**
     *
     * @param coffeeType
     * @return Will return null, if coffeeType is not 'espresso' or 'cappuccino'
     * @throws NotEnoughWaterException
     * @throws NotEnoughCoffeeException
     * @throws NotEnoughMilkException
     */
    fun brew(coffeeType: String): CoffeeBrew? {
        return if (coffeeType == "espresso") {

            // espresso   [water 30ml, coffee 50g]
            if (waterQuantityInMl < 30) {
                throw NotEnoughWaterException()
            }
            if (coffeeQuantityInGrams < 50) {
                throw NotEnoughCoffeeException()
            }
            waterQuantityInMl -= 30
            coffeeQuantityInGrams -= 50
            Brews.espresso
        } else if (coffeeType == "cappuccino") {

            // cappuccino [water 50ml, coffee 50g, milk 30ml]
            if (waterQuantityInMl < 50) {
                throw NotEnoughWaterException()
            }
            if (coffeeQuantityInGrams < 50) {
                throw NotEnoughCoffeeException()
            }
            if (milkQuantityInMl < 30) {
                throw NotEnoughMilkException()
            }
            waterQuantityInMl -= 50
            coffeeQuantityInGrams -= 50
            milkQuantityInMl -= 30
            Brews.cappuccino
        } else {
            null
        }
    }

    fun addCoffee(quantityInGrams: Int) {
        coffeeQuantityInGrams += quantityInGrams
    }

    fun addMilk(quantityInMl: Int) {
        milkQuantityInMl += quantityInMl
    }

    fun addWater(quantityInMl: Int) {
        waterQuantityInMl += quantityInMl
    }



    override fun toString(): String {
        val b = StringBuilder()
        b.append("CoffeeMaker CO:\n")
        b.append("\tavailable water: ").append(waterQuantityInMl).append(" ml").append("\n")
        b.append("\tavailable coffee: ").append(coffeeQuantityInGrams).append(" gr").append("\n")
        b.append("\tavailable milk: ").append(milkQuantityInMl).append(" ml").append("\n")
        return b.toString()
    }
}

open class CoffeeBrew(private val name: String) {
    override fun toString() = name
}

object Brews {
    val espresso
        get() = CoffeeBrew("espresso")
    val cappuccino
        get() = CoffeeBrew("cappuccino")
}

open class NotEnoughWaterException : RuntimeException()
class NotEnoughCoffeeException : RuntimeException()
class NotEnoughMilkException : RuntimeException("Looks like the machine doesn't have enough milk for your operation")
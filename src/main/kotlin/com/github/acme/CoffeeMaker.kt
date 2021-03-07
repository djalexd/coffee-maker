package com.github.acme

/**
 * Make me some coffee!
 */
class CoffeeMaker(
        private var milkQuantityInMl: Int = 0,
        private var waterQuantityInMl: Int = 0,
        private var coffeeQuantityInGrams: Int = 0) {

    val coffeeMenu: List<String>?
        get() = null

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
            Espresso()
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
            Cappuccino()
        } else {
            null
        }
    }

    fun makeMilkFoam(numberOfSeconds: Int): MilkFoam {
        val quantity = (numberOfSeconds * 5.5).toInt()
        return if (milkQuantityInMl < quantity) {
            throw NotEnoughMilkException()
        } else {
            milkQuantityInMl -= quantity
            MilkFoam()
        }
    }

    fun cleanItself() {
    }

    fun addCoffee(quantityInGrams: Int) {
        //int currentQuantity = coffeeQuantityInGrams;
        //coffeeQuantityInGrams = currentQuantity + quantityInGrams;
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

open class CoffeeBrew

internal class Espresso : CoffeeBrew() {
    override fun toString(): String {
        return "espresso"
    }
}

internal class Cappuccino : CoffeeBrew() {
    override fun toString(): String {
        return "cappuccino"
    }
}

class MilkFoam {
    override fun toString(): String {
        return "milk foam"
    }
}

internal class NotEnoughWaterException : RuntimeException()
internal class NotEnoughCoffeeException : RuntimeException()
internal class NotEnoughMilkException : RuntimeException("Looks like the machine doesn't have enough milk for your operation")
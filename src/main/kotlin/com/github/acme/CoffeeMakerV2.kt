package com.github.acme

data class Recipe(
        val name: String,
        val coffeeQuantityInGrams: Int,
        val waterQuantityInMl: Int,
        val milkQuantityInMl: Int,
        val generator: () -> CoffeeBrew)

/**
 * Make me some coffee!
 *
 * This time, the coffee machine gets a brand new algorithm and can be
 * tailor made for each customer. This is possible by configuring a list
 * of recipes when building the machine.
 */
class CoffeeMakerV2(private val recipes: List<Recipe>) {
    var milkQuantityInMl: Int = 0
        private set
    var waterQuantityInMl: Int = 0
        private set
    var coffeeQuantityInGrams: Int = 0
        private set

    val coffeeMenu: List<String>
        get() = recipes.map { it.name }

    /**
     *
     * @param coffeeType
     * @return Will return null, if coffeeType is not 'espresso' or 'cappuccino'
     * @throws NotEnoughWaterException
     * @throws NotEnoughCoffeeException
     * @throws NotEnoughMilkException
     */
    fun brew(coffeeType: String): CoffeeBrew? {
        // Cannot find the recipe
        if (!canBeBrewed(coffeeType)) {
            return null
        }

        val recipe = recipes.find { recipe -> recipe.name == coffeeType }!!

        if (waterQuantityInMl < recipe.waterQuantityInMl) {
            throw NotEnoughWaterException()
        }
        if (coffeeQuantityInGrams < recipe.coffeeQuantityInGrams) {
            throw NotEnoughCoffeeException()
        }
        if (milkQuantityInMl < recipe.milkQuantityInMl) {
            throw NotEnoughMilkException()
        }

        waterQuantityInMl -= recipe.waterQuantityInMl
        coffeeQuantityInGrams -= recipe.coffeeQuantityInGrams
        milkQuantityInMl -= recipe.milkQuantityInMl

        return recipe.generator.invoke()
    }

    private fun canBeBrewed(coffeeType: String): Boolean {
        if (!recipes.any { recipe -> recipe.name == coffeeType }) {
            return false
        }
        return true
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


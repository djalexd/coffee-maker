//package com.github.acme
//
//import kotlin.math.max
//
//abstract class LiquidSource {
//    @Throws(NotEnoughLiquidException::class)
//    abstract fun getAmount(quantityInMl: Int)
//}
//
//class NotEnoughLiquidException: RuntimeException()
//
///**
// * Holds any liquid in a tank with maximum capacity. Liquid can be
// * increased by calling method #topUp, however it will never expect
// * maximum capacity
// */
//class FixedSizeTankLiquidSource(
//        private val tankCapacityInMl: Int): LiquidSource() {
//    private var amount: Int = 0
//    init {
//        if (tankCapacityInMl < 0) {
//            throw RuntimeException("${this.javaClass} capacity must be positive number")
//        }
//    }
//    override fun getAmount(quantityInMl: Int) {
//        if (amount < quantityInMl) {
//            throw NotEnoughLiquidException()
//        }
//    }
//
//    fun topUp(quantityInMl: Int) {
//        amount += max(amount + quantityInMl, tankCapacityInMl)
//    }
//}
//
///**
// * The source is tap pipe, this is considered an infinite source
// * (will never be finished). At the same time it cannot be topped up.
// */
//class TapLiquidSource(): LiquidSource() {
//    override fun getAmount(quantityInMl: Int) {}
//}
//
//class CoffeeContainer(private val containerCapacityInGr: Int) {
//    private var amount: Int = 0
//    init {
//        if (containerCapacityInGr < 0) {
//            throw RuntimeException("${this.javaClass} capacity must be positive number")
//        }
//    }
//
//    fun getAmount(quantityInGr: Int) {
//        if (amount < quantityInGr) {
//            throw NotEnoughCoffeeException()
//        }
//    }
//
//    fun topUp(quantityInMl: Int) {
//        amount = max(amount + quantityInMl, containerCapacityInGr)
//    }
//}
//
///**
// * Make me some coffee!
// *
// * This time, the coffee machine gets a brand new algorithm and can be
// * tailor made for each customer. This is possible by configuring a list
// * of recipes when building the machine.
// */
//class CoffeeMakerV3(
//        private var milkSource: FixedSizeTankLiquidSource,
//        private val waterSource: LiquidSource,
//        private val coffeeContainer: CoffeeContainer,
//        private val recipes: List<Recipe>,
//        private val brewFactory: Map<String, () -> CoffeeBrew>) {
//
//    val coffeeMenu: List<String>
//        get() = recipes.map { it.name }
//
//    /**
//     *
//     * @param coffeeType
//     * @return Will return null, if coffeeType is not 'espresso' or 'cappuccino'
//     * @throws NotEnoughWaterException
//     * @throws NotEnoughCoffeeException
//     * @throws NotEnoughMilkException
//     */
//    fun brew(coffeeType: String): CoffeeBrew? {
//        // Cannot find the recipe
//        if (!canBeBrewed(coffeeType)) {
//            return null
//        }
//
//        val recipe = recipes.find { recipe -> recipe.name == coffeeType }!!
//
//        throwAs(NotEnoughWaterException()) { waterSource.getAmount(recipe.waterQuantityInMl) }
//        throwAs(NotEnoughMilkException()) { milkSource.getAmount(recipe.milkQuantityInMl) }
//
//        coffeeContainer.getAmount(recipe.coffeeQuantityInGrams)
//
//        return brewFactory[coffeeType]!!.invoke()
//    }
//
//    private fun canBeBrewed(coffeeType: String): Boolean {
//        if (!recipes.any { recipe -> recipe.name == coffeeType }) {
//            return false
//        }
//        if (!brewFactory.containsKey(coffeeType)) {
//            return false
//        }
//        return true
//    }
//
//    fun makeMilkFoam(numberOfSeconds: Int): CoffeeBrew {
//        try {
//            val quantity = (numberOfSeconds * 5.5).toInt()
//            milkSource.getAmount(quantity)
//            return Brews.milkFoam
//        } catch (e: NotEnoughLiquidException) {
//            throw NotEnoughMilkException()
//        }
//    }
//
//    fun cleanItself() {
//        try {
//            waterSource.getAmount(50)
//        } catch (e: NotEnoughLiquidException) {
//            throw NotEnoughWaterForCleanupException()
//        }
//    }
//
//    override fun toString(): String {
//        val b = StringBuilder()
//        b.append("CoffeeMaker CO:\n")
//        b.append("\tavailable water: ").append("?").append(" ml").append("\n")
//        b.append("\tavailable coffee: ").append("?").append(" gr").append("\n")
//        b.append("\tavailable milk: ").append("?").append(" ml").append("\n")
//        return b.toString()
//    }
//
//    companion object {
//        private fun throwAs(exception: Throwable, lambda: () -> Unit) {
//            try {
//                lambda.invoke()
//            } catch (e: RuntimeException) {
//                throw exception
//            }
//        }
//    }
//}
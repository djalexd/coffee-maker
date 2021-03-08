//package com.github.acme
//
//import kotlin.math.max
//
//data class MeasurementUnit(val name: String)
//
//object MeasurementUnits {
//    val Gram = MeasurementUnit("g")
//    val Millilitre = MeasurementUnit("ml")
//}
//
//data class Quantity(val amount: Double, val unit: MeasurementUnit): Comparable<Quantity> {
//    init {
//        if (amount < 0.0) {
//            throw IllegalArgumentException("quantity amount cannot be negative")
//        }
//    }
//    fun zero() = Quantity(amount = 0.0, unit = unit)
//
//    override fun compareTo(other: Quantity): Int {
//        assertSameUnit(other)
//        return this.amount.compareTo(other.amount)
//    }
//
//    operator fun plus(other: Quantity): Quantity = run {
//        assertSameUnit(other)
//        return Quantity(this.amount + other.amount, this.unit)
//    }
//
//    private fun assertSameUnit(other: Quantity) {
//        if (this.unit != other.unit) {
//            throw IllegalArgumentException("Quantities in different units (${this.unit}, ${other.unit}) cannot be compared")
//        }
//    }
//
//    companion object {
//        fun max(a: Quantity, b: Quantity): Quantity = run {
//            a.assertSameUnit(b)
//            return Quantity(max(a.amount, b.amount), a.unit)
//        }
//
//        fun of(amount: Double, unit: MeasurementUnit) = Quantity(amount, unit)
//    }
//}
//
//abstract class LiquidSourceV2 {
//    @Throws(NotEnoughLiquidException::class)
//    abstract fun getAmount(quantity: Quantity)
//}
//
///**
// * Holds any liquid in a tank with maximum capacity. Liquid can be
// * increased by calling method #topUp, however it will never expect
// * maximum capacity
// */
//class FixedSizeTankLiquidSourceV2(
//        private val tankCapacity: Quantity): LiquidSourceV2() {
//    private var amount: Quantity = tankCapacity.zero()
//    init {
//        if (tankCapacity.amount < 0) {
//            throw RuntimeException("${this.javaClass} capacity must be positive number")
//        }
//    }
//    override fun getAmount(quantity: Quantity) {
//        if (amount < quantity) {
//            throw NotEnoughLiquidException()
//        }
//    }
//
//    fun topUp(quantity: Quantity) {
//        amount = Quantity.max(amount + quantity, tankCapacity)
//    }
//}
//
///**
// * The source is tap pipe, this is considered an infinite source
// * (will never be finished). At the same time it cannot be topped up.
// */
//class TapLiquidSourceV2(): LiquidSourceV2() {
//    override fun getAmount(quantity: Quantity) {}
//}
//
//class CoffeeContainerV2(private val containerCapacity: Quantity) {
//    private var amount: Quantity = containerCapacity.zero()
//    init {
//        if (containerCapacity.amount < 0) {
//            throw RuntimeException("${this.javaClass} capacity must be positive number")
//        }
//    }
//
//    fun getAmount(quantity: Quantity) {
//        if (amount < quantity) {
//            throw NotEnoughCoffeeException()
//        }
//    }
//
//    fun topUp(quantity: Quantity) {
//        amount += Quantity.max(amount + quantity, containerCapacity)
//    }
//}
//
//data class BrewSize(val size: String)
//
//sealed class Part(open val name: String)
//
//class RecipeV2(
//        override val name: String,
//        val size: BrewSize,
//        val coffee: Quantity,
//        val water: Quantity,
//        val milk: Quantity): Part(name)
//
//class ToppingName(override val name: String): Part(name)
//
//open class Topping(private val name: String) {
//    override fun toString() = name
//}
//
//object Toppings {
//    val caramel
//        get() = Topping("caramel")
//    val whipCream
//        get() = Topping("whip cream")
//}
//
//data class Order(val parts: List<Part>)
//
//data class CompositeCoffeeBrew(
//        val brews: List<CoffeeBrew>,
//        val toppings: List<Topping>
//): CoffeeBrew((
//        brews.map { it.toString() } +
//                toppings.map { it.toString() }
//        ).joinToString { " + " })
//
//// espresso
//// double espresso
//// triple shot espresso
//// long espresso
//// cappuccino (M) with (+) espresso shot / with (+) caramel
//// latte machiatto (S, M, L) with (+) caramel
//// americano (S, M, L)
//
//class CoffeeMakerV4(
//        private var milkSource: FixedSizeTankLiquidSourceV2,
//        private val waterSource: LiquidSourceV2,
//        private val coffeeContainer: CoffeeContainerV2,
//        private val recipes: List<RecipeV2>,
//        private val brewFactory: Map<String, () -> CoffeeBrew>,
//        private val toppingFactory: Map<String, () -> Topping>) {
//
//    fun brew(order: Order): CoffeeBrew? {
//
//            val brews = order.parts
//                    .filterIsInstance<RecipeV2>()
//                    .map { recipe -> brew(recipe.name) }
//
//            val toppings = order.parts
//                    .filterIsInstance<ToppingName>()
//                    .map { topping -> addTopping(topping) }
//
//        return CompositeCoffeeBrew(brews, toppings)
//    }
//
//    private fun addTopping(toppingName: ToppingName): Topping {
//        if (!toppingFactory.containsKey(toppingName.name)) {
//            throw IllegalArgumentException("Unknown topping ${toppingName.name}")
//        }
//        return toppingFactory[toppingName.name]!!.invoke()
//    }
//
//    private fun brew(coffeeType: String): CoffeeBrew {
//        // Cannot find the recipe
//        if (!canBeBrewed(coffeeType)) {
//            throw IllegalArgumentException("Unknown brew $coffeeType")
//        }
//
//        val recipe = recipes.find { recipe -> recipe.name == coffeeType }!!
//
//        throwAs(NotEnoughWaterException()) { waterSource.getAmount(recipe.water) }
//        throwAs(NotEnoughMilkException()) { milkSource.getAmount(recipe.milk) }
//
//        coffeeContainer.getAmount(recipe.coffee)
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
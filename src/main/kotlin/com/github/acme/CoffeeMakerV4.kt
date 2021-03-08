package com.github.acme

import kotlin.math.max

data class MeasurementUnit(val name: String)

object MeasurementUnits {
    val Gram = MeasurementUnit("g")
    val Millilitre = MeasurementUnit("ml")
}

data class Quantity(val amount: Double, val unit: MeasurementUnit): Comparable<Quantity> {
    fun zero() = Quantity(amount = 0.0, unit = unit)

    override fun compareTo(other: Quantity): Int {
        assertSameUnit(other)
        return this.amount.compareTo(other.amount)
    }

    operator fun plus(other: Quantity): Quantity = run {
        assertSameUnit(other)
        return Quantity(this.amount + other.amount, this.unit)
    }

    private fun assertSameUnit(other: Quantity) {
        if (this.unit != other.unit) {
            throw IllegalArgumentException("Quantities in different units (${this.unit}, ${other.unit}) cannot be compared")
        }
    }

    companion object {
        fun max(a: Quantity, b: Quantity): Quantity = run {
            a.assertSameUnit(b)
            return Quantity(max(a.amount, b.amount), a.unit)
        }
    }
}

//fun Pair.doSomething() = run {}

abstract class LiquidSourceV2 {
    @Throws(NotEnoughLiquidException::class)
    abstract fun getAmount(quantity: Quantity)
}

/**
 * Holds any liquid in a tank with maximum capacity. Liquid can be
 * increased by calling method #topUp, however it will never expect
 * maximum capacity
 */
class FixedSizeTankLiquidSourceV2(
        private val tankCapacity: Quantity): LiquidSourceV2() {
    private var amount: Quantity = tankCapacity.zero()
    init {
        if (tankCapacity.amount < 0) {
            throw RuntimeException("${this.javaClass} capacity must be positive number")
        }
    }
    override fun getAmount(quantity: Quantity) {
        if (amount < quantity) {
            throw NotEnoughLiquidException()
        }
    }

    fun topUp(quantity: Quantity) {
        amount = Quantity.max(amount + quantity, tankCapacity)
    }
}

/**
 * The source is tap pipe, this is considered an infinite source
 * (will never be finished). At the same time it cannot be topped up.
 */
class TapLiquidSourceV2(): LiquidSourceV2() {
    override fun getAmount(quantity: Quantity) {}
}

class CoffeeContainerV2(private val containerCapacity: Quantity) {
    private var amount: Quantity = containerCapacity.zero()
    init {
        if (containerCapacity.amount < 0) {
            throw RuntimeException("${this.javaClass} capacity must be positive number")
        }
    }

    fun getAmount(quantity: Quantity) {
        if (amount < quantity) {
            throw NotEnoughCoffeeException()
        }
    }

    fun topUp(quantity: Quantity) {
        amount += Quantity.max(amount + quantity, containerCapacity)
    }
}

sealed class Ingredient

data class RecipeV2(
        val name: String,
        val coffee: Quantity,
        val water: Quantity,
        val milk: Quantity): Ingredient()

data class MiscIngredient(
        val name: String): Ingredient()

object MiscIngredients {
    val caramel: Ingredient
        get() = MiscIngredient("caramel")
}

data class Order(val parts: List<Ingredient>)

data class CompositeCoffeeBrew(val order: Order): CoffeeBrew(name = order.parts.joinToString(" + "))

// espresso
// double espresso
// triple shot espresso
// long espresso
// cappuccino (M) with (+) espresso shot / with (+) caramel
// latte machiatto (S, M, L) with (+) caramel
// americano (S, M, L)

class CoffeeMakerV4(
        private var milkSource: FixedSizeTankLiquidSourceV2,
        private val waterSource: LiquidSourceV2,
        private val coffeeContainer: CoffeeContainerV2,
        private val recipes: List<RecipeV2>,
        private val brewFactory: Map<String, () -> CoffeeBrew>) {

    fun brew(order: Order): CoffeeBrew? {
        return null
    }
}
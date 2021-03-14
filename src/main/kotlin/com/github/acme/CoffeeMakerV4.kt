package com.github.acme

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.acme.MeasurementUnits.MeasurementsRegistry
import java.io.InputStream
import kotlin.jvm.Throws
import kotlin.math.max

data class MeasurementUnit(val name: String)

object MeasurementUnits {
    val Gram = MeasurementUnit("gr")
    val Millilitre = MeasurementUnit("ml")

    val MeasurementsRegistry = listOf(Gram, Millilitre)
}

data class Quantity(val amount: Double, val unit: MeasurementUnit): Comparable<Quantity> {
    init {
        if (amount < 0.0) {
            throw IllegalArgumentException("quantity amount cannot be negative")
        }
    }
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

        fun of(amount: Double, unit: MeasurementUnit) = Quantity(amount, unit)
        fun zero(unit: MeasurementUnit) = Quantity(0.0, unit)

        private val parseRegex = Regex("(\\d+(?:\\.\\d+)?) ([a-z]+)")
        fun parse(text: String): Quantity {
            val match = parseRegex.matchEntire(text) ?: throw IllegalArgumentException("Cannot parse Quantity from: '$text'")
            val amount = match.groupValues[1].toDouble()
            val unit = MeasurementsRegistry.find { it.name == match.groupValues[2] } ?: throw IllegalArgumentException("Unknown measurement unit '${match.groupValues[2]}'")
            return of(amount, unit)
        }
    }
}

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

internal data class RecipeV2Json(
        val name: String,
        @JsonDeserialize(using = RecipeLoader.Companion.BrewSizeDeserializer::class) val size: BrewSize? = null,
        @JsonDeserialize(using = RecipeLoader.Companion.QuantityDeserializer::class) val coffee: Quantity,
        @JsonDeserialize(using = RecipeLoader.Companion.QuantityDeserializer::class) val water: Quantity,
        @JsonDeserialize(using = RecipeLoader.Companion.QuantityDeserializer::class) val milk: Quantity)
internal data class RecipeList(val recipes: List<RecipeV2Json>)

internal data class ToppingJson(
        val name: String,
        @JsonDeserialize(using = RecipeLoader.Companion.QuantityDeserializer::class) val amount: Quantity)
internal data class ToppingList(val toppings: List<ToppingJson>)

class RecipeLoader {
    companion object {
        private val mapper = ObjectMapper(YAMLFactory()).registerModule(KotlinModule())

        class QuantityDeserializer: JsonDeserializer<Quantity>() {
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext) = Quantity.parse(p.text)
        }

        class BrewSizeDeserializer: JsonDeserializer<BrewSize>() {
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext) = BrewSize(p.text)
        }

        fun fromYaml(input: InputStream): List<RecipeV2> {
            /* used internally to read a list of recipes */
            val list = mapper.readValue<RecipeList>(input)
            return list.recipes.map {
                RecipeV2(it.name, it.size, it.coffee, it.water, it.milk) { CoffeeBrew(it.name) }
            }
        }
    }
}

class ToppingLoader {
    companion object {
        private val mapper = ObjectMapper(YAMLFactory()).registerModule(KotlinModule())

        fun fromYaml(input: InputStream): List<ToppingName> {
            /* used internally to read a list of toppings */
            val list = mapper.readValue<ToppingList>(input)
            return list.toppings.map {
                ToppingName(it.name, it.amount) { Topping(it.name) }
            }
        }
    }
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

data class BrewSize(val size: String)

object BrewSizes {
    val small = BrewSize("small")
    val medium = BrewSize("medium")
    val large = BrewSize("large")
}

sealed class Part(open val name: String)

class RecipeV2(
        override val name: String,
        val size: BrewSize? = null,
        val coffee: Quantity,
        val water: Quantity,
        val milk: Quantity,
        val generator: () -> CoffeeBrew): Part(name)

class ToppingName(
        override val name: String,
        val amount: Quantity,
        val generator: () -> Topping): Part(name)

open class Topping(private val name: String) {
    override fun toString() = name
}

object Toppings {
    val caramel
        get() = Topping("caramel")
    val whipCream
        get() = Topping("whip cream")
}

data class Order(val parts: List<Part>)

data class CompositeCoffeeBrew(
        val brews: List<CoffeeBrew>,
        val toppings: List<Topping>
): CoffeeBrew((
        brews.map { it.toString() } +
                toppings.map { it.toString() }
        ).joinToString(" + " )) {
    override fun toString() = super.toString()
}

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
        private val toppings: List<ToppingName>) {

    fun brew(order: String): CoffeeBrew? {

        val pattern = Regex("with|and")
        val parts = order.split(pattern)
                .map { part -> part.trim() }

        val brews = parts
                .mapNotNull { part ->
                    try {
                        internalBrew(part)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }

        val toppings = parts
                .mapNotNull { part ->
                    try {
                        addTopping(part)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }

        return CompositeCoffeeBrew(brews, toppings)
    }

    private fun addTopping(topping: String): Topping {
        val toppingByName = toppings.find { it.name == topping }
        if (toppingByName == null) {
            throw IllegalArgumentException("Unknown topping $topping")
        }
        return toppingByName.generator.invoke()
    }

    private fun internalBrew(coffeeType: String): CoffeeBrew {
        // Cannot find the recipe
        if (!canBeBrewed(coffeeType)) {
            throw IllegalArgumentException("Unknown brew $coffeeType")
        }

        val recipe = recipes.find { recipe -> recipe.name == coffeeType }!!

        throwAs(NotEnoughWaterException()) { waterSource.getAmount(recipe.water) }
        throwAs(NotEnoughMilkException()) { milkSource.getAmount(recipe.milk) }

        coffeeContainer.getAmount(recipe.coffee)

        return recipe.generator.invoke()
    }

    private fun canBeBrewed(coffeeType: String): Boolean {
        if (!recipes.any { recipe -> recipe.name == coffeeType }) {
            return false
        }
        return true
    }

    companion object {
        private fun throwAs(exception: Throwable, lambda: () -> Unit) {
            try {
                lambda.invoke()
            } catch (e: RuntimeException) {
                throw exception
            }
        }
    }
}
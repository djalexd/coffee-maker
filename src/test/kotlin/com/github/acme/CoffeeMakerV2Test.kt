package com.github.acme

import org.junit.Assert
import org.junit.Test
import kotlin.math.exp

class CoffeeMakerV2Test {

    @Test
    fun `test new recipes work`() {
        val coffeeMakerV2 = CoffeeMakerV2(
                recipes = listOf(
                        Recipe("latte", 20, 50, 100, { CoffeeBrew("latte machiatto") })
                )
        )

        coffeeMakerV2.addWater(100)
        coffeeMakerV2.addCoffee(100)
        coffeeMakerV2.addMilk(100)
        val brew: CoffeeBrew? = coffeeMakerV2.brew("latte")
        Assert.assertNotNull(brew)

    }

    @Test
    fun `test unknown recipe doesn't work`() {
        val coffeeMakerV2 = CoffeeMakerV2(
                recipes = listOf()
        )

        val brew: CoffeeBrew? = coffeeMakerV2.brew("latte")
        Assert.assertNull(brew)
    }

    @Test
    fun `test that coffee machine correctly substracts ingredients`() {
        val coffeeMakerV2 = CoffeeMakerV2(
                recipes = listOf(
                        Recipe("latte", 20, 50, 60, { CoffeeBrew("latte") })
                )
        )

        coffeeMakerV2.addWater(100)
        coffeeMakerV2.addCoffee(100)
        coffeeMakerV2.addMilk(100)
        val brew: CoffeeBrew? = coffeeMakerV2.brew("latte")

        Assert.assertEquals(80, coffeeMakerV2.coffeeQuantityInGrams)
        Assert.assertEquals(50, coffeeMakerV2.waterQuantityInMl)
        Assert.assertEquals(40, coffeeMakerV2.milkQuantityInMl)

    }

    @Test(expected = NotEnoughCoffeeException::class)
    fun `test that coffee machine throws error if not enough coffee`() {
        val coffeeMakerV2 = CoffeeMakerV2(
                listOf(
                        Recipe("espresso", 20, 0, 0, { CoffeeBrew("espresso") })
                )
        )

        coffeeMakerV2.addCoffee(15)

        val brew: CoffeeBrew? = coffeeMakerV2.brew("espresso")
    }

    @Test(expected = NotEnoughMilkException::class)
    fun `test that coffee machine throws error if not enough milk`() {
        val coffeeMakerV2 = CoffeeMakerV2(
                listOf(
                        Recipe("latte", 20, 30, 60, { CoffeeBrew("latte") })
                )
        )

        coffeeMakerV2.addCoffee(21)
        coffeeMakerV2.addMilk(55)
        coffeeMakerV2.addWater(30)

        val brew = coffeeMakerV2.brew("latte")
    }

    @Test(expected = NotEnoughWaterException::class)
    fun `test that coffee machine throws error if not enough water`() {
        val coffeeMakerV2 = CoffeeMakerV2(
                listOf(
                        Recipe("Latte", 20, 30, 50, { CoffeeBrew("Latte") })
                )
        )

        coffeeMakerV2.addCoffee(20)
        coffeeMakerV2.addWater(20)
        coffeeMakerV2.addMilk(100)

        val brew = coffeeMakerV2.brew("Latte")
    }

    @Test
    fun `test that coffee maker displays the available ingredients`() {
        val coffeeMakerV2 = CoffeeMakerV2(
                listOf(
                        Recipe("Americano", 20, 40, 60, { CoffeeBrew("Latte") })
                )
        )

        val brew = coffeeMakerV2.toString()

        println(brew)
    }

    @Test
    fun `test that coffee maker can't brew unknown recipe although there is one existing`() {
        val coffeeMakerV2 = CoffeeMakerV2(
                listOf(
                        Recipe("Latte", 20, 50, 40, { CoffeeBrew("Latte") })
                )
        )
        val brew = coffeeMakerV2.brew("espresso")
        Assert.assertNull(brew)
    }
}
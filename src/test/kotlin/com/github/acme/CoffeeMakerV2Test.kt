package com.github.acme

import org.junit.Assert
import org.junit.Test

class CoffeeMakerV2Test {

    @Test
    fun `test new recipes work`() {
        val coffeeMakerV2 = CoffeeMakerV2(
                recipes = listOf(
                        Recipe("latte", 20, 50, 100)
                ),
                brewFactory = mapOf(
                        "latte" to { CoffeeBrew("latte machiatto") }
                )
        )

        coffeeMakerV2.addWater(100)
        coffeeMakerV2.addCoffee(100)
        coffeeMakerV2.addMilk(100)
        val brew: CoffeeBrew? = coffeeMakerV2.brew("latte")
        Assert.assertNotNull(brew)

        println(brew)
    }

    @Test
    fun `test unknown recipe doesn't work`(){
        val coffeeMakerV2 = CoffeeMakerV2(
                recipes = listOf(),
                brewFactory = mapOf()
        )

        val brew: CoffeeBrew? = coffeeMakerV2.brew("latte")
        Assert.assertNull(brew)
    }

    @Test
    fun `test that coffee machine correctly substracts ingredients`() {
        val coffeeMakerV2 = CoffeeMakerV2(
                recipes = listOf(
                        Recipe("espresso", 20, 50, 60)
                ),

                brewFactory = mapOf(
                        "espresso" to {CoffeeBrew("espresso")}

                )
        )

        coffeeMakerV2.addWater(100)
        coffeeMakerV2.addCoffee(100)
        coffeeMakerV2.addMilk(100)
        val brew: CoffeeBrew? = coffeeMakerV2.brew("espresso")

        Assert.assertEquals(80, coffeeMakerV2.coffeeQuantityInGrams)
            
    }
}
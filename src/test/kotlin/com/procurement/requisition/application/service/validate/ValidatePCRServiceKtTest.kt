package com.procurement.requisition.application.service.validate

import com.procurement.requisition.application.service.validate.SpecificWeightedPrice.Model.CriteriaMatrix
import com.procurement.requisition.application.service.validate.SpecificWeightedPrice.Model.Criterion
import com.procurement.requisition.application.service.validate.SpecificWeightedPrice.Model.RequirementGroup
import com.procurement.requisition.application.service.validate.SpecificWeightedPrice.Model.Requirements
import com.procurement.requisition.application.service.validate.SpecificWeightedPrice.Operations.getAllRequirementsCombinations
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.util.stream.Stream

internal class ValidatePCRServiceKtTest {

    class MinSpecificWeightPrice {

        companion object {
            @JvmStatic
            fun mswpValues(): Stream<Arguments> = (0..50)
                .map { Arguments.of(buildRandomMatrix()) }
                .stream()

            private val rq = Requirements(listOf("2.1"))

            private fun buildRandomMatrix(): CriteriaMatrix {
                val rowRange = (0 until 5)
                val columnRange = (0 until 7)

                val rgs = (0..rowRange.random()).map { rq }.map { RequirementGroup(it) }
                val crts = (0..columnRange.random()).map { rgs }.map { Criterion(it) }

                return CriteriaMatrix(crts)
            }
        }

        /*
         *      Requirements:
         *
         *      -------------|___Req_group_1__|__Req_group_2__|
         *      criteria-1   | (1.1.1, 1.1.2) | (1.2)         |
         *      Criterion-2  | (2.1)          | (2.2)         |
         *      ------------------------------------------------
         *
         *      Combinations:
         *      1.1.1 - 1.1.2 - 2.1
         *      1.1.1 - 1.1.2 - 2.2
         *      1.2 - 2.1
         *      1.2 - 2.2
         *
         */
        @Test
        fun rightCalculatedPrices() {
            val criterion1 = Criterion(
                listOf(RequirementGroup(Requirements(listOf("1.1.1", "1.1.2"))), RequirementGroup(Requirements(listOf("1.2"))))
            )
            val criterion2 = Criterion(
                listOf(RequirementGroup(Requirements(listOf("2.1"))), RequirementGroup(Requirements(listOf("2.2"))))
            )
            val matrix = CriteriaMatrix(listOf(criterion1, criterion2))

            val actualCombinations = getAllRequirementsCombinations(matrix)
            val expectedAmount = pow(matrix[0].size, matrix.size)

            val minCoefficients = mapOf(
                "1.1.1" to BigDecimal("0.411"),
                "1.1.2" to BigDecimal("0.412"),
                "1.2"   to BigDecimal("0.420"),
                "2.1"   to BigDecimal("0.510"),
                "2.2"   to BigDecimal("0.520"),
            )

            val specificWeightPrices = ValidatePCRService.calculateSpecificWeightPrice(
                actualCombinations,
                minCoefficients
            )
            println("Automated calculated prices: $specificWeightPrices")

            val c1 = minCoefficients["1.1.1"]!! * minCoefficients["1.1.2"]!! * minCoefficients["2.1"]!!
            val c2 = minCoefficients["1.1.1"]!! * minCoefficients["1.1.2"]!! * minCoefficients["2.2"]!!
            val c3 = minCoefficients["1.2"]!! * minCoefficients["2.1"]!!
            val c4 = minCoefficients["1.2"]!! * minCoefficients["2.2"]!!
            val prices = setOf(c1, c2, c3, c4)
            println("Manual calculated prices: $prices")

            assertEquals(expectedAmount, actualCombinations.size)
            assertEquals(prices.size, actualCombinations.size)
            prices.forEach { expectedPrice ->
                assertTrue(expectedPrice in specificWeightPrices)
            }
        }

        @Test
        fun emptyListOfRequirement() {
            val crts1 = emptyList<Criterion>()
            val m = CriteriaMatrix(crts1)

            val actualCombinations = getAllRequirementsCombinations(m)
            val expectedAmount = 0

            assertEquals(expectedAmount, actualCombinations.size)
        }

        @ParameterizedTest
        @MethodSource("mswpValues")
        fun rightAmountOfCombinations2(m: CriteriaMatrix) {
            val actualCombinations = getAllRequirementsCombinations(m)
            val expectedAmount = pow(m[0].size, m.size)

            assertEquals(expectedAmount, actualCombinations.size)
        }


        private fun pow(base: Int, x: Int): Int {
            return (0 until x).fold(1, { acc, _ -> acc * base })
        }
    }
}
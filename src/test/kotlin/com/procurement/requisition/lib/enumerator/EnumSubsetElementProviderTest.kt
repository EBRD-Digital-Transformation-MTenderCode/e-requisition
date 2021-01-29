package com.procurement.requisition.lib.enumerator

import com.procurement.requisition.lib.enumerator.EnumSubsetElementProvider.Companion.keysAsStrings
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class EnumSubsetElementProviderTest {

    enum class Parent(override val key: String) : EnumElementProvider.Element {

        VALID_CASE_1("valid_case_1"),
        VALID_CASE_2("valid_case_2"),
        CASE_NOT_PRESENT_IN_SUBSET("caseNotPresentInSubset"),

        @EnumElementProvider.DeprecatedElement
        DEPRECATED("deprecated"),

        @EnumElementProvider.ExcludedElement
        EXCLUDED("excluded");

        override fun toString(): String = key

        companion object : EnumElementProvider<Parent>(info = info())
    }

    enum class Subset(override val base: Parent) : EnumSubsetElementProvider.Element<Parent> {

        VALID_CASE_1(Parent.VALID_CASE_1),
        VALID_CASE_2(Parent.VALID_CASE_2),
        DEPRECATED(Parent.DEPRECATED),
        EXCLUDED(Parent.EXCLUDED);

        companion object : EnumSubsetElementProvider<Subset, Parent>(info = info())
    }

    @Test
    fun allowedElements_excludeExcludedElementsInParent_success() {
        val subsetCasesWithoutExcluded = Subset.allowedElements

        val expected = setOf(Subset.VALID_CASE_1, Subset.VALID_CASE_2, Subset.DEPRECATED)

        assertEquals(subsetCasesWithoutExcluded, expected)
    }

    @Test
    fun keysAsStrings_elementDeprecatedInParentIsTaggedDeprecatedInSubset_success(){
        val subsetWithDeprecatedElement = listOf(Subset.DEPRECATED)

        val actual = subsetWithDeprecatedElement.keysAsStrings()
        val expected = "[deprecated (Deprecated)]"

        assertEquals(expected, actual)
    }

    @Test
    fun orNull_success(){
        val key = Parent.VALID_CASE_1.key
        val actual = Subset.orNull(key)
        val expected = Subset.VALID_CASE_1

        assertEquals(expected, actual)
    }

    @Test
    fun orNull_keyInParentButNotInSubset_null(){
        val key = Parent.CASE_NOT_PRESENT_IN_SUBSET.key
        val actual = Subset.orNull(key)
        val expected = null

        assertEquals(expected, actual)
    }
}
package com.procurement.requisition.application.service.rule

import com.procurement.requisition.application.repository.rule.RulesRepository
import com.procurement.requisition.application.repository.rule.deserializer.LotStatesRuleDeserializer
import com.procurement.requisition.application.repository.rule.deserializer.MinSpecificWeightPriceRuleDeserializer
import com.procurement.requisition.application.repository.rule.deserializer.TenderStatesRuleDeserializer
import com.procurement.requisition.application.repository.rule.model.LotStatesRule
import com.procurement.requisition.application.repository.rule.model.MinSpecificWeightPriceRule
import com.procurement.requisition.application.repository.rule.model.TenderStatesRule
import com.procurement.requisition.domain.model.OperationType
import com.procurement.requisition.domain.model.ProcurementMethodDetails
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import org.springframework.stereotype.Service

interface RulesService {
    fun getMinSpecificWeightPrice(
        country: String,
        pmd: ProcurementMethodDetails
    ): Result<MinSpecificWeightPriceRule, Failure>

    fun getValidLotStates(
        country: String,
        pmd: ProcurementMethodDetails,
        operationType: OperationType
    ): Result<LotStatesRule, Failure>

    fun getValidTenderStates(
        country: String,
        pmd: ProcurementMethodDetails,
        operationType: OperationType
    ): Result<TenderStatesRule, Failure>
}

@Service
class RulesServiceImpl(
    private val ruleRepository: RulesRepository,
    private val tenderStatesRuleDeserializer: TenderStatesRuleDeserializer,
    private val lotStatesRuleDeserializer: LotStatesRuleDeserializer,
    private val minSpecificWeightPriceRuleDeserializer: MinSpecificWeightPriceRuleDeserializer,
) : RulesService {

    companion object {
        const val PARAMETER_VALID_TENDER_STATES = "validStates"
        const val PARAMETER_VALID_LOT_STATES = "validLotStates"
        const val PARAMETER_MIN_SPECIFIC_WEIGHT_PRICE = "minSpecificWeightPrice"
    }

    override fun getMinSpecificWeightPrice(
        country: String,
        pmd: ProcurementMethodDetails
    ): Result<MinSpecificWeightPriceRule, Failure> = ruleRepository
        .get(country = country, pmd = pmd, parameter = PARAMETER_MIN_SPECIFIC_WEIGHT_PRICE)
        .flatMap { value ->
            minSpecificWeightPriceRuleDeserializer.deserialize(value)
        }

    override fun getValidLotStates(
        country: String,
        pmd: ProcurementMethodDetails,
        operationType: OperationType
    ): Result<LotStatesRule, Failure> = ruleRepository
        .get(country, pmd, operationType, PARAMETER_VALID_LOT_STATES)
        .flatMap { value ->
            lotStatesRuleDeserializer.deserialize(value)
        }

    override fun getValidTenderStates(
        country: String,
        pmd: ProcurementMethodDetails,
        operationType: OperationType
    ): Result<TenderStatesRule, Failure> = ruleRepository
        .get(country, pmd, operationType, PARAMETER_VALID_TENDER_STATES)
        .flatMap { value ->
            tenderStatesRuleDeserializer.deserialize(value)
        }
}

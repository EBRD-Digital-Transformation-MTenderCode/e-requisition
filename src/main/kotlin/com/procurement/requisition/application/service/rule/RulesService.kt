package com.procurement.requisition.application.service.rule

import com.procurement.requisition.application.repository.rule.RulesRepository
import com.procurement.requisition.application.repository.rule.deserializer.LotStateForSettingRuleDeserializer
import com.procurement.requisition.application.repository.rule.deserializer.LotStatesRuleDeserializer
import com.procurement.requisition.application.repository.rule.deserializer.MinSpecificWeightPriceRuleDeserializer
import com.procurement.requisition.application.repository.rule.deserializer.TenderStatesRuleDeserializer
import com.procurement.requisition.application.repository.rule.model.LotStateForSettingRule
import com.procurement.requisition.application.repository.rule.model.LotStatesRule
import com.procurement.requisition.application.repository.rule.model.MinSpecificWeightPriceRule
import com.procurement.requisition.application.repository.rule.model.TenderStatesRule
import com.procurement.requisition.domain.model.OperationType
import com.procurement.requisition.domain.model.ProcurementMethodDetails
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.flatMap
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

    fun getLotState(
        country: String,
        pmd: ProcurementMethodDetails,
        operationType: OperationType
    ): Result<LotStateForSettingRule, Failure>
}

@Service
class RulesServiceImpl(
    private val ruleRepository: RulesRepository,
    private val lotStateForSettingRuleDeserializer: LotStateForSettingRuleDeserializer,
    private val lotStatesRuleDeserializer: LotStatesRuleDeserializer,
    private val minSpecificWeightPriceRuleDeserializer: MinSpecificWeightPriceRuleDeserializer,
    private val tenderStatesRuleDeserializer: TenderStatesRuleDeserializer,
) : RulesService {

    companion object {
        const val PARAMETER_LOT_STATE_FOR_SETTING = "lotStateForSetting"
        const val PARAMETER_MIN_SPECIFIC_WEIGHT_PRICE = "minSpecificWeightPrice"
        const val PARAMETER_VALID_LOT_STATES = "validLotStates"
        const val PARAMETER_VALID_TENDER_STATES = "validStates"
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

    override fun getLotState(
        country: String,
        pmd: ProcurementMethodDetails,
        operationType: OperationType
    ): Result<LotStateForSettingRule, Failure> = ruleRepository
        .get(country, pmd, operationType, PARAMETER_LOT_STATE_FOR_SETTING)
        .flatMap { value ->
            lotStateForSettingRuleDeserializer.deserialize(value)
        }
}

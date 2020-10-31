package com.procurement.requisition.domain.model.tender.target.observation

class Observations(values: List<Observation> = emptyList()) : List<Observation> by values {

    constructor(observation: Observation) : this(listOf(observation))
}

package com.procurement.requisition.infrastructure.bind.observation.measure

class ObservationMeasureException(coefficientValue: String, description: String = "") :
    RuntimeException("Incorrect value of the coefficient: '$coefficientValue'. $description")

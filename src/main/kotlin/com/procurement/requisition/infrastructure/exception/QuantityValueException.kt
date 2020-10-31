package com.procurement.requisition.infrastructure.exception

class QuantityValueException(quantity: String, description: String = "") :
    RuntimeException("Incorrect value of the quantity: '$quantity'. $description")

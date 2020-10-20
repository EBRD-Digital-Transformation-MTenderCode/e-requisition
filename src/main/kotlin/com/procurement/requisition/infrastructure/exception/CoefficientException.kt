package com.procurement.requisition.infrastructure.exception

class CoefficientException(coefficient: String, description: String = "") :
    RuntimeException("Incorrect coefficient: '$coefficient'. $description")
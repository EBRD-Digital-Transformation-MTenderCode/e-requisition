package com.procurement.requisition.domain.extension

fun String.dot(): String = if (this.last() == '.') this else "$this."

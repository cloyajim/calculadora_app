package com.example.calculadora

interface OnResolveListener {

    fun onShowResult(result: Double)
    fun onShowMessage(errorRes: Int)
}
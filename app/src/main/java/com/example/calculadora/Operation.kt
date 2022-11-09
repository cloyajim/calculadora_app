package com.example.calculadora

class Operation {

    companion object{
        fun getOperator(operation: String): String {
            var operator: String

            if (operation.contains(Constants.OPERATOR_MULTI)){
                operator = Constants.OPERATOR_MULTI
            } else if(operation.contains(Constants.OPERATOR_DIV)){
                operator = Constants.OPERATOR_DIV
            }else if(operation.contains(Constants.OPERATOR_SUM)){
                operator = Constants.OPERATOR_SUM
            }else {
                operator = Constants.OPERATOR_NULL
            }

            // se extrae el operador de resta al inicio
            if( operator == Constants.OPERATOR_NULL && operation.lastIndexOf(Constants.OPERATOR_SUB) > 0){
                operator = Constants.OPERATOR_SUB
            }

            return operator
        }

        //ultimo caracter
        fun canReplaceOperator(charSecuence: CharSequence): Boolean {
            if(charSecuence.length < 2) return false

            val lastElement = charSecuence[charSecuence.length - 1].toString()
            val penultimateElement = charSecuence[charSecuence.length - 2].toString()

            return (lastElement == Constants.OPERATOR_MULTI ||
                    lastElement == Constants.OPERATOR_DIV ||
                    lastElement == Constants.OPERATOR_SUM) &&
                    (penultimateElement == Constants.OPERATOR_MULTI ||
                            penultimateElement == Constants.OPERATOR_DIV ||
                            penultimateElement == Constants.OPERATOR_SUM ||
                            penultimateElement == Constants.OPERATOR_SUB)
        }
    }
}
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

        fun tryResult(operationRef: String, isFromResult: Boolean, listener: OnResolveListener) {
            if(operationRef.isEmpty()) return

            var operation = operationRef
            if(operation.contains(Constants.POINT) && operation.lastIndexOf(Constants.POINT) == operation.length - 1){
                operation = operation.substring(0, operation.length -1)
            }

            val operator = Operation.getOperator(operation)
            var values = arrayOfNulls<String>(0)

            //division de resta
            if(operator != Constants.OPERATOR_NULL){
                if(operator == Constants.OPERATOR_SUB){
                    val index = operation.lastIndexOf(Constants.OPERATOR_SUB)
                    if(index < operation.length-1){
                        values = arrayOfNulls(2)
                        values[0] = operation.substring(0, index)
                        values[1] = operation.substring(index+1)
                    }else{
                        values = arrayOfNulls(1)
                        values[0] = operation.substring(0, index)
                    }
                }else {
                    values = operation.split(operator).toTypedArray()
                }
            }

            if(values.size > 1) {
                try{ //validacion de signo
                    val numberOne = values[0]!!.toDouble()
                    val numberTwo = values[1]!!.toDouble()

                    listener.onShowResult(getResult(numberOne, operator, numberTwo))
                }catch (e:NumberFormatException){
                    if(isFromResult) listener.onShowMessage(R.string.message_num_incorrect)
                }
            }else{ //solo si es diferente de null muestra el mensaje
                if(isFromResult && operator != Constants.OPERATOR_NULL)
                    listener.onShowMessage(R.string.message_num_incorrect)
            }
        }

        //obtener el resultado
        fun getResult(numerOne: Double, operator: String, numberTwo: Double): Double{
            var result = 0.0

            //operacion simple
            when(operator){
                Constants.OPERATOR_MULTI -> result = numerOne * numberTwo
                Constants.OPERATOR_DIV -> result = numerOne / numberTwo
                Constants.OPERATOR_SUM -> result = numerOne + numberTwo
                Constants.OPERATOR_SUB -> result = numerOne - numberTwo
            }

            return result
        }
    }
}
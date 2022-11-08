package com.example.calculadora

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.calculadora.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

   private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvOperation.addTextChangedListener { charSecuence ->
            if(canReplaceOperator(charSecuence.toString())){
                val length = binding.tvOperation.text.length
                val newOperation = binding.tvOperation.text.toString().substring(0, length -2) +
                        binding.tvOperation.text.toString().substring( length - 1)
                binding.tvOperation.text = newOperation
            }
        }

    }

    //ultimo caracter
    private fun canReplaceOperator(charSecuence: CharSequence): Boolean {
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

    fun onClickButton(view: View){
        val valueStr = (view as Button).text.toString()

        when(view.id){
            R.id.btnDelete ->{
                val length = binding.tvOperation.text.length
                if(length > 0 ){ //para que no cierre al no tener caracter
                    val newOperation = binding.tvOperation.text.toString().substring(0, length-1)
                    binding.tvOperation.text = newOperation
                }
            }
            R.id.btnClear ->{
                binding.tvOperation.text = ""
                binding.tvResult.text = ""
            }
            R.id.btnResult ->{
                tryResult(binding.tvOperation.text.toString(), true)
            }
            R.id.btnMulti,
            R.id.btnDiv,
            R.id.btnSum,
            R.id.btnSub -> {
                tryResult(binding.tvOperation.text.toString(), false)

                val operator = valueStr
                val operation = binding.tvOperation.text.toString()
                addOperator(operator, operation)

            }
            R.id.btnPoint ->{
                val operation = binding.tvOperation.text.toString()
                addPoint(valueStr, operation)
            }
            else ->{
                binding.tvOperation.append(valueStr)
            }
        }
    }

    private fun addPoint(pointStr: String, operation: String) {
        if(!operation.contains(Constants.POINT)){
            binding.tvOperation.append(pointStr)
        } else {
            val operator = getOperator(operation)

            var values = arrayOfNulls<String>(0)
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
            //dos numeros  que usan punto
            if(values.size > 0){
                val numberOne = values[0]!!
                if(values.size > 1){
                    val numberTwo = values[1]!!
                    if(numberOne.contains(Constants.POINT) && !numberTwo.contains(Constants.POINT)){
                        binding.tvOperation.append(pointStr)
                    }
                } else {
                    if(numberOne.contains(Constants.POINT)){
                        binding.tvOperation.append(pointStr)
                    }
                }
            }
        }
    }

    private fun addOperator(operator: String, operation: String) {
        val lastElement = if(operation.isEmpty()) ""
        else operation.substring(operation.length - 1)

        if(operator == Constants.OPERATOR_SUB){
            if(operation.isEmpty() || lastElement != Constants.OPERATOR_SUB && lastElement != Constants.POINT){
                binding.tvOperation.append(operator)
            }
        }else{
            if(!operation.isEmpty() && lastElement != Constants.POINT){
                binding.tvOperation.append(operator)
            }
        }
    }

    private fun tryResult(operationRef: String, isFromResult: Boolean) {
        if(operationRef.isEmpty()) return

        var operation = operationRef
        if(operation.contains(Constants.POINT) && operation.lastIndexOf(Constants.POINT) == operation.length - 1){
            operation = operation.substring(0, operation.length -1)
        }

        val operator = getOperator(operation)
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

                binding.tvResult.text = getResult(numberOne, operator, numberTwo).toString()



                if(binding.tvResult.text.isNotEmpty() && !isFromResult){
                    binding.tvOperation.text = binding.tvResult.text
                }
            }catch (e:NumberFormatException){
               if(isFromResult) showMessage()
            }
        }else{ //solo si es diferente de null muestra el mensaje
            if(isFromResult && operator != Constants.OPERATOR_NULL) showMessage()
        }
    }

    private fun getOperator(operation: String): String {
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

    //obtener el resultado
    private fun getResult(numerOne: Double, operator: String, numberTwo: Double): Double{
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

    private fun showMessage(){
        Snackbar.make(binding.root, getString(R.string.message_expresion_incorrect),
            Snackbar.LENGTH_SHORT).setAnchorView(binding.llTop).show()
    }


}
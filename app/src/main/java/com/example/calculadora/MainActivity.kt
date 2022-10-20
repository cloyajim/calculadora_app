package com.example.calculadora

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.calculadora.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

   private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

                //binding.tvOperation.append(valueStr)
            }
            else ->{
                binding.tvOperation.append(valueStr)
            }
        }
    }

    private fun addOperator(operator: String, operation: String) {
        val lastElement = if(operation.isEmpty()) ""
        else operation.substring(operation.length - 1)

        if(operator == OPERATOR_SUB){
            if(operation.isEmpty() || lastElement != OPERATOR_SUB && lastElement != POINT){
                binding.tvOperation.append(operator)
            }
        }else{
            if(!operation.isEmpty() && lastElement != POINT){
                binding.tvOperation.append(operator)
            }
        }
    }

    private fun tryResult(operationRef: String, isFromResult: Boolean) {
        if(operationRef.isEmpty()) return

        var operation = operationRef
        if(operation.contains(POINT) && operation.lastIndexOf(POINT) == operation.length - 1){
            operation = operation.substring(0, operation.length -1)
        }

        val operator = getOperator(operation)
        var values = arrayOfNulls<String>(0)

        //division de resta
        if(operator != OPERATOR_NULL){
            if(operator == OPERATOR_SUB){
                val index = operation.lastIndexOf(OPERATOR_SUB)
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
            if(isFromResult && operator != OPERATOR_NULL) showMessage()
        }
    }

    private fun getOperator(operation: String): String {
        var operator: String

        if (operation.contains(OPERATOR_MULTI)){
            operator = OPERATOR_MULTI
        } else if(operation.contains(OPERATOR_DIV)){
            operator = OPERATOR_DIV
        }else if(operation.contains(OPERATOR_SUM)){
            operator = OPERATOR_SUM
        }else {
            operator = OPERATOR_NULL
        }

        // se extrae el operador de resta al inicio
        if( operator == OPERATOR_NULL && operation.lastIndexOf(OPERATOR_SUB) > 0){
            operator = OPERATOR_SUB
        }

        return operator
    }

    //obtener el resultado
    private fun getResult(numerOne: Double, operator: String, numberTwo: Double): Double{
        var result = 0.0

        //operacion simple
        when(operator){
            OPERATOR_MULTI -> result = numerOne * numberTwo
            OPERATOR_DIV -> result = numerOne / numberTwo
            OPERATOR_SUM -> result = numerOne + numberTwo
            OPERATOR_SUB -> result = numerOne - numberTwo
        }

        return result
    }

    private fun showMessage(){
        Snackbar.make(binding.root, getString(R.string.message_expresion_incorrect),
            Snackbar.LENGTH_SHORT).setAnchorView(binding.llTop).show()
    }

    companion object{
        const val OPERATOR_MULTI = "x"
        const val OPERATOR_DIV = "÷"
        const val OPERATOR_SUM = "+"
        const val OPERATOR_SUB = "-"
        const val OPERATOR_NULL = "null"
        const val POINT = "."
    }

}
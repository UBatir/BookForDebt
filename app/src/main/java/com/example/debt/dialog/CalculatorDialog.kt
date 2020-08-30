package com.example.debt.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.example.debt.R
import com.example.debt.interfaces.SetData
import kotlinx.android.synthetic.main.dialog_calculator.*
import net.objecthunter.exp4j.ExpressionBuilder
import java.lang.Exception

class CalculatorDialog(context: Context, private val listener: SetData): Dialog(context) {

    var sum:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_calculator)
        btn_0.setOnClickListener{ setTextFields("0")}
        btn_1.setOnClickListener{ setTextFields("1")}
        btn_2.setOnClickListener{ setTextFields("2")}
        btn_3.setOnClickListener{ setTextFields("3")}
        btn_4.setOnClickListener{ setTextFields("4")}
        btn_5.setOnClickListener{ setTextFields("5")}
        btn_6.setOnClickListener{ setTextFields("6")}
        btn_7.setOnClickListener{ setTextFields("7")}
        btn_8.setOnClickListener{ setTextFields("8")}
        btn_9.setOnClickListener{ setTextFields("9")}
        btn_minus.setOnClickListener{ setTextFields("-")}
        btn_plus.setOnClickListener{ setTextFields("+")}
        btn_umnoj.setOnClickListener{ setTextFields("*")}
        btn_devided.setOnClickListener{ setTextFields("/")}
        open_bracket.setOnClickListener{ setTextFields("(")}
        close_bracket.setOnClickListener{ setTextFields(")")}

        clear.setOnClickListener{
            math_operation.text = ""

            result_text.text = ""
        }
        btn_back.setOnClickListener{
            val str = math_operation.text.toString()
            if(str.isNotEmpty())
                math_operation.text = str.substring(0, str.length - 1)

            result_text.text = ""
        }
        btn_result.setOnClickListener{
            count()
        }
        btnNegative.setOnClickListener {
            dismiss()
        }
        btnPositive.setOnClickListener {
            count()
            listener.setSum(sum)
            dismiss()
        }
    }
    private fun setTextFields(str:String){
        if(result_text.text !=""){
            math_operation.text = result_text.text
            result_text.text = ""
        }
        math_operation.append(str)
    }

    private fun count(){
        try {
            val  ex = ExpressionBuilder(math_operation.text.toString()).build()
            val result = ex.evaluate()
            val longRes = result.toLong()
            if(result == longRes.toDouble())
                sum = longRes
            else
                sum = result.toLong()
        }catch (e: Exception){}
        result_text.text=sum.toString()
    }
}


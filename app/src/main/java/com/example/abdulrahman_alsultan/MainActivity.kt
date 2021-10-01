package com.example.abdulrahman_alsultan

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private var balanceAmount: Float = 0.0f
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var bankProcesses: ArrayList<String>

    // UI Elements
    private lateinit var adapter: RecyclerViewAdapter
    private lateinit var depositButton: Button
    private lateinit var withdrawButton: Button
    private lateinit var depositInput: EditText
    private lateinit var withdrawInput: EditText
    private lateinit var balanceTV: TextView
    private lateinit var myRV: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences= getSharedPreferences("AlsultanBank", MODE_PRIVATE)
        bankProcesses = ArrayList()

        // UI Elements
        depositButton = findViewById(R.id.deposit_button)
        withdrawButton = findViewById(R.id.withdraw_button)
        depositInput = findViewById(R.id.deposit_input)
        withdrawInput = findViewById(R.id.withdraw_input)
        balanceTV = findViewById(R.id.balance)
        myRV = findViewById(R.id.myRV)

        balanceAmount = sharedPreferences.getFloat("balance", 0.0F)
        balanceTV.text = "Current Balance: ${balanceAmount.toString()}"

        when(sharedPreferences.getString("color", "white")){
            "white" -> balanceTV.setTextColor(Color.WHITE)
            "black" -> balanceTV.setTextColor(Color.BLACK)
            "red" -> balanceTV.setTextColor(Color.RED)
        }

        adapter = RecyclerViewAdapter(bankProcesses)
        myRV.adapter = adapter
        myRV.layoutManager = LinearLayoutManager(this)

        fun canWithdrawal(){
            // to disable withdraw button if the balance is less than 0
            withdrawButton.isClickable = balanceAmount > 0
        }

        fun setColor(): Boolean{
            if(balanceAmount > 0){
                balanceTV.setTextColor(Color.BLACK)
                with(sharedPreferences.edit()){
                    putFloat("balance", balanceAmount)
                    putString("color", "black")
                }
                return true
            }
            else if( balanceAmount == 0F){
                balanceTV.setTextColor(Color.WHITE)
                with(sharedPreferences.edit()){
                    putFloat("balance", balanceAmount)
                    putString("color", "white")
                }
                return true
            }

            balanceTV.setTextColor(Color.RED)
            with(sharedPreferences.edit()){
                putFloat("balance", balanceAmount)
                putString("color", "red")
            }
            return false
        }


        depositButton.setOnClickListener{
            var input = depositInput.text.toString()
            depositInput.setText("")
            depositInput.hint = "Amount"
            if(input == null || input.trim() == ""){
                Snackbar.make(it, "Please make sure to enter an amount", Snackbar.LENGTH_SHORT).show()
            }
            else{
                try {
                    val bal: Float = input.toFloat()
                    balanceAmount += bal
                    balanceTV.text = "Current Balance: ${balanceAmount.toString()}"
                    setColor()
                    bankProcesses.add("Deposit: ${bal.toInt()}")

                    canWithdrawal()

                    myRV.scrollToPosition(bankProcesses.size - 1 )
                    adapter.notifyDataSetChanged()
                }catch (e: Exception){
                    Snackbar.make(it, "Please make sure to enter an amount", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        withdrawButton.setOnClickListener{
            var input = withdrawInput.text.toString()
            withdrawInput.setText("")
            withdrawInput.hint = "Amount"
            if(input == null || input.trim() == ""){
                Snackbar.make(it, "Please make sure to enter an amount", Snackbar.LENGTH_SHORT).show()
            }
            else{
                try {
                    val bal: Float = input.toFloat()
                    balanceAmount -= bal

                    bankProcesses.add("Withdrawal: ${bal.toInt()}")

                    if(!setColor()){
                        bankProcesses.add("Negative Balance Fee: 20")
                        balanceAmount -= 20
                        with(sharedPreferences.edit()){
                            putFloat("balance", balanceAmount)

                        }
                    }
                    balanceTV.text = "Current Balance: ${balanceAmount.toString()}"

                    canWithdrawal()
                    with(sharedPreferences.edit()){
                        putFloat("balance", balanceAmount)
                    }
                    myRV.scrollToPosition(bankProcesses.size - 1)
                    adapter.notifyDataSetChanged()
                }catch (e: Exception){
                    Snackbar.make(it, "Please make sure to enter an amount", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        val ed: Editor = sharedPreferences.edit()
        ed.putFloat("balance", balanceAmount)
        ed.commit()
    }

    override fun onPause() {
        super.onPause()
        val ed: Editor = sharedPreferences.edit()
        ed.putFloat("balance", balanceAmount)
        when{
            balanceAmount > 0F -> ed.putString("color", "black")
            balanceAmount == 0F -> ed.putString("color", "white")
            else -> ed.putString("color", "red")
        }
        ed.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        bankProcesses.clear()
        adapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }

}
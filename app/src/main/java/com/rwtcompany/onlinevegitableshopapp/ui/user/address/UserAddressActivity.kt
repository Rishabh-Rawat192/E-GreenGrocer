package com.rwtcompany.onlinevegitableshopapp.ui.user.address

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rwtcompany.onlinevegitableshopapp.databinding.ActivityUserAddressBinding
import com.rwtcompany.onlinevegitableshopapp.ui.helper.DatePickerFragment
import com.rwtcompany.onlinevegitableshopapp.ui.helper.TimePickerFragment

class UserAddressActivity : AppCompatActivity(),TimePickerDialog.OnTimeSetListener,DatePickerDialog.OnDateSetListener{
    private lateinit var binding:ActivityUserAddressBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUserAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etTime.setOnClickListener {
            showTimePickerDialog(it)
        }

        binding.etDate.setOnClickListener {
            showDatePickerDialog(it)
        }

        binding.btnPlaceOrder.setOnClickListener {
            TODO()
        }
    }

    private fun showTimePickerDialog(v: View) {
        TimePickerFragment().show(supportFragmentManager, "timePicker")
    }

    private fun showDatePickerDialog(v: View) {
        DatePickerFragment().show(supportFragmentManager, "datePicker")
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        binding.etTime.setText("$hourOfDay : $minute")
        Toast.makeText(this,"$hourOfDay $minute",Toast.LENGTH_LONG).show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        binding.etDate.setText("$dayOfMonth / $month / $year")
    }
}
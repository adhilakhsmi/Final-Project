package com.singlepointsol.navigatioindrawerr

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.singlepointsol.navigatioindrawerr.databinding.ActivityAgentBinding
import com.singlepointsol.navigatioindrawerr.databinding.ActivityPolicyBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class PolicyActivity : AppCompatActivity() {
    private lateinit var binding:ActivityPolicyBinding
    private val policyService=PolicyInstance.getInstance().create(PolicyApiService::class.java)

    private var isFirstFetchDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Set Listeners to the button
        binding.btnGet.setOnClickListener {
            if (!isFirstFetchDone) {
                fetchPolicy()
                isFirstFetchDone = true // Mark as done after the first fetch
            } else {
                clearInputFields()
            }
        }
        binding.btnPost.setOnClickListener {
            savePolicy()
        }
        binding.btnPut.setOnClickListener {
            updatePolicy()
        }
        binding.btnDelete.setOnClickListener {
            deletePolicy()
        }

        setupPolicyIdDropdown()

        setupDatePicker()

    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        binding.etReceiptDate.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawable = binding.etReceiptDate.compoundDrawables[2]  // Right drawable
                if (event.rawX >= binding.etReceiptDate.right - drawable.bounds.width()) {
                    DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                        val formattedDate = String.format(
                            "%04d-%02d-%02d",
                            selectedYear,
                            selectedMonth + 1,
                            selectedDay
                        )
                        binding.etReceiptDate.setText(formattedDate)
                    }, year, month, day).show()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }


        private fun setupPolicyIdDropdown() {
        // Initialize the AutoCompleteTextView for Payment Mode
        val paymentModeEditText: AutoCompleteTextView = findViewById(R.id.payment_mode_et)

        // Initialize the AutoCompleteTextView for Policy Number
        val policyNumberEditText: AutoCompleteTextView = findViewById(R.id.et_policy_no)

        // Initialize the AutoCompleteTextView for Proposal Number
        val proposalNumberEditText: AutoCompleteTextView = findViewById(R.id.et_proposal_no)

        // Create ArrayAdapters for each dropdown
        val paymentModeAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.payment_mode, // String array resource for Payment Modes
            android.R.layout.simple_dropdown_item_1line
        )

        val policyNumberAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.policyNumber, // String array resource for Policy Numbers
            android.R.layout.simple_dropdown_item_1line
        )

        val proposalNumberAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.proposalNumber, // String array resource for Proposal Numbers
            android.R.layout.simple_dropdown_item_1line
        )

        // Set the adapters for the AutoCompleteTextViews
        paymentModeEditText.setAdapter(paymentModeAdapter)
        policyNumberEditText.setAdapter(policyNumberAdapter)
        proposalNumberEditText.setAdapter(proposalNumberAdapter)
    }

    private fun clearInputFields() {
        binding.etPolicyNo.text?.clear()
        binding.etProposalNo.text?.clear()
        binding.etNoClaimBonus.text?.clear()
        binding.etReceiptNo.text?.clear()
        binding.etReceiptDate.text?.clear()
        binding.paymentModeEt.text?.clear()
        binding.etAmount.text?.clear()
    }
    private fun getPolicyFromInput(): PolicyItem? {
        val policyNumber= binding.etPolicyNo.text.toString()
        val proposalNumber = binding.etProposalNo.text.toString()
        val noClaimBonus = binding.etNoClaimBonus.text.toString()
        val receiptNumber = binding.etReceiptNo.text.toString()
        val receiptDate = binding.etReceiptDate.text.toString()
        val paymentMode = binding.paymentModeEt.text.toString()
        val amount = binding.etAmount.text.toString()

        return if (policyNumber.isNotEmpty() && proposalNumber.isNotEmpty() &&
            noClaimBonus.isNotEmpty() && receiptNumber.isNotEmpty() && receiptDate.isNotEmpty() &&paymentMode.isNotEmpty() && amount.isNotEmpty()
        ) {
            PolicyItem(policyNo = policyNumber, proposalNo = proposalNumber, noClaimBonus = noClaimBonus, receiptNo = receiptNumber, receiptDate = receiptDate, paymentMode = paymentMode, amount = amount)
        } else null
    }

    private fun fetchPolicy() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = policyService.fetchPolicyDetails()
                if (response.isSuccessful) {
                    val policy = response.body()
                    if (!policy.isNullOrEmpty()) {
                        val firstPolicy= policy.first()
                        binding.etPolicyNo.setText(firstPolicy.policyNo)
                        binding.etProposalNo.setText(firstPolicy.proposalNo)
                        binding.etNoClaimBonus.setText(firstPolicy.noClaimBonus)
                        binding.etReceiptNo.setText(firstPolicy.receiptNo)
                        binding.etReceiptDate.setText(firstPolicy.receiptDate)
                        binding.paymentModeEt.setText(firstPolicy.paymentMode)
                        binding.etAmount.setText(firstPolicy.amount)


                        Toast.makeText(this@PolicyActivity, "Fetched Policy successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@PolicyActivity, "No Policy data found!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@PolicyActivity, "Error fetching Policy!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("PolicyActivity", "Fetch error: ${e.message}")
            }
        }
    }

    private fun savePolicy() {
        val newPolicy =getPolicyFromInput ()
        if (newPolicy == null || newPolicy.policyNo.isNullOrBlank()) {
            Toast.makeText(this, "Please fill in all fields, including PolicyNo", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val policyNo = newPolicy.policyNo!!

                // Log the data before making the API call
                Log.d("AgentActivity", "Attempting to add agent with ID: $policyNo, Data: $policyNo")

                val response =policyService.addPolicyDetails(policyNo, newPolicy)

                if (response.isSuccessful) {
                    Log.d("PolicyActivity", "Policy  added successfully: ${response.body()}")
                    runOnUiThread {
                        Toast.makeText(this@PolicyActivity, "policy added successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields( )
                    }
                } else {
                    // Log the response code and error details
                    Log.e("PolicyActivity", "Failed to add policy: Response Code: ${response.code()}, Error: ${response.errorBody()?.string()}")
                    runOnUiThread {
                        Toast.makeText(this@PolicyActivity, "Failed to add agent: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("PolicyActivity", "Exception: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@PolicyActivity, "Error adding agent: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updatePolicy() {
        val updatePolicy = getPolicyFromInput()
        val policyNo= binding.etPolicyNo.text.toString().trim()

        if (updatePolicy == null || policyNo.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Log the request payload for debugging
                Log.d("PolicicyActivity", "Attempting to update product with ID: $policyNo, Payload: $updatePolicy")

                // Make the PUT request to update the product
                val response = policyService.updatePolicyDetails(policyNo, updatePolicy)

                // Check if the response is successful (status 200 or 204)
                if (response.isSuccessful) {
                    if (response.code() == 204) {
                        // Handle the case when the server returns 204 No Content
                        Log.d("PolicyActivity", "policy updated successfully (no content returned).")
                        runOnUiThread {
                            Toast.makeText(this@PolicyActivity, "policy updated successfully!", Toast.LENGTH_SHORT).show()
                            clearInputFields()  // Optionally clear the input fields after success
                        }
                    } else {
                        // Handle the successful case if the server responds with content (status 200)
                        Log.d("PolicyActivity", "policy updated successfully: ${response.body()}")
                        runOnUiThread {
                            Toast.makeText(this@PolicyActivity, "policy updated successfully!", Toast.LENGTH_SHORT).show()
                            clearInputFields()  // Optionally clear the input fields after success
                        }
                    }
                } else {
                    // Handle error response from the server
                    val errorMessage = response.errorBody()?.string() ?: "No error body"
                    Log.e("PolicyActivity", "Failed to update product: $errorMessage")
                    runOnUiThread {
                        Toast.makeText(this@PolicyActivity, "Failed to update product: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("PolicyActivity", "Error: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(this@PolicyActivity, "Error updating product: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deletePolicy() {
        val policyNo = binding.etPolicyNo.text.toString().trim()

        if (policyNo.isEmpty()) {
            Toast.makeText(this, "Please enter a policy number to delete", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = policyService.deletePolicyDetails(policyNo)

                if (response.isSuccessful) {
                    Log.d("PolicyActivity", "Policy deleted successfully.")
                    runOnUiThread {
                        Toast.makeText(this@PolicyActivity, "Policy deleted successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("PolicyActivity", "Failed to delete policy. HTTP ${response.code()}: $errorBody")

                    runOnUiThread {
                        val userMessage = when (response.code()) {
                            400 -> "Invalid request. Ensure the policy number is correct."
                            404 -> "Policy not found. Verify the policy number."
                            500 -> "Server error occurred. Please try again later."
                            else -> "Unexpected error occurred. HTTP ${response.code()}."
                        }
                        Toast.makeText(this@PolicyActivity, userMessage, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("PolicyActivity", "Error deleting policy: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(this@PolicyActivity, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }






    override fun onBackPressed() {
        super.onBackPressed()
        val backintent= Intent(this,MainActivity::class.java)
        startActivity(backintent)
    }
}


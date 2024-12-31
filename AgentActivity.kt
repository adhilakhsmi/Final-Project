package com.singlepointsol.navigatioindrawerr

import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AgentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agent)

        // Initialize the "Fetch" button (which will also handle save operation)
        val fetchButton: Button = findViewById(R.id.agent_save_button)
        fetchButton.setOnClickListener {
            val agentId = findViewById<AutoCompleteTextView>(R.id.et_agent_Id).text.toString()

            if (agentId.isBlank()) {
                Toast.makeText(this, "Please enter an Agent ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Fetch agent details from API
            fetchAgent(agentId)
        }
    }

    // Fetch agent details from the API
    private fun fetchAgent(agentId: String) {
        val call = AgentRetrofitInstance.agentApiService.getAgent(agentId)
        call.enqueue(object : Callback<Agent> {
            override fun onResponse(call: Call<Agent>, response: Response<Agent>) {
                if (response.isSuccessful) {
                    val agent = response.body()
                    if (agent != null) {
                        // Populate the form fields with the fetched data
                        displayAgentDetails(agent)
                    } else {
                        Toast.makeText(this@AgentActivity, "Agent not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@AgentActivity, "Failed to fetch agent: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Agent>, t: Throwable) {
                Toast.makeText(this@AgentActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Display agent details in the form
    private fun displayAgentDetails(agent: Agent) {
        findViewById<TextInputEditText>(R.id.et_agent_name).setText(agent.agentName)
        findViewById<TextInputEditText>(R.id.et_agent_phone).setText(agent.agentPhone)
        findViewById<TextInputEditText>(R.id.et_Agent_email).setText(agent.agentEmail)
        findViewById<TextInputEditText>(R.id.et_license_code).setText(agent.licenseCode)

        // After displaying, trigger the save function
        saveAgent(agent)
    }

    // Save agent details to the server
    private fun saveAgent(agent: Agent) {
        val call = AgentRetrofitInstance.agentApiService.saveAgent(agent)
        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AgentActivity, "Agent saved successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@AgentActivity, "Failed to save agent: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Toast.makeText(this@AgentActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

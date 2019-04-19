package de.marcelknupfer.studentenausweisguthaben

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;

import kotlinx.android.synthetic.main.activity_licenses.*
import kotlinx.android.synthetic.main.content_licenses.*
import java.lang.Exception

class LicensesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_licenses)
        setSupportActionBar(toolbar)
        setUpGPL3Text(mensaguthaben_license_text)
        setUpGPLText(farebot_license_text)

        mensaguthaben_github_button.setOnClickListener {
            onMensaLinkClick()
        }

        farebot_github_button.setOnClickListener {
            onFarebotLinkClick()
        }
    }

    /**
     * Reads in the GPL3 from a txt file and prints it inside the correct TextView
     */
    private fun setUpGPL3Text(view: TextView) {
        try {
            var myRessources = resources
            var inputStream = myRessources.openRawResource(R.raw.gpl3)
            var b: ByteArray = ByteArray(inputStream.available())
            inputStream.read(b)
            view.text = String(b)


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Reads in the license of GSON from a txt file and prints it inside the correct TextView
     */
    private fun setUpGPLText(view: TextView) {
        try {
            var myRessources = resources
            var inputStream = myRessources.openRawResource(R.raw.gpl)
            var b: ByteArray = ByteArray(inputStream.available())
            inputStream.read(b)
            view.text = String(b)


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Opens the GitHub Link to MensaGuthaben
     */
    private fun onMensaLinkClick() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.mensaguthaben_link)))
        startActivity(intent)
    }

    /**
     * Opens the GitHub Link to Farebot
     */
    private fun onFarebotLinkClick() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.farebot_link)))
        startActivity(intent)
    }

}

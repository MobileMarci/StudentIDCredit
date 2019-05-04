package de.marcelknupfer.studentenausweisguthaben.ui.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity;
import de.marcelknupfer.studentenausweisguthaben.R

import kotlinx.android.synthetic.main.activity_licenses.*
import kotlinx.android.synthetic.main.content_licenses.*
import java.lang.Exception

/**
 * @author © 2019 Marcel Knupfer
 * Displays all third party licenses of software used to build this application
 */
class LicensesActivity : AppCompatActivity() {

    private var darkMode = false
    private lateinit var sharedPres: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPres = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE)
        darkMode = sharedPres.getBoolean(getString(R.string.preference_darkmode_key), false)
        if (darkMode) {
            setTheme(R.style.DarkMode)
        }
        setContentView(R.layout.activity_licenses)
        setSupportActionBar(main_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpGPL3Text(mensaguthaben_license_text)
        setUpGPLText(farebot_license_text)

        mensaguthaben_github_button.setOnClickListener {
            onMensaLinkClick()
        }

        farebot_github_button.setOnClickListener {
            onFarebotLinkClick()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
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

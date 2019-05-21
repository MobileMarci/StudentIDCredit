package de.marcelknupfer.studentenausweisguthaben.ui.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity;

import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.content_settings.*
import android.content.Intent
import android.os.Build
import de.marcelknupfer.studentenausweisguthaben.R

/**
 * @author © 2019 Marcel Knupfer
 * Custom Settings Activity
 */

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPres: SharedPreferences
    private var autostart = false
    private var darkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPres = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE)
        autostart = sharedPres.getBoolean(getString(R.string.preference_autostart_key), false)
        darkMode = sharedPres.getBoolean(getString(R.string.preference_darkmode_key), false)
        if (darkMode) {
            setTheme(R.style.DarkMode)
        }
        setContentView(R.layout.activity_settings)
        setSupportActionBar(settings_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpSettings()
        setupDarkMode()
        autostartSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            autoStartChanged(isChecked)
            with (sharedPres.edit()){
                putBoolean(getString(R.string.preference_autostart_key), isChecked)
                commit()
            }
        }

        darkModeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            with(sharedPres.edit()){
                putBoolean(getString(R.string.preference_darkmode_key), isChecked)
                commit()
            }
            darkModeChanged(isChecked)
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
     * Activates the autostart for recognized Student IDs next to the phone
     */
    private fun autoStartChanged(checked: Boolean){
        AutostartRegister.register(packageManager, checked)
        if(checked) {
            autoStartSettingsText.text = getString(R.string.enabled)
        }else{
            autoStartSettingsText.text = getString(R.string.disabled)
        }
    }

    /**
     * Activates or deactivates the darkmode
     */
    private fun darkModeChanged(enabled:Boolean){
        if(enabled) {
            darkModeSettingsText.text = getString(R.string.enabled)
            restartApp()
        }else{
            darkModeSettingsText.text = getString(R.string.disabled)
            restartApp()
        }
    }

    /**
     * Sets up all the views related to settings
     */
    private fun setUpSettings(){
        autostartSwitch.isChecked = autostart
        darkModeSwitch.isChecked = darkMode

        if(darkMode) {
            darkModeSettingsText.text = getString(R.string.enabled)
        }else{
            darkModeSettingsText.text = getString(R.string.disabled)
        }

        if(autostart) {
            autoStartSettingsText.text = getString(R.string.enabled)
        }else{
            autoStartSettingsText.text = getString(R.string.disabled)
        }
    }

    /**
     * Closes the current activity, clears the activity stack and starts the Main activity
     */
    private fun restartApp(){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun setupDarkMode(){
        if(darkMode){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                autostartSettingsImage.setColorFilter(getColor(R.color.white))
                darkModeSettingsImage.setColorFilter(getColor(R.color.white))
            } else {
                autostartSettingsImage.setColorFilter(resources.getColor(R.color.white))
                darkModeSettingsImage.setColorFilter(resources.getColor(R.color.white))
            }
        }
    }
}

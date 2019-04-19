package de.marcelknupfer.studentenausweisguthaben

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;

import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.content_settings.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPres: SharedPreferences
    private var autostart = false
    private var darkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(settings_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        sharedPres = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE)
        autostart = sharedPres.getBoolean(getString(R.string.preference_autostart_key), false)
        darkMode = sharedPres.getBoolean(getString(R.string.preference_darkmode_key), false)
        setUpSettings()
        autostartSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            autoStartChanged(isChecked)
            with (sharedPres.edit()){
                putBoolean(getString(R.string.preference_autostart_key), isChecked)
                commit()
            }
        }

        darkModeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            darkModeChanged(isChecked)
            with(sharedPres.edit()){
                putBoolean(getString(R.string.preference_darkmode_key), isChecked)
                commit()
            }
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

    private fun autoStartChanged(checked: Boolean){
        AutostartRegister.register(packageManager, checked)
        if(checked) {
            autoStartSettingsText.text = getString(R.string.enabled)
        }else{
            autoStartSettingsText.text = getString(R.string.disabled)
        }
    }

    private fun darkModeChanged(enabled:Boolean){
        if(enabled) {
            darkModeSettingsText.text = getString(R.string.enabled)
        }else{
            darkModeSettingsText.text = getString(R.string.disabled)
        }
    }

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

}

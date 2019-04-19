package de.marcelknupfer.studentenausweisguthaben

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_about.*

/**
 * @author © 2019 Marcel Knupfer
 * Activity that shows information about LazyDo
 */

class AboutActivity : AppCompatActivity() {

    private var darkMode = false
    private lateinit var sharedPres:SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPres = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE)
        darkMode = sharedPres.getBoolean(getString(R.string.preference_darkmode_key), false)
        if (darkMode) {
            setTheme(R.style.AppTheme_dark)
        }
        setContentView(R.layout.activity_about)
        setSupportActionBar(about_toolbar)
        supportActionBar?.title = getString(R.string.title_activity_about)
        versionTextAboutLayout.text = "Version " + BuildConfig.VERSION_NAME

        //Link to my homepage
        web_button.setOnClickListener {
            onWebsiteClick()
        }
        //Link to my Twitter page
        twitter_button.setOnClickListener {
            onTwitterClick()
        }
        // Link to my Google Play Page
        google_play_button.setOnClickListener {
            onDevPageClick()
        }
        // Opens Activity for third party license stuff
        license_button.setOnClickListener {
            onThirdPartyLicencseClick()
        }

        github_button.setOnClickListener {
            onGitHubClick()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.about_menu, menu)
        return true
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
     * Method to open my Twitter page in the browser or the Twitter App
     */
    private fun onTwitterClick() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.twitter_link)))
        startActivity(intent)
    }

    /**
     * Opens the app subpage of my Homepage in a browser
     */
    private fun onWebsiteClick() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.website_link)))
        startActivity(intent)
    }

    /**
     * Opens my Developer Page on Google Play
     */
    private fun onDevPageClick() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.google_play_link)))
        startActivity(intent)
    }

    /**
     * Opens the Activity to show the licenses of the Third Party Software used in this project
     */
    private fun onThirdPartyLicencseClick() {
        val intent = Intent(this, LicensesActivity::class.java)
        startActivity(intent)
    }

    private fun onGitHubClick() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.github_link)))
        startActivity(intent)
    }

}

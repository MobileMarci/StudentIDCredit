package de.marcelknupfer.studentenausweisguthaben.ui.activities

import android.annotation.TargetApi
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle

import android.util.Log
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.codebutler.farebot.card.desfire.DesfireException
import de.marcelknupfer.studentenausweisguthaben.R
import de.marcelknupfer.studentenausweisguthaben.cardreader.Readers
import de.marcelknupfer.studentenausweisguthaben.cardreader.ValueHolder
import de.marcelknupfer.studentenausweisguthaben.ui.fragments.ValueFragment
import java.lang.IllegalStateException

/**
 * @author © 2019 Marcel Knupfer
 * Popup Activity that will open when NFC card is recognized
 */
class PopupActivity: AppCompatActivity() {

    private var darkMode = false
    private var sharedPres: SharedPreferences? = null
    private var valueFragment: ValueFragment? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPres = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE)
        darkMode = sharedPres!!.getBoolean(getString(R.string.preference_darkmode_key), false)
        if (darkMode) {
            setTheme(R.style.DarkMode)
        }
        setContentView(R.layout.popup_main)


        val toolbar = findViewById<View>(R.id.main_toolbar) as Toolbar
        toolbar.setTitle(R.string.credit)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)

        Log.i(TAG, "activity started")


        val fm = supportFragmentManager

        valueFragment = fm.findFragmentByTag(VALUE_TAG) as ValueFragment?
        if (valueFragment == null) {
            valueFragment = ValueFragment.newInstance("PopUp")
            fm.beginTransaction().replace(R.id.main, valueFragment!!, VALUE_TAG).commit()
        }



        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            Log.i(TAG, "Started by tag discovery")
            onNewIntent(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.popup_menu, menu)
        return true
    }

    public override fun onStart() {
        super.onStart()
        if (ValueHolder.getDataValues() != null)
            valueFragment!!.setValueData(ValueHolder.getDataValues() ?: return)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.fullscreen -> {
                val intent = Intent(this@PopupActivity, MainActivity::class.java)
                intent.action = getString(R.string.action_fullscreen_main)
                intent.putExtra(getString(R.string.extra_value_main), valueFragment!!.getValueData())

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    animateActivity21(intent)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    animateActivity16(intent)
                } else {
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }

                return true
            }

            android.R.id.home ->{
                this.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    @TargetApi(21)
    private fun animateActivity21(intent: Intent) {
        val options: ActivityOptions
        //TODO check what happens if last is empty
        if (valueFragment!!.getValueData() != null) {
            options = ActivityOptions.makeSceneTransitionAnimation(
                this@PopupActivity,
                Pair.create(findViewById(R.id.current), "current"),
                Pair.create(findViewById(R.id.last), "last"),
                Pair.create(findViewById(R.id.main_toolbar), "toolbar")
            )
        } else {
            options = ActivityOptions.makeSceneTransitionAnimation(
                this@PopupActivity,
                Pair.create(findViewById(R.id.current), "current"),
                Pair.create(findViewById(R.id.main_toolbar), "toolbar")
            )
        }

        startActivity(intent, options.toBundle())
        //ActivityCompat.startActivity(PopupActivity.this,intent,
        //		ActivityOptionsCompat.makeSceneTransitionAnimation(PopupActivity.this).toBundle());
        //finish();
    }

    @TargetApi(16)
    private fun animateActivity16(intent: Intent) {
        val root = findViewById<View>(R.id.popupRoot)

        val options = ActivityOptions.makeScaleUpAnimation(root, root.left, root.top, root.width, root.height)
        startActivity(intent, options.toBundle())
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            Log.i(TAG, "Discovered tag with intent: $intent")
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            try {
                val `val` = Readers.getInstance().readTag(tag)
                valueFragment!!.setValueData(`val`)
            } catch (e: DesfireException) {
                Toast.makeText(this, R.string.communication_fail, Toast.LENGTH_SHORT).show()
            }catch (ist: IllegalStateException){
                Toast.makeText(this, R.string.communication_fail, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        valueFragment = null
    }

    companion object {

        private val TAG = PopupActivity::class.java.simpleName

        private val VALUE_TAG = "value"
    }
}

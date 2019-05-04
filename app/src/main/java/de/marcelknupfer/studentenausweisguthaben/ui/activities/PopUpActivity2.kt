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
import androidx.fragment.app.FragmentManager
import com.codebutler.farebot.card.desfire.DesfireException
import de.marcelknupfer.studentenausweisguthaben.R
import de.marcelknupfer.studentenausweisguthaben.cardreader.Readers
import de.marcelknupfer.studentenausweisguthaben.cardreader.ValueData
import de.marcelknupfer.studentenausweisguthaben.cardreader.ValueHolder
import de.marcelknupfer.studentenausweisguthaben.ui.fragments.ValueFragment

/**
 * Created by wenzel on 28.11.14.
 */
class PopUpActivity2: AppCompatActivity() {

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
        toolbar.setTitle(R.string.app_name)
        setSupportActionBar(toolbar)

        Log.i(TAG, "activity started")


        val fm = supportFragmentManager

        valueFragment = fm.findFragmentByTag(VALUE_TAG) as ValueFragment?
        if (valueFragment == null) {
            valueFragment = ValueFragment()
            fm.beginTransaction().replace(R.id.main, valueFragment!!, VALUE_TAG).commit()
        }



        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            Log.i(TAG, "Started by tag discovery")
            onNewIntent(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(Menu.NONE, R.id.fullscreen, Menu.NONE, R.string.fullscreen).setIcon(R.drawable.ic_action_full_screen)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    public override fun onStart() {
        super.onStart()
        if (ValueHolder.getDataValues() != null)
            valueFragment!!.valueData = ValueHolder.getDataValues()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.fullscreen -> {
                val intent = Intent(this@PopUpActivity2, MainActivity::class.java)
                intent.action = getString(R.string.action_fullscreen_main)
                intent.putExtra(getString(R.string.extra_value_main), valueFragment!!.valueData)

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
            else -> return super.onOptionsItemSelected(item)
        }
    }


    @TargetApi(21)
    private fun animateActivity21(intent: Intent) {
        val options: ActivityOptions
        //TODO check what happens if last is empty
        if (valueFragment!!.valueData != null) {
            options = ActivityOptions.makeSceneTransitionAnimation(
                this@PopUpActivity2,
                Pair.create(findViewById(R.id.current), "current"),
                Pair.create(findViewById(R.id.last), "last"),
                Pair.create(findViewById(R.id.main_toolbar), "toolbar")
            )
        } else {
            options = ActivityOptions.makeSceneTransitionAnimation(
                this@PopUpActivity2,
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

                valueFragment!!.valueData = `val`


            } catch (e: DesfireException) {
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

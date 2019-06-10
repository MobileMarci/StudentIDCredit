package de.marcelknupfer.studentenausweisguthaben.ui.activities

import android.app.PendingIntent
import android.content.*
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.NfcA
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat
import com.codebutler.farebot.NfcOffFragment
import com.codebutler.farebot.card.desfire.DesfireException
import de.marcelknupfer.studentenausweisguthaben.R
import de.marcelknupfer.studentenausweisguthaben.cardreader.Readers
import de.marcelknupfer.studentenausweisguthaben.cardreader.ValueData
import de.marcelknupfer.studentenausweisguthaben.ui.fragments.ValueFragment

import kotlinx.android.synthetic.main.activity_main2.*
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

/**
 * @author © 2019 Marcel Knupfer
 * MainActivity and entry point of StudRead
 */
class MainActivity : AppCompatActivity() {

    private val VALUE_TAG = "Value Fragment"

    private var mAdapter: NfcAdapter? = null
    private var mPendingIntent: PendingIntent? = null
    private var mFilters: Array<IntentFilter>? = null
    private var mTechLists: Array<Array<String>>? = null
    private var mIntentFilter: IntentFilter? = null
    private var sharedPrefs: SharedPreferences? = null
    private var darkMode = false
    internal var mResumed = false
    private val TAG = MainActivity::class.java.name
    private var valueFragment: ValueFragment? = null
    private var hasNewData = false

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if (NfcAdapter.ACTION_ADAPTER_STATE_CHANGED == action) {
                updateNfcState()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPrefs = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE)
        darkMode = sharedPrefs?.getBoolean(getString(R.string.preference_darkmode_key), false) ?:false
        if (darkMode) {
            setTheme(R.style.DarkMode)
        }

        setContentView(R.layout.activity_main2)
        setSupportActionBar(main_toolbar)

        var fm = supportFragmentManager
        valueFragment = fm.findFragmentByTag(VALUE_TAG) as ValueFragment? ?: ValueFragment()
        if(valueFragment == null){
            Log.d("valueFragment", "null")
        }
        fm.beginTransaction().replace(R.id.main_fragment_container, valueFragment ?: ValueFragment()).commit()

        var action = intent.action ?: "noAction"
        if(action?.equals(getString(R.string.action_fullscreen_main))){
            var valueData = intent.getSerializableExtra(getString(R.string.extra_value_main)) as ValueData
            valueFragment?.setValueData(valueData)
            setResult(0)
        }

        AutostartRegister.register(packageManager,
            sharedPrefs?.getBoolean(getString(R.string.preference_autostart_key), false))


        ViewCompat.setTransitionName(main_toolbar, "toolbar")

        mAdapter = NfcAdapter.getDefaultAdapter(this)
        mIntentFilter = IntentFilter("android.nfc.action.ADAPTER_STATE_CHANGED")


        // Create a generic PendingIntent that will be deliver to this activity.
        // The NFC stack
        // will fill in the intent with the details of the discovered tag before
        // delivering to
        // this activity.
        mPendingIntent = PendingIntent.getActivity(
            this, 0, Intent(
                this,
                javaClass
            ).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )


        // Setup an intent filter
        val tech = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        mFilters = arrayOf(tech)
        mTechLists = arrayOf(arrayOf(IsoDep::class.java.name, NfcA::class.java.name))
        if (action == getString(R.string.action_fullscreen_main) && !hasNewData) {
                val valueData = intent.getSerializableExtra(getString(R.string.extra_value_main)) as ValueData
                Log.w(TAG, "restoring data for fullscreen")
                valueFragment?.setValueData(valueData)
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    public override fun onResume() {

        super.onResume()
        mResumed = true
        applicationContext.registerReceiver(mReceiver, mIntentFilter)
        updateNfcState()
        if (mAdapter != null) {
            mAdapter?.enableForegroundDispatch(
                this, mPendingIntent, mFilters,
                mTechLists
            )
        }
    }

    override fun onPause() {
        super.onPause()
        mResumed = false
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_about) {
            val myIntent = Intent(this, AboutActivity::class.java)
            startActivity(myIntent)
            return true
        }

        if (item.itemId == R.id.action_settings) {
            val myIntent = Intent(this, SettingsActivity::class.java)
            startActivity(myIntent)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        valueFragment = null
    }

    public override fun onNewIntent(intent: Intent) {
        Log.i(TAG, "Foreground dispatch")
        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            Log.i(TAG, "Discovered tag with intent: $intent")
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            try {
                val cardValues = Readers.getInstance().readTag(tag)
                Log.w(TAG, "Setting read data")
                valueFragment?.setValueData(cardValues)
                hasNewData = true
            } catch (e: DesfireException) {
                Toast.makeText(this, R.string.communication_fail, Toast.LENGTH_SHORT).show()
            }catch(f: IllegalArgumentException){
                Toast.makeText(this, R.string.communication_fail, Toast.LENGTH_SHORT).show()
            }catch(g: IllegalStateException){
                Toast.makeText(this, R.string.communication_fail, Toast.LENGTH_SHORT).show()
            }

        } else if (getIntent().action == getString(R.string.action_fullscreen_main)) {
            val valueData = getIntent().getSerializableExtra(getString(R.string.extra_value_main)) as ValueData
            valueFragment?.setValueData(valueData)

        }
    }

    fun updateNfcState() {
        mAdapter ?: NfcAdapter.getDefaultAdapter(this)
        val nfcEnabled = mAdapter?.isEnabled
        if(nfcEnabled != null){
            if (!nfcEnabled && mResumed) {
                val nfcOffFragment = NfcOffFragment()
                nfcOffFragment.show(supportFragmentManager, NfcOffFragment.TAG)
            }
        }
    }
}

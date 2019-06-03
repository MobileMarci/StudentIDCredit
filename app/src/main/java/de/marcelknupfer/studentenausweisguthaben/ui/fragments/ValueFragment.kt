package de.marcelknupfer.studentenausweisguthaben.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import de.marcelknupfer.studentenausweisguthaben.R
import de.marcelknupfer.studentenausweisguthaben.cardreader.ValueData
import kotlinx.android.synthetic.main.value_card.*
import java.util.*

class ValueFragment: Fragment(){
    val VALUE = "value"
    private var valueData: ValueData? = null
    private var parentTag: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.value_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //ViewCompat.setTransitionName(tvCurrentValue, "current")
        //ViewCompat.setTransitionName(tvLastValue, "last")
        if (savedInstanceState != null) {
            try {
                valueData = savedInstanceState.getSerializable(VALUE) as ValueData
            }catch (e:TypeCastException){
                //TODO catch this error
            }
        }
        if(parentTag.equals("PopUp")){
            infoText.visibility = View.INVISIBLE
        }
        updateView()
    }

    private fun moneyStr(i: Int): String {
        val germany = Locale.GERMANY
        val currencySymbol = Currency.getInstance(germany).symbol

        val amount = i.toFloat() / 1000

        return String.format(germany, "%.2f%s", amount, currencySymbol)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param parent name of the containing Activity
         * @return A new instance of fragment BlankFragment.
         */
        @JvmStatic
        fun newInstance(parent:String) =
            ValueFragment().apply {
                arguments = Bundle().apply {
                    parentTag = parent
                }
            }
    }

    private fun updateView() {
        if (valueData == null) {
            current.visibility = View.INVISIBLE
            last.visibility = View.INVISIBLE
        } else {
            val currentData = moneyStr(valueData?.value ?:return)
            current.visibility =View.VISIBLE
            current.text = currentData
            if (valueData?.lastTransaction != null) {
                val lastData = moneyStr(valueData?.lastTransaction ?:return)
                last.text = lastData
                last.visibility = View.VISIBLE
            } else {
                last.visibility = View.INVISIBLE
            }
        }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        bundle.putSerializable(VALUE, valueData)
    }

    fun getValueData(): ValueData? {
        return valueData
    }

    fun setValueData(valueData: ValueData) {
        this.valueData = valueData

        if (current != null)
            updateView()
    }



}
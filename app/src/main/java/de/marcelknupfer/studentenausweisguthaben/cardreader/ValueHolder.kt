package de.marcelknupfer.studentenausweisguthaben.cardreader

/**
 * @author © 2019 Marcel Knupfer
 * Holds the Data from reader
 */
 internal object ValueHolder{
   private var data: ValueData? = null

     fun setDataValues(dataValues: ValueData){
         data = dataValues
     }

     fun getDataValues():ValueData?{
         return data
     }
}
package de.marcelknupfer.studentenausweisguthaben.cardreader

 internal object ValueHolder{
   private var data: ValueData? = null

     fun setDataValues(dataValues: ValueData){
         data = dataValues
     }

     fun getDataValues():ValueData?{
         return data
     }
}
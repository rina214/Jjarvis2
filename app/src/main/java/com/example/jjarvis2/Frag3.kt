package com.example.jjarvis2

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.common.logger.Log
import com.example.common.logger.LogWrapper
import com.example.common.logger.MessageOnlyLogFilter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataDeleteRequest
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.DataUpdateRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.frag3.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

const val TAG = "JJARVIS"

enum class FitActionRequestCode {
    INSERT_AND_READ_DATA,
    UPDATE_AND_READ_DATA,
    DELETE_DATA
}

class Frag3 : Fragment() {
    private val dateFormat = DateFormat.getDateInstance()
    private var lineChart: LineChart? = null
    val dailystep = mutableListOf<String>()
    val SD = mutableListOf<String>()
    var i = 0
    var thread = Thread()

    private val fitnessOptions: FitnessOptions by lazy {
        FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.frag3,null)
        val currentTime = Calendar.getInstance().getTime();
        val dayFormat = SimpleDateFormat("dd", Locale.getDefault());
        val monthFormat = SimpleDateFormat("MM", Locale.getDefault());

        val month = monthFormat.format(currentTime);
        val day = dayFormat.format(currentTime)
        SD.add("")
        for(tempi in 6 downTo 1){
            SD.add(month + "/" +(Integer.valueOf(day)-tempi).toString())
        }
        SD.add(month + "/" +day)

        dailystep?.clear()
        lineChart = activity!!.findViewById(R.id.view_left_line)
        initializeLogging()
        fitSignIn(FitActionRequestCode.INSERT_AND_READ_DATA)
        view.button123.setOnClickListener{
            fitSignIn(FitActionRequestCode.UPDATE_AND_READ_DATA)
        }
        return view
    }

    private fun setChart(){
        val xAxis = lineChart!!.xAxis
        xAxis.apply{
            position = XAxis.XAxisPosition.BOTTOM
            textSize = 10f
            setDrawGridLines(false)
            granularity = 1f
            axisMinimum = 2f
            isGranularityEnabled = true
            valueFormatter = MonthFormatter(SD)
        }
        lineChart!!.apply {
            axisRight.isEnabled = false
            axisLeft.axisMaximum = 50f
            legend.apply {
                textSize = 15f
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
            }
        }

        val lineData = LineData()
        lineChart!!.data = lineData
        i = 0
        feedMultiple()
    }

    class MonthFormatter(days : MutableList<String>) : ValueFormatter() {
        private val stepdays = days
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return stepdays.getOrNull(value.toInt()) ?: value.toString()
        }
    }

    private fun feedMultiple(){
        if(thread != null){
            thread.interrupt()
        }
        var runnable = Runnable {
            addEntry()
        }
        thread = Thread(Runnable {
            while (i<7){
                activity!!.runOnUiThread(runnable)
                Thread.sleep(100)
            }
        })
        thread!!.start()
    }

    private fun addEntry(){
        val data = lineChart!!.data
        data?.let {
            var set: ILineDataSet? = data.getDataSetByIndex(0)
            if(set == null){
                set = createSet()
                data.addDataSet(set)
                data.addEntry(Entry(set.entryCount.toFloat(),dailystep.get(0).toFloat()),0)
                data.notifyDataChanged()
            }
            data.addEntry(Entry(set.entryCount.toFloat(),dailystep.get(i++).toFloat()),0)
            data.notifyDataChanged()
            lineChart!!.apply {
                notifyDataSetChanged()
                moveViewToX(data.entryCount.toFloat())
                setVisibleXRangeMaximum(7f)
                setPinchZoom(false)
                isDoubleTapToZoomEnabled = false
                description.text = "날짜"
                description.textSize = 15f
                setExtraOffsets(20f,16f,20f,16f)

                setVisibleXRangeMinimum(0f)
                setVisibleXRangeMaximum(6f)
                setVisibleYRange(0f,8000f,YAxis.AxisDependency.LEFT)
                axisLeft.isEnabled = false
            }

        }
    }

    private fun createSet():LineDataSet{
        //val yValue = mutableListOf<Entry>(Entry(0f,0f),Entry(1f,0f),Entry(2f,0f),Entry(3f,0f),Entry(4f,0f),Entry(5f,0f),Entry(6f,0f))
        val set = LineDataSet(null,"걸음수")
        set.apply {
            axisDependency = YAxis.AxisDependency.LEFT
            color = 0xFFB0C4DE.toInt()
            setCircleColor( 0xFFB0C4DE.toInt())
            valueTextSize = 12f
            lineWidth = 2f
            circleRadius = 8f
            fillAlpha = 0
            fillColor =  0xFFB0C4DE.toInt()
            highLightColor = Color.GRAY
            setDrawValues(true)

        }
        return set
    }

    private fun fitSignIn(requestCode: FitActionRequestCode) {
        if (oAuthPermissionsApproved()) {
            performActionForRequestCode(requestCode)
        } else {
            requestCode.let {
                GoogleSignIn.requestPermissions(
                        this,
                        requestCode.ordinal,
                        getGoogleAccount(), fitnessOptions)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            RESULT_OK -> {
                val postSignInAction = FitActionRequestCode.values()[requestCode]
                postSignInAction.let {
                    performActionForRequestCode(postSignInAction)
                }
            }
            else -> oAuthErrorMsg(requestCode, resultCode)
        }
    }


    private fun performActionForRequestCode(requestCode: FitActionRequestCode) = when (requestCode) {
        FitActionRequestCode.INSERT_AND_READ_DATA -> insertAndReadData()
        FitActionRequestCode.UPDATE_AND_READ_DATA -> updateAndReadData()
        FitActionRequestCode.DELETE_DATA -> deleteData()
    }

    private fun oAuthErrorMsg(requestCode: Int, resultCode: Int) {
        val message = """
            There was an error signing into Fit. Check the troubleshooting section of the README
            for potential issues.
            Request code was: $requestCode
            Result code was: $resultCode
        """.trimIndent()
        Log.e(TAG, message)
    }

    private fun oAuthPermissionsApproved() = GoogleSignIn.hasPermissions(getGoogleAccount(), fitnessOptions)

    private fun getGoogleAccount() = GoogleSignIn.getAccountForExtension(activity!!.applicationContext, fitnessOptions)

    private fun insertAndReadData() = insertData().continueWith { readHistoryData() }

    private fun insertData(): Task<Void> {
        // Create a new dataset and insertion request.
        val dataSet = insertFitnessData()

        // Then, invoke the History API to insert the data.
        Log.i(TAG, "Inserting the dataset in the History API.")
        return Fitness.getHistoryClient(activity!!.applicationContext, getGoogleAccount())
                .insertData(dataSet)
                .addOnSuccessListener { Log.i(TAG, "Data insert was successful!") }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "There was a problem inserting the dataset.", exception)
                }
    }

    private fun readHistoryData(): Task<DataReadResponse> {
        // Begin by creating the query.
        val readRequest = queryFitnessData()

        // Invoke the History API to fetch the data with the query
        return Fitness.getHistoryClient(activity!!.applicationContext, getGoogleAccount())
                .readData(readRequest)
                .addOnSuccessListener { dataReadResponse ->
                    printData(dataReadResponse)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "There was a problem reading the data.", e)
                }
    }

    /**
     * Creates and returns a {@link DataSet} of step count data for insertion using the History API.
     */
    private fun insertFitnessData(): DataSet {
        Log.i(TAG, "Creating a new data insert request.")

        // [START build_insert_data_request]
        // Set a start and end time for our data, using a start time of 1 hour before this moment.
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val now = Date()
        calendar.time = now
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_WEEK, -1)
        val startTime = calendar.timeInMillis

        // Create a data source
        val dataSource = DataSource.Builder()
                .setAppPackageName(activity!!.applicationContext)
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setStreamName("$TAG - step count")
                .setType(DataSource.TYPE_RAW)
                .build()

        // Create a data set
        val stepCountDelta = 950
        return DataSet.builder(dataSource)
                .add(DataPoint.builder(dataSource)
                        .setField(Field.FIELD_STEPS, stepCountDelta)
                        .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build()
                ).build()
        // [END build_insert_data_request]
    }

    private fun queryFitnessData(): DataReadRequest {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val now = Date()
        calendar.time = now
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        val startTime = calendar.timeInMillis

//        Log.i(TAG, "Range Start: ${dateFormat.format(startTime)}")
//        Log.i(TAG, "Range End: ${dateFormat.format(endTime)}")

        return DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build()
    }

    private fun printData(dataReadResult: DataReadResponse) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.buckets.isNotEmpty()) {
            Log.i(TAG, "Number of returned buckets of DataSets is: " + dataReadResult.buckets.size)
            for (bucket in dataReadResult.buckets) {
                bucket.dataSets.forEach { dumpDataSet(it) }
            }
        } else if (dataReadResult.dataSets.isNotEmpty()) {
            Log.i(TAG, "Number of returned DataSets is: " + dataReadResult.dataSets.size)
            dataReadResult.dataSets.forEach { dumpDataSet(it) }
        }
        setChart()
        // [END parse_read_data_result]
    }

    // [START parse_dataset]
    private fun dumpDataSet(dataSet: DataSet) {
//        Log.i(TAG, "Data returned for Data type: ${dataSet.dataType.name}")

        for (dp in dataSet.dataPoints) {
//            Log.i(TAG, "Data point:")
//            Log.i(TAG, "\tType: ${dp.dataType.name}")
//            Log.i(TAG, "\tStart: ${dp.getStartTimeString()}")
//            Log.i(TAG, "\tEnd: ${dp.getEndTimeString()}")
            dp.dataType.fields.forEach {
//                Log.i(TAG, "\tField: ${it.name} Value: ${dp.getValue(it)}")
                val s = "${dp.getValue(it)}"
                dailystep!!.add(s)
            }
        }
    }
    // [END parse_dataset]

    private fun deleteData() {
        Log.i(TAG, "Deleting today's step count data.")

        // [START delete_dataset]
        // Set a start and end time for our data, using a start time of 1 day before this moment.
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val now = Date()
        calendar.time = now
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        val startTime = calendar.timeInMillis

        //  Create a delete request object, providing a data type and a time interval
        val request = DataDeleteRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .build()

        // Invoke the History API with the HistoryClient object and delete request, and then
        // specify a callback that will check the result.
        Fitness.getHistoryClient(activity!!.applicationContext, getGoogleAccount())
                .deleteData(request)
                .addOnSuccessListener {
                    Log.i(TAG, "Successfully deleted today's step count data.")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to delete today's step count data.", e)
                }
    }

    private fun updateAndReadData() = updateData().continueWithTask { readHistoryData() }

    private fun updateData(): Task<Void> {
        // Create a new dataset and update request.
        val dataSet = updateFitnessData()
        val startTime = dataSet.dataPoints[0].getStartTime(TimeUnit.MILLISECONDS)
        val endTime = dataSet.dataPoints[0].getEndTime(TimeUnit.MILLISECONDS)
        // [START update_data_request]
        Log.i(TAG, "Updating the dataset in the History API.")

        val request = DataUpdateRequest.Builder()
                .setDataSet(dataSet)
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .build()

        // Invoke the History API to update data.
        return Fitness.getHistoryClient(activity!!.applicationContext, getGoogleAccount())
                .updateData(request)
//                .addOnSuccessListener { Log.i(TAG, "Data update was successful.") }
//                .addOnFailureListener { e ->
//                    Log.e(TAG, "There was a problem updating the dataset.", e)
//                }
    }

    private fun updateFitnessData(): DataSet {
//        Log.i(TAG, "Creating a new data update request.")

        // [START build_update_data_request]
        // Set a start and end time for the data that fits within the time range
        // of the original insertion.
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val now = Date()
        calendar.time = now
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.MINUTE, -50)
        val startTime = calendar.timeInMillis

        // Create a data source
        val dataSource = DataSource.Builder()
                .setAppPackageName(activity!!.applicationContext)
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setStreamName("$TAG - step count")
                .setType(DataSource.TYPE_RAW)
                .build()

        // Create a data set
        val stepCountDelta = 1000
        // For each data point, specify a start time, end time, and the data value -- in this case,
        // the number of new steps.
        return DataSet.builder(dataSource)
                .add(DataPoint.builder(dataSource)
                        .setField(Field.FIELD_STEPS, stepCountDelta)
                        .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build()
                ).build()
        // [END build_update_data_request]
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_data -> {
                fitSignIn(FitActionRequestCode.DELETE_DATA)
                true
            }
            R.id.action_update_data -> {
                fitSignIn(FitActionRequestCode.UPDATE_AND_READ_DATA)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initializeLogging() {
        // Wraps Android's native log framework.
        val logWrapper = LogWrapper()
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper)
        // Filter strips out everything except the message text.
        val msgFilter = MessageOnlyLogFilter()
        logWrapper.next = msgFilter
        // On screen logging via a customized TextView.
    }

}

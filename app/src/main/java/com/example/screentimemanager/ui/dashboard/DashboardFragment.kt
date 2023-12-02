package com.example.screentimemanager.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.screentimemanager.R
import com.example.screentimemanager.databinding.FragmentDashboardBinding
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.AxisValue
import lecho.lib.hellocharts.model.Column
import lecho.lib.hellocharts.model.ColumnChartData
import lecho.lib.hellocharts.model.SubcolumnValue
import lecho.lib.hellocharts.model.Viewport
import lecho.lib.hellocharts.view.ColumnChartView


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var logInBtn: Button
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val columnChartView: ColumnChartView = root.findViewById(R.id.chart)
        val columnChartData = generateColumnData()
        columnChartView.columnChartData = columnChartData

        // Optional: Customize chart properties
        val axisX = Axis().setHasLines(false)
        val axisY = Axis().setHasLines(false)
        val axisValues = mutableListOf<AxisValue>()
        val dayLabels = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        for (i in 0 until 7) {
            axisValues.add(AxisValue(i.toFloat()).setLabel(dayLabels[i]))
        }
        axisX.values = axisValues.toTypedArray().toMutableList()
        columnChartData.axisYLeft = axisY

        columnChartData.axisXBottom = axisX
        columnChartData.axisYLeft = axisY

        // Optional: Set custom Viewport (if needed)
        val viewport = Viewport(columnChartView.maximumViewport)
        viewport.top = 100F  // Adjust as needed
        columnChartView.maximumViewport = viewport
        columnChartView.currentViewport = viewport
        return root
    }

    private fun generateColumnData(): ColumnChartData {
        val numColumns = 7
        val columns = mutableListOf<Column>()

        // Generate dummy data (replace with your actual data)
        for (i in 0 until numColumns) {
            val values = mutableListOf<SubcolumnValue>()
            values.add(SubcolumnValue(25f + i * 10, resources.getColor(R.color.purple_500)))
            val column = Column(values)
            columns.add(column.setHasLabels(true))
        }

        return ColumnChartData(columns)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    data class ColumnValue(val value: Float, val color: Int)

}

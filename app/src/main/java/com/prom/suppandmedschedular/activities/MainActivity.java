package com.prom.suppandmedschedular.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.prom.suppandmedschedular.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;

import com.prom.suppandmedschedular.alarm.Alarm;
import com.prom.suppandmedschedular.db.StaticDatabase;
import com.prom.suppandmedschedular.db.classes.Plan;
import com.prom.suppandmedschedular.db.classes.SubstanceToTake;
import com.prom.suppandmedschedular.dialogs.AddOrEditSubstanceDialog;
import com.prom.suppandmedschedular.dialogs.EditPlanSettingsDialog;
import com.prom.suppandmedschedular.dialogs.EditSubstancesDialog;
import com.prom.suppandmedschedular.dialogs.SelectSubstancesToLinkInGraphDialog;
import com.prom.suppandmedschedular.dialogs.SelectSubstancesToShowInGraphDialog;
import com.prom.suppandmedschedular.helper.Functions;
import com.prom.suppandmedschedular.helper.IconContextMenu;
import com.prom.suppandmedschedular.helper.PublicData;
import com.prom.suppandmedschedular.helper.TableLayoutWithHeader;
import com.prom.suppandmedschedular.helper.classes.Link;

public class MainActivity extends Activity implements OnClickListener, OnItemSelectedListener, IconContextMenu.IconContextMenuOnClickListener
{

    private static int LAST = -2; // -1... nichts tun bzw. ignorieren
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // CallbackHandler + Data
    private final Handler mHandler = new Handler();
    private ProgressDialog progressDialog;
    private List<TableRow> rowsTableGraph;
    private List<TableRow> rowsTable;
    private List<TableRow> rowsTableHeader;
    private List<GraphViewSeries> graphViewSeries;

    // Graphview
    private HorizontalScrollView scrollViewTableGraph;
    private GraphView graphView;
    private TableLayout tableLayoutGraph;

    // Tabellenansicht
    private boolean showTableGraph = true;
    private TableLayoutWithHeader tableWithHeader;
    private ScrollView scrollViewTable;
    private TableLayout table;
    private TableLayout tableHeader;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        PublicData.init(this.getApplicationContext());
        StaticDatabase.init(this);
        StaticDatabase.getDataSource().createInitData();
        setContentView(R.layout.main);

        rowsTableGraph = new ArrayList<TableRow>();
        rowsTable = new ArrayList<TableRow>();
        rowsTableHeader = new ArrayList<TableRow>();

        graphView = new LineGraphView(this, "")
        {
            @Override
            protected String formatLabel(double value, boolean isValueX)
            {
                if (isValueX)
                {
                    return PublicData.decimalFormat0.format(value);
                }
                else
                    return PublicData.decimalFormat2.format(value);
            }
        };
        graphView.setScrollable(true);
        graphView.setScalable(true);
        graphView.setShowLegend(true);
        graphView.setLegendAlign(LegendAlign.BOTTOM);
        graphViewSeries = new ArrayList<GraphViewSeries>();

        scrollViewTableGraph = new HorizontalScrollView(this);
        tableLayoutGraph = new TableLayout(this);
        scrollViewTableGraph.addView(tableLayoutGraph);

        tableHeader = new TableLayout(this);
        tableHeader.setId(R.id.HeaderTable);
        tableHeader.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        scrollViewTable = new ScrollView(this);
        scrollViewTable.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        table = new TableLayout(this);
        table.setId(R.id.BodyTable);
        table.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        scrollViewTable.addView(table);

        tableWithHeader = new TableLayoutWithHeader(this);
        tableWithHeader.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
        tableWithHeader.setOrientation(LinearLayout.VERTICAL);
        tableWithHeader.addView(tableHeader);
        tableWithHeader.addView(scrollViewTable);

        ((LinearLayout) findViewById(R.id.llInScrollView)).addView(tableWithHeader);
        ((LinearLayout) findViewById(R.id.layout)).addView(scrollViewTableGraph);
        ((LinearLayout) findViewById(R.id.layout)).addView(graphView);

        ((Button) findViewById(R.id.btView)).setText(R.string.show_view_graph);
        scrollViewTableGraph.setVisibility(View.GONE);
        graphView.setVisibility(View.GONE);

        updateSpinner(false, getSharedPreferences(PublicData.MY_PREFS, -1).getInt(getString(R.string.SHARED_PREF_LAST_PLAN), -1));

        updatePlanData();

        ((Spinner) findViewById(R.id.spPlan)).setOnItemSelectedListener(this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        SharedPreferences.Editor editor = getSharedPreferences(PublicData.MY_PREFS, -1).edit();
        editor.putInt(getString(R.string.SHARED_PREF_LAST_PLAN), ((Spinner) findViewById(R.id.spPlan)).getSelectedItemPosition());
        editor.commit();
    }

    @SuppressWarnings("unchecked")
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
    {
        if (position >= 0)
        {
            PublicData.selectedPlan = ((ArrayAdapter<Plan>) parent.getAdapter()).getItem(position);
            update(true, true, false, false, -1, false, -1, false);
        }
    }

    public void onNothingSelected(AdapterView<?> arg0)
    {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.settings)
        {
            startActivity(new Intent(getBaseContext(), PreferencesActivity.class));
        }
        else if (item.getItemId() == R.id.edit_substances)
        {
            EditSubstancesDialog dialog = new EditSubstancesDialog(this);
            dialog.show();
        }
        else if (item.getItemId() == R.id.filter)
        {
            new SelectSubstancesToShowInGraphDialog(this).show();
        }
        else if (item.getItemId() == R.id.show_table_graph)
        {
            showTableGraph = !showTableGraph;
            if (showTableGraph && graphView.getVisibility() == View.VISIBLE)
                scrollViewTableGraph.setVisibility(View.VISIBLE);
            else if (!showTableGraph && graphView.getVisibility() == View.VISIBLE)
                scrollViewTableGraph.setVisibility(View.GONE);
        }
        else if (item.getItemId() == R.id.link_data_in_graph)
        {
            new SelectSubstancesToLinkInGraphDialog(this).show();
        }

        return super.onOptionsItemSelected(item);
    }

    // IconContextMenu click
    public void onClick(int menuId, Intent intent)
    {

        if (menuId == R.id.delete_plan)
        {
            if (PublicData.selectedPlan == null)
                PublicData.showToast(this, R.string.error_no_current_plan);
            else
            {
                long id = PublicData.selectedPlan.getID();
                PublicData.selectedPlan.delete();
                PublicData.selectedPlan = null;
                update(true, true, true, false, -1, true, id, false);
            }
        }
        else if (menuId == R.id.edit_plan)
        {
            if (PublicData.selectedPlan == null)
                PublicData.showToast(this, R.string.error_no_current_plan);
            else
                new EditPlanSettingsDialog(this).show();
        }
        else if (menuId == R.id.create_plan)
        {
            Plan newPlan = new Plan(getString(R.string.new_plan_standard_name));
            newPlan.create();
            PublicData.selectedPlan = newPlan;
            PublicData.selectedPlan.clearSteroids();
            PublicData.selectedPlan.update();

            update(true, true, true, false, LAST, false, -1, false);
        }
        else if (menuId == R.id.add_substance)
        {
            if (PublicData.selectedPlan == null)
                PublicData.showToast(this, R.string.error_no_current_plan);
            else if (StaticDatabase.getDataSource().getAllSubstances().length == 0)
                PublicData.showToast(this, R.string.error_no_substances_exist);
            else
                new AddOrEditSubstanceDialog(this, null).show();
        }
        else if (menuId == R.id.delete_all_substances)
        {
            if (PublicData.selectedPlan == null)
                PublicData.showToast(this, R.string.error_no_current_plan);
            else
            {
                for (int i = 0; i < graphViewSeries.size(); i++)
                    graphView.removeSeries(graphViewSeries.get(i));
                PublicData.selectedPlan.clearSteroids();
                PublicData.selectedPlan.update();

                update(true, true, false, false, -1, true, PublicData.selectedPlan.getID(), false);
            }
        }
        else if (menuId == R.id.delete_substance)
        {
            Integer substanceIndex = intent.getIntExtra("substance_index", 0);
            SubstanceToTake stt = PublicData.selectedPlan.removeSubstanceToTake(substanceIndex);
            stt.delete();
            PublicData.selectedPlan.update();
            update(true, false, false, false, -1, true, PublicData.selectedPlan.getID(), false);

            PublicData.showToast(this, getString(R.string.substance) + " " + (substanceIndex + 1) + " (" + stt.getSubstance().getName() + ") wurde gel�scht!");
        }
        else if (menuId == R.id.edit_substance)
        {
            Integer substanceIndex = intent.getIntExtra("substance_index", 0);
            SubstanceToTake stt = PublicData.selectedPlan.getSubstancesToTake().get(substanceIndex);
            if (PublicData.selectedPlan == null)
                PublicData.showToast(this, R.string.error_no_current_plan);
            else
                new AddOrEditSubstanceDialog(this, stt).show();
        }
    }

    public void onClick(View view)
    {
        if (view.getId() == R.id.btPlanMenu)
        {
            PublicData.getIconContextMenuPlan(this).createMenu(getString(R.string.plan)).show();
        }
        else if (view.getId() == R.id.btSteroidMenu)
        {
            PublicData.getIconContextMenuSubstance(this).createMenu(getString(R.string.substance)).show();
        }
        else if (view.getId() == R.id.key_rowheader_substance)
        {
            Intent intent = new Intent();
            Integer substanceIndex = Integer.parseInt(view.getTag().toString());
            intent.putExtra("substance_index", Integer.parseInt(view.getTag().toString()));
            SubstanceToTake stt = PublicData.selectedPlan.getSubstancesToTake().get(substanceIndex);
            PublicData.getIconContextMenuSubstanceHeader(this, intent).createMenu(stt.getSubstance().getName()).show();
        }
        else if (view.getId() == R.id.btView)
        {
            if (((Button) view).getText().toString().equals(getString(R.string.show_view_graph)))
            {
                ((Button) view).setText(R.string.show_view_table);
                ((HorizontalScrollView) findViewById(R.id.containerTable)).setVisibility(View.GONE);
                if (showTableGraph)
                    scrollViewTableGraph.setVisibility(View.VISIBLE);
                else
                    scrollViewTableGraph.setVisibility(View.GONE);
                graphView.setVisibility(View.VISIBLE);
            }
            else if (((Button) view).getText().toString().equals(getString(R.string.show_view_table)))
            {
                ((Button) view).setText(R.string.show_view_graph);
                ((HorizontalScrollView) findViewById(R.id.containerTable)).setVisibility(View.VISIBLE);
                scrollViewTableGraph.setVisibility(View.GONE);
                graphView.setVisibility(View.GONE);
            }
        }
        else if (view.getId() == R.id.key_week)
        {
            int week = (Integer) view.getTag();
            graphView.setViewPort(week * 7, week * 7 + 7);
            graphView.invalidate();
        }
        else if (view.getId() == R.id.key_cell)
        {
            // String x = "Tag " + (Integer.parseInt(view.getTag(R.id.key_day_index).toString()) + 1) +
            // ", Steroid Nr.: " + (Integer.parseInt(view.getTag(R.id.key_substance_index).toString()) + 1);
            // PublicData.showToast(this, x);
        }
    }

    private void updateAlarm(long plan_id)
    {
        // Alarme anpassen
        if (plan_id != -1)
        {
            Alarm.removeAlarm(this, plan_id);
            Plan plan = StaticDatabase.getDataSource().getPlan(plan_id);
            plan.createAlarms(this);
        }
    }

    public void update(final boolean graphAndTable, final boolean currentPlanData, final boolean spinner,
            final boolean currentSpinnerItemOnly, final int selectedIndex, final boolean updateAlarm,
            final long alarm_plan_id, final boolean updateVisibilities)
    {
        if (graphAndTable)
        {
            rowsTableGraph.clear();
            rowsTable.clear();
            rowsTableHeader.clear();
            graphViewSeries.clear();

            if (PublicData.selectedPlan != null && PublicData.selectedPlan.getSubstancesToTake() != null && PublicData.selectedPlan.getSubstancesToTake().size() > 0)
            {
                progressDialog = ProgressDialog.show(this,
                        getString(R.string.working),
                        getString(R.string.data_is_calculated_text));
                Thread t = new Thread()
                {
                    public void run()
                    {
                        updateGraphData();
                        updateTableData();
                        mHandler.post(new Runnable()
                        {
                            public void run()
                            {
                                updateWorkFinished();
                            }
                        });
                    }
                };
                t.start();
            }
            else
                updateWorkFinished();
        }
        if (spinner)
            updateSpinner(currentSpinnerItemOnly, selectedIndex);
        if (currentPlanData)
            updatePlanData();
        if (updateAlarm)
            updateAlarm(alarm_plan_id);
        if (!graphAndTable && updateVisibilities)
            updateGraphVisibleSubstancesOnly();
    }

    private void updateWorkFinished()
    {
        tableHeader.removeAllViews();
        table.removeAllViews();
        tableLayoutGraph.removeAllViews();
        try
        {
            while (true)
                graphView.removeSeries(0);
        }
        catch (IndexOutOfBoundsException e)
        {
        }

        if (PublicData.selectedPlan == null)
        {
            if (progressDialog != null)
                progressDialog.dismiss();
            return;
        }

        // Daten neu hinzufügen (außer graph view, das macht die updateGraphVisibleSubstancesOnly funktion
        for (int i = 0; i < rowsTable.size(); i++)
            table.addView(rowsTable.get(i));
        for (int i = 0; i < rowsTableGraph.size(); i++)
            tableLayoutGraph.addView(rowsTableGraph.get(i));
        for (int i = 0; i < rowsTableHeader.size(); i++)
            tableHeader.addView(rowsTableHeader.get(i));

        updateGraphVisibleSubstancesOnly();

        table.invalidate();
        tableLayoutGraph.invalidate();
        tableHeader.invalidate();

        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @SuppressWarnings("unchecked")
    private void updateSpinner(boolean reloadSelectedOnly, int selectedIndex)
    {
        Spinner sp = ((Spinner) findViewById(R.id.spPlan));
        if (reloadSelectedOnly && sp.getAdapter() != null && sp.getAdapter().getCount() == 0)
            return;
        if (reloadSelectedOnly)
        {
            ((Plan) sp.getAdapter().getItem(((Spinner) findViewById(R.id.spPlan)).getSelectedItemPosition())).reload(true);
            ((ArrayAdapter<Plan>) sp.getAdapter()).notifyDataSetChanged();
        }
        else
        {
            sp.setAdapter(new ArrayAdapter<Plan>(this, android.R.layout.simple_spinner_item, StaticDatabase.getDataSource().getAllPlans()));
            ((ArrayAdapter<Plan>) sp.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            if (selectedIndex >= 0 && selectedIndex < sp.getAdapter().getCount())
                sp.setSelection(selectedIndex);
            else if (selectedIndex == LAST && sp.getAdapter().getCount() > 0 && sp.getAdapter().getCount() > 1)
                sp.setSelection(sp.getAdapter().getCount() - 1);
        }
    }

    private void updatePlanData()
    {
        String date = "";
        int planCount = 0;
        String status = "";
        if (((Spinner) findViewById(R.id.spPlan)).getAdapter() != null)
            planCount = ((Spinner) findViewById(R.id.spPlan)).getAdapter().getCount();
        String planInfo = getResources().getQuantityString(R.plurals.plan_count_info_string, planCount, new Object[] {
                planCount
        });
        if (PublicData.selectedPlan != null && PublicData.selectedPlan.getDate() != null)
            date = dateFormat.format(PublicData.selectedPlan.getDate());
        if (PublicData.selectedPlan != null)
            status = PublicData.selectedPlan.getStatus(this);
        if (date.equals(""))
            findViewById(R.id.llTopInfoLeft).setVisibility(View.INVISIBLE);
        else
            findViewById(R.id.llTopInfoLeft).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tvStartDate)).setText(date);
        ((TextView) findViewById(R.id.tvPlanCountInfo)).setText(planInfo);
        ((TextView) findViewById(R.id.tvStatus)).setText(status);
    }

    private void updateGraphVisibleSubstancesOnly()
    {
        if (PublicData.selectedPlan == null)
            return;
        try
        {
            while (true)
                graphView.removeSeries(0);
        }
        catch (IndexOutOfBoundsException e)
        {
        }

        List<Link> links = PublicData.selectedPlan.getLinksForGraph(true);
        for (int i = 0; i < links.size(); i++)
            if (PublicData.selectedPlan.getSubstancesToTake().get(links.get(i).getParent()).getShowInGraph())
                graphView.addSeries(graphViewSeries.get(i));

        graphView.setViewPort(0, PublicData.selectedPlan.getMaxWeekNumbers() * 7 + PublicData.bufferWeeks * 7 - 1);
        graphView.invalidate();
    }

    private void updateGraphData()
    {
        if (PublicData.selectedPlan == null)
            return;

        TableRow.LayoutParams llpSpan7 = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        llpSpan7.span = 7;

        TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.header_row, null);
        TableRow rowDummy = (TableRow) LayoutInflater.from(this).inflate(R.layout.header_row, null);
        TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.header_text, null);
        tv.setText(getString(R.string.substance));
        row.addView(tv);
        tv = (TextView) LayoutInflater.from(this).inflate(R.layout.header_text, null);
        rowDummy.addView(tv);

        // Titelzeile erstellen (Woche)
        for (int i = 0; i < PublicData.selectedPlan.getMaxWeekNumbers() + PublicData.bufferWeeks; i++)
        {
            tv = (TextView) LayoutInflater.from(this).inflate(R.layout.header_text, null);
            tv.setText(getString(R.string.week) + " " + (i + 1));
            tv.setId(R.id.key_week);
            tv.setTag(i);
            tv.setClickable(true);
            tv.setOnClickListener(this);
            rowDummy.addView(tv, llpSpan7);
        }

        // Titelzeile erstellen (Tag)
        for (int i = 0; i < PublicData.selectedPlan.getMaxWeekNumbers() + PublicData.bufferWeeks; i++)
        {
            for (int j = 1; j <= 7; j++)
            {
                tv = (TextView) LayoutInflater.from(this).inflate(R.layout.header_text, null);
                tv.setText("Tag " + j);
                row.addView(tv);
            }
        }
        rowsTableGraph.add(rowDummy);
        rowsTableGraph.add(row);

        // Daten hinzuf�gen
        List<Link> indizesAndChildIndizes = PublicData.selectedPlan.getLinksForGraph(true);
        for (int i = 0; i < indizesAndChildIndizes.size(); i++)
        {
            // TODO
            // data von allen children zusammenrechnen
            // namen zusammenfügen
            SubstanceToTake substanceToTake = PublicData.selectedPlan.getSubstancesToTake().get(indizesAndChildIndizes.get(i).getParent());
            Vector<GraphViewData> data = substanceToTake.calcDataFullWeeks(true);

            int startWeek = substanceToTake.getStartWeek();
            int startDay = substanceToTake.getStartWeekDay();

            String name = substanceToTake.getSubstance().getName();
            List<Integer> indizesChildren = indizesAndChildIndizes.get(i).getChildrenIndizes();
            for (int j = 0; j < indizesChildren.size(); j++)
            {
                SubstanceToTake substanceToTakeChild = PublicData.selectedPlan.getSubstancesToTake().get(indizesChildren.get(j));
                name += ", " + substanceToTakeChild.getSubstance().getName();
                List<GraphViewData> dataChild = substanceToTakeChild.calcDataFullWeeks(true);

                // unterschiedliche Startwoche/Endwoche Starttag/endtag beachten!!!
                // auch danach beachten
                int startWeekChild = substanceToTakeChild.getStartWeek();
                int startDayChild = substanceToTakeChild.getStartWeekDay();

                if (startWeekChild < startWeek)
                {
                    startWeek = startWeekChild;
                    startDay = startDayChild;
                }
                else if (startWeek == startWeekChild && startDayChild < startDay)
                {
                    startDay = startDayChild;
                }

                for (int k = 0; k < dataChild.size(); k++)
                {
                    if (k < data.size())
                        data.setElementAt(new GraphViewData(data.get(k).valueX, data.get(k).valueY + dataChild.get(k).valueY), k);
                    else
                        data.add(dataChild.get(k));
                }
            }

            // �berfl�ssige Eintrage am Anfang wieder l�schen
            for (int j = 0; j < startWeek * 7 + startDay; j++)
                // while (data.size() > 0 && data.get(0).valueY == 0)
                data.remove(0);

            graphViewSeries.add(new GraphViewSeries(name, new GraphViewSeriesStyle(PublicData.getColor(i), 2), (GraphViewData[]) data.toArray(new GraphViewData[data.size()])));

            row = (TableRow) LayoutInflater.from(this).inflate(R.layout.body_row, null);
            tv = (TextView) LayoutInflater.from(this).inflate(R.layout.body_text, null);
            tv.setText(name);
            tv.setTextColor(PublicData.getColor(i));
            row.addView(tv);

            if (data.size() > 0)
            {
                for (int j = 0; j < startWeek * 7 + startDay; j++)
                {
                    tv = (TextView) LayoutInflater.from(this).inflate(R.layout.body_text, null);
                    row.addView(tv);
                }

                for (int j = 0; j < data.size(); j++)
                {
                    tv = (TextView) LayoutInflater.from(this).inflate(R.layout.body_text, null);
                    tv.setText(PublicData.decimalFormat2.format(data.get(j).valueY));
                    row.addView(tv);
                }

                for (int j = 0; j < (PublicData.selectedPlan.getMaxWeekNumbers() + PublicData.bufferWeeks - startWeek) * 7 - data.size() - startDay; j++)
                {
                    tv = (TextView) LayoutInflater.from(this).inflate(R.layout.body_text, null);
                    row.addView(tv);
                }
            }

            rowsTableGraph.add(row);
        }
    }

    private void updateTableData()
    {
        if (PublicData.selectedPlan == null)
            return;

        // HEADER - Titelzeile 1 erstellen (Total)
        // + Dummy-Zeile f�r BODY
        TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.header_row, null);
        TableRow rowDummy = (TableRow) LayoutInflater.from(this).inflate(R.layout.body_row, null);

        TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.header_text, null);
        TextView tvDummy = (TextView) LayoutInflater.from(this).inflate(R.layout.body_text_invisible, null);

        tv.setText(getString(R.string.week));
        tvDummy.setText(getString(R.string.week));
        row.addView(tv);
        rowDummy.addView(tvDummy);
        tv = (TextView) LayoutInflater.from(this).inflate(R.layout.header_text, null);
        tvDummy = (TextView) LayoutInflater.from(this).inflate(R.layout.body_text_invisible, null);
        tv.setText(R.string.day);
        tvDummy.setText(R.string.day);
        row.addView(tv);
        rowDummy.addView(tvDummy);
        for (int i = 0; i < PublicData.selectedPlan.getSubstancesToTake().size(); i++)
        {
            tv = (TextView) LayoutInflater.from(this).inflate(R.layout.header_text, null);
            tvDummy = (TextView) LayoutInflater.from(this).inflate(R.layout.body_text_invisible, null);
            tv.setText(PublicData.selectedPlan.getSubstancesToTake().get(i).getSubstance().getName());
            tv.setTextColor(getResources().getColor(R.color.clickable_text));
            tv.setId(R.id.key_rowheader_substance);
            registerForContextMenu(tv);
            tv.setTag(i);
            tv.setOnClickListener(this);
            tvDummy.setText(PublicData.selectedPlan.getSubstancesToTake().get(i).getSubstance().getName());
            row.addView(tv);
            rowDummy.addView(tvDummy);
        }
        rowsTableHeader.add(row);
        rowsTable.add(rowDummy);

        // HEADER - Titelzeile 2 erstellen (Total)
        // + Dummy-Zeile f�r BODY
        row = (TableRow) LayoutInflater.from(this).inflate(R.layout.header_row, null);
        rowDummy = (TableRow) LayoutInflater.from(this).inflate(R.layout.body_row, null);

        tv = (TextView) LayoutInflater.from(this).inflate(R.layout.header_text, null);
        tv.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        ((TableRow.LayoutParams) tv.getLayoutParams()).span = 2;
        tvDummy = (TextView) LayoutInflater.from(this).inflate(R.layout.body_text_invisible, null);
        tvDummy.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        ((TableRow.LayoutParams) tvDummy.getLayoutParams()).span = 2;
        tv.setText(getString(R.string.total));
        tvDummy.setText(getString(R.string.total));
        row.addView(tv);
        rowDummy.addView(tvDummy);
        for (int i = 0; i < PublicData.selectedPlan.getSubstancesToTake().size(); i++)
        {
            float[] data = PublicData.selectedPlan.getSubstancesToTake().get(i).getIntakeData();
            float sum = 0;
            for (int j = 0; j < data.length; j++)
                sum += data[j];

            tv = (TextView) LayoutInflater.from(this).inflate(R.layout.header_text, null);
            tvDummy = (TextView) LayoutInflater.from(this).inflate(R.layout.body_text_invisible, null);
            tv.setText(String.valueOf(sum));
            tvDummy.setText(String.valueOf(sum));
            row.addView(tv);
            rowDummy.addView(tvDummy);
        }
        rowsTableHeader.add(row);
        rowsTable.add(rowDummy);

        // HEADER - zweite Dummy Row erstellen
        if (PublicData.selectedPlan.getDate() != null)
        {
            rowDummy = (TableRow) LayoutInflater.from(this).inflate(R.layout.body_row, null);
            tvDummy = (TextView) LayoutInflater.from(this).inflate(R.layout.body_text_invisible, null);
            tvDummy.setText("0000-00-00");
            rowDummy.addView(tvDummy);
            rowsTableHeader.add(rowDummy);
        }

        // Dummyzeile f�r HEADER erstellen
        rowDummy = (TableRow) LayoutInflater.from(this).inflate(R.layout.header_row, null);
        tvDummy = (TextView) LayoutInflater.from(this).inflate(R.layout.body_text_invisible, null);
        tvDummy.setText(getString(R.string.week) + " 000");
        rowDummy.addView(tvDummy);

        tvDummy = (TextView) LayoutInflater.from(this).inflate(R.layout.body_text_invisible, null);
        tvDummy.setText("000");
        rowDummy.addView(tvDummy);

        for (int i = 0; i < PublicData.selectedPlan.getSubstancesToTake().size(); i++)
        {
            tvDummy = (TextView) LayoutInflater.from(this).inflate(R.layout.body_text_invisible, null);
            tvDummy.setText("00000.0");
            rowDummy.addView(tvDummy);
        }
        rowsTableHeader.add(rowDummy);

        // Dummyzeile f�r BODY erstellen
        rowDummy = (TableRow) LayoutInflater.from(this).inflate(R.layout.body_row, null);
        tvDummy = (TextView) LayoutInflater.from(this).inflate(R.layout.header_text_invisible, null);
        tvDummy.setText(getString(R.string.week) + " 000");
        rowDummy.addView(tvDummy);

        tvDummy = (TextView) LayoutInflater.from(this).inflate(R.layout.header_text_invisible, null);
        tvDummy.setText("000");
        rowDummy.addView(tvDummy);

        for (int i = 0; i < PublicData.selectedPlan.getSubstancesToTake().size(); i++)
        {
            tvDummy = (TextView) LayoutInflater.from(this).inflate(R.layout.header_text_invisible, null);
            tvDummy.setText("00000.0");
            rowDummy.addView(tvDummy);
        }
        rowsTable.add(rowDummy);

        // BODY - alle Zeilen erstellen + Spalte Woche, Datum, Tag setzen
        Calendar cal = Calendar.getInstance();
        Date today = new Date();
        int daysToToday = -1;
        if (PublicData.selectedPlan.getDate() != null)
        {
            cal.setTime(PublicData.selectedPlan.getDate());
            daysToToday = Functions.getDaysBetweenDates(PublicData.selectedPlan.getDate(), today);
        }
        for (int i = 0; i < PublicData.selectedPlan.getMaxWeekNumbers() * 7; i++)
        {
            row = (TableRow) LayoutInflater.from(this).inflate(R.layout.body_row, null);

            if (i % 7 == 0)
            {
                tv = (TextView) LayoutInflater.from(this).inflate(R.layout.body_text, null);
                tv.setText(getString(R.string.week) + " " + (i / 7 + 1));
                row.addView(tv);
            }
            else if (i % 7 == 1)
            {
                tv = (TextView) LayoutInflater.from(this).inflate(R.layout.body_text, null);
                if (PublicData.selectedPlan.getDate() != null)
                    tv.setText(PublicData.dateShortFormat.format(cal.getTime()));
                cal.add(Calendar.DATE, 7);
                row.addView(tv);
            }
            else
            {
                tv = (TextView) LayoutInflater.from(this).inflate(R.layout.body_text, null);
                row.addView(tv);
            }

            tv = (TextView) LayoutInflater.from(this).inflate(R.layout.body_text, null);
            tv.setText(String.valueOf(i % 7 + 1));
            row.addView(tv);

            if (i == daysToToday)
                row.setBackgroundColor(Color.LTGRAY);

            rowsTable.add(row);
        }

        // Daten hinzuf�gen
        for (int i = 0; i < PublicData.selectedPlan.getSubstancesToTake().size(); i++)
        {
            SubstanceToTake substanceToTake = PublicData.selectedPlan.getSubstancesToTake().get(i);
            int startWeek = substanceToTake.getStartWeek();
            int startDay = substanceToTake.getStartWeekDay();
            float[] data = substanceToTake.getIntakeData();
            if (data.length > 0)
            {
                for (int j = 0; j < startWeek * 7 + startDay; j++)
                {
                    tv = (TextView) LayoutInflater.from(this).inflate(R.layout.body_text, null);
                    rowsTable.get(3 + j).addView(tv);
                }

                for (int j = 0; j < data.length; j++)
                {
                    tv = (TextView) LayoutInflater.from(this).inflate(R.layout.body_text, null);
                    if (data[j] != 0)
                        tv.setText(String.valueOf(data[j]));
                    tv.setId(R.id.key_cell);
                    tv.setTag(R.id.key_substance_index, i);
                    tv.setTag(R.id.key_day_index, j);
                    tv.setClickable(true);
                    tv.setOnClickListener(this);
                    rowsTable.get(3 + startWeek * 7 + startDay + j).addView(tv);
                }

                for (int j = 0; j < (PublicData.selectedPlan.getMaxWeekNumbers() - startWeek) * 7 - data.length - startDay; j++)
                {
                    tv = (TextView) LayoutInflater.from(this).inflate(R.layout.body_text, null);
                    rowsTable.get(3 + startWeek * 7 + startDay + data.length + j).addView(tv);
                }
            }
        }
    }
}

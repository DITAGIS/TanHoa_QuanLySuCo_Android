package qlsctanhoa.hcm.ditagis.com.qlsc;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import qlsctanhoa.hcm.ditagis.com.qlsc.adapter.CustomAdapter;
import qlsctanhoa.hcm.ditagis.com.qlsc.adapter.ThongKeAdapter;
import qlsctanhoa.hcm.ditagis.com.qlsc.utities.Constant;
import qlsctanhoa.hcm.ditagis.com.qlsc.utities.TimePeriodReport;

public class ThongKeActivity extends AppCompatActivity {
    private TextView txtTongSuCo, txtChuaSua, txtDangSua, txtDaSua;
    private TextView txtPhanTramChuaSua, txtPhanTramDangSua, txtPhanTramDaSua;
    private QuanLySuCo mQuanLySuCo;
    private ServiceFeatureTable mServiceFeatureTable;
    private PieChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke);
        mServiceFeatureTable = new ServiceFeatureTable(getResources().getString(R.string.service_feature_table));


        this.txtTongSuCo = this.findViewById(R.id.txtTongSuCo);
        this.txtChuaSua = this.findViewById(R.id.txtChuaSua);
        this.txtDangSua = this.findViewById(R.id.txtDangSua);
        this.txtDaSua = this.findViewById(R.id.txtDaSua);
        this.txtPhanTramChuaSua = this.findViewById(R.id.txtPhanTramChuaSua);
        this.txtPhanTramDangSua = this.findViewById(R.id.txtPhanTramDangSua);
        this.txtPhanTramDaSua = this.findViewById(R.id.txtPhanTramDaSua);
        thongKe();

        ((LinearLayout) ThongKeActivity.this.findViewById(R.id.layout_thongke_thoigian)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSelectTime();
            }
        });
    }

    private void showDialogSelectTime() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
        View layout = getLayoutInflater().inflate(R.layout.layout_listview_thongketheothoigian, null);
        ListView listView = (ListView) layout.findViewById(R.id.lstView_thongketheothoigian);
        TimePeriodReport timePeriodReport = new TimePeriodReport();
        List<ThongKeAdapter.Item> items = new ArrayList<>();
        items = timePeriodReport.getItems();
        ThongKeAdapter thongKeAdapter = new ThongKeAdapter(this, items);
        listView.setAdapter(thongKeAdapter);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        final List<ThongKeAdapter.Item> finalItems = items;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ThongKeAdapter.Item itemAtPosition = (ThongKeAdapter.Item) parent.getItemAtPosition(position);

                dialog.dismiss();
                if (itemAtPosition.getId() == finalItems.size()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ThongKeActivity.this, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
                    View layout = getLayoutInflater().inflate(R.layout.layout_thongke_thoigiantuychinh, null);
                    builder.setView(layout);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    final AlertDialog dialog = builder.create();
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.show();
                } else {
                    query(itemAtPosition);
                }
                ((TextView) ThongKeActivity.this.findViewById(R.id.txt_thongke_mota)).setText(itemAtPosition.getMota());
                TextView txtThoiGian = ThongKeActivity.this.findViewById(R.id.txt_thongke_thoigian);
                if (itemAtPosition.getThoigianhienthi() == null)
                    txtThoiGian.setVisibility(View.GONE);
                else {
                    txtThoiGian.setText(itemAtPosition.getThoigianhienthi());
                    txtThoiGian.setVisibility(View.VISIBLE);
                }


            }
        });
    }

    private void query(ThongKeAdapter.Item item) {
        final int[] tongloaitrangthai = {0, 0, 0, 0};// tong, chuasua, dangsua, dasua
        String whereClause = "1 = 1";
        if (item.getThoigianbatdau() == null || item.getThoigianketthuc() == null) {
            whereClause = "1 = 1";
        } else
            whereClause = "NgayCapNhat" + " >= date '" + item.getThoigianbatdau() + "' and " + "NgayCapNhat" + " <= date '" + item.getThoigianketthuc() + "'";
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setWhereClause(whereClause);
        final ListenableFuture<FeatureQueryResult> feature = mServiceFeatureTable.queryFeaturesAsync(queryParameters);
        feature.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FeatureQueryResult result = feature.get();
                    Iterator iterator = result.iterator();
                    while (iterator.hasNext()) {
                        Feature item = (Feature) iterator.next();
                        tongloaitrangthai[0] += 1;
                        int trangthai = Integer.parseInt(item.getAttributes().get(Constant.TRANG_THAI).toString());
                        if (trangthai == 0) tongloaitrangthai[1] += 1;
                        else if (trangthai == 2) tongloaitrangthai[2] += 1;
                        else if (trangthai == 1) tongloaitrangthai[3] += 1;
                    }
                    displayReport(tongloaitrangthai);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void displayReport(int[] tongloaitrangthai) {
        txtTongSuCo.setText(getString(R.string.nav_thong_ke_tong_su_co) + tongloaitrangthai[0]);
        txtChuaSua.setText(tongloaitrangthai[1] + "");
        txtDangSua.setText(tongloaitrangthai[2] + "");
        txtDaSua.setText(tongloaitrangthai[3] + "");

        double percentChuaSua, percentDangSua, percentDaSua;
        if (tongloaitrangthai[0] > 0) {
            percentChuaSua = tongloaitrangthai[1] * 100 / tongloaitrangthai[0];
            percentDangSua = tongloaitrangthai[2] * 100 / tongloaitrangthai[0];
            percentDaSua = tongloaitrangthai[3] * 100 / tongloaitrangthai[0];
        } else {
            percentChuaSua = percentDangSua = percentDaSua = 0.00;
        }
        txtPhanTramChuaSua.setText(percentChuaSua + "%");
        txtPhanTramDangSua.setText(percentDangSua + "%");
        txtPhanTramDaSua.setText(percentDaSua + "%");
        mChart = (PieChart) findViewById(R.id.piechart);
        mChart = configureChart(mChart);
        mChart = setData(mChart, tongloaitrangthai);
        mChart.animateXY(1500, 1500);
    }

    public void thongKe() {
        final int[] tongloaitrangthai = getIntent().getIntArrayExtra(this.getString(R.string.tongloaitrangthai));
        txtTongSuCo.setText(getString(R.string.nav_thong_ke_tong_su_co) + " " + tongloaitrangthai[0]);
        txtChuaSua.setText(tongloaitrangthai[1] + "");
        txtDangSua.setText(tongloaitrangthai[2] + "");
        txtDaSua.setText(tongloaitrangthai[3] + "");
        txtPhanTramChuaSua.setText((tongloaitrangthai[1] * 100) / tongloaitrangthai[0] + "%");
        txtPhanTramDangSua.setText((tongloaitrangthai[2] * 100) / tongloaitrangthai[0] + "%");
        txtPhanTramDaSua.setText((tongloaitrangthai[3] * 100) / tongloaitrangthai[0] + "%");
        mChart = (PieChart) findViewById(R.id.piechart);
        mChart = configureChart(mChart);
        mChart = setData(mChart, tongloaitrangthai);
        mChart.animateXY(1500, 1500);
    }

    public PieChart configureChart(PieChart chart) {
        chart.setHoleColor(getResources().getColor(android.R.color.background_dark));
        chart.setHoleRadius(60f);
        chart.setDescription("");
        chart.setTransparentCircleRadius(5f);
        chart.setDrawCenterText(true);
        chart.setDrawHoleEnabled(false);
        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);

        chart.setUsePercentValues(false);

        Legend legend = chart.getLegend();
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        return chart;
    }

    private PieChart setData(PieChart chart, int[] tongloaitrangthai) {
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        yVals1.add(new Entry(tongloaitrangthai[1], 0));
        yVals1.add(new Entry(tongloaitrangthai[2], 1));
        yVals1.add(new Entry(tongloaitrangthai[3], 2));
        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add(getString(R.string.nav_thong_ke_chua_sua_chua));
        xVals.add(getString(R.string.nav_thong_ke_dang_sua_chua));
        xVals.add(getString(R.string.nav_thong_ke_da_sua_chua));
        PieDataSet set1 = new PieDataSet(yVals1, "");
        set1.setSliceSpace(0f);
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(getResources().getColor(android.R.color.holo_red_light));
        colors.add(getResources().getColor(android.R.color.holo_orange_light));
        colors.add(getResources().getColor(android.R.color.holo_green_light));
        set1.setColors(colors);
        PieData data = new PieData(xVals, set1);
        data.setValueTextSize(15);
        set1.setValueTextSize(0);
        chart.setData(data);
        chart.highlightValues(null);
//        chart.invalidate();
        return chart;
    }
}

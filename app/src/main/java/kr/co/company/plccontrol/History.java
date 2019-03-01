package kr.co.company.plccontrol;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class History extends AppCompatActivity {
    private LineChart lineChart;
    dbHelper helper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        helper = new dbHelper(this);
        db = helper.getWritableDatabase();

        Background bg = new Background();
        bg.drawGraph1();
        bg.drawGraph2();
        bg.drawGraph3();

    }

    class Background {
        /*THI*/
        public void drawGraph1() {
            lineChart = (LineChart) findViewById(R.id.chart1);

            Cursor cursor = db.rawQuery("SELECT * FROM value", null);

            ArrayList<String> xAXES = new ArrayList<>();
            ArrayList<Entry> yAXESvalue = new ArrayList<>();

            double x = 0;


            float THIvalArr[] = new float[60];     //60개의 데이터를 저장할 배열
            int THIcount = 0;
            while(cursor.moveToNext()){
                THIvalArr[THIcount] = cursor.getFloat(cursor.getColumnIndex("THI"));
                THIcount++;
            }

            for (int i = 0; i < THIcount; i++) {
                x = x + 1;
                yAXESvalue.add(new Entry(THIvalArr[i], i));     //i에 해당하는 value값을 add(짝지어서)
                xAXES.add(i, String.valueOf(x));   //x축의 값을 저장합니다.
            }

            String[] xaxes = new String[xAXES.size()];
            for (int i = 0; i < xAXES.size(); i++) {
                xaxes[i] = xAXES.get(i).toString();   // 아래그림의 동그란 부분에 표시되는 x축 값.
            }

            ArrayList<LineDataSet> lineDataSets = new ArrayList<>();
            //ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

            //그래프 그리기
            LineDataSet lineDataSet = new LineDataSet(yAXESvalue, "THI");
            lineDataSet.setDrawCircles(false);
            lineDataSet.setColor(Color.BLUE);
            lineDataSet.setLineWidth(2);
            lineDataSet.setValueTextSize(0.0f);
            lineDataSets.add(lineDataSet);

            lineChart.setData(new LineData(xaxes, lineDataSets));
            lineChart.setVisibleXRangeMaximum(60f);
        }


        /*건,습구 온도*/
        public void drawGraph2() {
            lineChart = (LineChart) findViewById(R.id.chart2);

            ArrayList<String> xAXES = new ArrayList<>();
            ArrayList<Entry> yAXESsin = new ArrayList<>();
            ArrayList<Entry> yAXEScos = new ArrayList<>();

            double x = 0;

            int DryCount = 0, WetCount = 0;
            float DRYvalArr[] = new float[60];
            float WETvalArr[] = new float[60];
            Cursor cursor = db.rawQuery("SELECT * FROM value", null);

            while(cursor.moveToNext()){
                DRYvalArr[DryCount] = cursor.getFloat(cursor.getColumnIndex("Dry"));
                WETvalArr[WetCount] = cursor.getFloat(cursor.getColumnIndex("Wet"));
                DryCount++;
                WetCount++;
            }

            for (int i = 0; i < DryCount; i++) {
                x = x + 1;
                yAXESsin.add(new Entry(DRYvalArr[i], i));
                yAXEScos.add(new Entry(WETvalArr[i], i));
                xAXES.add(i, String.valueOf(x));   //x축의 값을 저장합니다.
            }

            String[] xaxes = new String[xAXES.size()];
            for (int i = 0; i < xAXES.size(); i++) {
                xaxes[i] = xAXES.get(i).toString();   // 아래그림의 동그란 부분에 표시되는 x축 값.
            }

            ArrayList<LineDataSet> lineDataSets = new ArrayList<>();

            //아래 그림의 파란색 그래프
            LineDataSet lineDataSet1 = new LineDataSet(yAXEScos, "건구");
            lineDataSet1.setDrawCircles(false);
            lineDataSet1.setColor(Color.BLUE);
            lineDataSet1.setLineWidth(2);
            lineDataSet1.setValueTextSize(0.0f);

            //아래 그림의 빨간색 그래프
            LineDataSet lineDataSet2 = new LineDataSet(yAXESsin, "습구");
            lineDataSet2.setDrawCircles(false);
            lineDataSet2.setColor(Color.RED);
            lineDataSet2.setValueTextSize(0.0f);
            lineDataSet2.setLineWidth(2);

            lineDataSets.add(lineDataSet1);
            lineDataSets.add(lineDataSet2);

            lineChart.setData(new LineData(xaxes, lineDataSets));
            lineChart.setVisibleXRangeMaximum(60f);
        }

        /*상대*/
        public void drawGraph3() {
            lineChart = (LineChart) findViewById(R.id.chart3);

            ArrayList<String> xAXES = new ArrayList<>();
            ArrayList<Entry> yAXESsin = new ArrayList<>();

            double x = 0;

            int RelCount = 0;
            float RELvalArr[] = new float[60];
            Cursor cursor = db.rawQuery("SELECT * FROM value", null);

            while(cursor.moveToNext()){
                RELvalArr[RelCount] = cursor.getFloat(cursor.getColumnIndex("Dry"));
                RelCount++;
            }

            for (int i = 0; i < RelCount; i++) {
                x = x + 1;
                yAXESsin.add(new Entry(RELvalArr[i], i));
                xAXES.add(i, String.valueOf(x));   //x축의 값을 저장합니다.
            }

            String[] xaxes = new String[xAXES.size()];
            for (int i = 0; i < xAXES.size(); i++) {
                xaxes[i] = xAXES.get(i).toString();   // 아래그림의 동그란 부분에 표시되는 x축 값.
            }

            ArrayList<LineDataSet> lineDataSets = new ArrayList<>();
            //ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

            //그래프 그리기
            LineDataSet lineDataSet = new LineDataSet(yAXESsin, "상대습도");
            lineDataSet.setDrawCircles(false);
            lineDataSet.setColor(Color.BLUE);
            lineDataSet.setLineWidth(2);
            lineDataSet.setValueTextSize(0.0f);
            lineDataSets.add(lineDataSet);

            lineChart.setData(new LineData(xaxes, lineDataSets));
            lineChart.setVisibleXRangeMaximum(60f);
        }
    }
}


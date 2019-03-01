package kr.co.company.plccontrol;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Observation extends AppCompatActivity {
    dbHelper helper;
    SQLiteDatabase db;
    Button btn1, btn2, btn3, btn4, btn5, btn6;
    boolean col_st = false, hum_st = false, blo_st = false, run_st = false, alm_st = false, auto_st = false, fail_st = false;
    private EditText textField;
    private Button button;
    private TextView textView, THItext, DryText,WaterText, RelText,sendText;
    private Socket client;
    private PrintWriter printwriter;
    private BufferedReader bufferedReader;


    private String CHAT_SERVER_IP = "192.168.0.87";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.observation);

        helper = new dbHelper(this);
        db = helper.getWritableDatabase();

        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        btn5 = (Button) findViewById(R.id.btn5);
        btn6 = (Button) findViewById(R.id.btn6);
        btn1.setEnabled(false);
        btn2.setEnabled(false);
        btn3.setEnabled(false);

        sendText = (TextView) findViewById(R.id.sendText);
        THItext = (TextView) findViewById(R.id.THItext);
        DryText = (TextView) findViewById(R.id.DryText);
        WaterText = (TextView) findViewById(R.id.WaterText);
        RelText = (TextView) findViewById(R.id.RelText);

        ChatOperator chatOperator = new ChatOperator();
        chatOperator.execute();
    }

    private class ChatOperator extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                client = new Socket(CHAT_SERVER_IP, 4444); // Creating the server socket.

                if (client != null) {
                    printwriter = new PrintWriter(client.getOutputStream(), true);
                    InputStreamReader inputStreamReader = new InputStreamReader(client.getInputStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                } else {
                    System.out.println("Server has not bean started on port 4444.");
                }
            } catch (UnknownHostException e) {
                System.out.println("Faild to connect server host " + CHAT_SERVER_IP);
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Faild to connect server " + CHAT_SERVER_IP);
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Following method is executed at the end of doInBackground method.
         */
        @Override
        protected void onPostExecute(Void result) {

            Receiver receiver = new Receiver(); // Initialize chat receiver AsyncTask.
            receiver.execute();

        }

    }

    /**
     * This AsyncTask continuously reads the input buffer and show the chat
     * message if a message is availble.
     */
    private class Receiver extends AsyncTask<Void, Void, Void> {

        private String message;
        private char [] sdd = new char[512];
        private float [] Data = new float[5];
        private char[] tc = new char[10];
        private int dstt;


        @Override
        protected Void doInBackground(Void... params) {
            while (true) {
                try {
                    if (bufferedReader.ready()) {
                        Arrays.fill(Data, 0); // Data array initialize
                        Arrays.fill(tc, '\0');
                        message = bufferedReader.readLine(); // String to Char

                        message = message.replace(" ","");
                        System.out.println(message);
                        String hex = "";
                        for(int i = 0; i < message.length(); i+=2) {
                            hex = "" + message.charAt(i) + message.charAt(i+1);
                            int ival = Integer.parseInt(hex, 16);
                            sdd[i/2] = (char)ival;
                        }
                        publishProgress(null);
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                }
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            if(sdd[3] == 'R' && sdd[4] == 'S' && sdd[5] == 'B') {
                for (int i = 0; i < 5; i++) {
                    String temp;
                    tc[0] = sdd[(10 + i * 4)];
                    tc[1] = sdd[(10 + i * 4) + 1];
                    tc[2] = sdd[(10 + i * 4) + 2];
                    tc[3] = sdd[(10 + i * 4) + 3];
                    temp = String.valueOf(tc);
                    temp = temp.replace("\0", "");
                    dstt = Integer.parseInt(temp, 16);

                    if (dstt > 16000) {
                        dstt = dstt - 65536;
                    }
                    Data[i] = (float) dstt;
                }
            }
            float THI_value = (float) ((Data[0]-400.0)/10.0) ; // THI
            float Dry_Temp = (float) ((Data[1]-400.0)/10.0) ; // 건구온도
            float Wet_Temp = (float) ((Data[2]-400.0)/10.0) ; // 습구온도
            float Rel_Hum = (float) ((Data[3])/10.0) ; // 상대습도

            int col_st, hum_st, blo_st, run_st, alm_st, auto_st, fail_st;

            if((((int)Data[4]) & 0x0001) == 0x0001) col_st = 1; else col_st = 0; // 냉방기
            if((((int)Data[4]) & 0x0002) == 0x0002) hum_st = 1; else hum_st = 0; // 가습기
            if((((int)Data[4]) & 0x0004) == 0x0004) blo_st = 1; else blo_st = 0; // 송풍기
            if((((int)Data[4]) & 0x0008) == 0x0008) run_st = 1; else run_st = 0; // 운전, 정지
            if((((int)Data[4]) & 0x0010) == 0x0010) alm_st = 1; else alm_st = 0; // 화재, 정상
            if((((int)Data[4]) & 0x0020) == 0x0020) auto_st = 1; else auto_st = 0; // 자동, 수동
            if((((int)Data[4]) & 0x0040) == 0x0040) fail_st = 1; else fail_st = 0; //정전

            /* 받은 값 출력 */
            THItext.setText(THI_value +"");
            DryText.setText(Dry_Temp +"");
            WaterText.setText(Wet_Temp +"");
            RelText.setText(Rel_Hum +"");

            if(run_st == 0 ){
                btn4.setBackground(getDrawable(R.drawable.clicked_blue));
                btn4.setText("정지");
            }
            else {
                btn4.setBackground(getDrawable(R.drawable.btn_blue));
                btn4.setText("운전");
            }
            if(alm_st == 1 ){
                btn5.setBackground(getDrawable(R.drawable.clicked_orange));
                btn5.setText("화재");
            }
            else {
                btn5.setBackground(getDrawable(R.drawable.btn_orange));
                btn5.setText("정상");
            }
            if(fail_st == 1 ){
                btn4.setBackground(getDrawable(R.drawable.clicked_blue));
                btn4.setText("정전");
            }
            Cursor cursor = db.rawQuery("SELECT * FROM value", null);
            int dbCount = cursor.getCount();
            if( dbCount == 60) {
                db.execSQL("DELETE FROM value WHERE _id = (select min(_id) FROM value)");
                db.execSQL("INSERT INTO value VALUES (null," + THI_value + "," + Dry_Temp + "," + Wet_Temp + "," + Rel_Hum + ")");
            }
            else {
                db.execSQL("INSERT INTO value VALUES (null," + THI_value + "," + Dry_Temp + "," + Wet_Temp + "," + Rel_Hum + ")");
            }

//            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
        }

    }

    /**
     * This AsyncTask sends the chat message through the output stream.
     */
    private class Sender extends AsyncTask<Void, Void, Void> {

        private String message;

        @Override
        protected Void doInBackground(Void... params) {
            message = sendText.getText().toString();
            printwriter.write(message + "\n");
            printwriter.flush();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
//            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//            textView.append("Client: " + message + "\n");
        }
    }


    public void onclick(View view) {
        int Ctrl = 0;
        int auto = 0, cold = 0, dry = 0, wind = 0, on = 0;
        if (btn6.getText().toString().equals("자동")) {
            btn6.setText("수동");
            btn1.setEnabled(true);
            btn2.setEnabled(true);
            btn3.setEnabled(true);
            btn1.setBackground(getDrawable(R.drawable.btn_blue));
            btn2.setBackground(getDrawable(R.drawable.btn_orange));
            btn3.setBackground(getDrawable(R.drawable.btn_green));
            btn4.setBackground(getDrawable(R.drawable.btn_blue));
            btn5.setBackground(getDrawable(R.drawable.btn_orange));
            auto = 1;
        } else if (btn6.getText().toString().equals("수동")) {
            auto = 0;
            switch (view.getId()) {
                case R.id.btn1: // 냉방기
                    if (col_st == false) {
                        btn1.setBackground(getDrawable(R.drawable.clicked_blue));
                        col_st = true;
                        cold = 1;
                    }else{
                        btn1.setBackground(getDrawable(R.drawable.btn_blue));
                        col_st = false;
                        cold = 0;
                    }
                    break;

                case R.id.btn2: // 가습기
                    if(hum_st == false) {
                        btn2.setBackground(getDrawable(R.drawable.clicked_orange));
                        hum_st = true;
                        dry = 1;
                    }else{
                        btn2.setBackground(getDrawable(R.drawable.btn_orange));
                        hum_st = false;
                        dry = 0;
                    }
                    break;

                case R.id.btn3: // 송풍기
                    if(blo_st == false) {
                        btn3.setBackground(getDrawable(R.drawable.clicked_green));
                        blo_st = true;
                        wind = 1;
                    }else{
                        btn3.setBackground(getDrawable(R.drawable.btn_green));
                        blo_st = false;
                        wind = 0;
                    }
                    break;

                case R.id.btn6: // 자동
                    btn6.setText("자동");
                    btn1.setEnabled(false);
                    btn2.setEnabled(false);
                    btn3.setEnabled(false);
                    btn1.setBackground(getDrawable(R.drawable.btn_disable_blue));
                    btn2.setBackground(getDrawable(R.drawable.btn_disable_orange));
                    btn3.setBackground(getDrawable(R.drawable.btn_disable_green));
                    auto = 1;
                    break;
            }
        }
        
        if((auto == 0) && (cold == 1)) Ctrl = Ctrl | 0x0001;
        if((auto == 0) && (dry == 1)) Ctrl = Ctrl | 0x0002;
        if((auto == 0) && (wind == 1)) Ctrl = Ctrl | 0x0004;
        if((on == 1)) Ctrl = Ctrl | 0x0008;
        if((auto == 1)) Ctrl = Ctrl | 0x0010;

        String result = String.format("\5%s%02X%04X\4", "00WSB06%DW100", 1, Ctrl);

        sendText.setText(result);

        final Sender messageSender = new Sender(); // Initialize chat sender AsyncTask.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            messageSender.execute();
        }

    }
}



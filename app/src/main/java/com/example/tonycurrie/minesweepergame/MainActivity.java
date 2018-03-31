package com.example.tonycurrie.minesweepergame;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import java.util.ArrayList;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {


    public GridView gv;
    public String a;
    int bombcount=0;
    public static final int width=9;
    public static final int height=9;
    private String[][] MsGrid=new String[width][height];
    public ArrayList<String> items = new ArrayList<String>(); //holds the grid values
    int size=81,x; // Size of grid
    int bombsPlaced=0; // Initial bomb value
    int f=0,p=0,flag=0,w=0;
    TimerTask mTimerTask;
    Timer t = new Timer();
    Handler handle;
    TextView time;
    int secondsPassed = 0; //Initially timer is set to 0
    MediaPlayer bomb,clickS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       for(int x=0;x<9;x++)
       {
           for(int y=0;y<9;y++)
           {
               MsGrid[x][y]=" ";
           }
       }

        for(int x=0;x<81;x++) { //Initializing with value "O" by default
            items.add("O");
        }

        Button easy = (Button) findViewById(R.id.btnA);
        Button medium = (Button) findViewById(R.id.btnB);
        Button advance = (Button) findViewById(R.id.btnC);
        Button playagain = (Button) findViewById(R.id.playagain);
        gv = (GridView) this.findViewById(R.id.MyGrid);
        //gv.setBackgroundColor(Color.parseColor("#7F7F7F"));
        time=(TextView) findViewById(R.id.Timer);
        time.setText("Timer : 0"); //Initially timer is set to 0
        handle = new Handler();
        bomb = MediaPlayer.create(this,R.raw.bombsound);
        clickS = MediaPlayer.create(this,R.raw.clicksound);

        final CustomGridAdapter gridadapter = new CustomGridAdapter(MainActivity.this, items);

        gv.setEnabled(false); //Disabling the grid because player need to select difficulty level first
        if(gv.isEnabled()==false)
        {
            Toast.makeText(getApplicationContext(), "Please choose difficulty level!!", Toast.LENGTH_LONG).show();
        }

        //Selecting Easy level
        easy.setOnClickListener(new View.OnClickListener() {  //Selecting Easy level
            @Override
            public void onClick(View view) {
                startTimer();
                gv.setEnabled(true);
                f=0;
                bombcount = 8;
            }
        });

        medium.setOnClickListener(new View.OnClickListener() {  //Selecting Medium level
            @Override
            public void onClick(View view) {
                startTimer();
                gv.setEnabled(true);
                f=0;
                bombcount = 24;
            }
        });

        advance.setOnClickListener(new View.OnClickListener() { //Selecting Advance level
            @Override
            public void onClick(View view) {
                startTimer();
                gv.setEnabled(true);
                f=0;
                bombcount = 40;
            }
        });

        playagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //If player wants to try once again
                gv.setEnabled(false);
                if(gv.isEnabled()==false)
                {
                    Toast.makeText(getApplicationContext(), "Please choose difficulty level!!", Toast.LENGTH_LONG).show();
                }

                f = 0;
                bombcount = 0;
                for (int x = 0; x < 9; x++) {
                    for (int y = 0; y < 9; y++) {
                        MsGrid[x][y] = " ";
                    }
                }
                for(int x=0;x<81;x++) {
                    items.set(x,"O");
                }
                gv.setAdapter(gridadapter);
            }
        });


        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() { //Creating and placing mines and neighbours on grid click event
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                clickS.start();
                int x = position/9;
                int y = position%9;

                if(MsGrid[x][y]=="*"){

                    //result.setText("Game Over");
                    bombcount=0;

                    items.set(position, MsGrid[position / 9][position % 9]);
                    for(int k=0;k<9;k++) {
                        for (int l = 0; l < 9; l++) {
                            if(MsGrid[k][l]=="*")
                            {
                                items.set((9*k+l),MsGrid[k][l]);
                            }

                        }
                    }
                    for(int i=0;i<81;i++) {
                        if(items.get(i)=="F" && MsGrid[i/9][i%9]!="*")
                        {
                            items.set(i,"X");
                        }
                        if(items.get(i)=="F" && MsGrid[i/9][i%9]=="*")
                        {
                            items.set(i,"*");
                        }
                    }
                    gv.setAdapter(gridadapter);
                    Toast.makeText(getApplicationContext(), "Game Over!! Better luck next time!", Toast.LENGTH_LONG).show();
                    bomb.start();
                    gv.setEnabled(false);
                    stopTimer();
                    secondsPassed=0;
                }

                else {


                    if (f == 0) {
                        createMines(position);
                        click(x,y);
                        gv.setAdapter(gridadapter);
                        f++;

                    } else {

                        click(x, y);
                        int win = checkWin();
                        if(win==1){

                            Toast.makeText(getApplicationContext(), "Congratulations. You Won!!", Toast.LENGTH_LONG).show();
                            gv.setAdapter(gridadapter);
                            //gv.setEnabled(false);
                            //stopTimer();
                            //secondsPassed=0;

                        }

                        else
                            gv.setAdapter(gridadapter);
                    }


                }
            }
        });


        gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) { //Setting up flags on longclick

                items.set(position, "F");
                gv.setAdapter(gridadapter);


                return true;
            }
        });


        gv.setAdapter(gridadapter);

    }

    public void createMines(int position) {   //Placing Mines using random function

        if (bombcount == 0) {
            Toast.makeText(getApplicationContext(), "Please choose the difficulty Level", Toast.LENGTH_LONG).show();

        } else {

            Random ra = new Random();
            int count = 0;
            //int x=position/9;
            //int y=position%9;
            int xPoint;
            int yPoint;
            //int noOfMines = 10;
            while (count < bombcount) {

                xPoint = ra.nextInt(9);
                yPoint = ra.nextInt(9);
                p = (9 * xPoint) + yPoint;
                if (MsGrid[xPoint][yPoint] != "*" && p != position ) {

                    MsGrid[xPoint][yPoint] = "*";
                    count++;

                }
            }

            for (int x = 0; x < 9; x++) {
                for (int y = 0; y < 9; y++) {
                    if (MsGrid[x][y] != "*") {
                        MsGrid[x][y] = neighbors(x, y);
                    }
                }
            }
        }
    }

    public String neighbors(int y, int x) { //Calculating neighbour count
        int c = 0;
        c += minePresent(y - 1, x - 1);
        c += minePresent(y - 1, x);
        c += minePresent(y - 1, x + 1);
        c += minePresent(y, x - 1);
        c += minePresent(y, x + 1);
        c += minePresent(y + 1, x - 1);
        c += minePresent(y + 1, x);
        c += minePresent(y + 1, x + 1);
        if (c > 0)
            return Integer.toString(c);
        else
            return " ";

    }

    public void click( int x , int y ){  //Setting neighbour count
        if( x >= 0 && y >= 0 && x < 9 && y < 9 && items.get((9*x)+y)=="O"&&MsGrid[x][y]!="*"){

            items.set((9*x)+y,MsGrid[x][y]);

            if( neighbors(x,y)==" " ){
                for( int xt = -1 ; xt <= 1 ; xt++ ){
                    for( int yt = -1 ; yt <= 1 ; yt++){

                        click(x + xt , y + yt);

                    }
                }
            }


        }


    }
    public int minePresent(int x, int y) { //Checking if mines is present at particular location or not

        if (x >= 0 && x < 9 && y >= 0 && y < 9 && MsGrid[x][y] == "*")
            return 1;

        else
            return 0;

    }
    /*int k=0;
    public void hintsReveal(int x, int y) {


        if (neighbors(x, y) != " ") {
            return;
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                if (MsGrid[x + row - 1][y + column - 1] == "*"
                        && (x + row - 1 > 0) && (y + column - 1 > 0)
                        && (x + row - 1 < 10)
                        && (y + column - 1 < 10)) {
                    items.set(9 * (x + row - 1) + (y + column - 1), MsGrid[x + row - 1][y + column - 1]);
                }
            }
        }


    } */

    int checkWin(){    //Checking win conditions
        int c=0;
        w=0;
        flag=0;

        int O=0;
        for(int i=0;i<81;i++){

            for(int j=1;j<bombcount-1;j++){
                if(MsGrid[i/9][i%9]!= Integer.toString(j) && MsGrid[i/9][i%9]!=" ");
                c=1;

            }

            if(items.get(i)=="O"&&c==1)
                O++;
            if(items.get(i)=="F" && MsGrid[i/9][i%9]=="*")
                flag++;
        }

        if(O+flag == bombcount)
            w=1;
        return w;
    }


    public void startTimer() //Start game timer when player starts playing game
    {
        if (secondsPassed == 0)
        {
            handle.removeCallbacks(updateTimeElasped);
            // tell timer to run call back after 1 second
            handle.postDelayed(updateTimeElasped, 1000);
        }
    }

    public void stopTimer() //Stop game timer when player lost or win
    {
        handle.removeCallbacks(updateTimeElasped);
        time.setText("Timer : 0");
    }

    private Runnable updateTimeElasped = new Runnable()
    {
        public void run()
        {  // To calculate seconds
            long currentMilliseconds = System.currentTimeMillis();
            ++secondsPassed;
            time.setText("Timer : "+Integer.toString(secondsPassed));

            handle.postAtTime(this, currentMilliseconds);
            handle.postDelayed(updateTimeElasped, 1000);
        }
    };
}

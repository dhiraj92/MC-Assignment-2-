package com.example.dhiraj.messagepassing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;


/**
 * GraphView creates a scaled line or bar graph with x and y axis labels.
 * @author Arno den Hond
 *
 */
public class GraphView extends View {

    public static boolean BAR = false;
    public static boolean LINE = true;

    private Paint paint;
    private float[] values1;
    private float[] values2;
    private float[] values3;
    private String[] horlabels;
    private String[] verlabels;
    private String title;
    private boolean type;
    float border;
    float horstart;
    float height;
    float width;
    float max;
    float min;
    float diff;
    float graphheight;
    float graphwidth;

    public GraphView(Context context, float[] values1,float[] values2,float[] values3,String title, String[] horlabels, String[] verlabels, boolean type) {
        super(context);
        if (values1 == null)
            values1 = new float[0];
        else
            this.values1 = values1;
        if (values2 == null)
            values2 = new float[0];
        else
            this.values2 = values2;
        if (values3 == null)
            values3 = new float[0];
        else
            this.values3 = values3;
        if (title == null)
            title = "";
        else
            this.title = title;
        if (horlabels == null)
            this.horlabels = new String[0];
        else
            this.horlabels = horlabels;
        if (verlabels == null)
            this.verlabels = new String[0];
        else
            this.verlabels = verlabels;
        this.type = type;
        paint = new Paint();
    }

    public void setValues(float[] newValues1,float[] newValues2, float[] newValues3)
    {
        this.values1 = newValues1;
        this.values2 = newValues2;
        this.values3 = newValues3;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        border = 20;
        horstart = border * 2;
        height = getHeight();
        width = getWidth() - 1;
        max = getMax();
        min = getMin();
        diff = max - min;
        graphheight = height - (2 * border);
        graphwidth = width - (2 * border);


        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(20);
        paint.setAntiAlias(true);

        int vers = verlabels.length - 1;
        for (int i = 0; i < verlabels.length; i++) {
            paint.setColor(Color.CYAN);
            float y = ((graphheight / vers) * i) + border;
            canvas.drawLine(horstart, y, width, y, paint);
            paint.setColor(Color.CYAN);
            canvas.drawText(verlabels[i], 0, y, paint);
        }
        int hors = horlabels.length - 1;
        for (int i = 0; i < horlabels.length; i++) {
            paint.setColor(Color.CYAN);
            float x = ((graphwidth / hors) * i) + horstart;
            canvas.drawLine(x, height - border, x, border, paint);
            paint.setTextAlign(Paint.Align.CENTER);
            if (i==horlabels.length-1)
                paint.setTextAlign(Paint.Align.RIGHT);
            if (i==0)
                paint.setTextAlign(Paint.Align.LEFT);
            paint.setColor(Color.CYAN);
            canvas.drawText(horlabels[i], x, height - 4, paint);
        }

        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(title, (graphwidth / 2) + horstart, border - 4, paint);

        if (max != min) {
            paint.setColor(Color.LTGRAY);
            if (type == BAR) {
               /* float datalength = values.length;
                float colwidth = (width - (2 * border)) / datalength;
                for (int i = 0; i < values.length; i++) {
                    float val = values[i] - min;
                    float rat = val / diff;
                    float h = graphheight * rat;
                    canvas.drawRect((i * colwidth) + horstart, (border - h) + graphheight, ((i * colwidth) + horstart) + (colwidth - 1), height - (border - 1), paint);

            }*/
            } else {
                    drawlines(values1,canvas,"red");
                    drawlines(values2,canvas, "blue");
                    drawlines(values3,canvas, "green");
                }
            }
        }

    private void drawlines(float[] values, Canvas canvas,String color) {
        System.out.println("inisde 1");
        paint.setColor(Color.LTGRAY);
        float datalength = values.length;
        float colwidth = (width - (2 * border)) / datalength;
        float halfcol = colwidth / 2;
        float lasth = 0;
        for (int i = 0; i < values.length; i++) {
            System.out.println("inisde for" + values[i]);

            float val = values[i] - min;
            float rat = val / diff;
            float h = graphheight * rat;
            if (i > 0)
                paint.setColor(Color.parseColor(color));
            paint.setStrokeWidth(5.0f);

            canvas.drawLine(((i - 1) * colwidth) + (horstart + 1) + halfcol, (border - lasth) + graphheight, (i * colwidth) + (horstart + 1) + halfcol, (border - h) + graphheight, paint);
            lasth = h;
    }

}

    private float getMax() {
        float largest = Integer.MIN_VALUE;
        for (int i = 0; i < values1.length; i++)
            if (values1[i] > largest)
                largest = values1[i];
        for (int i = 0; i < values2.length; i++)
            if (values2[i] > largest)
                largest = values2[i];
        for (int i = 0; i < values3.length; i++)
            if (values3[i] > largest)
                largest = values3[i];

        //largest = 10;
        return largest;
    }

    private float getMin() {
        float smallest = Integer.MAX_VALUE;
        for (int i = 0; i < values1.length; i++)
            if (values1[i] < smallest)
                smallest = values1[i];
        for (int i = 0; i < values2.length; i++)
            if (values2[i] < smallest)
                smallest = values2[i];
        for (int i = 0; i < values3.length; i++)
            if (values3[i] < smallest)
                smallest = values3[i];


        //smallest = -2;
        return smallest;
    }

}
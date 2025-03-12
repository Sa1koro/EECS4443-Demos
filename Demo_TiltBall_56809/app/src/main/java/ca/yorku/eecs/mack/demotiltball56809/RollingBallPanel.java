package ca.yorku.eecs.mack.demotiltball56809;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.View;

import java.util.Locale;
//import Random for Lap Direction Indicators
import java.util.Random;
//import Timer for User Performance Data
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.ArrayList;

public class RollingBallPanel extends View
{
    final static float DEGREES_TO_RADIANS = 0.0174532925f;

    // the ball diameter will be min(width, height) / this_value
    final static float BALL_DIAMETER_ADJUST_FACTOR = 30;

    final static int DEFAULT_LABEL_TEXT_SIZE = 20; // tweak as necessary
    final static int DEFAULT_STATS_TEXT_SIZE = 10;
    final static int DEFAULT_GAP = 7; // between lines of text
    final static int DEFAULT_OFFSET = 10; // from bottom of display

    final static int MODE_NONE = 0;
    final static int PATH_TYPE_SQUARE = 1;
    final static int PATH_TYPE_CIRCLE = 2;

    final static float PATH_WIDTH_NARROW = 2f; // ... x ball diameter
    final static float PATH_WIDTH_MEDIUM = 4f; // ... x ball diameter
    final static float PATH_WIDTH_WIDE = 8f; // ... x ball diameter

    float radiusOuter, radiusInner;

    Bitmap ball, decodedBallBitmap;
    int ballDiameter;

    float dT; // time since last sensor event (seconds)

    float width, height, pixelDensity;
    int labelTextSize, statsTextSize, gap, offset;

    RectF innerRectangle, outerRectangle, innerShadowRectangle, outerShadowRectangle, ballNow;
    boolean touchFlag;
    Vibrator vib;
    int wallHits;

    float xBall, yBall; // top-left of the ball (for painting)
    float xBallCenter, yBallCenter; // center of the ball

    float pitch, roll;
    float tiltAngle, tiltMagnitude;

    // parameters from Setup dialog
    String orderOfControl;
    float gain, pathWidth;
    int pathType;

    float velocity; // in pixels/second (velocity = tiltMagnitude * tiltVelocityGain
    float dBall; // the amount to move the ball (in pixels): dBall = dT * velocity
    float xCenter, yCenter; // the center of the screen
    long now, lastT;
    Paint statsPaint, labelPaint, linePaint, fillPaint, backgroundPaint;
    float[] updateY;

    // declare variables for lap line and direction arrow
    // Lap line coordinates
    private float lapLineX;
    private float lapLineY;
    private float lapLineX1;
    private float lapLineY1;

    // Paint object for the lap line
    private Paint lapLinePaint;


    // Variables for arrow direction
    private boolean clockwiseDirection; // 控制方向
    private  Paint arrowPaint;
    private  Path arrowPath;
    private boolean arrowDirectionUp; // true if arrow points up, false if down
    private float arrowXStart;
    private float arrowYStart;
    private float arrowXEnd;
    private float arrowYEnd;

    // Random number generator
    private Random random;

    // add  variables Task4.4 User Performance Data
    int targetLaps; // target Laps（从Bundle获取）
    int currentLap; // Lap tracking
    private long lapStartTime = 0; // Time when current lap started
    private long experimentStartTime = 0; // Time when experiment started
    private List<Long> lapTimes = new ArrayList<>(); // Store lap times
    private boolean isBallInsidePath = true; // Track if ball is inside the path
    private long timeOutsidePathStart = 0; // Time when ball went outside path
    private long totalTimeOutsidePath = 0; // Total time spent outside path
    private boolean hasCrossedMidline = false; // Track if ball crossed midline (anti-cheat)
    private boolean lapInProgress = false; // Track if a lap is in progress
    private long totalInPathTime; // 总路径内时间
    private boolean wasInsidePath; // 用于墙壁碰撞检测
    RectF detectOutRect, detectInRect, startOval;


    // Midline for anti-cheat verification
    private float midlineX;
    private Paint midlinePaint;
    private boolean midlineCrossed = false;
    float prevXBallCenter, prevYBallCenter; // previous center of the ball position (for tracking direction)
    private long lastLapTime = 0;


    // add new variable for soundEffect
    private MediaPlayer lapSound; // 圈数音效
    ToneGenerator toneGen;



    // add variables for Color Style
    final static int COLOR_TYPE_BLUE = 1;
    final static int COLOR_TYPE_RED = 2;
    final static int COLOR_TYPE_GREEN = 3;
    final static int COLOR_TYPE_YELLOW = 4;
    int pathColor;
    String colorOfPath;

    public RollingBallPanel(Context contextArg)
    {
        super(contextArg);
        initialize(contextArg);
    }

    public RollingBallPanel(Context contextArg, AttributeSet attrs)
    {
        super(contextArg, attrs);
        initialize(contextArg);
    }

    public RollingBallPanel(Context contextArg, AttributeSet attrs, int defStyle)
    {
        super(contextArg, attrs, defStyle);
        initialize(contextArg);
    }

    // things that can be initialized from within this View
    private void initialize(Context c)
    {
        linePaint = new Paint();
        linePaint.setColor(Color.RED);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2);
        linePaint.setAntiAlias(true);

        fillPaint = new Paint();
        fillPaint.setColor(0xffccbbbb);
        fillPaint.setStyle(Paint.Style.FILL);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.LTGRAY);
        backgroundPaint.setStyle(Paint.Style.FILL);

        labelPaint = new Paint();
        labelPaint.setColor(Color.BLACK);
        labelPaint.setTextSize(DEFAULT_LABEL_TEXT_SIZE);
        labelPaint.setAntiAlias(true);

        statsPaint = new Paint();
        statsPaint.setAntiAlias(true);
        statsPaint.setTextSize(DEFAULT_STATS_TEXT_SIZE);

        // Initialize the midline paint
        midlinePaint = new Paint();
        midlinePaint.setColor(Color.GRAY);
        midlinePaint.setAlpha(50); // Semi-transparent
        midlinePaint.setStrokeWidth(2f);
        midlinePaint.setStyle(Paint.Style.STROKE);
        midlinePaint.setPathEffect(new android.graphics.DashPathEffect(new float[]{10, 10}, 0));

        // NOTE: we'll create the actual bitmap in onWindowFocusChanged
        decodedBallBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ball);

        lastT = System.nanoTime();
        this.setBackgroundColor(Color.LTGRAY);
        touchFlag = false;
        outerRectangle = new RectF();
        innerRectangle = new RectF();
        innerShadowRectangle = new RectF();
        outerShadowRectangle = new RectF();
        ballNow = new RectF();
        wallHits = 0;
        // Initial lap count
        currentLap = 0;

        // Initialize the lap line paint
        lapLinePaint = new Paint();
        lapLinePaint.setColor(Color.RED); // Example: Red lap line
        lapLinePaint.setStrokeWidth(5f); // Example: 5-pixel wide line
        lapLinePaint.setStyle(Paint.Style.STROKE); // Draw a line, not a filled shape

        // Initialize the direction arrow paint
        arrowPaint = new Paint();
        arrowPaint.setColor(Color.BLUE);
        arrowPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        arrowPaint.setStrokeWidth(4f);
        arrowPaint.setAntiAlias(true);

        arrowPath = new Path();
        arrowPath.moveTo(0, 0);
        arrowPath.lineTo(-20, -40);
        arrowPath.lineTo(20, -40);
        arrowPath.close();

        vib = (Vibrator)c.getSystemService(Context.VIBRATOR_SERVICE);
        // initialize the audio signal
        toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        // Initialize random generator for direction
        random = new Random();
    }

    /**
     * Called when the window hosting this view gains or looses focus.  Here we initialize things that depend on the
     * view's width and height.
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        if (!hasFocus)
            return;

        width = this.getWidth();
        height = this.getHeight();

        // the ball diameter is nominally 1/30th the smaller of the view's width or height
        ballDiameter = width < height ? (int)(width / BALL_DIAMETER_ADJUST_FACTOR)
                : (int)(height / BALL_DIAMETER_ADJUST_FACTOR);

        // now that we know the ball's diameter, get a bitmap for the ball
        ball = Bitmap.createScaledBitmap(decodedBallBitmap, ballDiameter, ballDiameter, true);

        // center of the view
        xCenter = width / 2f;
        yCenter = height / 2f;

        // top-left corner of the ball
        xBall = xCenter;
        yBall = yCenter;

        // center of the ball
        xBallCenter = xBall + ballDiameter / 2f;
        yBallCenter = yBall + ballDiameter / 2f;
        // Initialize the previous ball center position
        prevXBallCenter = xBallCenter;
        prevYBallCenter = yBallCenter;

        // configure outer rectangle of the path
        radiusOuter = width < height ? 0.40f * width : 0.40f * height;
        outerRectangle.left = xCenter - radiusOuter;
        outerRectangle.top = yCenter - radiusOuter;
        outerRectangle.right = xCenter + radiusOuter;
        outerRectangle.bottom = yCenter + radiusOuter;

        // configure inner rectangle of the path
        // NOTE: medium path width is 4 x ball diameter
        radiusInner = radiusOuter - pathWidth * ballDiameter;
        innerRectangle.left = xCenter - radiusInner;
        innerRectangle.top = yCenter - radiusInner;
        innerRectangle.right = xCenter + radiusInner;
        innerRectangle.bottom = yCenter + radiusInner;


        // Setup midline (vertical line through center for anti-cheat)
        midlineX = xCenter;


        // 在 onWindowFocusChanged 方法中调整圈线位置
        if (pathType == PATH_TYPE_SQUARE) {
            // 方形路径：圈线位于右侧边缘
            lapLineX = outerRectangle.right;
            lapLineY = yCenter;
            lapLineX1 = innerRectangle.right;
        } else if (pathType == PATH_TYPE_CIRCLE) {
            // 圆形路径：圈线位于右侧（角度 0 度方向）
            lapLineX = xCenter + radiusOuter;
            lapLineY = yCenter;
            lapLineX1 = xCenter + radiusInner;
        }
        lapLineY1 = lapLineY;
        // configure outer shadow rectangle (needed to determine wall hits)
        // NOTE: line thickness (aka stroke width) is 2
        outerShadowRectangle.left = outerRectangle.left + ballDiameter - 2f;
        outerShadowRectangle.top = outerRectangle.top + ballDiameter - 2f;
        outerShadowRectangle.right = outerRectangle.right - ballDiameter + 2f;
        outerShadowRectangle.bottom = outerRectangle.bottom - ballDiameter + 2f;

        // configure inner shadow rectangle (needed to determine wall hits)
        innerShadowRectangle.left = innerRectangle.left + ballDiameter - 2f;
        innerShadowRectangle.top = innerRectangle.top + ballDiameter - 2f;
        innerShadowRectangle.right = innerRectangle.right - ballDiameter + 2f;
        innerShadowRectangle.bottom = innerRectangle.bottom - ballDiameter + 2f;

        // initialize a few things (e.g., paint and text size) that depend on the device's pixel density
        pixelDensity = this.getResources().getDisplayMetrics().density;
        labelTextSize = (int)(DEFAULT_LABEL_TEXT_SIZE * pixelDensity + 0.5f);
        labelPaint.setTextSize(labelTextSize);

        statsTextSize = (int)(DEFAULT_STATS_TEXT_SIZE * pixelDensity + 0.5f);
        statsPaint.setTextSize(statsTextSize);

        gap = (int)(DEFAULT_GAP * pixelDensity + 0.5f);
        offset = (int)(DEFAULT_OFFSET * pixelDensity + 0.5f);

        // compute y offsets for painting stats (bottom-left of display)
        updateY = new float[9]; // up to 6 lines of stats will appear
        for (int i = 0; i < updateY.length; ++i)
            updateY[i] = height - offset - i * (statsTextSize + gap);
    }

    /*
     * Do the heavy lifting here! Update the ball position based on the tilt angle, tilt
     * magnitude, order of control, etc.
     */
    public void updateBallPosition(float pitchArg, float rollArg, float tiltAngleArg, float tiltMagnitudeArg)
    {
        // Store previous ball position for direction calculation
        prevXBallCenter = xBallCenter;
        prevYBallCenter = yBallCenter;

        pitch = pitchArg; // for information only (see onDraw)
        roll = rollArg; // for information only (see onDraw)
        tiltAngle = tiltAngleArg;
        tiltMagnitude = tiltMagnitudeArg;

        // get current time and delta since last onDraw
        now = System.nanoTime();
        dT = (now - lastT) / 1000000000f; // seconds
        lastT = now;

        // don't allow tiltMagnitude to exceed 45 degrees
        final float MAX_MAGNITUDE = 45f;
        tiltMagnitude = tiltMagnitude > MAX_MAGNITUDE ? MAX_MAGNITUDE : tiltMagnitude;

        // This is the only code that distinguishes velocity-control from position-control
        if (orderOfControl.equals("Velocity")) // velocity control
        {
            // compute ball velocity (depends on the tilt of the device and the gain setting)
            velocity = tiltMagnitude * gain;

            // compute how far the ball should move (depends on the velocity and the elapsed time since last update)
            dBall = dT * velocity; // make the ball move this amount (pixels)

            // compute the ball's new coordinates (depends on the angle of the device and dBall, as just computed)
            float dx = (float)Math.sin(tiltAngle * DEGREES_TO_RADIANS) * dBall;
            float dy = -(float)Math.cos(tiltAngle * DEGREES_TO_RADIANS) * dBall;
            xBall += dx;
            yBall += dy;

        } else
        // position control
        {
            // compute how far the ball should move (depends on the tilt of the device and the gain setting)
            dBall = tiltMagnitude * gain;

            // compute the ball's new coordinates (depends on the angle of the device and dBall, as just computed)
            float dx = (float)Math.sin(tiltAngle * DEGREES_TO_RADIANS) * dBall;
            float dy = -(float)Math.cos(tiltAngle * DEGREES_TO_RADIANS) * dBall;
            xBall = xCenter + dx;
            yBall = yCenter + dy;
        }

        // make an adjustment, if necessary, to keep the ball visible (also, restore if NaN)
        if (Float.isNaN(xBall) || xBall < 0)
            xBall = 0;
        else if (xBall > width - ballDiameter)
            xBall = width - ballDiameter;
        if (Float.isNaN(yBall) || yBall < 0)
            yBall = 0;
        else if (yBall > height - ballDiameter)
            yBall = height - ballDiameter;

        // oh yea, don't forget to update the coordinate of the center of the ball (needed to determine wall  hits)
        xBallCenter = xBall + ballDiameter / 2f;
        yBallCenter = yBall + ballDiameter / 2f;

        // if ball touches wall, vibrate and increment wallHits count
        // NOTE: We also use a boolean touchFlag so we only vibrate on the first touch
        if (ballTouchingLine() && !touchFlag)
        {
            touchFlag = true; // the ball has *just* touched the line: set the touchFlag
            vib.vibrate(50); // 50 ms vibrotactile pulse
            ++wallHits;

        } else if (!ballTouchingLine() && touchFlag)
            touchFlag = false; // the ball is no longer touching the line: clear the touchFlag

        // Check if ball is inside the path
        boolean currentlyInsidePath = isInsidePath();

        // Track time outside path
        if (isBallInsidePath && !currentlyInsidePath) {
            // Ball just went outside the path
            timeOutsidePathStart = System.currentTimeMillis();
        } else if (!isBallInsidePath && currentlyInsidePath) {
            // Ball just came back inside the path
            if (timeOutsidePathStart > 0) {
                totalTimeOutsidePath += System.currentTimeMillis() - timeOutsidePathStart;
                timeOutsidePathStart = 0;
            }
        }

        isBallInsidePath = currentlyInsidePath;

        // Check for midline crossing (anti-cheat)
        if (lapInProgress && !midlineCrossed) {
            if ((prevXBallCenter < midlineX && xBallCenter >= midlineX) ||
                    (prevXBallCenter > midlineX && xBallCenter <= midlineX)) {
                midlineCrossed = true;
            }
        }

        // Check for lap line crossing in the correct direction
        checkLapCompletion();

        // 当球在圈线附近晃动时，忽略微小移动
        if (Math.abs(xBallCenter - lapLineX) < ballDiameter/4) {
            return;
        }

        invalidate(); // force onDraw to redraw the screen with the ball in its new position
    }
    /**
     * Check if the ball has completed a lap by crossing the lap line in the correct direction
     */
    private void checkLapCompletion() {
        // Check if ball is crossing the lap line
        boolean crossingLapLine = isCrossingLapLine();

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastLapTime > 1000) { // 1秒冷却时间
            toneGen.startTone(ToneGenerator.TONE_CDMA_PIP, 200);
            lastLapTime = currentTime;
        }

        if (crossingLapLine && midlineCrossed && currentLap < targetLaps) {
            currentLap++;
            midlineCrossed = false; // 重置中线标志
            toneGen.startTone(ToneGenerator.TONE_CDMA_PIP, 200);
        }
    }
    /**
     * Check if the ball is crossing the lap line in the correct direction
     */
    private boolean isCrossingLapLine() {
        if (pathType == PATH_TYPE_SQUARE) {
            // 方形路径：判断横向穿越
            if (clockwiseDirection) {
                // 顺时针：从右向左穿过右侧圈线
                return prevXBallCenter >= lapLineX && xBallCenter < lapLineX
                        && Math.abs(yBallCenter - lapLineY) < ballDiameter/2;
            } else {
                // 逆时针：从左向右穿过右侧圈线
                return prevXBallCenter <= lapLineX && xBallCenter > lapLineX
                        && Math.abs(yBallCenter - lapLineY) < ballDiameter/2;
            }
        } else if (pathType == PATH_TYPE_CIRCLE) {
            // 圆形路径：判断径向穿越
            float prevAngle = (float) Math.toDegrees(Math.atan2(prevYBallCenter - yCenter, prevXBallCenter - xCenter));
            float currentAngle = (float) Math.toDegrees(Math.atan2(yBallCenter - yCenter, xBallCenter - xCenter));
            if (clockwiseDirection) {
                // 顺时针：角度减少超过 180 度视为穿越
                return (prevAngle - currentAngle) > 180;
            } else {
                // 逆时针：角度增加超过 180 度视为穿越
                return (currentAngle - prevAngle) > 180;
            }
        }
        return false;
    }

    /**
     * Check if the ball is inside the path
     */
    private boolean isInsidePath() {
        if (pathType == PATH_TYPE_SQUARE) {
            return xBallCenter >= outerRectangle.left && xBallCenter <= outerRectangle.right &&
                    yBallCenter >= outerRectangle.top && yBallCenter <= outerRectangle.bottom &&
                    !(xBallCenter >= innerRectangle.left && xBallCenter <= innerRectangle.right &&
                            yBallCenter >= innerRectangle.top && yBallCenter <= innerRectangle.bottom);
        } else if (pathType == PATH_TYPE_CIRCLE) {
            float distanceFromCenter = (float)Math.sqrt(
                    (xBallCenter - xCenter) * (xBallCenter - xCenter) +
                            (yBallCenter - yCenter) * (yBallCenter - yCenter)
            );
            return distanceFromCenter <= radiusOuter && distanceFromCenter >= radiusInner;
        }
        return true; // Default if no path type is set
    }

    protected void onDraw(Canvas canvas)
    {
        // add variables for lap line
        float   lapLineX = innerRectangle.left,
                lapLineY = innerRectangle.top + (innerRectangle.bottom - innerRectangle.top) / 2,
                lapLineX1 = outerRectangle.left,
                lapLineY1 = lapLineY;

        // check if view is ready for drawing
        if (updateY == null)
            return;

        // draw the paths
        if (pathType == PATH_TYPE_SQUARE)
        {
            // draw fills
            canvas.drawRect(outerRectangle, fillPaint);
            canvas.drawRect(innerRectangle, backgroundPaint);

            // draw lines
            canvas.drawRect(outerRectangle, linePaint);
            canvas.drawRect(innerRectangle, linePaint);

        } else if (pathType == PATH_TYPE_CIRCLE)
        {
            // draw fills
            canvas.drawOval(outerRectangle, fillPaint);
            canvas.drawOval(innerRectangle, backgroundPaint);

            // draw lines
            canvas.drawOval(outerRectangle, linePaint);
            canvas.drawOval(innerRectangle, linePaint);


        }

        // Draw the midline (anti-cheat line)
        canvas.drawLine(midlineX, outerRectangle.top, midlineX, outerRectangle.bottom, midlinePaint);


        // Draw the direction arrow
        // 绘制方向箭头（在屏幕中心）
        canvas.save();
        canvas.translate(width/2f, height/2f); // 移动到中心

        // 根据方向旋转画布
        canvas.rotate(clockwiseDirection ? 0 : 180);

        // 绘制箭头（自动适应方向）
        canvas.drawPath(arrowPath, arrowPaint);
        canvas.restore();

        // Draw the lap line
        canvas.drawLine(lapLineX, lapLineY, lapLineX1, lapLineY1, lapLinePaint);

        // draw label
        canvas.drawText("Demo_TiltBall_56809", 6f, labelTextSize, labelPaint);
        // Calculate performance metrics to display
        String lapTimeStr = "Waiting for first lap...";
        String totalTimeStr = "Waiting for experiment to begin...";

        if (experimentStartTime > 0) {
            long currentTime = System.currentTimeMillis();
            long totalElapsedTime = currentTime - experimentStartTime;
            long currentLapTime = currentTime - lapStartTime;

            // Format times as seconds with milliseconds
            totalTimeStr = String.format(Locale.CANADA, "Total time: %.1f s", totalElapsedTime / 1000.0f);
            lapTimeStr = String.format(Locale.CANADA, "Current lap: %.1f s", currentLapTime / 1000.0f);

            // If we have any completed laps, calculate average
            if (!lapTimes.isEmpty()) {
                long sum = 0;
                for (Long time : lapTimes) {
                    sum += time;
                }
                float avgLapTime = sum / (float) lapTimes.size() / 1000.0f;
                lapTimeStr += String.format(Locale.CANADA, " (Avg: %.1f s)", avgLapTime);
            }
        }

        // Format outside path time
        long outsideTime = totalTimeOutsidePath;
        if (timeOutsidePathStart > 0) {
            // Add current outside time if still outside
            outsideTime += System.currentTimeMillis() - timeOutsidePathStart;
        }
        String outsidePathStr = String.format(Locale.CANADA, "Time outside path: %.1f s", outsideTime / 1000.0f);


        // draw stats (pitch, roll, tilt angle, tilt magnitude)
        if (pathType == PATH_TYPE_SQUARE || pathType == PATH_TYPE_CIRCLE)
        {
            canvas.drawText("Wall hits = " + wallHits, 6f, updateY[7], statsPaint);
            canvas.drawText("Numbers of laps = " + currentLap + "/" + targetLaps, 6f, updateY[6], statsPaint);
            long lapTime;
            if (lapStartTime == 0) {
                lapTime = lapStartTime;
            } else{
                lapTime = System.currentTimeMillis() - lapStartTime/1000;
            }
            canvas.drawText("Lap times: " + lapTime, 6f, updateY[5], statsPaint);
            canvas.drawText("-----------------", 6f, updateY[4], statsPaint);
        }
        canvas.drawText(String.format(Locale.CANADA, "Tablet pitch (degrees) = %.2f", pitch), 6f, updateY[3],
                statsPaint);
        canvas.drawText(String.format(Locale.CANADA, "Tablet roll (degrees) = %.2f", roll), 6f, updateY[2], statsPaint);
        canvas.drawText(String.format(Locale.CANADA, "Ball x = %.2f", xBallCenter), 6f, updateY[1], statsPaint);
        canvas.drawText(String.format(Locale.CANADA, "Ball y = %.2f", yBallCenter), 6f, updateY[0], statsPaint);

        // draw the ball in its new location
        canvas.drawBitmap(ball, xBall, yBall, null);


    } // end onDraw

    /**
     * Draw an lap line
     * @param paint
     * @param canvas
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     */
    private void drawArrow(Paint paint, Canvas canvas, float fromX, float fromY, float toX, float toY) {
        float angle, anglerad, radius, lineangle;

        //values to change for other appearance
        radius = 30f;
        angle = 35f;

        anglerad = (float) Math.toRadians(angle);
        lineangle = (float) Math.toRadians(angle / 2);

        canvas.drawLine(fromX, fromY, toX, toY, paint);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(toX, toY);
        path.lineTo((float) (toX - radius * Math.cos(lineangle - (anglerad / 2.0))), (float) (toY - radius * Math.sin(lineangle - (anglerad / 2.0))));
        path.lineTo((float) (toX - radius * Math.cos(lineangle + (anglerad / 2.0))), (float) (toY - radius * Math.sin(lineangle + (anglerad / 2.0))));
        path.close();

        canvas.drawPath(path, paint);

    }

    // Call this method when a new game starts
    public void resetArrowDirection() {
        // Randomly choose the arrow direction (up or down)
        arrowDirectionUp = random.nextBoolean();

        // Set the arrow's end point based on the chosen direction
        if (arrowDirectionUp) {
            arrowXEnd = arrowXStart;
            arrowYEnd = arrowYStart - 100; // Pointing up
        } else {
            arrowXEnd = arrowXStart;
            arrowYEnd = arrowYStart + 100; // Pointing down
        }
    }


    /*
     * Configure the rolling ball panel according to setup parameters
     */
    public void configure(String pathMode, String pathWidthArg, int gainArg, String orderOfControlArg, int numbersOfLaps)
    {
        // square vs. circle
        if (pathMode.equals("Square"))
            pathType = PATH_TYPE_SQUARE;
        else if (pathMode.equals("Circle"))
            pathType = PATH_TYPE_CIRCLE;
        else
            pathType = MODE_NONE;

        // narrow vs. medium vs. wide
        if (pathWidthArg.equals("Narrow"))
            pathWidth = PATH_WIDTH_NARROW;
        else if (pathWidthArg.equals("Wide"))
            pathWidth = PATH_WIDTH_WIDE;
        else
            pathWidth = PATH_WIDTH_MEDIUM;

        gain = gainArg;
        orderOfControl = orderOfControlArg;

        // Generate a random arrow direction
        clockwiseDirection = new Random().nextBoolean();
        // Set the numbers of Laps
        targetLaps = numbersOfLaps;
    }

    // returns true if the ball is touching (i.e., overlapping) the line of the inner or outer path border
    public boolean ballTouchingLine()
    {
        if (pathType == PATH_TYPE_SQUARE)
        {
            ballNow.left = xBall;
            ballNow.top = yBall;
            ballNow.right = xBall + ballDiameter;
            ballNow.bottom = yBall + ballDiameter;

            if (RectF.intersects(ballNow, outerRectangle) && !RectF.intersects(ballNow, outerShadowRectangle))
                return true; // touching outside rectangular border

            if (RectF.intersects(ballNow, innerRectangle) && !RectF.intersects(ballNow, innerShadowRectangle))
                return true; // touching inside rectangular border

        } else if (pathType == PATH_TYPE_CIRCLE)
        {
            final float ballDistance = (float)Math.sqrt((xBallCenter - xCenter) * (xBallCenter - xCenter)
                    + (yBallCenter - yCenter) * (yBallCenter - yCenter));

            if (Math.abs(ballDistance - radiusOuter) < (ballDiameter / 2f))
                return true; // touching outer circular border

            if (Math.abs(ballDistance - radiusInner) < (ballDiameter / 2f))
                return true; // touching inner circular border
        }
        return false;
    }

    /**
     * Check if the ball is touching the lap line
     * @return
     */
    public boolean ballTouchingLapLine(){
        ballNow.left = xBall;
        ballNow.top = yBall;
        ballNow.right = xBall + ballDiameter;
        ballNow.bottom = yBall + ballDiameter;

        float toYArrow = (float)Math.cos(tiltAngle * DEGREES_TO_RADIANS);

        return toYArrow > 0 && RectF.intersects(ballNow, outerRectangle);
    }

    /**
     * Check if the ball is crossing the lap line via a valid directions
      * @param prevX
     * @param prevY
     * @return
     */
    public boolean isValidLapCrossing(float prevX, float prevY) {
        final float lapLineX = width * 0.8f; // 右侧20%位置为圈线

        // 方向验证逻辑
        if(clockwiseDirection) {
            // 顺时针：从左到右穿越圈线
            return prevX < lapLineX && xBallCenter >= lapLineX;
        } else {
            // 逆时针：从右到左穿越圈线
            return prevX > lapLineX && xBallCenter <= lapLineX;
        }
    }

}

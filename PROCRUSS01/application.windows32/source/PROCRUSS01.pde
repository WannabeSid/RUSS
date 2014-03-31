import processing.serial.*;
import java.util.StringTokenizer;
import processing.video.*;


Capture cam;
Serial port;
PrintWriter output;
float[] theData;
Compass compass;
PitchGauge pitchGauge;
HeadingGauge headingGauge;
RollGauge rollGauge;
VelocityGauge velocityGauge;
DepthGauge depthGauge;
ClimbGauge climbGauge;
MotorGauge motorGaugeL1;
MotorGauge motorGaugeL2;
MotorGauge motorGaugeR1;
MotorGauge motorGaugeR2;
float lastA;
float lastAZ;

void setup()
{
  size(1000,700);
  output = createWriter("data.csv");
  output.println("HEADING,PITCH,ROLL,TEMP,DEPTH,Ax,Ay,Az,Vh,NetV,NetA");
  port = new Serial(this, Serial.list()[0], 57600);
  port.bufferUntil('\n');
  theData = new float[10];
  pitchGauge = new PitchGauge(325,540,100);
  velocityGauge = new VelocityGauge(220,540,100);
  headingGauge = new HeadingGauge(325,645,100);
  rollGauge = new RollGauge(220,645,100);
  depthGauge = new DepthGauge(430,540,100);
  climbGauge = new ClimbGauge(430,645,100);
  compass = new Compass();
  motorGaugeL1 = new MotorGauge(40,590,75);
  motorGaugeL2 = new MotorGauge(120,590,75);
  motorGaugeR1 = new MotorGauge(535,590,75);
  motorGaugeR2 = new MotorGauge(615,590,75);
  lastA = 0;
  lastAZ = 0;
  port.clear();
  noLoop();
  //cam = new Capture(this, Capture.list()[0]);
  //cam.start();     
  
  
}

public String dataToString()
{
  String result = "";
  for(int i = 0; i < 8; i++)
  {
    if(i != 7)
      result += theData[i] + ",";     
    else
      result += ""+theData[i];
  }
  return result;
}

void draw()
{
  background(100);
  rectMode(CORNER);
  rect(5,5,640,480);
  rectMode(CENTER);
  ellipseMode(CENTER);
  pitchGauge.update(compass.pitch, compass.roll);
  headingGauge.update(compass.heading);
  rollGauge.update(compass.roll);
  velocityGauge.update(compass.netA);
  climbGauge.update(compass.accel[2]);
  rollGauge.draw();
  pitchGauge.draw();
  headingGauge.draw();
  velocityGauge.draw();
  depthGauge.draw();
  climbGauge.draw();
  motorGaugeL1.draw();
  motorGaugeL2.draw();
  motorGaugeR1.draw();
  motorGaugeR2.draw();  
  //if (cam.available() == true) {
  //  cam.read();
  //}
  //image(cam, 5, 5    );
  output.println(compass.toString());
}

public void setData(String data)
{
  StringTokenizer tok = new StringTokenizer(data,"CTRP*xyzA$D");
  for(int i = 0; tok.hasMoreTokens(); i++)
  {
    String temp = tok.nextToken();
    if(Character.isLetter(temp.charAt(0)) || temp.charAt(0) == '$')
    {
      i--;
    }
    else
    {
      System.out.print("([" + i +"]=" + temp +")");
      theData[i] = (float)Float.parseFloat(temp);
    }
  }
  compass.update(theData);
}

void serialEvent(Serial p)
{
  try
  {
  String buf = p.readString();
  System.out.println(buf.substring(1,buf.length()-4));
  //$C207.5P4.3R116.9T26.9D0.0000Ax0.075Ay0.894Az-0.450*13
  //Compass, Pitch, Roll, Temp, Depth, Ax, Ay, Az, Parity
  if(buf.charAt(0) == '$')
    setData(buf.substring(1,buf.length()-4));
  //System.out.println(theData[0]);
  redraw();
  }
  catch(Exception e)
  {
    System.out.println("OOPS");
    e.printStackTrace();
  }
}

void keyPressed()
{
  if(key == 's')
  {
    output.flush();
    output.close();
    exit();
  }
  if(keyCode == UP)
  {
    motorGaugeL1.update(motorGaugeL1.reading+1);
    motorGaugeL2.update(motorGaugeL2.reading+1);
    motorGaugeR1.update(motorGaugeR1.reading+1);
    motorGaugeR2.update(motorGaugeR2.reading+1);
  }
  if(keyCode == DOWN)
  {
    motorGaugeL1.update(motorGaugeL1.reading-1);
    motorGaugeL2.update(motorGaugeL2.reading-1);
    motorGaugeR1.update(motorGaugeR1.reading-1);
    motorGaugeR2.update(motorGaugeR2.reading-1);
  }
  if(keyCode == LEFT)
  {
    motorGaugeL1.update(motorGaugeL1.reading+1);
    motorGaugeL2.update(motorGaugeL2.reading+1);
    motorGaugeR1.update(motorGaugeR1.reading-1);
    motorGaugeR2.update(motorGaugeR2.reading-1);
  }
  if(keyCode == RIGHT)
  {
    motorGaugeL1.update(motorGaugeL1.reading-1);
    motorGaugeL2.update(motorGaugeL2.reading-1);
    motorGaugeR1.update(motorGaugeR1.reading+1);
    motorGaugeR2.update(motorGaugeR2.reading+1);
  }
  if(key == ' ')  
  {
    motorGaugeL1.update(0);
    motorGaugeL2.update(0);
    motorGaugeR1.update(0);
    motorGaugeR2.update(0);
  }
}

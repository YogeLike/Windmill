# Windmill
一个仿华为天气的转动风车自定义View

最近在做一个天气预报的app。因为本人使用的是华为的手机。发现华为自带的天气预报软件还挺好看的。所以我的天气预报软件的主界面就主要模仿华为天气了。地址在这[OneWeather](https://github.com/YugengWang/OneWeather.git "一个天气").

这个转动风车是其中的一个自定义View。自我感觉做完后效果还是不错的。新手一枚，有什么可以完善或者理解错误的地方欢迎提出哦。
效果如下：
![风车](http://i.imgur.com/ZLwBhnU.gif)

## 1. 概述
风车叶和风车杆都是在同一个View 里的 ，动画主要是使用属性动画来控制角度的大小，然后重绘叶片的位置，这个位置的计算需要用到三角函数的知识，可惜我基本忘光了那些公式，导致我在这一块弄了弄了好久才弄出正确的公式。

## 2. 变量和初始化

弧度是叶片离原始位置的弧度
以风车中间点为圆心和原点，即(x1,y1).
圆心与其他四个点连线，所以会有四个弧度和四个斜边。(x1,y1)与(x2,y2)为r1 和 rad1 ，以此类推。
```java
private Paint mWindmillPaint; //支柱画笔
    private Path mWindPath;
    private Path mPillarPath;
    private int width,height;
    private int mWindmillColor;//风车颜色
    private float mWindLengthPercent;//扇叶长度
    private Point mCenterPoint;//圆心
    private float x1,y1,x2,y2,x3,y3,x4,y4,x5,y5;//扇叶的点
    private double rad1,rad2,rad3,rad4;//弧度
    private double r1,r2,r3,r4;//斜边
    private ObjectAnimator mAnimator;//动画
    private float angle;//旋转角度
    private int windSpeed = 1;
```

```java
 private void init(AttributeSet attrs) {
        initAttrs(attrs);


        mWindmillPaint = new Paint();
        mCenterPoint = new Point();
        mWindmillPaint.setStyle(Paint.Style.FILL);
        mWindmillPaint.setStrokeWidth(2);//设置画笔粗细
        mWindmillPaint.setAntiAlias(true);
        mWindmillPaint.setColor(mWindmillColor);

        mAnimator = ObjectAnimator.ofFloat(this,"angle",0,(float)(2*PI));//
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setInterpolator(new LinearInterpolator());

    }
```
## 3. 绘制风车

柱子和叶片的绘制都用了贝塞尔曲线，其实用直线也可以。如果你没听过贝塞尔曲线，你可以搜索了解，[简书上的一篇教程](http://www.jianshu.com/p/55c721887568),其实我也没仔细看过，貌似很多漂亮的自定义View都会用到，或者记住贝塞尔曲线是让尖锐的角变成一条弧线就行了。
`mWindPath.cubicTo(x2,y2,x3,y3,x4,y4);`
`mWindPath.quadTo(x5,y5,x1,y1);`
这两个函数就是画贝塞尔曲线的。

(x1,y1)...(x5,y5) 这五个点是叶片上的点

叶片的绘制是画一片然后旋转画布canvas 120度，画其他两片。比较好的解决方案，不用去移动确定path的位置。


### 画柱子和叶片

```java
private void drawPillar(Canvas canvas) {
        mPillarPath = new Path();
        mPillarPath.moveTo(mCenterPoint.x-width/90,mCenterPoint.y-width/90);
        mPillarPath.lineTo(mCenterPoint.x+width/90,mCenterPoint.y-width/90);//连线
        mPillarPath.lineTo(mCenterPoint.x+width/35,height-height/35);
        mPillarPath.quadTo(mCenterPoint.x,height,mCenterPoint.x-width/35,height-height/35);//贝塞尔曲线，控制点和终点
        mPillarPath.close();//闭合图形
        canvas.drawPath(mPillarPath,mWindmillPaint);

    }
    private void drawWind(Canvas canvas) {
        mWindPath = new Path();
        canvas.drawCircle(mCenterPoint.x,mCenterPoint.y,width/40,mWindmillPaint);
        mWindPath.moveTo(x1,y1);
        x2 = mCenterPoint.x + (float) (r1 * Math.cos(rad1 + angle));
        y2 = mCenterPoint.y + (float) (r1 * Math.sin(rad1 + angle));
        x3 = mCenterPoint.x + (float) (r2 * Math.cos(rad2 + angle));
        y3 = mCenterPoint.y + (float) (r2 * Math.sin(rad2 + angle));
        x4 = mCenterPoint.x + (float) (r3 * Math.cos(rad3 + angle));
        y4 = mCenterPoint.y + (float) (r3 * Math.sin(rad3 + angle));
        x5 = mCenterPoint.x + (float) (r4 * Math.cos(rad4 + angle));
        y5 = mCenterPoint.y + (float) (r4 * Math.sin(rad4 + angle));


        mWindPath.cubicTo(x2,y2,x3,y3,x4,y4);
        mWindPath.quadTo(x5,y5,x1,y1);
        canvas.drawPath(mWindPath,mWindmillPaint);
        canvas.rotate(120,mCenterPoint.x,mCenterPoint.y);
        canvas.drawPath(mWindPath,mWindmillPaint);
        canvas.rotate(120,mCenterPoint.x,mCenterPoint.y);
        canvas.drawPath(mWindPath,mWindmillPaint);
        canvas.rotate(120,mCenterPoint.x,mCenterPoint.y);
    }
    
```


## 4. 动画


使用属性动画	ObjectAnimator，
设置重复次数，弧度变化范围从0~2PI 也就是360度，线性变化

```java
mAnimator = ObjectAnimator.ofFloat(this,"angle",0,(float)(2*PI));//
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setInterpolator(new LinearInterpolator());
```
windSpeed 控制风车的速度，其实就是缩短动画周期
```java
 public void startAnimation(){
        mAnimator.setDuration((long) (10000/(windSpeed*0.80)));//乘以小于1的系数降低影响
        mAnimator.start();
    }
```

### 初始点的弧度和斜边的计算
这里我是以初始叶片位于y轴正半轴来计算的，
rad 是 与x轴正半轴的夹角的弧度，

这里width/15 ,width/30 这些是用来控制叶片的厚度的，可以更改，不过要保持弧度和斜边了对应数值的一致。其实应该弄一个变量的。

```
   private void setBladeLocate() {

        x1 = mCenterPoint.x;
        y1 = mCenterPoint.y;

        //radian(弧度)
        rad1 = atan(width/15/(width/30)); //x1,y1与x2,y2形成的角,以圆点为坐标原点,返回角度为-pi/2 至 pi/2  artan（y/x）
        rad2 = atan(width/6/(width/30));//x1,y1 与 x3,y3
        rad3 = PI/2;//tan90 不存在
        rad4 = atan(mCenterPoint.y/2/(-width/30))+PI;//因为返回角度为 -pi/2 至pi,所以加PI


        //r 为斜边长度,与上面要对应
        r1 = Math.hypot(width/30,width/15);
        r2 = Math.hypot(width/30,width/6);
        r3 = Math.hypot(0,mCenterPoint.y);
        r4 = Math.hypot(width/30,mCenterPoint.y/2);
    }
    
```

### 弧度angle变化过程与坐标点的关系

rad+angle 即为此时相对于x正轴的角度
r1为斜边
斜边乘以cos 值 即为 邻边 ，
>cos = 邻/斜

```java
		x2 = mCenterPoint.x + (float) (r1 * Math.cos(rad1 + angle));
        y2 = mCenterPoint.y + (float) (r1 * Math.sin(rad1 + angle));
        x3 = mCenterPoint.x + (float) (r2 * Math.cos(rad2 + angle));
        y3 = mCenterPoint.y + (float) (r2 * Math.sin(rad2 + angle));
```


其实上面两部分转换为数学问题就是：
>知道半径和角度，求从原点出发的线段的终点坐标。

## 5.属性设置和布局
这里定义的xml属性只有风车颜色和风车叶占总长度的比值，经测试0.35 左右是比较好看的。

风车转速一般都由外部传入，通过代码设置。
```xml
<attr name="windmillColors"format="color|reference"/>
<attr name="windLengthPerent" format="float" />
```
### 布局
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="140dp"
    android:layout_height="match_parent">

    <com.yogw.windmill.Windmill
        android:id="@+id/windmill_big"
        android:layout_width="120dp"
        android:layout_height="120dp"
        app:windLengthPerent="0.35"
        app:windmillColors="@color/colorPrimary"
        android:layout_alignParentLeft="true"/>
    <com.yogw.windmill.Windmill
        android:id="@+id/windmill_small"
        android:layout_width="80dp"
        android:layout_height="80dp"
        app:windLengthPerent="0.35"
        app:windmillColors="@color/colorPrimary"
        android:layout_alignParentRight="true"
        android:layout_alignBottom="@+id/windmill_big"/>

</RelativeLayout>

```


如果帮到您了请点击star 哦，谢谢了！


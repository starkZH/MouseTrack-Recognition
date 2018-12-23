# MouseTrack-Recognition
Through a simple way to recognize the track of mouse
<br>一种简单的鼠标轨迹识别方案
## 演示GIF
<img src="https://img1.doubanio.com/view/note/raw/qq2hGSlv5IC7IWPZOLZBZg/135224369/x56765319.jpg">


## 配置文件
>  {    "track":[<br>
        {"trackImage":"d:/0/0.png", <br>
        "description":"下一张", <br>
        "function":"NEXT",<br>"scene":0,<br>"direction":[[0,1],[1,0]]}
    ]   }
<br>
### 配置说明:
>* trackImage : 鼠标轨迹图片
>* description : 鼠标轨迹描述，将在程序下方提示
>* function : 要执行的功能编号
>* scene : 使用场景(暂时不用)
>* direction : 鼠标的轨迹的移动方向
### 轨迹图片样例
<img src="https://img3.doubanio.com/view/note/l/zZY5_coJMHOYyNx6x8PBlw/135224369/x56764735.jpg">


## 识别原理
>1. 不断获取鼠标坐标，并与上一次的比较，若大于则记为1，小于为-1，等于则为0，然后记录下来，若比较结果与上次相同则忽略。如前次坐标(3,5)，此次坐标(5,4),则将结果记为(1,-1)，这时我们便知道鼠标是向右下方滑动的。在比较时会有一个误差范围，因为用户滑动时的轨迹几乎总是曲线。
>2. 通过第一步，可以初步判断出与用户的滑动轨迹吻合的轨迹种类，此时还无法准确判断出是哪种轨迹，还需要通过与轨迹图片进行相似度对比，若相似度大于阈值，则可得出轨迹的种类。
>* 图片相似度使用了均值哈希算法进行计算
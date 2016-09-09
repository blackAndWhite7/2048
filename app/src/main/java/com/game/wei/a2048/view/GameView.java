package com.game.wei.a2048.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.GridLayout;

import com.game.wei.a2048.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/1 0001.
 */
//GridLayout是一个布局，它大大简化了对复杂布局的处理，提高了性能。
// 他直接继承自ViewGroup，和LinearLayout这种是类似的。
// 类似九宫格这种可以采用GridView。
// 但是像Android系统自带的计算器的界面就不可能通过GridView实现，
// 因为有些按键不一样大，如果使用GridLayout实现那就很简单了。

public class GameView extends GridLayout {

    public void setTarget_score(int target_score) {
        this.target_score = target_score;
    }

    private int target_score = 2048;
    private int current_score = 0;

    private Context context;
    private MainActivity activity;
    private boolean can_revert = false;
    private int pre_score;
    private SharedPreferences sp;
    private int row_number = 4;
    private int column_number = 4;

    List<Point> blankNumberItem;
    NumberItem[][] itemMatrix; //一个NumberItem类型的二维数组，存放棋盘上所有的NumerItem
    int[][] history_matrix; //记录滑动前一次的每个NumberItem的数字值

    public GameView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        activity = (MainActivity) context;
        sp = context.getSharedPreferences("info", context.MODE_PRIVATE);
        row_number = sp.getInt("lineNumber", 4);
        column_number = row_number;
        initView(context);
    }

    private void initView(Context context) {
        this.removeAllViews();  //重新创建新的棋盘时，要把以前的棋盘界面干掉
        //      updateCurrentScore(0);  //重新创建新的棋盘时,把score重置为0
        // 如果把这条语句写在这里，老是报空指针异常，真搞不懂为什么，所以把它放在了handleNext中

        setRowCount(row_number);
        setColumnCount(column_number);
        history_matrix = new int[row_number][column_number];
        //初始化这个数组，把数组大小确定
        itemMatrix = new NumberItem[row_number][column_number];
        //记录当前棋盘上，空白位置的一个数组,放一个（x,y）表示空白的棋盘上的每一个NumberItem的行和列
        blankNumberItem = new ArrayList<Point>();
        //获得屏幕的宽度，来确定每一个NumberItem的大小
        WindowManager windowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;

        for (int i = 0; i < row_number; i++) {
            for (int j = 0; j < column_number; j++) {
                NumberItem numberItem = new NumberItem(context);
                //初始化这个数组，把new出来的具体的 numerItem全部放入这个数组中，维持所有NumberItem的引用
                itemMatrix[i][j] = numberItem;
                //初始化blankNumberItem 放一个（x,y）表示空白的棋盘上的每一个NumberItem的行号和列号
                Point p = new Point();
                p.x = i;
                p.y = j;
                blankNumberItem.add(p);
                //注意：在Android代码中出现的控件不带单位的宽高，默认单位是像素px
                addView(numberItem, widthPixels / row_number, widthPixels / row_number);
            }
        }
        //随机找个位置，去显示一个不为0的数字
        addRandomNumber();
        addRandomNumber();
    }

    public void setRowNumber(int row) {
        this.row_number = row;
    }

    public void setColumnNumber(int column) {
        this.column_number = column;
    }

    private void addRandomNumber() {
        //每次随机找位置之前，先更新一下棋盘，只能在0的位置上添加新的NumberItem
        updateBlankNumberItem();

        //要求是在棋盘上，没有被占用的地方随机找一个位置
        int size = blankNumberItem.size();
        int random_locaton = (int) Math.floor(Math.random() * size);
        Point point = blankNumberItem.get(random_locaton);
       /* int x = point.x;
        int y = point.y;*/
        NumberItem numberItem = itemMatrix[point.x][point.y];
        //numberItem.setText("2");
        //这个函数同时设定具体的int型的值和显示的String型的值，还同时设定不同值时的背景颜色
        numberItem.setNumber(4);

    }

    private void updateBlankNumberItem() {
        blankNumberItem.clear();
        for (int i = 0; i < row_number; i++)
            for (int j = 0; j < column_number; j++) {
                int number = itemMatrix[i][j].getNumber();
                if (number == 0) {
                    blankNumberItem.add(new Point(i, j));
                }
            }
    }

    int start_x;
    int start_y;
    int end_x;
    int end_y;

    //这里会调用 OnTouchListener()的onTouch  callback
    //检测用户在GameView上的上下左右滑动事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                start_x = (int) event.getX();
                start_y = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                end_x = (int) event.getX();
                end_y = (int) event.getY();

                judgeDirection(); //判断是上下左右滑动事件
                updateCurrentScore(current_score); //更新当前的分数
                activity.updateRecordScore(current_score); //更新记录
                handleNext(isOver());  //判断游戏状态

                break;
        }
        //返回false，表示后续的事件不需要处理，系统不会将后续的事件报给当前的控件
        //必须返回true才可以
        return true;
    }

    public void updateCurrentScore(int current_score) {
        this.current_score = current_score;
        activity.setScore(current_score);
    }

    public void restart() {
        initView(context);
        updateCurrentScore(0);  //重新创建新的棋盘时,把score重置为0
    }


    //1:正常状态  0：win  -1:fail
    public int isOver() {
        for (int i = 0; i < row_number; i++) {
            for (int j = 0; j < column_number; j++) {
                if (itemMatrix[i][j].getNumber() == target_score) {
                    return 0;
                }
            }
        }
        //水平方向是否有可以合并的
        int pre_number = -1;
        for (int i = 0; i < row_number; i++) {
            for (int j = 0; j < column_number; j++) {
                int number = itemMatrix[i][j].getNumber();
                if (pre_number != -1) {
                    if (pre_number == number) {
                        return 1;//存在可以合并的两个item
                    }
                }
                pre_number = number;
            }
        }

        //上下方向是否有可以合并的
        pre_number = -1;
        for (int i = 0; i < column_number; i++) {
            for (int j = 0; j < row_number; j++) {
                int number = itemMatrix[j][i].getNumber();
                if (pre_number != -1) {
                    if (pre_number == number) {
                        return 1;//存在可以合并的两个item
                    }
                }
                pre_number = number;
            }
        }


        updateBlankNumberItem();
        if (blankNumberItem.size() == 0) {
            return -1;
        }

        return 1;
    }

    //去处理下一步该怎么做
    private void handleNext(int over) {
        if (over == 0) {  //win
            activity.updateRecordScore(current_score);//当win的时候，把score赋值给record，用这个函数去判断

            new AlertDialog.Builder(context)
                    .setTitle("恭喜你")
                    .setMessage("成功完成任务，接下来？")
                    .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.finish();
                        }
                    })
                    .setNegativeButton("继续刷分", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            target_score = Integer.MAX_VALUE;

                        }
                    })
                    .show();

        } else if (over == -1) {  //fail
            new AlertDialog.Builder(context)
                    .setTitle("很遗憾")
                    .setMessage("任务失败，再来一局？")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            restart();  //重新开始新的一局
                        }
                    })
                    .setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.finish();
                        }
                    })
                    .show();

        } else {  //正常状态
            //棋盘有变化，can_slide会变成true，并且有空位，就会创建新的数字
            updateBlankNumberItem();

            if (can_slide && blankNumberItem.size() != 0) {
                //每次滑动后随机产生一个新的数字
                addRandomNumber();
                can_slide = false;
            }
        }
    }

    private void judgeDirection() {
        int dx = Math.abs(end_x - start_x);
        int dy = Math.abs(end_y - start_y);

        saveToHistory();//滑动前保存状态，以便来revert
        int[][] a = new int[row_number][row_number];
        for (int i = 0; i < row_number; i++)
            for (int j = 0; j < column_number; j++) {
                a[i][j] = itemMatrix[i][j].getNumber();
            }

        if (dx > dy) {   //水平方向滑动
            if (end_x - start_x > 0) {   //右滑
                slideRight();
            } else {   //左滑
                slideLeft();
            }
        } else {   //竖直方向滑动
            if (end_y - start_y > 0) {   //下滑
                slideDown();
            } else {   //上滑
                slideUp();
            }
        }

        // 这连个for循环是判断当往一个方向滑动，不能合并时，即使有很多空白区域，也不能产生随机数字，需要往别的方向滑动
        int[][] b = new int[row_number][row_number];
        for (int i = 0; i < row_number; i++)
            for (int j = 0; j < column_number; j++) {
                b[i][j] = itemMatrix[i][j].getNumber();
            }

        for (int i = 0; i < row_number; i++)
            for (int j = 0; j < column_number; j++) {
                if (a[i][j] != b[i][j]) {
                    can_slide = true;
                }
            }
        //滑动完后更新一下空的地方
        updateBlankNumberItem();
    }

    ///////////////////////////////////////////////
    boolean can_slide = false;

    private void slideLeft() {
        //  这一块是这个游戏的核心思想

        //来存放每一行左滑后，临时产生的不为0的数字
        ArrayList<Integer> temporary = new ArrayList<Integer>();

        for (int i = 0; i < row_number; i++) {
            int pre_number = -1;
            //拿到每一行的数字
            for (int j = 0; j < column_number; j++) {
                int currentItemNumber = itemMatrix[i][j].getNumber();
                if (currentItemNumber == 0) {
                    continue;
                } else {
                    if (currentItemNumber == pre_number) {  //如果当前number和前一个一样的时候，合并
                        temporary.add(currentItemNumber * 2);
                        current_score += currentItemNumber * 2; //只要合并，就给它加上对应的分数
                        pre_number = -1;
                    } else {  //如果当前number和前一个不一样的时候
                        if (pre_number == -1) { //前一个是0或者前面刚合并
                            pre_number = currentItemNumber;
                        } else {  //前一个不是-1，当前number和前一个不一样
                            temporary.add(pre_number);
                            pre_number = currentItemNumber;
                        }
                    }
                }
            }
            //内层循环结束后，有些行的最后一个数字有可能加入不进去temporary，
            // （如某一行只有最后一个有不为0的数字,比如还有0,2,0,4  4就会加不进去）
            if (pre_number != -1) {
                temporary.add(pre_number);
            }
            //把temporary集合中的值(肯定不为0的值)，放入到最新的棋盘中
            for (int k = 0; k < temporary.size(); k++) {
                int new_number = temporary.get(k);
                itemMatrix[i][k].setNumber(new_number);

                //添加到这里，表示滑动后你的棋盘有变化（temporary！=0 就会执行到这里，就是有变化）就会随机产生新的数字，否则（即没有变化），就不会产生新的数字了
                //并不是上面这句话说的这样，就算棋盘没有变化，只要有不为0的数字，temporary.size就一定不会为0.
                //所以这条语句加在这里是不对的
                // can_slide = true;
            }

            //i行中剩下的部分，用0来填充
            for (int p = temporary.size(); p < column_number; p++) {
                itemMatrix[i][p].setNumber(0);
            }
            //把这一层i循环的temporary清空，以便下一轮使用
            temporary.clear();
        }

    }

    private void slideRight() {
        //来存放每一行右滑后，临时产生的不为0的数字
        ArrayList<Integer> temporary = new ArrayList<Integer>();

        for (int i = 0; i < row_number; i++) {
            int pre_number = -1;
            //拿到每一行的数字
            for (int j = column_number - 1; j >= 0; j--) {
                int currentItemNumber = itemMatrix[i][j].getNumber();
                if (currentItemNumber == 0) {
                    continue;
                } else {
                    if (currentItemNumber == pre_number) {  //如果当前number和前一个一样的时候，合并
                        temporary.add(currentItemNumber * 2);
                        current_score += currentItemNumber * 2; //只要合并，就给它加上对应的分数
                        pre_number = -1;
                    } else {  //如果当前number和前一个不一样的时候
                        if (pre_number == -1) { //前一个是0或者前面刚合并
                            pre_number = currentItemNumber;
                        } else {  //前一个不是-1，当前number和亲一个不一样
                            temporary.add(pre_number);
                            pre_number = currentItemNumber;
                        }
                    }
                }
            }
            //内层循环结束后，有些行的最后一个数字有可能加入不进去temporary，（如某一行只有最后一个有不为0的数字）
            if (pre_number != -1) {
                temporary.add(pre_number);
            }

            //把temporary集合中的值(肯定不为0的值)，放入到最新的棋盘中
            for (int k = 0; k < temporary.size(); k++) {
                int new_number = temporary.get(k);
                itemMatrix[i][column_number - 1 - k].setNumber(new_number);
            }

            //i行中剩下的部分，用0来填充
            for (int p = column_number - 1 - temporary.size(); p >= 0; p--) {
                itemMatrix[i][p].setNumber(0);
            }
            //把这一层i循环的temporary清空，以便下一轮使用
            temporary.clear();
        }
    }

    private void slideUp() {
        // 来存放每一列上滑后，临时产生的不为0的数字
        ArrayList<Integer> temporary = new ArrayList<Integer>();

        for (int i = 0; i < column_number; i++) {
            int pre_number = -1;
            //拿到每一行的数字
            for (int j = 0; j < row_number; j++) {
                int currentItemNumber = itemMatrix[j][i].getNumber();
                if (currentItemNumber == 0) {
                    continue;
                } else {
                    if (currentItemNumber == pre_number) {  //如果当前number和前一个一样的时候，合并
                        temporary.add(currentItemNumber * 2);
                        current_score += currentItemNumber * 2; //只要合并，就给它加上对应的分数
                        pre_number = -1;
                    } else {  //如果当前number和前一个不一样的时候
                        if (pre_number == -1) { //前一个是0或者前面刚合并
                            pre_number = currentItemNumber;
                        } else {  //前一个不是-1，当前number和亲一个不一样
                            temporary.add(pre_number);
                            pre_number = currentItemNumber;
                        }
                    }
                }
            }
            //内层循环结束后，有些行的最后一个数字有可能加入不进去temporary，（如某一行只有最后一个有不为0的数字）
            if (pre_number != -1) {
                temporary.add(pre_number);
            }
            //把temporary集合中的值(肯定不为0的值)，放入到最新的棋盘中
            for (int k = 0; k < temporary.size(); k++) {
                int new_number = temporary.get(k);
                itemMatrix[k][i].setNumber(new_number);
            }

            //i行中剩下的部分，用0来填充
            for (int p = temporary.size(); p < row_number; p++) {
                itemMatrix[p][i].setNumber(0);
            }
            //把这一层i循环的temporary清空，以便下一轮使用
            temporary.clear();
        }

    }

    private void slideDown() {
        //来存放每一列下滑后，临时产生的不为0的数字
        ArrayList<Integer> temporary = new ArrayList<Integer>();

        for (int i = 0; i < column_number; i++) {
            int pre_number = -1;
            //拿到每一行的数字
            for (int j = row_number - 1; j >= 0; j--) {
                int currentItemNumber = itemMatrix[j][i].getNumber();
                if (currentItemNumber == 0) {
                    continue;
                } else {
                    if (currentItemNumber == pre_number) {  //如果当前number和前一个一样的时候，合并
                        temporary.add(currentItemNumber * 2);
                        current_score += currentItemNumber * 2; //只要合并，就给它加上对应的分数
                        pre_number = -1;
                    } else {  //如果当前number和前一个不一样的时候
                        if (pre_number == -1) { //前一个是0或者前面刚合并
                            pre_number = currentItemNumber;
                        } else {  //前一个不是-1，当前number和亲一个不一样
                            temporary.add(pre_number);
                            pre_number = currentItemNumber;
                        }
                    }
                }
            }
            //内层循环结束后，有些行的最后一个数字有可能加入不进去temporary，（如某一行只有最后一个有不为0的数字）
            if (pre_number != -1) {
                temporary.add(pre_number);
            }
            //把temporary集合中的值(肯定不为0的值)，放入到最新的棋盘中
            for (int k = 0; k < temporary.size(); k++) {
                int new_number = temporary.get(k);
                itemMatrix[row_number - 1 - k][i].setNumber(new_number);
            }

            //i行中剩下的部分，用0来填充
            for (int p = row_number - 1 - temporary.size(); p >= 0; p--) {
                itemMatrix[p][i].setNumber(0);
            }
            //把这一层i循环的temporary清空，以便下一轮使用
            temporary.clear();
        }

    }

    // 撤销到上一次滑动前棋盘的样子，注意：分数也要撤销到上一步
    public void revert() {
        if (can_revert) {  //can_revert为true的时候才可以执行撤销，防止还没滑动前就撤销，在saveToHistory后才置为true
            for (int i = 0; i < row_number; i++)
                for (int j = 0; j < column_number; j++) {
                    itemMatrix[i][j].setNumber(history_matrix[i][j]);
                }
            updateCurrentScore(pre_score); // 把分数也撤销回去
        }
    }

    // 在滑动前保存每个NumberItem的数字值给history_matrix,这里会有bug，
    // 就是刚进入界面是，你点击revert，会把棋盘刚初始化的两个数字置为空
    public void saveToHistory() {
        for (int i = 0; i < row_number; i++)
            for (int j = 0; j < column_number; j++) {
                history_matrix[i][j] = itemMatrix[i][j].getNumber();
            }
        pre_score = current_score;
        can_revert = true;//这里解决了bug
    }

}

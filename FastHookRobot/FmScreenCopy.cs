using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace FastHook
{
    //声明委托 和 事件
    public delegate void TransfDelegate(int x, int y, int width, int height, Bitmap bmp);

    public partial class FmScreenCopy : Form
    {
        public FmScreenCopy()
        {
            InitializeComponent();
        }

        public FmScreenShot fmshot;

        #region 初始化相关准备参数
        // 委托事件
        public event TransfDelegate TransfEvent;

        // 用来记录鼠标按下的坐标，用来确定绘图起点
        private Point downPoint;

        // 用来表示是否截图完成
        private bool catchFinish = false;

        // 用来表示截图开始
        private bool catchStart = false;

        // 用来保存原始图像
        private Bitmap oriBmp;

        // 用来保存截图的矩形
        private Rectangle catchRec;

        // 结束点，保持点位始终在左上角
        private Point endPoint;
        #endregion

        private void FmScreenCopy_Load(object sender, EventArgs e)
        {
            oriBmp = new Bitmap(this.BackgroundImage);  // 获取初始图片
        }

        /// <summary>
        /// 鼠标左键点击开始截图事件
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void FmScreenCopy_MouseDown(object sender, MouseEventArgs e)
        {
            // 鼠标左键按下是开始画图，也就是截图
            if (e.Button == MouseButtons.Left)
            {
                // 如果捕捉没有开始
                if (!catchStart)
                {
                    catchStart = true;
                    // 保存此时鼠标按下坐标，经实验这里最好使用Control.MousePosition.X和Control.MousePosition.Y
                    downPoint = new Point(Control.MousePosition.X, Control.MousePosition.Y);                    
                }
            }
        }

        /// <summary>
        /// 鼠标移动事件
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void FmScreenCopy_MouseMove(object sender, MouseEventArgs e)
        {
            // 确保截图开始
            if (catchStart)
            {
                // 新建一个图片对象，让它与屏幕图片相同
                Bitmap copyBmp = (Bitmap)oriBmp.Clone();
                // 获取鼠标按下的坐标
                endPoint = new Point(downPoint.X, downPoint.Y);
                // 新建画板和画笔
                Graphics g = Graphics.FromImage(copyBmp);
                Pen p = new Pen(Color.Red, 1);
                // 获取矩形的长宽
                int width = Math.Abs(Control.MousePosition.X - downPoint.X);
                int height = Math.Abs(Control.MousePosition.Y - downPoint.Y);
                if (Control.MousePosition.X < downPoint.X)
                {
                    endPoint.X = Control.MousePosition.X;
                }
                if (Control.MousePosition.Y < downPoint.Y)
                {
                    endPoint.Y = Control.MousePosition.Y;
                }
                catchRec = new Rectangle(endPoint, new Size(width, height));         
                // 将矩形画在画板上
                g.DrawRectangle(p, catchRec);
                // 释放目前的画板
                g.Dispose();
                p.Dispose();
                // 从当前窗体创建新的画板
                Graphics g1 = this.CreateGraphics();
                // 将刚才所画的图片画到截图窗体上
                // 为什么不直接在当前窗体画图呢？
                // 如果自己解决将矩形画在窗体上，会造成图片抖动并且有无数个矩形
                // 这样实现也属于二次缓冲技术
                g1.DrawImage(copyBmp, new Point(0, 0));
                g1.Dispose();
                // 释放拷贝图片，防止内存被大量消耗
                copyBmp.Dispose();
            }
        }

        /// <summary>
        /// 鼠标抬起事件
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void FmScreenCopy_MouseUp(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Left)
            {
                // 如果截图已经开始，鼠标左键弹起设置截图完成
                if (catchStart)
                {
                    catchStart = false;
                    catchFinish = true;
                }
            }
        }

        /// <summary>
        /// 鼠标右键点击结束截图
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void FmScreenCopy_MouseClick(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Right)
            {
                this.DialogResult = DialogResult.OK;
                this.Close();
            }
        }

        /// <summary>
        /// 双击左键确认截图
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void FmScreenCopy_MouseDoubleClick(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Left && catchFinish)
            {
                // 新建一个与矩形一样大小的空白图片
                if (catchRec.Width > 0 && catchRec.Height > 0)
                {
                    Bitmap catchedBmp = new Bitmap(catchRec.Width, catchRec.Height);
                    Graphics g = Graphics.FromImage(catchedBmp);
                    // 把oriBmp中指定部分按照指定大小画到空白图片上
                    // catchRec指定originBmp中指定部分
                    // 第二个参数指定绘制到空白图片的位置和大小
                    // 画完后catchedBmp不再是空白图片了，而是具有与截取的图片一样的内容
                    g.DrawImage(oriBmp, new Rectangle(0, 0, catchRec.Width, catchRec.Height), catchRec, GraphicsUnit.Pixel);
                    // 将图片传递给事件
                    TransfEvent(endPoint.X, endPoint.Y, catchedBmp.Width, catchedBmp.Height, catchedBmp);
                }
                catchFinish = false;
                this.BackgroundImage = oriBmp;
                this.DialogResult = DialogResult.OK;                          
            }
        }







    }
}

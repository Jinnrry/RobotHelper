using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.IO;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using Tesseract;

namespace FastHook
{
    public partial class FmScreenShot : Form
    {
        public FmScreenShot()
        {
            InitializeComponent();

            //开启双缓存避免闪烁
            this.DoubleBuffered = true;
        }

        string findcolorRegionAdd50 = "";//找色区域外扩展50
        FormTmp formtmp;
        FormLDRecordConvert formLdConvert;

        int cutPicX = 0;
        int cutPicY = 0;
        int cutPicWidth = 0;
        int cutPicHeight = 0;

        public int screenX = 720;
        public int screenY = 1280;


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
        private Rectangle catchRecFill;

        // 结束点，保持点位始终在左上角
        private Point endPoint;

        private Point lastPoint = new Point(0, 0);


        public List<PictureBox> piclist = new List<PictureBox>();

        TesseractEngine ocrCn;
        TesseractEngine ocrEn;

        Bitmap cutBmp=null;


        private void btnScreenShot_Click(object sender, EventArgs e)
        {
            formtmp.ShowDialog();
        }

        /// <CIE 1976 L*a*b*色差公式>
        /// </summary>
        /// <param name="L1"></param>
        /// <param name="a1"></param>
        /// <param name="b1"></param>
        /// <param name="L2"></param>
        /// <param name="a2"></param>
        /// <param name="b2"></param>
        /// <returns></returns>

        public double delta_Eab(double L1, double a1, double b1, double L2, double a2, double b2)
        {
            double Eab = 0;             //  △Eab
            double chafang_L = 0;             //  (L1-L2)*(L1-L2)
            double chafang_a = 0;             //   (a1-a2)*(a1-a2)
            double chafang_b = 0;             //   (b1-b2)*(b1-b2)

            chafang_L = (L1 - L2) * (L1 - L2);      //差-方
            chafang_a = (a1 - a2) * (a1 - a2);
            chafang_b = (b1 - b2) * (b1 - b2);

            Eab = Math.Pow(chafang_L + chafang_a + chafang_b, 0.5);

            return Eab;
        }

        /// <CIE DE 2000色差公式>
        /// </summary>
        /// <param name="L1"></param>
        /// <param name="a1"></param>
        /// <param name="b1"></param>
        /// <param name="L2"></param>
        /// <param name="a2"></param>
        /// <param name="b2"></param>
        /// <returns></returns>

        public double delta_E00(double L1, double a1, double b1, double L2, double a2, double b2)
        {
            //参考《现代颜色技术原理及应用》P88数据
            double E00 = 0;               //CIEDE2000色差E00
            double LL1, LL2, aa1, aa2, bb1, bb2; //声明L' a' b' （1,2）
            double delta_LL, delta_CC, delta_hh, delta_HH;        // 第二部的四个量
            double kL, kC, kH;
            double RT = 0;                //旋转函数RT
            double G = 0;                  //G表示CIELab 颜色空间a轴的调整因子,是彩度的函数.
            double mean_Cab = 0;    //两个样品彩度的算术平均值
            double SL, SC, SH, T;
            //------------------------------------------
            //参考实验条件见P88
            kL = 1;
            kC = 1;
            kH = 1;
            //------------------------------------------
            mean_Cab = (CaiDu(a1, b1) + CaiDu(a2, b2)) / 2;
            double mean_Cab_pow7 = Math.Pow(mean_Cab, 7);       //两彩度平均值的7次方
            G = 0.5 * (1 - Math.Pow(mean_Cab_pow7 / (mean_Cab_pow7 + Math.Pow(25, 7)), 0.5));

            LL1 = L1;
            aa1 = a1 * (1 + G);
            bb1 = b1;

            LL2 = L2;
            aa2 = a2 * (1 + G);
            bb2 = b2;

            double CC1, CC2;               //两样本的彩度值
            CC1 = CaiDu(aa1, bb1);
            CC2 = CaiDu(aa2, bb2);
            double hh1, hh2;                  //两样本的色调角
            hh1 = SeDiaoJiao(aa1, bb1);
            hh2 = SeDiaoJiao(aa2, bb2);

            delta_LL = LL1 - LL2;
            delta_CC = CC1 - CC2;
            delta_hh = SeDiaoJiao(aa1, bb1) - SeDiaoJiao(aa2, bb2);
            delta_HH = 2 * Math.Sin(Math.PI * delta_hh / 360) * Math.Pow(CC1 * CC2, 0.5);

            //-------第三步--------------
            //计算公式中的加权函数SL,SC,SH,T
            double mean_LL = (LL1 + LL2) / 2;
            double mean_CC = (CC1 + CC2) / 2;
            double mean_hh = (hh1 + hh2) / 2;

            SL = 1 + 0.015 * Math.Pow(mean_LL - 50, 2) / Math.Pow(20 + Math.Pow(mean_LL - 50, 2), 0.5);
            SC = 1 + 0.045 * mean_CC;
            T = 1 - 0.17 * Math.Cos((mean_hh - 30) * Math.PI / 180) + 0.24 * Math.Cos((2 * mean_hh) * Math.PI / 180)
                  + 0.32 * Math.Cos((3 * mean_hh + 6) * Math.PI / 180) - 0.2 * Math.Cos((4 * mean_hh - 63) * Math.PI / 180);
            SH = 1 + 0.015 * mean_CC * T;

            //------第四步--------
            //计算公式中的RT
            double mean_CC_pow7 = Math.Pow(mean_CC, 7);
            double RC = 2 * Math.Pow(mean_CC_pow7 / (mean_CC_pow7 + Math.Pow(25, 7)), 0.5);
            double delta_xita = 30 * Math.Exp(-Math.Pow((mean_hh - 275) / 25, 2));        //△θ 以°为单位
            RT = -Math.Sin((2 * delta_xita) * Math.PI / 180) * RC;

            double L_item, C_item, H_item;
            L_item = delta_LL / (kL * SL);
            C_item = delta_CC / (kC * SC);
            H_item = delta_HH / (kH * SH);

            E00 = Math.Pow(L_item * L_item + C_item * C_item + H_item * H_item + RT * C_item * H_item, 0.5);

            return E00;
        }
        //彩度计算
        private double CaiDu(double a, double b)
        {
            double Cab = 0;
            Cab = Math.Pow(a * a + b * b, 0.5);
            return Cab;
        }
        //色调角计算
        private double SeDiaoJiao(double a, double b)
        {
            double h = 0;
            double hab = 0;

            h = (180 / Math.PI) * Math.Atan(b / a);           //有正有负

            if (a > 0 && b > 0)
            {
                hab = h;
            }
            else if (a < 0 && b > 0)
            {
                hab = 180 + h;
            }
            else if (a < 0 && b < 0)
            {
                hab = 180 + h;
            }
            else     //a>0&&b<0
            {
                hab = 360 + h;
            }
            return hab;
        }


        private double CompareColorx(Color a, Color b)
        {
            try
            {
                byte[] RGB1 = { a.R, a.G, a.B };
                byte[] RGB2 = { b.R, b.G, b.B };
                double[] Lab1 = ColorUtil.ConvertRGBToLab(RGB1);
                double[] Lab2 = ColorUtil.ConvertRGBToLab(RGB2);
                double difa = delta_E00(Lab1[0], Lab1[1], Lab1[2], Lab2[0], Lab2[1], Lab2[2]);
                return difa;

                //var hsv1 = new HSV(a.R, a.G, a.B);
                //var hsv2 = new HSV(b.R, b.G, b.B);
                //return ColorUtil.DistanceOf(hsv1, hsv2);
            }
            catch
            {
                return -1;
            }
        }


        List<CmpColor> colorList;
        List<List<CmpColor>> colorGroup;
        int cmpColorVal = 20;//6-26

        bool compHSVFlag = true;

        string currentRegion = "";
        private void genColorGroup()
        {



            currentRegion = cutPicX + "," + cutPicY + "," + (cutPicX + cutPicWidth) + "," + (cutPicY + cutPicHeight);

            Bitmap bmp = (Bitmap)pictureBoxShow.Image;
            colorList = new List<CmpColor>();
            colorGroup = new List<List<CmpColor>>();
            String[] picPoints = currentRegion.Split(',');
            int picX = int.Parse(picPoints[0]);
            int picY = int.Parse(picPoints[1]);
            for (int i = 0; i < bmp.Width; i += 2)
            {
                for (int j = 0; j < bmp.Height; j += 2)
                {
                    CmpColor clor = new CmpColor();
                    clor.p = new Point(i, j);
                    clor.srcPoint = new Point(i + picX, j + picY);
                    clor.cmpColor = bmp.GetPixel(i, j);

                    if (compHSVFlag)
                    {
                        var hsv1 = new HSV(clor.cmpColor.R, clor.cmpColor.G, clor.cmpColor.B);
                        if (((hsv1.S > 0.3) || (hsv1.S < 0.005)) && hsv1.V > 0.3)
                        {
                            colorList.Add(clor);
                        }
                    }
                    else
                    {
                        colorList.Add(clor);
                    }
                }
            }
            if (colorList.Count == 0)
            {
                return;
            }
            colorList.Sort();
            List<CmpColor> groupOne = new List<CmpColor>();
            groupOne.Add(colorList[0]);
            colorGroup.Add(groupOne);
            Color cmpScolor;
            bool cmpOk = false;
            for (int j = 1; j < colorList.Count; j++)
            {
                Color cmpDcolor = colorList[j].cmpColor;
                cmpOk = false;
                for (int x = 0; x < colorGroup.Count; x++)
                {
                    cmpScolor = colorGroup[x][0].cmpColor;
                    double sc = CompareColorx(cmpScolor, cmpDcolor);

                    //var hsv1 = new HSV(cmpScolor.R, cmpScolor.G, cmpScolor.B);
                    //var hsv2 = new HSV(cmpDcolor.R, cmpDcolor.G, cmpDcolor.B);
                    //double hdifval = Math.Abs(hsv1.H - hsv2.H);
                    //if (sc < cmpColorVal && (hdifval<50 || hdifval>325))

                    if (sc < cmpColorVal)
                    {
                        colorGroup[x].Add(colorList[j]);
                        cmpOk = true;
                        break;
                    }
                }
                //每个组都超出范围 就新创建组
                if (!cmpOk)
                {
                    List<CmpColor> addGroup = new List<CmpColor>();
                    addGroup.Add(colorList[j]);
                    colorGroup.Add(addGroup);
                }
            }
            StringBuilder sbinfo = new StringBuilder();

            sbinfo.Append("总颜色数量：").Append(colorList.Count).Append(Environment.NewLine).Append("总颜色分组数量：").Append(colorGroup.Count).Append(Environment.NewLine);
            int sumx = 0;
            List<CmpColor> colorListGroupSort = new List<CmpColor>(); ;
            for (int i = 0; i < colorGroup.Count; i++)
            {
                sbinfo.Append("分组：" + (i + 1) + "颜色数量：" + colorGroup[i].Count).Append(Environment.NewLine);
                sumx += colorGroup[i].Count;
                for (int j = 0; j < colorGroup[i].Count; j++)
                {
                    colorListGroupSort.Add(colorGroup[i][j]);
                }
                colorGroup[i].Sort();
            }
            //sbinfo.Append("所有分组加起来数量：" + sumx);
            this.txtResult.Text = sbinfo.ToString();

            //画出分组后的颜色块
            //Bitmap bmpx = new Bitmap(a.Width, a.Height);
            Bitmap bmpx = new Bitmap(this.pictureBox4.Width, this.pictureBox4.Height);
            int colorIndex;

            colorGroup.Sort(new SortColorList());
            int avgWidth = bmpx.Width / colorGroup.Count;
            for (int g = 0; g < colorGroup.Count; g++)
            {
                colorIndex = -1;
                int startWidth = avgWidth * g;
                for (int i = startWidth; i < startWidth + avgWidth; i++)
                {
                    for (int j = 0; j < this.pictureBox4.Height; j++)
                    {
                        colorIndex++;
                        if (colorIndex < colorGroup[g].Count)
                        {
                            Color c = colorGroup[g][colorIndex].cmpColor;
                            bmpx.SetPixel(i, j, c);
                        }
                    }
                }
            }

            this.pictureBox4.Image = bmpx;
        }


        string[] colorIndexStr = { "➊", "➋", "➌", "➍", "➎", "➏", "➐", "➑", "➒", "➓" };
        Random random = new Random();
        private void showColorsCard()
        {
            while (true)
            {
                genColorGroup();
                if (colorGroup.Count > 17)
                {
                    cmpColorVal++;
                    genColorGroup();
                }
                else if (colorGroup.Count < 7)
                {
                    cmpColorVal--;
                    genColorGroup();
                }
                if ((colorGroup.Count > 6 && colorGroup.Count < 18) || cmpColorVal > 26 || cmpColorVal < 7)
                {
                    break;
                }
            }

            if (colorGroup.Count < 7)
            {
                compHSVFlag = false;
                cmpColorVal = 6;
                while (true)
                {
                    genColorGroup();
                    if (colorGroup.Count > 17)
                    {
                        cmpColorVal++;
                        genColorGroup();
                    }
                    else if (colorGroup.Count < 7)
                    {
                        cmpColorVal--;
                        genColorGroup();
                    }
                    if ((colorGroup.Count > 6 && colorGroup.Count < 18) || cmpColorVal > 26 || cmpColorVal < 4)
                    {
                        break;
                    }
                }
            }


            if (colorGroup.Count == 0)
            {
                return;
            }

            Font ft = new Font("宋体", 20);
            List<Label> addLables = new List<Label>();
            List<List<CmpColor>> genColorList = new List<List<CmpColor>>();
            ToolTip ttpSettings = new ToolTip();
            ttpSettings.InitialDelay = 200;
            ttpSettings.AutoPopDelay = 10 * 1000;
            ttpSettings.ReshowDelay = 200;
            ttpSettings.ShowAlways = true;
            ttpSettings.IsBalloon = true;

            //String[] picPoints = currentRegion.Split(',');
            //int picX = int.Parse(picPoints[0]);
            //int picY = int.Parse(picPoints[1]);
            int groupCount = int.Parse(textBox4.Text);
            if (groupCount > 10)
            {
                groupCount = 10;
            }
            else if (groupCount < 1)
            {
                groupCount = 1;
            }
            for (int x = 0; x < groupCount; x++)
            {
                List<CmpColor> lt = new List<CmpColor>();
                int top = x * 35 + 10;
                for (int i = 0; i < colorGroup.Count; i++)
                {
                    if (i > 9)
                    {
                        break;
                    }
                    int left = i * 20 + 10;

                    Label label = new Label();
                    label.Size = new Size(25, 25);
                    label.Font = ft;
                    label.Text = colorIndexStr[i];
                    int selColorIndex = random.Next(colorGroup[i].Count);
                    CmpColor tmpColor = colorGroup[i][selColorIndex];
                    CmpColor clor = colorGroup[i][selColorIndex];
                    string tmpTip = clor.srcPoint.X + "," + clor.srcPoint.Y + "\t" + clor.BGR16() + "\r\n" + "RGB(" + clor.RGB() + ")";
                    ttpSettings.SetToolTip(label, tmpTip);
                    int b = 0;
                    while (true)
                    {
                        b++;
                        int notSameFlag = 0;
                        for (int g = 0; g < x; g++)
                        {
                            if (genColorList[g][i].cmpColor == tmpColor.cmpColor)
                            {
                                //tmpColor = colorGroup[i][random.Next(colorGroup[i].Count / 2 + 1)].cmpColor;
                                selColorIndex = random.Next(colorGroup[i].Count);
                                tmpColor = colorGroup[i][selColorIndex];
                                clor = colorGroup[i][selColorIndex];
                                tmpTip = clor.srcPoint.X + "," + clor.srcPoint.Y + "\t" + clor.BGR16() + "\r\n" + "RGB(" + clor.RGB() + ")";
                                ttpSettings.SetToolTip(label, tmpTip);
                                break;
                            }
                            else
                            {
                                notSameFlag++;
                            }
                        }
                        if (notSameFlag == x || colorGroup[i].Count < 10 || b > 50)
                        {
                            lt.Add(tmpColor);
                            break;
                        }
                    }

                    label.ForeColor = tmpColor.cmpColor;
                    label.Location = new Point(left, top);
                    addLables.Add(label);
                }
                genColorList.Add(lt);
            }
            this.panel6.Controls.Clear();
            this.panel6.Controls.AddRange(addLables.ToArray());
            StringBuilder mulFindColorSb = new StringBuilder();
            StringBuilder mulCmpColorSb = new StringBuilder();
            //StringBuilder regionColorSb = new StringBuilder();


            if (genColorList.Count > 0)
            {
                mulFindColorSb.Append("\"");
                mulCmpColorSb.Append("\"");
                //regionColorSb.Append("\"");
            }

            //生成颜色代码
            //多点找色 颜色描述 "1FC5F4","22|22|000000,52|47|1FC5F4"
            //多点比色 颜色描述 "256|235|1FC5F4,305|271|1FC5F4"
            //区域找色 颜色描述 "1FC5F4|1FC5F4" 
            for (int i = 0; i < genColorList.Count; i++)
            {
                for (int j = 0; j < genColorList[i].Count; j++)
                {
                    CmpColor clor = genColorList[i][j];
                    //regionColorSb.Append(clor.BGR16());
                    mulCmpColorSb.Append(clor.srcPoint.X).Append("|").Append(clor.srcPoint.Y).Append("|").Append(clor.BGR16());
                    //if (textBox3.Text != "000000")
                    //{
                    //    //regionColorSb.Append("-").Append(textBox3.Text);
                    //    mulCmpColorSb.Append("-").Append(textBox3.Text);
                    //    mulFindColorSb.Append("-").Append(textBox3.Text);
                    //}
                    if (j == 0)
                    {
                        mulFindColorSb.Append(clor.BGR16());
                        //if (textBox3.Text != "000000")
                        //{
                        //    mulFindColorSb.Append("-").Append(textBox3.Text);
                        //}
                        //mulFindColorSb.Append("\",\"");
                        mulFindColorSb.Append(",");
                    }
                    else
                    {
                        int desX = clor.srcPoint.X - genColorList[i][0].srcPoint.X;
                        int desY = clor.srcPoint.Y - genColorList[i][0].srcPoint.Y;
                        mulFindColorSb.Append(desX).Append("|").Append(desY).Append("|");
                        mulFindColorSb.Append(clor.BGR16());
                        //if (textBox3.Text != "000000")
                        //{
                        //    mulFindColorSb.Append("-").Append(textBox3.Text);
                        //}
                    }
                    if (j != genColorList[i].Count - 1)
                    {
                        //regionColorSb.Append("|");
                        mulCmpColorSb.Append(",");
                        if (j != 0)
                        {
                            mulFindColorSb.Append(",");
                        }
                    }
                }
                if (genColorList[i].Count > 0)
                {
                    mulFindColorSb.Append("\"");

                    mulCmpColorSb.Append("\"");
                    //regionColorSb.Append("\"");
                    //regionColorSb.Append(Environment.NewLine);
                    mulCmpColorSb.Append(Environment.NewLine);
                    mulFindColorSb.Append(Environment.NewLine);

                    if (i != genColorList.Count - 1)
                    {                        //if (i == 0)
                        //{

                        mulFindColorSb.Append("\"");
                        //    dmMulFindColorSb.Append("\"");
                        //}
                        mulCmpColorSb.Append("\"");
                        //regionColorSb.Append("\"");
                    }
                }
            }
            //[多点比色_颜色描述]
            textBox5.Text = mulCmpColorSb.ToString();
            textBox55.Text = mulCmpColorSb.ToString();
            textBox555.Text = mulCmpColorSb.ToString();
            //[多点找色_颜色描述]
            textBox8.Text = mulFindColorSb.ToString();
            textBox88.Text = mulFindColorSb.ToString();
            textBox888.Text = mulFindColorSb.ToString();

            //[区域找色_颜色描述]

            var cnOcrPage = ocrCn.Process(cutBmp);
            string cnOcrStr = cnOcrPage.GetText();
            cnOcrPage.Dispose();

            var enOcrPage = ocrEn.Process(cutBmp);
            string enOcrStr = enOcrPage.GetText();
            enOcrPage.Dispose();

            StringBuilder regionColorSb = new StringBuilder();
            if (!String.IsNullOrEmpty(cnOcrStr))
            {
                regionColorSb.Append("中文识别结果：" + cnOcrStr + Environment.NewLine);
            }
            if (!String.IsNullOrEmpty(enOcrStr))
            {
                regionColorSb.Append("英文识别结果：" + enOcrStr + Environment.NewLine);
            }
            textBox10.Text = regionColorSb.ToString();


            //生成代码
            string[] group1str = textBox5.Text.Split(Environment.NewLine.ToCharArray());
            string[] group2str = textBox8.Text.Split(Environment.NewLine.ToCharArray());
            //string[] group3str = textBox10.Text.Split(Environment.NewLine.ToCharArray());

            List<string> group11str = new List<string>();
            List<string> group22str = new List<string>();
            List<string> group33str = new List<string>();

            for (int i = 0; i < group1str.Length; i++)
            {
                if (!string.IsNullOrEmpty(group1str[i]))
                {
                    group11str.Add(group1str[i]);
                }
            }
            for (int i = 0; i < group2str.Length; i++)
            {
                if (!string.IsNullOrEmpty(group2str[i]))
                {
                    group22str.Add(group2str[i]);
                }
            }

            //for (int i = 0; i < group3str.Length; i++)
            //{
            //    if (!string.IsNullOrEmpty(group3str[i]))
            //    {
            //        group22str.Add(group3str[i]);
            //    }
            //}

            string str1 = formtmp.txtTmp1.Text;
            string str11 = formtmp.txtTmp11.Text;
            string str111 = formtmp.txtTmp111.Text;
            string str2 = formtmp.txtTmp2.Text;
            string str22 = formtmp.txtTmp22.Text;
            string str222 = formtmp.txtTmp222.Text;
            string str3 = formtmp.txtTmp3.Text;

            //string strDm1 = formtmp.txtTmpDm1.Text;
            //string strDm2 = formtmp.txtTmpDm2.Text;


            screenX = piclist[this.tabControl1.SelectedIndex].BackgroundImage.Width;
            screenY = piclist[this.tabControl1.SelectedIndex].BackgroundImage.Height;
            int crX1 = cutPicX - 50;
            int crY1 = cutPicY - 50;
            int crX2 = (cutPicX + cutPicWidth) + 50;
            int crY2 = (cutPicY + cutPicHeight) + 50;
            if (crX1 < 0)
            {
                crX1 = 0;
            }
            if (crX2 > screenX)
            {
                crX2 = screenX;
            }
            if (crY1 < 0)
            {
                crY1 = 0;
            }
            if (crY2 > screenY)
            {
                crY2 = screenY;
            }

            findcolorRegionAdd50 = crX1 + "," + crY1 + "," + crX2 + "," + crY2;


            for (int i = 0; i < group11str.Count; i++)
            {
                str1 = str1.Replace("[找色区域外扩50]", findcolorRegionAdd50);
                str2 = str2.Replace("[找色区域外扩50]", findcolorRegionAdd50);
                str11 = str11.Replace("[找色区域外扩50]", findcolorRegionAdd50);
                str22 = str22.Replace("[找色区域外扩50]", findcolorRegionAdd50);
                str111 = str111.Replace("[找色区域外扩50]", findcolorRegionAdd50);
                str222 = str222.Replace("[找色区域外扩50]", findcolorRegionAdd50);
                //str3 = str3.Replace("[找色区域外扩50]", findcolorRegionAdd50);

                //([多点比色_颜色特征]
                str1 = str1.Replace("[找色区域]", currentRegion).Replace("[多点比色_颜色描述][" + i + "]", group11str[i]);
                str2 = str2.Replace("[找色区域]", currentRegion).Replace("[多点找色_颜色描述][" + i + "]", group22str[i]);
                str11 = str11.Replace("[找色区域]", currentRegion).Replace("[多点比色_颜色描述][" + i + "]", group11str[i]);
                str22 = str22.Replace("[找色区域]", currentRegion).Replace("[多点找色_颜色描述][" + i + "]", group22str[i]);
                str111 = str111.Replace("[找色区域]", currentRegion).Replace("[多点比色_颜色描述][" + i + "]", group11str[i]);
                str222 = str222.Replace("[找色区域]", currentRegion).Replace("[多点找色_颜色描述][" + i + "]", group22str[i]);
                
            }
            //多点比色数据 多点找色数据 区域找色数据
            //string vid = "var" + System.Guid.NewGuid().ToString("N");
            //str1 = str1.Replace("多点比色数据", vid);
            //str2 = str2.Replace("多点找色数据", vid);
            //str3 = str3.Replace("区域找色数据", vid);

            //处理[全部]
            if (str1.Contains("[多点比色_颜色描述][全部]"))
            {
                int splitIndex = str1.IndexOf("[多点比色_颜色描述][全部]") + ("[多点比色_颜色描述][全部]").Length;
                string splitStr = str1.Substring(splitIndex + 1, 1);
                StringBuilder sbx = new StringBuilder();
                for (int i = 0; i < group11str.Count; i++)
                {
                    sbx.Append(group11str[i]);
                    if (i != group11str.Count - 1)
                    {
                        sbx.Append(splitStr);
                    }
                }
                str1 = str1.Replace("[多点比色_颜色描述][全部][" + splitStr + "]", sbx.ToString());
                str11 = str11.Replace("[多点比色_颜色描述][全部][" + splitStr + "]", sbx.ToString());
                str111 = str111.Replace("[多点比色_颜色描述][全部][" + splitStr + "]", sbx.ToString());
            }

            if (str2.Contains("[多点找色_颜色描述][全部]"))
            {
                int splitIndex = str2.IndexOf("[多点找色_颜色描述][全部]") + ("[多点找色_颜色描述][全部]").Length;
                string splitStr = str2.Substring(splitIndex + 1, 1);
                StringBuilder sbx = new StringBuilder();
                for (int i = 0; i < group22str.Count; i++)
                {
                    sbx.Append(group22str[i]);
                    if (i != group22str.Count - 1)
                    {
                        sbx.Append(splitStr);
                    }
                }
                str2 = str2.Replace("[多点找色_颜色描述][全部][" + splitStr + "]", sbx.ToString());
                str22 = str22.Replace("[多点找色_颜色描述][全部][" + splitStr + "]", sbx.ToString());
                str222 = str222.Replace("[多点找色_颜色描述][全部][" + splitStr + "]", sbx.ToString());
            }

            if (str3.Contains("找色区域"))
            {
                str3 = str3.Replace("[找色区域]", currentRegion);
            }
            if (textBox3.Text != "000000")
            {
                str1 = str1.Replace("[偏色]", "," + textBox3.Text);
                str2 = str2.Replace("[偏色]", "," + textBox3.Text);
                str11 = str11.Replace("[偏色]", "," + textBox3.Text);
                str22 = str22.Replace("[偏色]", "," + textBox3.Text);
                str111 = str111.Replace("[偏色]", "," + textBox3.Text);
                str222 = str222.Replace("[偏色]", "," + textBox3.Text);
            }
            else
            {
                str1 = str1.Replace("[偏色]", "");
                str2 = str2.Replace("[偏色]", "");
                str11 = str11.Replace("[偏色]", "");
                str22 = str22.Replace("[偏色]", "");
                str111 = str111.Replace("[偏色]", "");
                str222 = str222.Replace("[偏色]", "");
            }


            txtScript1.Text = str1;
            txtScript2.Text = str2;
            txtScript11.Text = str11;
            txtScript22.Text = str22;
            txtScript111.Text = str111;
            txtScript222.Text = str222;

            txtScript3.Text = str3;


            tabControl2.SelectedIndex = 0;
            Clipboard.Clear();
            Clipboard.SetText(str1);
        }



        private void showAssCode()
        {
            //从作图区生成新图
            //Image saveImage = Image.FromHbitmap(bitmap.GetHbitmap());
            cmpColorVal = 20;
            compHSVFlag = true;
            showColorsCard();
        }

        public Color FromRGB10(string cmpColor)
        {
            string[] rgbstr = cmpColor.Split(',');
            Color colorx = Color.FromArgb(int.Parse(rgbstr[0]), int.Parse(rgbstr[1]), int.Parse(rgbstr[2]));
            return colorx;
        }

        private void genAllSelColorImg()
        {
            if (genCodePointList.Count > 0)
            {
                Bitmap bmpshow = new Bitmap(cutPicWidth, cutPicHeight);
                Bitmap Bimg = new Bitmap(piclist[this.tabControl1.SelectedIndex].BackgroundImage);
                for (int i = 0; i < genCodePointList.Count; i++)
                {
                    Point cPoint = genCodePointList[i];
                    Color currentIcolor = Bimg.GetPixel(cPoint.X, cPoint.Y);
                    bmpshow.SetPixel(cPoint.X - cutPicX, cPoint.Y - cutPicY, currentIcolor);
                }

                PictureBox pboxs = new PictureBox();
                pboxs.Width = bmpshow.Width;
                pboxs.Height = bmpshow.Height;
                pboxs.Image = bmpshow;
                this.panel6.Controls.Add(pboxs);
            }
        }

        private void genMobangColorData()
        {
            pictureBox4.Image = null;
            txtResult.Clear();

            for (int i = 0; i < catchPointList.Count; i++)
            {
                if (!catchAllPointList.Contains(catchPointList[i]))
                {
                    catchAllPointList.Add(catchPointList[i]);
                }
            }

            //savePointList.AddRange(catchPointList);
            if (catchPointList.Count <= 120)
            {
                savePointList.AddRange(catchPointList);
            }
            else 
            {
                for (int i = 0; i < catchPointList.Count; i++)
                {
                    if (i%3==0)
                    {
                        savePointList.Add(catchPointList[i]);
                    }
                }
            }
   
            Bitmap bmpxsrc = new Bitmap(piclist[this.tabControl1.SelectedIndex].BackgroundImage);
            Bitmap bmpx = new Bitmap(this.pictureBox4.Width, this.pictureBox4.Height);

            bool breakFlag = false;
            for (int i = 0; i < bmpx.Width; i++)
            {
                for (int j = 0; j < bmpx.Height; j++)
                {
                    int cuindex = j + (i * bmpx.Width);
                    if (cuindex >= savePointList.Count)
                    {
                        breakFlag = true;
                        break;
                    }
                    Point p = savePointList[cuindex];
                    bmpx.SetPixel(i, j, bmpxsrc.GetPixel(p.X, p.Y));
                }
                if (breakFlag)
                {
                    break;
                }
            }
            this.pictureBox4.Image = bmpx;

            if (!(savePointList.Count > 0))
            {
                return;
            }

            currentRegion = cutPicX + "," + cutPicY + "," + (cutPicX + cutPicWidth) + "," + (cutPicY + cutPicHeight);
            //savePointList
            StringBuilder mulCmpColorSb = new StringBuilder();
            StringBuilder mulFindColorSb = new StringBuilder();
            //StringBuilder regionColorSb = new StringBuilder();

            if (savePointList.Count > 0)
            {
                mulFindColorSb.Append("\"");
                mulCmpColorSb.Append("\"");
                //regionColorSb.Append("\"");
            }

            genCodePointList = new List<Point>();
            StringBuilder genStr = new StringBuilder();
            Random r = new Random();

            if (savePointList.Count <= 120)
            {
                for (int i = 0; i < savePointList.Count; i++)
                {
                    Point p = savePointList[i];
                    genCodePointList.Add(p);
                    genStr.Append(p.X + "," + p.Y + ":" + BGR16(bmpxsrc.GetPixel(p.X, p.Y))).Append(Environment.NewLine);
                }
            }
            else
            {
                int rcount = 0;
                while (true)
                {
                    rcount++;
                    int num = r.Next(1, savePointList.Count);
                    Point p = savePointList[num];
                    if (!genCodePointList.Contains(p))
                    {
                        genCodePointList.Add(p);
                        genStr.Append(p.X + "," + p.Y + ":" + BGR16(bmpxsrc.GetPixel(p.X, p.Y))).Append(Environment.NewLine);
                    }
                    if (genCodePointList.Count >= 120 || genCodePointList.Count >= savePointList.Count || rcount > 150000)
                    {
                        break;
                    }
                }
            }

            //计算偏色
            List<int> colorRlist = new List<int>();
            List<int> colorGlist = new List<int>();
            List<int> colorBlist = new List<int>();
            for (int i = 0; i < savePointList.Count; i++)
            {
                Point p = savePointList[i];
                Color cl = bmpxsrc.GetPixel(p.X, p.Y);
                colorRlist.Add(cl.R);
                colorGlist.Add(cl.G);
                colorBlist.Add(cl.B);
            }

            colorRlist.Sort();
            colorGlist.Sort();
            colorBlist.Sort();

            int minR = colorRlist[0];
            int minG = colorGlist[0];
            int minB = colorBlist[0];

            int maxR = colorRlist[colorRlist.Count - 1];
            int maxG = colorGlist[colorRlist.Count - 1];
            int maxB = colorBlist[colorRlist.Count - 1];

            int colorRdif = maxR - minR;
            int colorGdif = maxG - minG;
            int colorBdif = maxB - minB;

            int rcompare = (int)(Math.Ceiling(new Decimal(Math.Abs(colorRdif) / 2)));
            int gcompare = (int)(Math.Ceiling(new Decimal(Math.Abs(colorGdif) / 2)));
            int bcompare = (int)(Math.Ceiling(new Decimal(Math.Abs(colorBdif) / 2)));

            //int bigcompare = rcompare;
            //if (bigcompare<gcompare)
            //{
            //    bigcompare = gcompare;
            //}
            //if (bigcompare < bcompare)
            //{
            //    bigcompare = bcompare;
            //}

            int rRate = (int)Math.Round((float)rcompare / 5, 0);
            int gRate = (int)Math.Round((float)gcompare / 5, 0);
            int bRate = (int)Math.Round((float)bcompare / 5, 0);


            //int rcomparePlus = rcompare + rRate;
            //int gcomparePlus = gcompare + gRate;
            //int bcomparePlus = bcompare + bRate;

            int rcomparePlus = rcompare + rRate;
            int gcomparePlus = gcompare + gRate;
            int bcomparePlus = bcompare + bRate;


            int middleR = maxR - rcompare;
            if (middleR < minR)
            {
                middleR = minR;
            }

            int middleG = maxG - gcompare;
            if (middleG < minG)
            {
                middleG = minG;
            }

            int middleB = maxB - bcompare;
            if (middleB < minB)
            {
                middleB = minB;
            }

            string useColorStr = middleR + "," + middleG + "," + middleB;
            Color useColor = FromRGB10(useColorStr);

            //string usePianseStr = rcomparePlus + "," + gcomparePlus + "," + bcomparePlus;
            //Color usePianseColor = FromRGB10(usePianseStr);

            int usePianse = rcomparePlus + gcomparePlus + bcomparePlus;

            this.panel9.BackColor = useColor;


            //生成颜色代码
            //多点找色 颜色描述 "1FC5F4","22|22|000000,52|47|1FC5F4"
            //多点比色 颜色描述 "256|235|1FC5F4,305|271|1FC5F4"
            //区域找色 颜色描述 "1FC5F4|1FC5F4" 
            for (int i = 0; i < genCodePointList.Count; i++)
            {
                Point pxy = genCodePointList[i];
                Color pcolor = bmpxsrc.GetPixel(pxy.X, pxy.Y);
                //Color pcolor = useColor;

                //多点比色
                mulCmpColorSb.Append(pxy.X + "|" + pxy.Y + "|" + BGR16(pcolor));
                //区域找色
                //regionColorSb.Append(BGR16(pcolor));

                //偏色处理
                //if (textBox3.Text != "000000")
                //{
                //    mulCmpColorSb.Append("-").Append(textBox3.Text);
                //    regionColorSb.Append("-").Append(textBox3.Text);
                //}

                //mulCmpColorSb.Append("-").Append(BGR16(usePianseColor));

                //regionColorSb.Append("-").Append(BGR16(usePianseColor));


                //多点找色
                if (i == 0)
                {
                    //多点找色
                    mulFindColorSb.Append(BGR16(pcolor));
                    //if (textBox3.Text != "000000")
                    //{
                    //    mulFindColorSb.Append("-").Append(textBox3.Text);
                    //}
                    
                    //mulFindColorSb.Append("-").Append(BGR16(usePianseColor));
                    
                    mulFindColorSb.Append("\",\"");
                }
                else
                {
                    int desX = pxy.X - genCodePointList[0].X;
                    int desY = pxy.Y - genCodePointList[0].Y;
                    mulFindColorSb.Append(desX).Append("|").Append(desY).Append("|");
                    mulFindColorSb.Append(BGR16(pcolor));
                    //if (textBox3.Text != "000000")
                    //{
                    //    mulFindColorSb.Append("-").Append(textBox3.Text);
                    //}

                    //mulFindColorSb.Append("-").Append(BGR16(usePianseColor));

                    if (i != genCodePointList.Count - 1)
                    {
                        mulFindColorSb.Append(",");
                    }
                }

                if (i != genCodePointList.Count - 1)
                {
                    mulCmpColorSb.Append(",");
                    //regionColorSb.Append("|");
                    //mulFindColorSb.Append(",");
                }

                if (i == genCodePointList.Count - 1)
                {
                    mulCmpColorSb.Append("\"");
                    //regionColorSb.Append("\"");
                    mulFindColorSb.Append("\"");
                }
            }


            //[多点比色_颜色描述]
            textBox5.Text = mulCmpColorSb.ToString();
            textBox55.Text = mulCmpColorSb.ToString();
            textBox555.Text = mulCmpColorSb.ToString();
            //[多点找色_颜色描述]
            textBox8.Text = mulFindColorSb.ToString();
            textBox88.Text = mulFindColorSb.ToString();
            textBox888.Text = mulFindColorSb.ToString();
            //[区域找色_颜色描述]

            

            var cnOcrPage = ocrCn.Process(cutBmp);
            string cnOcrStr = cnOcrPage.GetText();
            cnOcrPage.Dispose();

            var enOcrPage = ocrEn.Process(cutBmp);
            string enOcrStr = enOcrPage.GetText();
            enOcrPage.Dispose();

            StringBuilder regionColorSb = new StringBuilder();
            if (!String.IsNullOrEmpty(cnOcrStr))
            {
                regionColorSb.Append("中文识别结果：" + cnOcrStr + Environment.NewLine);
            }
            if (!String.IsNullOrEmpty(enOcrStr))
            {
                regionColorSb.Append("英文识别结果：" + enOcrStr + Environment.NewLine);
            }
            textBox10.Text = regionColorSb.ToString();



            string str1 = formtmp.txtTmp1.Text;
            string str11 = formtmp.txtTmp11.Text;
            string str111 = formtmp.txtTmp111.Text;
            string str2 = formtmp.txtTmp2.Text;
            string str22 = formtmp.txtTmp22.Text;
            string str222 = formtmp.txtTmp222.Text;
            string str3 = formtmp.txtTmp3.Text;

            screenX = piclist[this.tabControl1.SelectedIndex].BackgroundImage.Width;
            screenY = piclist[this.tabControl1.SelectedIndex].BackgroundImage.Height;
            int crX1 = cutPicX - 50;
            int crY1 = cutPicY - 50;
            int crX2 = (cutPicX + cutPicWidth) + 50;
            int crY2 = (cutPicY + cutPicHeight) + 50;
            if (crX1 < 0)
            {
                crX1 = 0;
            }
            if (crX2 > screenX)
            {
                crX2 = screenX;
            }
            if (crY1 < 0)
            {
                crY1 = 0;
            }
            if (crY2 > screenY)
            {
                crY2 = screenY;
            }

            findcolorRegionAdd50 = crX1 + "," + crY1 + "," + crX2 + "," + crY2;

            int splitIndex = str1.IndexOf("[多点比色_颜色描述][全部]") + ("[多点比色_颜色描述][全部]").Length;
            string splitStr = str1.Substring(splitIndex + 1, 1);
            splitIndex = str2.IndexOf("[多点找色_颜色描述][全部]") + ("[多点找色_颜色描述][全部]").Length;
            string splitStr2 = str2.Substring(splitIndex + 1, 1);
            splitIndex = str3.IndexOf("[区域找色_颜色描述][全部]") + ("[区域找色_颜色描述][全部]").Length;
            string splitStr3 = str3.Substring(splitIndex + 1, 1);

            str1 = str1.Replace("[找色区域外扩50]", findcolorRegionAdd50);
            str11 = str11.Replace("[找色区域外扩50]", findcolorRegionAdd50);
            str111 = str111.Replace("[找色区域外扩50]", findcolorRegionAdd50);
            str2 = str2.Replace("[找色区域外扩50]", findcolorRegionAdd50);
            str22 = str22.Replace("[找色区域外扩50]", findcolorRegionAdd50);
            str222 = str222.Replace("[找色区域外扩50]", findcolorRegionAdd50);
            //str3 = str3.Replace("[找色区域外扩50]", findcolorRegionAdd50);

            //([多点比色_颜色特征]
            str1 = str1.Replace("[找色区域]", currentRegion).Replace("[多点比色_颜色描述][全部][" + splitStr + "]", mulCmpColorSb.ToString());
            str11 = str11.Replace("[找色区域]", currentRegion).Replace("[多点比色_颜色描述][全部][" + splitStr + "]", mulCmpColorSb.ToString());
            str111 = str111.Replace("[找色区域]", currentRegion).Replace("[多点比色_颜色描述][全部][" + splitStr + "]", mulCmpColorSb.ToString());
            str222 = str222.Replace("[找色区域]", currentRegion).Replace("[多点找色_颜色描述][全部][" + splitStr2 + "]", mulFindColorSb.ToString());
            str22 = str22.Replace("[找色区域]", currentRegion).Replace("[多点找色_颜色描述][全部][" + splitStr2 + "]", mulFindColorSb.ToString());
            str2 = str2.Replace("[找色区域]", currentRegion).Replace("[多点找色_颜色描述][全部][" + splitStr2 + "]", mulFindColorSb.ToString());
            str3 = str3.Replace("[找色区域]", currentRegion);

            //多点比色数据 多点找色数据 区域找色数据
            //string vid = "var" + System.Guid.NewGuid().ToString("N");
            //str1 = str1.Replace("多点比色数据", vid);
            //str2 = str2.Replace("多点找色数据", vid);
            //str3 = str3.Replace("区域找色数据", vid);

            if (usePianse > 0)
            {
                str1 = str1.Replace("[偏色]", "," + usePianse);
                str2 = str2.Replace("[偏色]", "," + usePianse);
                str11 = str11.Replace("[偏色]", "," + usePianse);
                str22 = str22.Replace("[偏色]", "," + usePianse);
                str111 = str111.Replace("[偏色]", "," + usePianse);
                str222 = str222.Replace("[偏色]", "," + usePianse);
            }
            else
            {
                str1 = str1.Replace("[偏色]", "");
                str2 = str2.Replace("[偏色]", "");
                str11 = str11.Replace("[偏色]", "");
                str22 = str22.Replace("[偏色]", "");
                str111 = str111.Replace("[偏色]", "");
                str222 = str222.Replace("[偏色]", "");
            }


            txtScript1.Text = str1;
            txtScript11.Text = str11;
            txtScript111.Text = str111;
            txtScript2.Text = str2;
            txtScript22.Text = str22;
            txtScript222.Text = str222;


            txtScript3.Text = str3;


            tabControl2.SelectedIndex = 0;
            Clipboard.Clear();
            Clipboard.SetText(str1);


            txtResult.Text = "取颜色偏色：" + BGR16(useColor) + "-" + usePianse + Environment.NewLine + "总颜色数量：" + catchAllPointList.Count + Environment.NewLine + "取颜色数量：" + genCodePointList.Count + Environment.NewLine + genStr.ToString();
            genAllSelColorImg();
        }

        public void frm_TransfEvent(int x, int y, int width, int height, Bitmap bmp)
        {
            this.pictureBoxShow.Image = bmp;
            this.panel6.Controls.Clear();
            textBoxQd.Text = x.ToString() + "," + y.ToString();
            textBoxZd.Text = (x + width) + "," + (y + height);
            textBoxDx.Text = width.ToString() + "," + height.ToString();

            cutPicX = x;
            cutPicY = y;
            cutPicWidth = bmp.Width;
            cutPicHeight = bmp.Height;

            if (checkBox1.Checked)
            {
                //=====================================显示取色的截图//=====================================
                
                Bitmap Bimg = new Bitmap(piclist[this.tabControl1.SelectedIndex].BackgroundImage);
                for (int i = 0; i < bmp.Width; i++)
                {
                    for (int j = 0; j < bmp.Height; j++)
                    {
                        Point srcPoint = new Point((cutPicX + i), (cutPicY + j));
                        Point cPoint = new Point(i, j);
                        Color currentIcolor = bmp.GetPixel(i, j);
                        if (currentIcolor.R != 0 || currentIcolor.G != 0 || currentIcolor.B != 0)
                        {
                            Color pixelColor = Bimg.GetPixel(srcPoint.X, srcPoint.Y);
                            catchPointList.Add(new Point(srcPoint.X, srcPoint.Y));
                        }
                    }
                }
                //=====================================显示取色的截图//=====================================

                //=====================================显示最后一次的截图//=====================================
                //Bitmap bmpshow = new Bitmap(cutPicWidth, cutPicHeight);
                //Bitmap Bimg = new Bitmap(piclist[this.tabControl1.SelectedIndex].BackgroundImage);
                //for (int i = 0; i < bmp.Width; i++)
                //{
                //    for (int j = 0; j < bmp.Height; j++)
                //    {
                //        Point srcPoint = new Point((cutPicX + i), (cutPicY + j));
                //        Point cPoint = new Point(i, j);
                //        Color currentIcolor = bmp.GetPixel(i, j);
                //        if (currentIcolor.R != 0 || currentIcolor.G != 0 || currentIcolor.B != 0)
                //        {
                //            Color pixelColor = Bimg.GetPixel(srcPoint.X, srcPoint.Y);
                //            bmpshow.SetPixel(i, j, pixelColor);
                //            catchPointList.Add(new Point(srcPoint.X, srcPoint.Y));
                //        }
                //    }
                //}
                //PictureBox pboxs = new PictureBox();
                //pboxs.Width = bmpshow.Width;
                //pboxs.Height = bmpshow.Height;
                //pboxs.Image = bmpshow;
                //this.panel6.Controls.Add(pboxs);
                //=====================================显示最后一次的截图//=====================================
            }
            else
            {
                showAssCode();
            }
        }


        /// <summary>
        /// 载入
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void btnLoad_Click(object sender, EventArgs e)
        {
            this.tabControl1.Visible = false;
            DialogResult rst = this.openFileDialog1.ShowDialog();
            int pgcount = this.tabControl1.SelectedIndex;
            if (rst.Equals(DialogResult.OK))
            {
                string[] selFiles = this.openFileDialog1.FileNames;

                for (int i = 0; i < selFiles.Length; i++)
                {
                    Stream s = File.Open(selFiles[i], FileMode.Open);
                    Image img = Bitmap.FromStream(s);
                    s.Close();
                    s.Dispose();
                    Bitmap bmp = new Bitmap(img);
                    TabPage tp = new TabPage(this.tabControl1.TabPages.Count + 1 + "");
                    tp.ToolTipText = System.IO.Path.GetFileName(selFiles[i]);
                    PictureBox pb = new PictureBox();
                    //if (bmp.Width > bmp.Height)
                    //{
                    //    //pb.BackgroundImage = rotateImage(bmp, -90);
                    //    bmp.RotateFlip(RotateFlipType.Rotate90FlipNone);
                    //    pb.BackgroundImage = bmp;
                    //}
                    //else
                    //{
                    //    pb.BackgroundImage = bmp;
                    //}

                    pb.BackgroundImage = bmp;

                    pb.Size = new Size(pb.BackgroundImage.Width, pb.BackgroundImage.Height);
                    Panel pn = new Panel();
                    pn.Dock = DockStyle.Fill;
                    pn.AutoScroll = true;
                    tp.Controls.Add(pn);
                    pn.Controls.Add(pb);
                    piclist.Add(pb);
                    tp.Controls.Add(pn);
                    this.tabControl1.TabPages.Add(tp);


                    pb.Cursor = Cursors.Cross;


                    pb.MouseClick += new System.Windows.Forms.MouseEventHandler(this.FmScreen_MouseClick);
                    pb.MouseDoubleClick += new System.Windows.Forms.MouseEventHandler(this.FmScreen_MouseDoubleClick);
                    pb.MouseDown += new System.Windows.Forms.MouseEventHandler(this.FmScreen_MouseDown);
                    pb.MouseMove += new System.Windows.Forms.MouseEventHandler(this.FmScreen_MouseMove);
                    pb.MouseUp += new System.Windows.Forms.MouseEventHandler(this.FmScreen_MouseUp);
                }
                if (this.tabControl1.TabPages.Count > pgcount)
                {
                    this.tabControl1.SelectedIndex = pgcount + 1;
                }
            }
            this.tabControl1.Visible = true;
        }

        private void btnClear_Click(object sender, EventArgs e)
        {
            piclist.Clear();

            this.tabControl1.TabPages.Clear();
        }

        private void FmScreenShot_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Escape)
            {
                if (this.tabControl1.TabPages.Count > 0)
                {
                    if (this.tabControl1.SelectedIndex > -1)
                    {
                        int rindex = this.tabControl1.SelectedIndex;
                        this.tabControl1.TabPages.RemoveAt(rindex);
                        piclist.RemoveAt(rindex);
                    }
                }
            }
            else if (e.KeyCode == Keys.Up)
            {
                Point mousePos = Control.MousePosition;
                Cursor.Position = new Point(mousePos.X, mousePos.Y - 1);
            }
            else if (e.KeyCode == Keys.Down)
            {
                Point mousePos = Control.MousePosition;
                Cursor.Position = new Point(mousePos.X, mousePos.Y + 1);
            }
            else if (e.KeyCode == Keys.Left)
            {
                Point mousePos = Control.MousePosition;
                Cursor.Position = new Point(mousePos.X - 1, mousePos.Y);
            }
            else if (e.KeyCode == Keys.Right)
            {
                Point mousePos = Control.MousePosition;
                Cursor.Position = new Point(mousePos.X + 1, mousePos.Y);
            }
        }

        private void initTemplateConfig()
        {
            string querySql = "select templateStr,templateIndex from template order by templateIndex";
            DataTable dtb = SQLiteHelper.ExecuteDataTable(querySql, null);
            string templateStr1 = (string)dtb.Rows[0]["templateStr"];
            //int templateIndex1 = (int)dtb.Rows[0]["templateIndex"];
            string templateStr2 = (string)dtb.Rows[1]["templateStr"];
            //int templateIndex2 = (int)dtb.Rows[1]["templateIndex"];
            string templateStr3 = (string)dtb.Rows[2]["templateStr"];
            //int templateIndex3 = (int)dtb.Rows[2]["templateIndex"];

            this.formtmp.txtTmp1.Text = templateStr1;
            this.formtmp.txtTmp2.Text = templateStr2;
            this.formtmp.txtTmp3.Text = templateStr3;

            if (dtb.Rows.Count>3)
            {
                string templateStr11 = (string)dtb.Rows[3]["templateStr"];
                string templateStr22 = (string)dtb.Rows[4]["templateStr"];

                this.formtmp.txtTmp11.Text = templateStr11;
                this.formtmp.txtTmp22.Text = templateStr22;
            }

            if (dtb.Rows.Count > 5)
            {
                string templateStr111 = (string)dtb.Rows[5]["templateStr"];
                string templateStr222 = (string)dtb.Rows[6]["templateStr"];

                this.formtmp.txtTmp111.Text = templateStr111;
                this.formtmp.txtTmp222.Text = templateStr222;
            }


            querySql = "select configVal,configType from paramconfig";
            dtb = SQLiteHelper.ExecuteDataTable(querySql, null);
            for (int i = 0; i < dtb.Rows.Count; i++)
            {
                string configVal = (string)dtb.Rows[i]["configVal"];
                string configType = (string)dtb.Rows[i]["configType"];
                if (configType.Equals("GroupCount"))
                {
                    this.textBox4.Text = configVal;
                    this.formtmp.textBox4.Text = configVal;
                }
                else if (configType.Equals("PianseVal"))
                {
                    this.textBox3.Text = configVal;
                    this.formtmp.textBox3.Text = configVal;
                }
                //else if (configType.Equals("ScreenX"))
                //{
                //    this.formtmp.textBox5.Text = configVal;
                //    this.screenX = int.Parse(configVal);
                //}
                //else if (configType.Equals("ScreenY"))
                //{
                //    this.formtmp.textBox6.Text = configVal;
                //    this.screenY = int.Parse(configVal);
                //}

            }
        }

        private void FmScreenShot_Load(object sender, EventArgs e)
        {
            if (formtmp == null)
            {
                formtmp = new FormTmp();
                formtmp.fm1 = this;
                
                ocrCn = new TesseractEngine(Application.StartupPath+ "\\tessdata", "chi_sim", EngineMode.Default);
                ocrEn = new TesseractEngine(Application.StartupPath + "\\tessdata", "eng", EngineMode.Default);
            }

            initTemplateConfig();
        }



        /// <summary>
        /// 鼠标左键点击开始截图事件
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void FmScreen_MouseDown(object sender, MouseEventArgs e)
        {

            // 鼠标左键按下是开始画图，也就是截图
            if (e.Button == MouseButtons.Left)
            {
                // 如果捕捉没有开始
                if (!catchStart)
                {
                    catchStart = true;
                    // 保存此时鼠标按下坐标，经实验这里最好使用Control.MousePosition.X和Control.MousePosition.Y
                    //downPoint = new Point(Control.MousePosition.X, Control.MousePosition.Y);
                    downPoint = new Point(e.Location.X, e.Location.Y);

                    Bitmap catchBmp = new Bitmap(piclist[this.tabControl1.SelectedIndex].BackgroundImage);
                    oriBmp = new Bitmap(catchBmp);  // 获取初始图片
                }
            }
        }


        Graphics g;
        Pen p;
        Graphics g1;
        Bitmap copyBmp;

        private Point ptBegin = new Point();


        public string RGB16(Color cmpColor)
        {
            string color16 = cmpColor.ToArgb().ToString("X2").Substring(2);
            if (string.IsNullOrEmpty(color16))
            {
                return "";
            }
            color16 = color16.Substring(0, 2) + color16.Substring(2, 2) + color16.Substring(4);
            return color16;
        }

        public string BGR16(Color cmpColor)
        {
            string color16 = cmpColor.ToArgb().ToString("X2").Substring(2);
            color16 = color16.Substring(4) + color16.Substring(2, 2) + color16.Substring(0, 2);
            return color16;
        }

        /// <summary>
        /// 鼠标移动事件
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void FmScreen_MouseMove(object sender, MouseEventArgs e)
        {
            int LX = e.Location.X;
            int LY = e.Location.Y;
            int bigCount = 10;

            //label5

            Bitmap Bimg = new Bitmap(piclist[this.tabControl1.SelectedIndex].BackgroundImage);

            Color pixelColor = Bimg.GetPixel(LX, LY);
            this.label5.Text = BGR16(pixelColor);
            this.panel8.BackColor = pixelColor;

            lblScreenCurrentPoint.Text = LX.ToString() + "," + LY.ToString();

            PictureBox pictureBoxOr1 = piclist[this.tabControl1.SelectedIndex];

            if (e.X - bigCount <= 0)
            {
                ptBegin.X = 0;
            }
            else if (pictureBoxOr1.Size.Width - e.X <= bigCount)
            {
                ptBegin.X = pictureBoxOr1.Size.Width - bigCount * 2;
            }
            else
            {
                ptBegin.X = e.X - bigCount;
            }

            if (e.Y - bigCount <= 0)
            {
                ptBegin.Y = 0;
            }
            else if (pictureBoxOr1.Size.Height - e.Y <= bigCount)
            {
                ptBegin.Y = pictureBoxOr1.Size.Height - bigCount * 2;
            }
            else
            {
                ptBegin.Y = e.Y - bigCount;
            }
            //pictureBoxOr1.Refresh();


            //对图像进行放大显示　        
            Rectangle rec = new Rectangle(ptBegin.X, ptBegin.Y, bigCount * 2, bigCount * 2);
            Graphics g = pictureBoxBig.CreateGraphics();
            //防止模糊
            g.InterpolationMode = InterpolationMode.NearestNeighbor;
            g.DrawImage(pictureBoxOr1.BackgroundImage, pictureBoxBig.ClientRectangle, rec, GraphicsUnit.Pixel);
            Pen pp = new Pen(Color.Red, 1);//线为红色，线宽为一个像素
            g.DrawLine(pp, pictureBoxBig.Width / 2, 0, pictureBoxBig.Width / 2, pictureBoxBig.Height);//第一条线
            g.DrawLine(pp, 0, pictureBoxBig.Height / 2, pictureBoxBig.Width, pictureBoxBig.Height / 2);//第二条线           
            g.Flush();

            // 确保截图开始
            if (catchStart)
            {
                // 新建一个图片对象，让它与屏幕图片相同
                copyBmp = (Bitmap)oriBmp.Clone();
                // 获取鼠标按下的坐标
                endPoint = new Point(downPoint.X, downPoint.Y);
                // 新建画板和画笔
                g = Graphics.FromImage(copyBmp);
                p = new Pen(Color.Red, 1);

                if (checkBox1.Checked)
                {
                    p.DashStyle = System.Drawing.Drawing2D.DashStyle.Dash;

                    pictureBox4.Image = null;
                    savePointList.Clear();
                    catchAllPointList.Clear();
                    txtResult.Clear();
                }


                int width = Math.Abs(LX - downPoint.X);
                int height = Math.Abs(LY - downPoint.Y);
                if (LX < downPoint.X)
                {
                    endPoint.X = LX;
                }
                if (LY < downPoint.Y)
                {
                    endPoint.Y = LY;
                }

                catchRec = new Rectangle(endPoint, new Size(width, height));
                if (catchRec.Width > 0)
                {
                    catchRecFill = new Rectangle(endPoint, new Size(catchRec.Width, catchRec.Height));
                }
                // 将矩形画在画板上
                g.DrawRectangle(p, catchRec);
                // 释放目前的画板
                g.Dispose();
                p.Dispose();
                // 从当前窗体创建新的画板
                g1 = this.piclist[this.tabControl1.SelectedIndex].CreateGraphics(); //this.CreateGraphics();
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
        private void FmScreen_MouseUp(object sender, MouseEventArgs e)
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
        private void FmScreen_MouseClick(object sender, MouseEventArgs e)
        {

            if (e.Button == MouseButtons.Right)
            {
                //this.DialogResult = DialogResult.OK;
                //this.Close();

                if (g != null)
                {
                    g.Dispose();
                    p.Dispose();
                    g1.Dispose();
                    copyBmp.Dispose();
                }
                //piclist[this.tabControl1.SelectedIndex].Cursor = Cursors.Arrow;
                this.piclist[this.tabControl1.SelectedIndex].Image = piclist[this.tabControl1.SelectedIndex].BackgroundImage;
            }
        }

        //魔棒取色 取到的颜色位置数据
        List<Point> catchPointList = new List<Point>();
        //保存的数据
        List<Point> savePointList = new List<Point>();
        //魔棒取色 取到的颜色位置数据
        List<Point> catchAllPointList = new List<Point>();

        List<Point> genCodePointList = new List<Point>();

        /// <summary>
        ///  漫水填充 FloodFill()按灰度检测，FloodFill_argb()按四通道分量检测
        /// </summary>
        /// <param name="src">原图</param>
        /// <param name="location">检测点</param>
        /// <param name="fillColor">填充颜色</param>
        /// <param name="threshould">阈值</param>
        /// <returns>填充图，非填充部分为默认值</returns>
        unsafe public Bitmap FloodFill(Rectangle myrect, Bitmap src, Point locationx, Color fillColor, int threshould)
        {
            try
            {
                catchPointList.Clear();
                Point startPoint = myrect.Location;
                Point location = new Point((locationx.X - myrect.Location.X), (locationx.Y - myrect.Y));

                //创建新图位图
                Bitmap bitmapx = new Bitmap(myrect.Width, myrect.Height);
                //创建作图区域
                Graphics graphic = Graphics.FromImage(bitmapx);
                //截取原图相应区域写入作图区
                graphic.DrawImage(src, 0, 0, new Rectangle(myrect.X, myrect.Y, myrect.Width, myrect.Height), GraphicsUnit.Pixel);
                //从作图区生成新图
                //Image saveImage = Image.FromHbitmap(bitmapx.GetHbitmap());
                Bitmap srcbmp = bitmapx;

                Color backColor = srcbmp.GetPixel(location.X, location.Y);
                Bitmap dstbmp = new Bitmap(myrect.Width, myrect.Height);
                int w = myrect.Width;
                int h = myrect.Height;
                Stack<Point> fillPoints = new Stack<Point>(w * h);
                System.Drawing.Imaging.BitmapData srcbmpData = srcbmp.LockBits(new Rectangle(0, 0, srcbmp.Width, srcbmp.Height), System.Drawing.Imaging.ImageLockMode.ReadOnly, System.Drawing.Imaging.PixelFormat.Format24bppRgb);
                System.Drawing.Imaging.BitmapData dstbmpData = dstbmp.LockBits(new Rectangle(0, 0, dstbmp.Width, dstbmp.Height), System.Drawing.Imaging.ImageLockMode.ReadWrite, System.Drawing.Imaging.PixelFormat.Format24bppRgb);

                int stride = srcbmpData.Stride;

                byte* srcbuf = (byte*)srcbmpData.Scan0.ToPointer();
                byte* dstbuf = (byte*)dstbmpData.Scan0.ToPointer();

                int cr = backColor.R, cg = backColor.G, cb = backColor.B, ca = backColor.A;
                byte fcr = fillColor.R, fcg = fillColor.G, fcb = fillColor.B;
                if (location.X < 0 || location.Y < 0 || (locationx.X < (myrect.Location.X)) || (locationx.Y < (myrect.Location.Y)) || (locationx.X > (myrect.Location.X + myrect.Width)) || (locationx.Y > (myrect.Location.Y + myrect.Height))) return null;
                fillPoints.Push(new Point(location.X, location.Y));
                int[,] mask = new int[w, h];

                while (fillPoints.Count > 0)
                {
                    Point p = fillPoints.Pop();
                    mask[p.X, p.Y] = 1;
                    dstbuf[3 * p.X + p.Y * stride] = fcb;
                    dstbuf[3 * p.X + 1 + p.Y * stride] = fcg;
                    dstbuf[3 * p.X + 2 + p.Y * stride] = fcr;
                    if (p.X > 0 && (mask[p.X - 1, p.Y] != 1)
                        && Math.Abs(cb - srcbuf[3 * (p.X - 1) + p.Y * stride]) + Math.Abs(cg - srcbuf[3 * (p.X - 1) + 1 + p.Y * stride]) + Math.Abs(cr - srcbuf[3 * (p.X - 1) + 2 + p.Y * stride]) < threshould
                        )
                    {
                        dstbuf[3 * (p.X - 1) + p.Y * stride] = fcb;
                        dstbuf[3 * (p.X - 1) + 1 + p.Y * stride] = fcg;
                        dstbuf[3 * (p.X - 1) + 2 + p.Y * stride] = fcr;
                        fillPoints.Push(new Point(p.X - 1, p.Y));
                        mask[p.X - 1, p.Y] = 1;

                        if (!catchPointList.Contains(new Point(p.X - 1, p.Y)))
                        {
                            catchPointList.Add(new Point(p.X - 1, p.Y));
                        }
                    }
                    if (p.X < w - 1 && (mask[p.X + 1, p.Y] != 1)
                        && Math.Abs(cb - srcbuf[3 * (p.X + 1) + p.Y * stride]) + Math.Abs(cg - srcbuf[3 * (p.X + 1) + 1 + p.Y * stride]) + Math.Abs(cr - srcbuf[3 * (p.X + 1) + 2 + p.Y * stride]) < threshould
                        )
                    {
                        dstbuf[3 * (p.X + 1) + p.Y * stride] = fcb;
                        dstbuf[3 * (p.X + 1) + 1 + p.Y * stride] = fcg;
                        dstbuf[3 * (p.X + 1) + 2 + p.Y * stride] = fcr;
                        fillPoints.Push(new Point(p.X + 1, p.Y));
                        mask[p.X + 1, p.Y] = 1;

                        if (!catchPointList.Contains(new Point(p.X - 1, p.Y)))
                        {
                            catchPointList.Add(new Point(p.X - 1, p.Y));
                        }
                    }
                    if (p.Y > 0 && (mask[p.X, p.Y - 1] != 1)
                        && Math.Abs(cb - srcbuf[3 * p.X + (p.Y - 1) * stride]) + Math.Abs(cg - srcbuf[3 * p.X + 1 + (p.Y - 1) * stride]) + Math.Abs(cr - srcbuf[3 * p.X + 2 + (p.Y - 1) * stride]) < threshould
                        )
                    {
                        dstbuf[3 * p.X + (p.Y - 1) * stride] = fcb;
                        dstbuf[3 * p.X + 1 + (p.Y - 1) * stride] = fcg;
                        dstbuf[3 * p.X + 2 + (p.Y - 1) * stride] = fcr;
                        fillPoints.Push(new Point(p.X, p.Y - 1));
                        mask[p.X, p.Y - 1] = 1;

                        if (!catchPointList.Contains(new Point(p.X - 1, p.Y)))
                        {
                            catchPointList.Add(new Point(p.X - 1, p.Y));
                        }
                    }
                    if (p.Y < h - 1 && (mask[p.X, p.Y + 1] != 1)
                        && Math.Abs(cb - srcbuf[3 * p.X + (p.Y + 1) * stride]) + Math.Abs(cg - srcbuf[3 * p.X + 1 + (p.Y + 1) * stride]) + Math.Abs(cr - srcbuf[3 * p.X + 2 + (p.Y + 1) * stride]) < threshould
                        )
                    {
                        dstbuf[3 * p.X + (p.Y + 1) * stride] = fcb;
                        dstbuf[3 * p.X + 1 + (p.Y + 1) * stride] = fcg;
                        dstbuf[3 * p.X + 2 + (p.Y + 1) * stride] = fcr;
                        fillPoints.Push(new Point(p.X, p.Y + 1));
                        mask[p.X, p.Y + 1] = 1;

                        if (!catchPointList.Contains(new Point(p.X - 1, p.Y)))
                        {
                            catchPointList.Add(new Point(p.X - 1, p.Y));
                        }
                    }
                }

                Console.WriteLine(catchPointList.Count);

                fillPoints.Clear();
                srcbmp.UnlockBits(srcbmpData);
                dstbmp.UnlockBits(dstbmpData);
                return dstbmp;
            }
            catch (Exception exp)
            {
                //System.Windows.MessageBox.Show(exp.Message);
                MessageBox.Show(exp.Message);
                return null;
            }
        }


        unsafe public Bitmap FloodFill_argb(Rectangle myrect, Bitmap src, Point locationx, Color fillColor, int threshould)
        {
            try
            {

                catchPointList.Clear();
                Point startPoint = myrect.Location;
                Point location = new Point((locationx.X - myrect.Location.X), (locationx.Y - myrect.Y));

                //创建新图位图
                Bitmap bitmapx = new Bitmap(myrect.Width, myrect.Height);
                //创建作图区域
                Graphics graphic = Graphics.FromImage(bitmapx);
                //截取原图相应区域写入作图区
                graphic.DrawImage(src, 0, 0, new Rectangle(myrect.X, myrect.Y, myrect.Width, myrect.Height), GraphicsUnit.Pixel);
                //从作图区生成新图
                //Image saveImage = Image.FromHbitmap(bitmapx.GetHbitmap());
                Bitmap srcbmp = bitmapx;

                Color backColor = srcbmp.GetPixel(location.X, location.Y);
                Bitmap dstbmp = new Bitmap(myrect.Width, myrect.Height);
                int w = myrect.Width;
                int h = myrect.Height;
                Stack<Point> fillPoints = new Stack<Point>(w * h);
                System.Drawing.Imaging.BitmapData bmpData = srcbmp.LockBits(new Rectangle(0, 0, srcbmp.Width, srcbmp.Height), System.Drawing.Imaging.ImageLockMode.ReadOnly, System.Drawing.Imaging.PixelFormat.Format32bppArgb);
                System.Drawing.Imaging.BitmapData dstbmpData = dstbmp.LockBits(new Rectangle(0, 0, dstbmp.Width, dstbmp.Height), System.Drawing.Imaging.ImageLockMode.ReadWrite, System.Drawing.Imaging.PixelFormat.Format32bppArgb);

                int stride = bmpData.Stride;
                int stridedst = dstbmpData.Stride;

                byte* srcbuf = (byte*)bmpData.Scan0.ToPointer();
                int* dstbuf = (int*)dstbmpData.Scan0.ToPointer();

                int cr = backColor.R, cg = backColor.G, cb = backColor.B, ca = backColor.A;
                byte fcr = fillColor.R, fcg = fillColor.G, fcb = fillColor.B;
                int fc = fillColor.ToArgb();
                if (location.X < 0 || location.Y < 0 || (locationx.X < (myrect.Location.X)) || (locationx.Y < (myrect.Location.Y)) || (locationx.X > (myrect.Location.X + myrect.Width)) || (locationx.Y > (myrect.Location.Y + myrect.Height))) return null;
                fillPoints.Push(new Point(location.X, location.Y));
                int[,] mask = new int[w, h];

                while (fillPoints.Count > 0)
                {
                    Point p = fillPoints.Pop();
                    mask[p.X, p.Y] = 1;
                    dstbuf[p.X + p.Y * w] = fc;
                    if (p.X > 0 && (mask[p.X - 1, p.Y] != 1)
                        && Math.Abs(cb - srcbuf[4 * (p.X - 1) + p.Y * stride]) + Math.Abs(cg - srcbuf[4 * (p.X - 1) + 1 + p.Y * stride]) + Math.Abs(cr - srcbuf[4 * (p.X - 1) + 2 + p.Y * stride]) < threshould
                        && Math.Abs(ca - srcbuf[4 * (p.X - 1) + 3 + p.Y * stride]) < threshould / 2
                        )
                    {
                        dstbuf[(p.X - 1) + p.Y * w] = fc;
                        fillPoints.Push(new Point(p.X - 1, p.Y));
                        mask[p.X - 1, p.Y] = 1;
                    }
                    if (p.X < w - 1 && (mask[p.X + 1, p.Y] != 1)
                        && Math.Abs(cb - srcbuf[4 * (p.X + 1) + p.Y * stride]) + Math.Abs(cg - srcbuf[4 * (p.X + 1) + 1 + p.Y * stride]) + Math.Abs(cr - srcbuf[4 * (p.X + 1) + 2 + p.Y * stride]) < threshould
                        && Math.Abs(ca - srcbuf[4 * (p.X + 1) + 3 + p.Y * stride]) < threshould / 2
                        )
                    {

                        dstbuf[(p.X + 1) + p.Y * w] = fc;
                        fillPoints.Push(new Point(p.X + 1, p.Y));
                        mask[p.X + 1, p.Y] = 1;
                    }
                    if (p.Y > 0 && (mask[p.X, p.Y - 1] != 1)
                        && Math.Abs(cb - srcbuf[4 * p.X + (p.Y - 1) * stride]) + Math.Abs(cg - srcbuf[4 * p.X + 1 + (p.Y - 1) * stride]) + Math.Abs(cr - srcbuf[4 * p.X + 2 + (p.Y - 1) * stride]) < threshould
                        && Math.Abs(ca - srcbuf[4 * p.X + 3 + (p.Y - 1) * stride]) < threshould / 2
                        )
                    {
                        dstbuf[p.X + (p.Y - 1) * w] = fc;
                        fillPoints.Push(new Point(p.X, p.Y - 1));
                        mask[p.X, p.Y - 1] = 1;
                    }
                    if (p.Y < h - 1 && (mask[p.X, p.Y + 1] != 1)
                        && Math.Abs(cb - srcbuf[4 * p.X + (p.Y + 1) * stride]) + Math.Abs(cg - srcbuf[4 * p.X + 1 + (p.Y + 1) * stride]) + Math.Abs(cr - srcbuf[4 * p.X + 2 + (p.Y + 1) * stride]) < threshould
                        && Math.Abs(ca - srcbuf[4 * p.X + 3 + (p.Y + 1) * stride]) < threshould / 2
                        )
                    {
                        dstbuf[p.X + (p.Y + 1) * w] = fc;
                        fillPoints.Push(new Point(p.X, p.Y + 1));
                        mask[p.X, p.Y + 1] = 1;
                    }
                }

                //Console.WriteLine(catchPointList.Count);
                fillPoints.Clear();
                srcbmp.UnlockBits(bmpData);
                dstbmp.UnlockBits(dstbmpData);
                return dstbmp;
            }
            catch (Exception exp)
            {
                MessageBox.Show(exp.Message);
                return null;
            }
        }

        public Bitmap FloodFill_ByColor(Rectangle myrect, Bitmap src, Point locationx, int threshould)
        {
            try
            {
                catchPointList.Clear();
                Color firstColor = src.GetPixel(locationx.X,locationx.Y);

                Bitmap catchedBmp = new Bitmap(catchRec.Width, catchRec.Height);
                Graphics g = Graphics.FromImage(catchedBmp);
                g.DrawImage(oriBmp, new Rectangle(0, 0, catchRec.Width, catchRec.Height), catchRec, GraphicsUnit.Pixel);
                cutBmp = new Bitmap(catchedBmp);

                Bitmap fillBmp = new Bitmap(catchRec.Width, catchRec.Height);
                for (int i=0;i<cutBmp.Width;i++)
                {
                    for (int j=0;j<cutBmp.Height;j++)
                    {
                        //Math.Abs(cb - srcbuf[3 * (p.X - 1) + p.Y * stride]) + Math.Abs(cg - srcbuf[3 * (p.X - 1) + 1 + p.Y * stride]) + Math.Abs(cr - srcbuf[3 * (p.X - 1) + 2 + p.Y * stride]) < threshould
                        Color currentColor = cutBmp.GetPixel(i, j);
                        if (Math.Abs(currentColor.R-firstColor.R)+ Math.Abs(currentColor.G- firstColor.G)+ Math.Abs(currentColor.B - firstColor.B)< threshould)
                        {
                            fillBmp.SetPixel(i,j,currentColor);
                        }
                    }
                }
                return fillBmp;
            }
            catch (Exception exp)
            {
                MessageBox.Show(exp.Message);
                return null;
            }
        }

        /// <summary>
        /// 双击左键确认截图
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void FmScreen_MouseDoubleClick(object sender, MouseEventArgs e)
        {
            if (!checkBox1.Checked)//非魔棒取色
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
                        cutBmp = new Bitmap(catchedBmp);
                        // 将图片传递给事件
                        frm_TransfEvent(endPoint.X, endPoint.Y, catchedBmp.Width, catchedBmp.Height, catchedBmp);
                        
                    }
                    catchFinish = false;
                    //piclist[this.tabControl1.SelectedIndex].Cursor = Cursors.Arrow;

                    //g.Dispose();
                    //p.Dispose();
                    //g1.Dispose();
                    //copyBmp.Dispose();

                    this.piclist[this.tabControl1.SelectedIndex].Image = piclist[this.tabControl1.SelectedIndex].BackgroundImage;
                    //this.DialogResult = DialogResult.OK;

                    if (pictureBoxShow.Image != null)
                    {
                        //对图像进行放大显示　 
                        Rectangle rec = new Rectangle(0, 0, pictureBoxShow.Image.Width, pictureBoxShow.Image.Height);
                        Graphics gx = pictureBoxResultBig.CreateGraphics();
                        //防止模糊
                        gx.InterpolationMode = InterpolationMode.NearestNeighbor;
                        gx.Clear(Color.Gray);
                        gx.DrawImage(pictureBoxShow.Image, pictureBoxResultBig.ClientRectangle, rec, GraphicsUnit.Pixel);
                        gx.Flush();
                    }
                }

            }
            else
            {
                if (e.Button == MouseButtons.Left && catchFinish)
                {
                    if (checkBoxLianxu.Checked)
                    {
                        //范围内所有接近颜色
                        // 新建一个与矩形一样大小的空白图片
                        if (catchRecFill.Width > 0 && catchRecFill.Height > 0)
                        {
                            lastPoint = e.Location;
                            Bitmap catchedBmp = FloodFill_ByColor(catchRecFill, new Bitmap(this.piclist[this.tabControl1.SelectedIndex].BackgroundImage), lastPoint, (int)(numericUpDownMb.Value));
                            if (null == catchedBmp)
                            {
                                return;
                            }

                            Bitmap catchedBmpSave = new Bitmap(catchRec.Width, catchRec.Height);
                            Graphics g = Graphics.FromImage(catchedBmpSave);
                            // 把oriBmp中指定部分按照指定大小画到空白图片上
                            // catchRec指定originBmp中指定部分
                            // 第二个参数指定绘制到空白图片的位置和大小
                            // 画完后catchedBmp不再是空白图片了，而是具有与截取的图片一样的内容
                            g.DrawImage(oriBmp, new Rectangle(0, 0, catchRec.Width, catchRec.Height), catchRec, GraphicsUnit.Pixel);
                            cutBmp = new Bitmap(catchedBmpSave);

                            // 将图片传递给事件
                            frm_TransfEvent(endPoint.X, endPoint.Y, catchedBmp.Width, catchedBmp.Height, catchedBmp);
                        }
                    }
                    else 
                    {
                        // 新建一个与矩形一样大小的空白图片
                        if (catchRecFill.Width > 0 && catchRecFill.Height > 0)
                        {
                            lastPoint = e.Location;
                            Bitmap catchedBmp = FloodFill_argb(catchRecFill, new Bitmap(this.piclist[this.tabControl1.SelectedIndex].BackgroundImage), lastPoint, Color.White, (int)(numericUpDownMb.Value));
                            if (null == catchedBmp)
                            {
                                return;
                            }

                            Bitmap catchedBmpSave = new Bitmap(catchRec.Width, catchRec.Height);
                            Graphics g = Graphics.FromImage(catchedBmpSave);
                            // 把oriBmp中指定部分按照指定大小画到空白图片上
                            // catchRec指定originBmp中指定部分
                            // 第二个参数指定绘制到空白图片的位置和大小
                            // 画完后catchedBmp不再是空白图片了，而是具有与截取的图片一样的内容
                            g.DrawImage(oriBmp, new Rectangle(0, 0, catchRec.Width, catchRec.Height), catchRec, GraphicsUnit.Pixel);
                            cutBmp = new Bitmap(catchedBmpSave);

                            // 将图片传递给事件
                            frm_TransfEvent(endPoint.X, endPoint.Y, catchedBmp.Width, catchedBmp.Height, catchedBmp);
                        }
                    }

                    catchFinish = false;
                    this.piclist[this.tabControl1.SelectedIndex].Image = piclist[this.tabControl1.SelectedIndex].BackgroundImage;
                    if (pictureBoxShow.Image != null)
                    {
                        //对图像进行放大显示　 
                        Rectangle rec = new Rectangle(0, 0, pictureBoxShow.Image.Width, pictureBoxShow.Image.Height);
                        Graphics gx = pictureBoxResultBig.CreateGraphics();
                        gx.Clear(Color.Gray);
                        gx.DrawImage(pictureBoxShow.Image, pictureBoxResultBig.ClientRectangle, rec, GraphicsUnit.Pixel);
                        gx.Flush();
                    }
                }
            }

        }

        private void tabControl2_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (this.pictureBoxShow.Image != null)
            {
                if (tabControl2.SelectedIndex == 0)
                {
                    Clipboard.Clear();
                    Clipboard.SetText(txtScript1.Text);
                }
                else if (tabControl2.SelectedIndex == 1)
                {
                    Clipboard.Clear();
                    Clipboard.SetText(txtScript11.Text);
                }
                else if (tabControl2.SelectedIndex == 2)
                {
                    Clipboard.Clear();
                    Clipboard.SetText(txtScript111.Text);
                }
                else if (tabControl2.SelectedIndex == 3)
                {
                    Clipboard.Clear();
                    Clipboard.SetText(txtScript2.Text);
                }
                else if (tabControl2.SelectedIndex == 4)
                {
                    Clipboard.Clear();
                    Clipboard.SetText(txtScript22.Text);
                }
                else if (tabControl2.SelectedIndex == 5)
                {
                    Clipboard.Clear();
                    Clipboard.SetText(txtScript222.Text);
                }
                else if (tabControl2.SelectedIndex == 6)
                {
                    Clipboard.Clear();
                    Clipboard.SetText(txtScript3.Text);
                }
            }
        }

        private void checkBox1_CheckedChanged(object sender, EventArgs e)
        {
            if (checkBox1.Checked)
            {
                buttonSaveMobang.Visible = true;
                buttonClearMbcolor.Visible = true;
                numericUpDownMb.Visible = true;
                label6.Visible = true;
                panel9.Visible = true;
                label7.Visible = true;
            }
            else
            {
                buttonSaveMobang.Visible = false;
                buttonClearMbcolor.Visible = false;
                numericUpDownMb.Visible = false;
                label6.Visible = false;
                panel9.Visible = false;
                label7.Visible = false;
            }

            this.panel6.Controls.Clear();
            pictureBox4.Image = null;
            pictureBoxBig.Image = null;
            pictureBoxResultBig.Image = null;
            txtResult.Clear();
        }

        private void pictureBoxBig_PreviewKeyDown(object sender, PreviewKeyDownEventArgs e)
        {

        }

        private void buttonSaveMobang_Click(object sender, EventArgs e)
        {
            genMobangColorData();
        }

        private void buttonClearMbcolor_Click(object sender, EventArgs e)
        {
            pictureBox4.Image = null;
            savePointList.Clear();
            catchAllPointList.Clear();
            txtResult.Clear();
        }

        //雷电脚本转按键
        private void button1_Click(object sender, EventArgs e)
        {
            if (null == formLdConvert)
            {
                formLdConvert = new FormLDRecordConvert();
            }
            formLdConvert.ShowDialog();
        }

        private void FmScreenShot_FormClosed(object sender, FormClosedEventArgs e)
        {
            if (ocrCn!=null)
            {
                ocrCn.Dispose();
            }
            if (ocrEn != null)
            {
                ocrEn.Dispose();
            }
        }
    }
}

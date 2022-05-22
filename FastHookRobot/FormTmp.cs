using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Data.SQLite;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace FastHook
{

    public partial class FormTmp : Form
    {
        public FmScreenShot fm1;
        //point=cmp("", [多点比色_颜色描述][全部][,][偏色]);
        //if(!point.isEmpty()){
        //  sleep(sleepDft());

        //    }
        string tmp1 = "point=cmp(\"\",[多点比色_颜色描述][全部][,][偏色]);"+Environment.NewLine
            + "if(!point.isEmpty()){"+Environment.NewLine
            + "sleep(sleepDft());" + Environment.NewLine + Environment.NewLine
            + "}";


        //ret=cmpTap("", [多点比色_颜色描述][全部][,][偏色]);

        string tmp11 = "ret=cmpTap(\"\",[多点比色_颜色描述][全部][,][偏色]);";

//        ret=cmpTap("", [多点比色_颜色描述][全部][,][偏色]);
//if(ret){
//  sleep(sleepDft());

//    }

    string tmp111 = "ret=cmpTap(\"\",[多点比色_颜色描述][全部][,][偏色]);"
            + Environment.NewLine + "if(ret){"
            + Environment.NewLine + "sleep(sleepDft());"
            + Environment.NewLine + Environment.NewLine + "}";


//point=find("", [多点找色_颜色描述][全部][,], [找色区域外扩50][偏色]);
//if(!point.isEmpty()){
//   sleep(sleepDft());

//    }
    string tmp2 = "point=find(\"\",[多点找色_颜色描述][全部][,],[找色区域外扩50][偏色]);"
+ Environment.NewLine + "if(!point.isEmpty()){"
            + Environment.NewLine + "sleep(sleepDft());"
             + Environment.NewLine+ Environment.NewLine + "}";

        string tmp22 = "ret=findTap(\"\",[多点找色_颜色描述][全部][,],[找色区域外扩50][偏色]);";

//ret=findTap("", [多点找色_颜色描述][全部][,], [找色区域外扩50][偏色]);
//if(ret){
//   sleep(sleepDft());

//    }

    string tmp222 = "ret=findTap(\"\",[多点找色_颜色描述][全部][,],[找色区域外扩50][偏色]);"
+ Environment.NewLine + "if(ret){"
            + Environment.NewLine + "if(!point.isEmpty()){"
            + Environment.NewLine + "sleep(sleepDft());" + Environment.NewLine +Environment.NewLine + "}";

        string tmp3 = "OcrStr=ocr(\"\",[找色区域], \"chi_sim\",\"\",\"\");";

        public FormTmp()
        {
            InitializeComponent();
        }

        /// <summary>
        /// 还原设置
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void button1_Click(object sender, EventArgs e)
        {
            this.txtTmp1.Text = tmp1;
            this.txtTmp2.Text = tmp2;
            this.txtTmp3.Text = tmp3;

            this.txtTmp11.Text = tmp11;
            this.txtTmp22.Text = tmp22;


            this.txtTmp111.Text = tmp111;
            this.txtTmp222.Text = tmp222;


            this.textBox4.Text = "5";
            this.textBox3.Text = "000000";
            //this.textBox5.Text = "720";
            //this.textBox6.Text = "1280";

            this.fm1.textBox4.Text = "5";
            this.fm1.textBox3.Text = "000000";

        }

        /// <summary>
        /// 保存设置
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void button2_Click(object sender, EventArgs e)
        {
            //Console.ReadKey();

            //string Current = Directory.GetCurrentDirectory();//获取当前根目录
            //// 写入ini
            //IniHelper ini = new IniHelper(Current + "/config.ini");
            //ini.WriteValue("Setting", "template1", this.txtTmp1.Text);
            //ini.WriteValue("Setting", "template2", this.txtTmp2.Text);
            //ini.WriteValue("Setting", "template3", this.txtTmp3.Text);
            //// 读取ini
            //string stemp = ini.ReadValue("Setting", "template1");
            //Console.WriteLine(stemp);

            string deleteSqlStr = "delete from template";
            SQLiteHelper.ExecuteNonQuery(deleteSqlStr, null);

            string insertSqlStr = "insert into template values(@templateStr,@templateIndex)";
            SQLiteParameter[] parameters = new SQLiteParameter[2];
            parameters[0] = new SQLiteParameter("templateStr", DbType.String);
            parameters[1] = new SQLiteParameter("templateIndex", DbType.Int32);

            parameters[0].Value = this.txtTmp1.Text;
            parameters[1].Value = 0;
            SQLiteHelper.ExecuteNonQuery(insertSqlStr, parameters);

            parameters[0].Value = this.txtTmp2.Text;
            parameters[1].Value = 1;
            SQLiteHelper.ExecuteNonQuery(insertSqlStr, parameters);

            parameters[0].Value = this.txtTmp3.Text;
            parameters[1].Value = 2;
            SQLiteHelper.ExecuteNonQuery(insertSqlStr, parameters);

            parameters[0].Value = this.txtTmp11.Text;
            parameters[1].Value = 3;
            SQLiteHelper.ExecuteNonQuery(insertSqlStr, parameters);

            parameters[0].Value = this.txtTmp22.Text;
            parameters[1].Value = 4;
            SQLiteHelper.ExecuteNonQuery(insertSqlStr, parameters);

            parameters[0].Value = this.txtTmp111.Text;
            parameters[1].Value = 5;
            SQLiteHelper.ExecuteNonQuery(insertSqlStr, parameters);

            parameters[0].Value = this.txtTmp222.Text;
            parameters[1].Value = 6;
            SQLiteHelper.ExecuteNonQuery(insertSqlStr, parameters);


            //parameters[0].Value = this.txtTmpDm1.Text;
            //parameters[1].Value = 3;
            //SQLiteHelper.ExecuteNonQuery(insertSqlStr, parameters);

            //parameters[0].Value = this.txtTmpDm2.Text;
            //parameters[1].Value = 4;
            //SQLiteHelper.ExecuteNonQuery(insertSqlStr, parameters);


            //参数
            deleteSqlStr = "delete from paramconfig";
            SQLiteHelper.ExecuteNonQuery(deleteSqlStr, null);

            insertSqlStr = "insert into paramconfig values(@configVal,@configType)";
            parameters = new SQLiteParameter[2];
            parameters[0] = new SQLiteParameter("configVal", DbType.String);
            parameters[1] = new SQLiteParameter("configType", DbType.String);

            parameters[0].Value = this.textBox4.Text;
            parameters[1].Value = "GroupCount";
            SQLiteHelper.ExecuteNonQuery(insertSqlStr, parameters);

            parameters[0].Value = this.textBox3.Text;
            parameters[1].Value = "PianseVal";
            SQLiteHelper.ExecuteNonQuery(insertSqlStr, parameters);


            //parameters[0].Value = this.textBox5.Text;
            //parameters[1].Value = "ScreenX";
            //SQLiteHelper.ExecuteNonQuery(insertSqlStr, parameters);
            //parameters[0].Value = this.textBox6.Text;
            //parameters[1].Value = "ScreenY";
            //SQLiteHelper.ExecuteNonQuery(insertSqlStr, parameters);

            this.fm1.textBox4.Text = this.textBox4.Text;
            this.fm1.textBox3.Text = this.textBox3.Text;

            MessageBox.Show("保存成功");
        }

        private void FormTmp_Load(object sender, EventArgs e)
        {

        }
    }
}
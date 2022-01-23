using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace FastHook
{
    public partial class FormLDRecordConvert : Form
    {
        public FormLDRecordConvert()
        {
            InitializeComponent();
        }

        private void textBox1_DragDrop(object sender,DragEventArgs e)
        {
            if (e.Data.GetDataPresent(DataFormats.FileDrop))
            {
                e.Effect = DragDropEffects.Link;
            }
            else
            {
                e.Effect = DragDropEffects.None;
            }
        }

        /// <summary>
        /// 是否为json（开头是'{','['的）
        /// </summary>
        public static bool IsJson(string json)
        {
            json = json.Trim();
            if (string.IsNullOrEmpty(json))
                return false;

            var t = json.First();
            if (t == '{' || t == '[')
                return true;

            return false;
        }


        private int convertPointX(int x) 
        {
            float xx = (float)x / 15;
            return  (int)(xx * 0.5625);
        }

        private int convertPointY(int y)
        {
            float yy = (float)y / 15;
            return (int)(yy * 1.777777777777778);
        }

        private void textBox1_DragEnter(object sender,DragEventArgs e)
        {
            List<LdOperation> listLd = new List<LdOperation>();
            //DataFormats 数据的格式，下有多个静态属性都为string型，除FileDrop格式外还有Bitmap,Text,WaveAudio等格式    
            string path = ((System.Array)e.Data.GetData(DataFormats.FileDrop)).GetValue(0).ToString();
            string txtContext = File.ReadAllText(path);
            JObject job = JsonConvert.DeserializeObject<JObject>(txtContext);
            JArray operationsObj = (JArray)job["operations"];
            
            foreach (var item in operationsObj)
            {
                JObject jobopera = (JObject)item;
                if (null != jobopera["points"])
                {
                    JArray pointObj = (JArray)jobopera["points"];
                    if (pointObj != null && pointObj.Count > 0)
                    {
                        LdOperation ld = new LdOperation();
                        //Console.WriteLine(jobopera["timing"] + " " + jobopera["operationId"] + " " + pointObj[0]["x"] + " " + pointObj[0]["y"] + " " + pointObj[0]["state"]);
                        ld.timing = (int)jobopera["timing"];
                        ld.operationId = (string)jobopera["operationId"];
                        ld.x = (int)pointObj[0]["x"];
                        ld.y = (int)pointObj[0]["y"];
                        ld.state = (int)pointObj[0]["state"];
                        listLd.Add(ld);
                    }
                }
            }

            int startIndex = 0;
            int endIndex = 0;
            int delayTimeX = 0;
            StringBuilder sbScript = new StringBuilder();
            bool swipeFlag = false;
            LdOperation ldstart = new LdOperation();
            if (listLd.Count>0)
            {
                ldstart = listLd[0];
            }
            for (int i=0;i<listLd.Count;i++)
            {
                LdOperation ld = listLd[i];
                if (ld.operationId.Equals("PutMultiTouch"))
                {
                    if (ld.state == 0)
                    {
                        ldstart = listLd[startIndex];
                        if (sbScript.Length>0)
                        {
                            sbScript.Append("sleep(" + (delayTimeX + 100)+");");
                            sbScript.Append(Environment.NewLine);
                        }
                        endIndex = i;
                        startIndex = endIndex + 1;
                        if (swipeFlag)
                        {
                            sbScript.Append("swipe(" + convertPointX(ldstart.x)+ "," + convertPointY(ldstart.y) + "," + convertPointX(ld.x) + "," + convertPointY(ld.y) + "," + ((ld.timing - ldstart.timing))+");");
                            sbScript.Append(Environment.NewLine);
                        }
                        else 
                        {
                            sbScript.Append("tap(" + convertPointX(ldstart.x)+ "," + convertPointY(ldstart.y)+");");
                            sbScript.Append(Environment.NewLine);
                        }
                        swipeFlag = false;
                    }
                    else 
                    {
                        delayTimeX = ld.timing - ldstart.timing;
                        if (i>startIndex && ld.state==1)
                        {
                            swipeFlag = true;
                        }
                    }
                }
            }

            this.textBox1.Text = sbScript.ToString();
            Clipboard.Clear();
            Clipboard.SetText(this.textBox1.Text);
        }
    }
}

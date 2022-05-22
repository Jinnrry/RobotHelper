using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;

namespace FastHook
{
    public class CmpColor : IComparable
    {
        public Color cmpColor;
        public Point p;
        public Point srcPoint;

        public string BGR16()
        {
            string color16 = cmpColor.ToArgb().ToString("X2").Substring(2);
            color16 = color16.Substring(4) + color16.Substring(2,2) + color16.Substring(0,2);
            return color16;
        }

        public string RGB16()
        {
            string color16 = cmpColor.ToArgb().ToString("X2").Substring(2);
            color16 =  color16.Substring(0,2)+color16.Substring(2,2)+ color16.Substring(4);
            return color16;
        }

        public string RGB()
        {
            return cmpColor.R + "," + cmpColor.G + "," + cmpColor.B;
        }


        public int CompareTo(Object cmpColor)
        {
            CmpColor cmpObj = (CmpColor)cmpColor;
            //int mcolor = this.cmpColor.ToArgb();
            //int ccolor = cmpObj.cmpColor.ToArgb();
            //if (mcolor > ccolor)
            //{
            //    return 1;
            //}
            //else if (mcolor < ccolor)
            //{
            //    return -1;
            //}
            //else 
            //{
            //    return 0;
            //}

            var hsv1 = new HSV(this.cmpColor.R,this.cmpColor.G,this.cmpColor.B);
            var hsv2 = new HSV(cmpObj.cmpColor.R,cmpObj.cmpColor.G,cmpObj.cmpColor.B);


            // H S V
            //if (hsv1.H > hsv2.H)
            //{
            //    return 1;
            //}
            //else if (hsv1.H == hsv2.H)
            //{
            //    if (hsv1.S > hsv2.S)
            //    {
            //        return 1;
            //    }
            //    else if (hsv1.S < hsv2.S)
            //    {
            //        return -1;
            //    }
            //    else
            //    {
            //        return 0;
            //    }
            //}
            //else
            //{
            //    return -1;
            //}

            //S V H
            if (hsv1.S + hsv1.V > hsv2.S + hsv2.V)
            {
                return -1;
            }
            else if (hsv1.S + hsv1.V == hsv2.S + hsv2.V)
            {
                if (hsv1.H > hsv2.H)
                {
                    return -1;
                }
                else if (hsv1.H < hsv2.H)
                {
                    return 1;
                }
                else
                {
                    return 0;
                }
            }
            else
            {
                return 1;
            }




            //double hsv1cmval = hsv1.H * 100 + hsv1.S * 10 + hsv1.V;
            //double hsv2cmval = hsv2.H * 100 + hsv2.S * 10 + hsv2.V;
            //if (hsv1cmval > hsv2cmval)
            //{
            //    return 1;
            //}
            //else
            //{
            //    return -1;
            //}
        }
    }
}

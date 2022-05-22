using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FastHook
{
    /// <summary>
    /// HSV颜色坐标
    /// </summary>
    public class HSV
    {
        public double H;
        public double S;
        public double V;
        public HSV(int red,int green,int blue)
        {
            ColorUtil.RGB2HSV(red,green,blue,out H,out S,out V);
        }

    }
    public static class ColorUtil
    {
        //self-defined
        private static double R = 100;
        private static double angle = 30;
        private static double h = R * Math.Cos(angle / 180 * Math.PI);
        private static double r = R * Math.Sin(angle / 180 * Math.PI);

        /// <summary>
        /// 返回两个颜色在HSV颜色空间的距离
        /// </summary>
        /// <param name="hsv1"></param>
        /// <param name="hsv2"></param>
        /// <returns></returns>
        public static double DistanceOf(HSV hsv1,HSV hsv2)
        {
            double x1 = r * hsv1.V * hsv1.S * Math.Cos(hsv1.H / 180 * Math.PI);
            double y1 = r * hsv1.V * hsv1.S * Math.Sin(hsv1.H / 180 * Math.PI);
            double z1 = h * (1 - hsv1.V);
            double x2 = r * hsv2.V * hsv2.S * Math.Cos(hsv2.H / 180 * Math.PI);
            double y2 = r * hsv2.V * hsv2.S * Math.Sin(hsv2.H / 180 * Math.PI);
            double z2 = h * (1 - hsv2.V);
            double dx = x1 - x2;
            double dy = y1 - y2;
            double dz = z1 - z2;
            return Math.Sqrt(dx * dx + dy * dy + dz * dz);
        }

        /// <summary>
        /// RGB转换HSV
        /// </summary>
        /// <param name="red"></param>
        /// <param name="green"></param>
        /// <param name="blue"></param>
        /// <param name="hue"></param>
        /// <param name="sat"></param>
        /// <param name="bri"></param>
        public static void RGB2HSV(int red,int green,int blue,out double hue,out double sat,out double bri)
        {
            double r = ((double)red / 255.0);
            double g = ((double)green / 255.0);
            double b = ((double)blue / 255.0);

            double max = Math.Max(r,Math.Max(g,b));
            double min = Math.Min(r,Math.Min(g,b));

            hue = 0.0;
            if (max == r && g >= b)
            {
                if (max - min == 0) hue = 0.0;
                else hue = 60 * (g - b) / (max - min);
            }
            else if (max == r && g < b)
            {
                hue = 60 * (g - b) / (max - min) + 360;
            }
            else if (max == g)
            {
                hue = 60 * (b - r) / (max - min) + 120;
            }
            else if (max == b)
            {
                hue = 60 * (r - g) / (max - min) + 240;
            }

            sat = (max == 0) ? 0.0 : (1.0 - ((double)min / (double)max));
            bri = max;
        }

        private static double[] M = {
            0.412453,0.357580,0.180423,
            0.212671,0.715160,0.072169,
            0.019334,0.119193,0.950227
        };

        public static double[] ConvertRGBToLab(byte[] rgb)
        {
            double R = Gamma(rgb[0] / 255.0);
            double G = Gamma(rgb[1] / 255.0);
            double B = Gamma(rgb[2] / 255.0);
            double X = R * M[0] + G * M[1] + B * M[2];
            double Y = R * M[3] + G * M[4] + B * M[5];
            double Z = R * M[6] + G * M[7] + B * M[8];
            X /= 0.95047;
            Y /= 1.0;
            Z /= 1.08883;
            double L = Y > Math.Pow(6.0 / 29,3) ? 116 * F(Y) - 16 : 903.3 * Y;
            double a = 500 * (F(X) - F(Y));
            double b = 200 * (F(Y) - F(Z));
            return new[] { L,a,b };
        }
        private static double Gamma(double x)
        {
            return x > 0.04045 ? Math.Pow((x + 0.055) / 1.055,2.4) : x / 12.92;
        }
        private static double F(double t)
        {
            return t > Math.Pow(6.0 / 29,3) ? Math.Pow(t,1.0 / 3) : Math.Pow(4.0 / 29,2) / 3 * t + 4.0 / 29;
        }

        public static double CalColorDifference(double[] lab1,double[] lab2,string method)
        {
            double sub = 0;
            if (method == "CIE2000")
                sub = CIE2000(lab1,lab2);
            return sub;
        }

        public static double CIE2000(double[] lab1,double[] lab2)
        {
            double L1 = lab1[0],a1 = lab1[1],b1 = lab1[2];
            double L2 = lab2[0],a2 = lab2[1],b2 = lab2[2];
            double C1 = Math.Sqrt(a1 * a1 + b1 * b1),C2 = Math.Sqrt(a2 * a2 + b2 * b2);
            double LSub_ = L2 - L1;
            double LMean = (L1 + L2) / 2;
            double CMean = (C1 + C2) / 2;
            double a1_ = a1 + a1 / 2 * (1 - Math.Sqrt(Math.Pow(CMean,7) / (Math.Pow(CMean,7) + Math.Pow(25,7))));
            double a2_ = a2 + a2 / 2 * (1 - Math.Sqrt(Math.Pow(CMean,7) / (Math.Pow(CMean,7) + Math.Pow(25,7))));
            double C1_ = Math.Sqrt(a1_ * a1_ + b1 * b1),C2_ = Math.Sqrt(a2_ * a2_ + b2 * b2);
            double CMean_ = (C1_ + C2_) / 2,CSub_ = C2_ - C1_;
            double h1_ = Math.Atan2(b1,a1_),h2_ = Math.Atan2(b2,a2_);
            double hSub_ = Math.Abs(h1_ - h2_) <= 180 ? h2_ - h1_ : h2_ <= h1_ ? h2_ - h1_ + 360 : h2_ - h1_ - 360;
            double HSub_ = 2 * Math.Sqrt(C1_ * C2_) * Math.Sin(hSub_ / 2);
            double HMean_ = Math.Abs(h1_ - h2_) > 180 ? (h1_ + h2_ + 360) / 2 : (h1_ + h2_) / 2;
            double T = 1 - 0.17 * Math.Cos(HMean_ - 30) + 0.24 * Math.Cos(2 * HMean_) +
                       0.32 * Math.Cos(3 * HMean_ + 6) - 0.2 * Math.Cos(4 * HMean_ - 63);
            double SL = 1 + 0.015 * Math.Pow(LMean - 50,2) / Math.Sqrt(20 + Math.Pow(LMean - 50,2));
            double SC = 1 + 0.045 * CMean_,SH = 1 + 0.015 * CMean_ * T;
            double RT = -2 * Math.Sqrt(Math.Pow(CMean_,7) / (Math.Pow(CMean_,7) + Math.Pow(25,7))) *
                        Math.Sin(60 * Math.Exp(-Math.Pow((HMean_ - 275) / 25,2)));
            double result = Math.Sqrt(Math.Pow(LSub_ / SL,2) + Math.Pow(CSub_ / SC,2) + Math.Pow(HSub_ / SH,2) +
                                      RT * CSub_ / SC * HSub_ / SH);
            return result;
        }
    }
}

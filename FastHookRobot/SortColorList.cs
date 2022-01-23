using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FastHook
{
    class SortColorList : IComparer<List<CmpColor>>
    {

        public int Compare(List<CmpColor> x,List<CmpColor> y)
        {
            if (x.Count > y.Count)
            {
                return -1;
            }
            else if (x.Count < y.Count)
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
    }
}

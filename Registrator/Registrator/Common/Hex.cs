using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Registrator.Common
{
    public class Hex
    {
        public static string ToString<T>(T mark)
        {
            return String.Format("{0:X" + System.Runtime.InteropServices.Marshal.SizeOf(typeof(T)) * 2+ "}", mark);
        }

        public static string ToString<T>(T mark, uint counts)
        {
            string str = Hex.ToString<T>(mark);
            string res = "";
            for (uint i = 0; i < counts; ++i)
            {
                res += str;
            }
            return res;
        }

    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Registrator.Common;

namespace Registrator.ExternalInteface
{
    public class ShortMarker
           : Marker
    {
        private bool fill_ = false;
        private const uint chunkLength = 6;

        public ShortMarker(byte[] resource, bool fill)
            : base(resource)           
        {
            fill_ = fill;
        }

        protected override string  getAttributes(string filename, uint mark, string destination, uint offset)
        {
            string chunk = String.Format("{0:X4}", mark);
            string total_mark = chunk;
            if (fill_) {
                WAVFileInfo f = new WAVFileInfo(filename);
                uint counts = (uint)Math.Floor(f.TimeLength / chunkLength);
                for (uint i = 1; i < counts; ++i) {
                    total_mark += chunk;
                }
            }
            return  String.Format("\"{0}\" \"{1}\" {2} 1 {3}", filename, destination, total_mark, offset);            
        }
    }
}

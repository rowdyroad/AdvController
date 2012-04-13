using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using System.IO;

namespace Registrator.ExternalInteface
{
    public class ByteMarker 
        : Marker
    {
        public ByteMarker(byte[] resource)
            : base(resource)
        {

        }

        protected override string getAttributes(string filename, uint mark, string destination, uint offset)
        {
            return String.Format("\"{0}\" \"{1}\" {2:X2} 2 {3}", filename, destination, mark, offset);
        }
    }
}

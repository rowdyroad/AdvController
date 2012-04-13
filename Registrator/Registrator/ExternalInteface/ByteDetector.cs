using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Registrator.ExternalInteface
{
    class ByteDetector
        : Detector
    {
        public ByteDetector(byte[] resource)
            : base(resource)
        {

        }

        protected override string getAttributes(string filename, uint offset)
        {
            return String.Format("\"{0}\" 2 {1}", filename, offset);
        }

        protected override void notifyDetectByte(byte mark)
        {
            Handler.OnDetect(this, mark);
        }
    }
}

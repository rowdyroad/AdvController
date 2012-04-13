using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Registrator.ExternalInteface
{
    class ShortDetector
            : Detector
    {
        private byte last_ = 0;

        public ShortDetector(byte[] resource)
            : base(resource)
        {

        }

        protected override void notifyDetectByte(byte mark)
        {
            if (last_ == 0)
            {
                last_ = mark;
            }
            else
            {
                uint m = (uint)(mark | last_ << 8);
                Handler.OnDetect(this, m);
            }            
        }

        protected override string getAttributes(string filename, uint offset)
        {
            return String.Format("\"{0}\" 1 {1}", filename, offset);
        }
    }
}

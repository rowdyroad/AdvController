using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Registrator.ExternalInteface
{
    public interface IDetectHandler
    {
        void OnDetect(IDetector sender, uint mark);
        void OnDetectProcess(IDetector sender, byte percent);
        void OnDetectComplete(IDetector sender, int errno);
    }

    public interface IDetector
    {
        void Detect(string filename, uint offset, IDetectHandler detectHandler);
    }
}
